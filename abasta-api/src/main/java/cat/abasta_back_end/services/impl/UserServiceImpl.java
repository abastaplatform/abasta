package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.PasswordResetDTO;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.entities.Company;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.CompanyRepository;
import cat.abasta_back_end.repositories.UserRepository;
import cat.abasta_back_end.security.JwtUtil;
import cat.abasta_back_end.services.EmailService;
import cat.abasta_back_end.services.UserService;
import lombok.RequiredArgsConstructor;
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
     * Autentica un usuari mitjançant les seves credencials d'accés.
     * <p>
     * El procés d'autenticació inclou les següents validacions:
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
     * <p>Després de verificar l'email, s'envia un correu de benvinguda.
     * Si l'usuari és ADMIN i la seva empresa està en estat PENDING, l'empresa
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
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
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
}