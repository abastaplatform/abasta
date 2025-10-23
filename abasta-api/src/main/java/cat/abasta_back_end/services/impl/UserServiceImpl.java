package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.dto.PasswordResetDTO;
import cat.abasta_back_end.entities.User;
import cat.abasta_back_end.exceptions.BadRequestException;
import cat.abasta_back_end.exceptions.ResourceNotFoundException;
import cat.abasta_back_end.repositories.UserRepository;
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
 * Proporciona la lògica de negoci per a la gestió d'usuaris, incloent
 * restabliment de contrasenyes i verificació d'emails.
 *
 * <p>Totes les operacions són transaccionals per garantir la consistència de les dades.</p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Encoder per encriptar i verificar contrasenyes utilitzant BCrypt.
     * Injectat automàticament per Spring gràcies a @RequiredArgsConstructor de Lombok.
     */
    private final PasswordEncoder passwordEncoder;


    /**
     * {@inheritDoc}
     *
     * Genera un token UUID únic i estableix una data d'expiració d'1 hora.
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
     * La nova contrasenya s'encripta abans de ser desada.
     * Després de restablir la contrasenya, el token i la seva data d'expiració s'eliminen.
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
     * Després de verificar l'email, s'envia un correu de benvinguda.
     * Si l'usuari és ADMIN i la seva empresa està en estat PENDING, l'empresa s'activa.
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
     * Genera un nou token de verificació amb una validesa de 24 hores.
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