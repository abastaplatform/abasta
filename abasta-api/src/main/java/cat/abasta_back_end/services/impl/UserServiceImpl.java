package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.*;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.DuplicateResourceException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.UserRepository;
import cat.abasta_back_end.security.JwtUtil;
import cat.abasta_back_end.services.EmailService;
import cat.abasta_back_end.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementació del servei UserService.
 * Proporciona la lògica de negoci per a la gestió d'usuaris, incloent-hi
 * autenticació, restabliment de contrasenyes i verificació d'emails.
 *
 * <p>Totes les operacions són transaccionals per garantir la consistència de les dades.
 * La classe utilitza Spring Security per a l'autenticació i JWT per a la gestió de sessions.</p>
 *
 * <p>Funcionalitats principals:
 * <ul>
 *   <li>Autenticació d'usuaris amb generació de tokens JWT</li>
 *   <li>Restabliment segur de contrasenyes amb tokens temporals</li>
 *   <li>Verificació d'emails amb activació automàtica d'empreses</li>
 *   <li>Reenviament de correus de verificació</li>
 * </ul>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @see UserService
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    /**
     * Repositori per a operacions de persistència d'usuaris.
     * Proporciona mètodes per cercar usuaris per email, tokens de verificació, etc.
     */
    private final UserRepository userRepository;

    /**
     * Repositori per a operacions de persistència d'empreses.
     * Utilitzat per activar empreses quan els administradors verifiquen el seu email.
     */
    private final CompanyRepository companyRepository;

    /**
     * Servei per a l'enviament de correus electrònics.
     * Gestiona l'enviament de correus de verificació, recuperació de contrasenya i benvinguda.
     */
    private final EmailService emailService;

    /**
     * Utilitat per gestionar tokens JWT.
     * Genera i valida tokens d'autenticació per a les sessions dels usuaris.
     */
    private final JwtUtil jwtUtil;

    /**
     * Encoder per encriptar i verificar contrasenyes utilitzant BCrypt.
     * Injectat automàticament per Spring gràcies a @RequiredArgsConstructor de Lombok.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("Ja existeix un usuari amb l'email: " + registrationDTO.getEmail());
        }

        String companyUuid = getCompanyUuidFromAuthenticatedUser();
        // Verificar que l'empresa existeix
        Company company = companyRepository.findByUuid(companyUuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa no trobada amb UUID: " + companyUuid));

        // Generar token de verificació (vàlid per 24 hores)
        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .company(company)
                .email(registrationDTO.getEmail())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .role(registrationDTO.getRole() != null ? registrationDTO.getRole() : User.UserRole.USER)
                .phone(registrationDTO.getPhone())
                .isActive(true)
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationExpires(LocalDateTime.now().plusHours(24))
                .build();

        User savedUser = userRepository.save(user);

        // Correu electrònic per a usuaris que s'uneixen a una empresa existent
        emailService.sendEmailVerification(
                savedUser.getEmail(),
                verificationToken,
                savedUser.getFirstName()
        );

        return mapToResponseDTO(savedUser);
    }

    /**
     * Autentica un usuari mitjançant les seves credencials d'accés.
     * <p>
     * El procés d'autenticació inclou aquestes validacions:
     * <ul>
     *   <li>Comprova que l'usuari existeixi segons l'email proporcionat.</li>
     *   <li>Verifica que el compte estigui actiu.</li>
     *   <li>Comprova que l'email hagi estat verificat.</li>
     *   <li>Valida que la contrasenya introduïda coincideixi amb la guardada a la base de dades.</li>
     * </ul>
     * Si totes les validacions són correctes, s'actualitza la data de l'últim inici de sessió
     * i es genera un token JWT per a l'usuari autenticat.
     *
     * @param loginDTO l'objecte que conté les credencials d'inici de sessió (correu electrònic i contrasenya)
     * @return un {@link LoginResponseDTO} amb el token JWT generat, el seu tipus ("Bearer") i la informació de l'usuari
     * @throws BadRequestException si les credencials són incorrectes, l'usuari està inactiu o no ha verificat el correu electrònic
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new BadRequestException("Credencials invàlides"));

        if (!user.getIsActive()) {
            throw new BadRequestException("L'usuari està inactiu");
        }

        if (!user.getEmailVerified()) {
            throw new BadRequestException("Has de verificar el teu correu electrònic abans d'iniciar sessió. Revisa la teva safata d'entrada.");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Credencials invàlides");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .user(mapToResponseDTO(user))
                .build();
    }


    /**
     * {@inheritDoc}
     *
     * <p>Genera un token UUID únic i estableix una data d'expiració d'1 hora.
     * Envia un correu electrònic amb l'enllaç de restabliment.</p>
     */
    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat amb l'email: " + email));

        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken, user.getFirstName());
    }

    /**
     * {@inheritDoc}
     *
     * <p>La nova contrasenya s'encripta abans de ser desada utilitzant BCrypt.
     * Després de restablir la contrasenya, el token i la seva data d'expiració s'eliminen
     * per garantir que no es pugui reutilitzar.</p>
     */
    @Override
    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        User user = userRepository.findByValidResetToken(passwordResetDTO.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Token invàlid o expirat"));

        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Si l'usuari és ADMIN i la seva empresa està en estat PENDING, l'empresa
     * s'activa automàticament, cosa que permet que altres usuaris puguin unir-se.</p>
     */
    @Override
    public void verifyEmail(String token) {
        User user = userRepository.findByValidVerificationToken(token, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Token de verificació invàlid o expirat"));

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpires(null);

        // Activar l'empresa si l'usuari és administrador
        if (user.getRole() == User.UserRole.ADMIN) {
            Company company = user.getCompany();
            if (company != null && company.getStatus() == Company.CompanyStatus.PENDING) {
                company.setStatus(Company.CompanyStatus.ACTIVE);
                companyRepository.save(company);
            }
        }

        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Genera un nou token de verificació amb una validesa de 24 hores.
     * Aquest mètode és útil quan l'usuari no ha rebut el correu inicial o
     * el token ha expirat.</p>
     */
    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat amb l'email: " + email));

        if (user.getEmailVerified()) {
            throw new BadRequestException("Aquest email ja està verificat");
        }

        // Generar nou token de verificació
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationExpires(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Reenviar email
        emailService.sendEmailVerification(user.getEmail(), verificationToken, user.getFirstName());
    }

    /**
     * Obté l'UUID de l'empresa de l'usuari autenticat des del context de Spring Security.
     * Aquest mètode s'utilitza per garantir que l'usuari
     * només pugui accedir als empleats de la seva pròpia empresa.
     *
     * @return UUID de l'empresa associada a l'usuari autenticat
     * @throws ResourceNotFoundException si l'usuari no existeix o no té empresa assignada
     */
    private String getCompanyUuidFromAuthenticatedUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuari no trobat: " + username));

        if (user.getCompany() == null || user.getCompany().getUuid() == null) {
            throw new ResourceNotFoundException("L'usuari no té empresa assignada");
        }

        return user.getCompany().getUuid();
    }

    /**
     * Mapeja una entitat User a un DTO UserResponseDTO per enviar al client.
     * Aquest mètode extreu només les dades segures i públiques de l'usuari,
     * excloent informació delicada com contrasenyes o tokens.
     *
     * <p>El mapeig inclou:
     * <ul>
     *   <li>Identificador (UUID)</li>
     *   <li>Informació de l'empresa (UUID i nom)</li>
     *   <li>Dades personals (nom, cognoms, email, telèfon)</li>
     *   <li>Estat del compte (rol, actiu, email verificat)</li>
     *   <li>Timestamps (últim login, creació, actualització)</li>
     * </ul>
     * </p>
     *
     * <p><strong>Nota:</strong> Aquest mètode assumeix que l'usuari té una empresa associada.
     * Si un usuari pot no tenir empresa, caldria afegir validacions null-safe per evitar
     * NullPointerException.</p>
     *
     * @param user l'entitat User de la base de dades
     * @return UserResponseDTO amb les dades públiques de l'usuari
     * @throws NullPointerException si l'usuari no té una empresa associada (company és null)
     * @see UserResponseDTO
     */
    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .uuid(user.getUuid())
                .companyUuid(user.getCompany().getUuid())
                .companyName(user.getCompany().getName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}