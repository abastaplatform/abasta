package cat.abasta_back_end.services.impl;

import cat.abasta_back_end.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei d'enviament de correus electrònics.
 * Gestiona l'enviament de diferents tipus de correus relacionats amb l'autenticació,
 * verificació i benvinguda d'usuaris i empreses.
 *
 * <p>Utilitza JavaMailSender per enviar correus HTML amb format professional.
 * Tots els correus inclouen plantilles HTML responsives amb estils inline.</p>
 *
 * <p>Els correus enviats inclouen:
 * <ul>
 *   <li>Recuperació de contrasenya amb enllaç temporal</li>
 *   <li>Verificació d'email per a usuaris estàndard</li>
 *   <li>Verificació d'empresa per a administradors</li>
 *   <li>Correus de benvinguda després de verificar el compte</li>
 * </ul>
 * </p>
 *
 * @author Enrique Pérez
 * @version 1.0
 * @see EmailService
 * @see JavaMailSender
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    /**
     * Client JavaMail per enviar correus electrònics.
     * Injectat automàticament per Spring.
     */
    private final JavaMailSender mailSender;

    /**
     * Adreça de correu electrònic d'origen configurada a application.properties.
     * S'obté de la propietat spring.mail.username.
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * URL del frontend de l'aplicació per generar enllaços de verificació.
     * S'obté de la propietat app.frontend.url amb valor per defecte https://deveps.ddns.net/abasta.
     */
    @Value("${app.frontend.url:https://deveps.ddns.net/abasta}")
    private String frontendUrl;

    /**
     * {@inheritDoc}
     * <p>
     * Envia un correu HTML amb un enllaç per restablir la contrasenya.
     * L'enllaç inclou un token de seguretat que expira després d'1 hora.
     *
     * @throws RuntimeException si es produeix un error en l'enviament del correu
     */
    @Override
    public void sendPasswordResetEmail(String to, String token, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Recuperació de Contrasenya - Abasta");
            helper.setText(buildPasswordResetEmailBody(token, userName), true); // true = HTML

            mailSender.send(message);
            log.info("Email de recuperació enviat a: {}", to);
        } catch (MessagingException e) {
            log.error("Error en enviar l'email de recuperació: {}", e.getMessage());
            throw new RuntimeException("Error en enviar l'email de recuperació");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Envia un correu HTML de benvinguda després que l'usuari hagi verificat
     * el seu compte correctament. Aquest correu no conté cap acció requerida.
     *
     * <p>Si hi ha un error en l'enviament, es registra però no es llança excepció
     * per no interrompre el flux de verificació de l'usuari.</p>
     */
    @Override
    public void sendWelcomeEmail(String to, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("¡Benvingut a Abasta!");
            helper.setText(buildWelcomeEmailBody(userName), true); // true = HTML

            mailSender.send(message);
            log.info("Email de benvinguda enviat a: {}", to);
        } catch (MessagingException e) {
            log.error("Error en enviar l'email de benvinguda: {}", e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Envia un correu HTML amb un enllaç per verificar l'adreça de correu electrònic.
     * L'enllaç inclou un token de verificació que expira després de 24 hores.
     * Aquest mètode s'utilitza per a usuaris estàndard (no administradors d'empresa).
     *
     * @throws RuntimeException si es produeix un error en l'enviament del correu
     */
    @Override
    public void sendEmailVerification(String to, String token, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Verifica el teu compte d'Abasta");
            helper.setText(buildEmailVerificationBody(token, userName), true); // true = HTML

            mailSender.send(message);
            log.info("Email de verificació enviat a: {}", to);
        } catch (MessagingException e) {
            log.error("Error en enviar l'email de verificació: {}", e.getMessage());
            throw new RuntimeException("Error en enviar l'email de verificació");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Envia un correu HTML especial per a administradors d'empresa amb informació
     * sobre la verificació i activació de l'empresa. L'enllaç inclou un token que
     * expira després de 24 hores.
     *
     * <p>Aquest correu inclou informació addicional sobre els privilegis d'administrador
     * i les funcionalitats que estarán disponibles després de la verificació.</p>
     *
     * @throws RuntimeException si es produeix un error en l'enviament del correu
     */
    @Override
    public void sendCompanyAdminVerification(String to, String token, String userName, String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("¡Benvingut a Abasta! - Verifica la teva empresa");
            helper.setText(buildCompanyAdminVerificationBody(token, userName, companyName), true);

            mailSender.send(message);
            log.info("Email de verificació d'empresa enviat a: {} per a l'empresa: {}", to, companyName);
        } catch (MessagingException e) {
            log.error("Error en enviar l'email de verificació de l'empresa: {}", e.getMessage());
            throw new RuntimeException("Error en enviar l'email de verificació d'empresa");
        }
    }

    /**
     * Construeix el cos HTML del correu de recuperació de contrasenya.
     *
     * <p>La plantilla HTML inclou:
     * <ul>
     *   <li>Capçalera amb el logo i nom d'Abasta</li>
     *   <li>Missatge personalitzat amb el nom de l'usuari</li>
     *   <li>Botó destacat amb l'enllaç de restabliment</li>
     *   <li>Advertència sobre l'expiració del token (1 hora)</li>
     *   <li>Nota de seguretat si no va sol·licitar el canvi</li>
     *   <li>Peu de pàgina amb informació de contacte</li>
     * </ul>
     * </p>
     *
     * @param token    el token únic de restabliment de contrasenya
     * @param userName el nom de l'usuari per personalitzar el missatge
     * @return el cos HTML del correu com a String
     */
    private String buildPasswordResetEmailBody(String token, String userName) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        return """
                 <!DOCTYPE html>
                        <html lang="ca">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Recuperació de Contrasenya</title>
                        </head>
                        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                                <tr>
                                    <td align="center">
                                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                            <!-- Header -->
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center;">
                                                    <h1 style="color: #ffffff; margin: 0; font-size: 28px;">Abasta</h1>
                                                </td>
                                            </tr>
                
                                            <!-- Body -->
                                            <tr>
                                                <td style="padding: 40px 30px;">
                                                    <h2 style="color: #333333; margin-top: 0;">Hola %s,</h2>
                                                    <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                                        Has sol·licitat restablir la teva contrasenya. Fes clic al botó de sota per crear una contrasenya nova:
                                                    </p>
                
                                                    <!-- Button -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 30px 0;">
                                                        <tr>
                                                            <td align="center">
                                                                <a href="%s" style="display: inline-block; padding: 16px 40px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);">
                                                                    Restablir Contrasenya
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <!-- Timer Info -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 20px 0; background-color: #fff3cd; border-radius: 6px;">
                                                        <tr>
                                                            <td style="padding: 15px; text-align: center;">
                                                                <p style="margin: 0; color: #856404; font-size: 13px;">
                                                                    ⏱️ Aquest enllaç expirarà en <strong>1 hora</strong>
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <p style="color: #999999; font-size: 13px; line-height: 1.6; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eeeeee;">
                                                        Si no has sol·licitat restablir la teva contrasenya, pots ignorar aquest correu de forma segura. La teva contrasenya actual continuarà sent vàlida.
                                                    </p>
                                                </td>
                                            </tr>
                
                                            <!-- Footer -->
                                            <tr>
                                                <td style="background-color: #f8f8f8; padding: 20px 30px; text-align: center; border-top: 1px solid #eeeeee;">
                                                    <p style="color: #999999; font-size: 12px; margin: 0;">
                                                        © 2025 Abasta. Tots els drets reservats.
                                                    </p>
                                                    <p style="color: #999999; font-size: 11px; margin: 10px 0 0 0;">
                                                        Si el botó no funciona, copia i enganxa aquest enllaç al teu navegador:<br>
                                                        <span style="color: #667eea;">%s</span>
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </body>
                        </html>
                \s""".formatted(userName, resetLink, resetLink);
    }

    /**
     * Construeix el cos HTML del correu de benvinguda.
     *
     * <p>La plantilla HTML inclou:
     * <ul>
     *   <li>Capçalera amb missatge de benvinguda</li>
     *   <li>Missatge personalitzat amb el nom de l'usuari</li>
     *   <li>Informació sobre les funcionalitats disponibles</li>
     *   <li>Enllaç al dashboard de l'aplicació</li>
     *   <li>Peu de pàgina amb informació de contacte</li>
     * </ul>
     * </p>
     *
     * @param userName el nom de l'usuari per personalitzar el missatge
     * @return el cos HTML del correu com a String
     */
    private String buildWelcomeEmailBody(String userName) {
        String dashboardLink = frontendUrl + "/dashboard";

        return """
                 <!DOCTYPE html>
                        <html lang="ca">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Benvingut a Abasta</title>
                        </head>
                        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                                <tr>
                                    <td align="center">
                                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                            <!-- Header -->
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center;">
                                                    <h1 style="color: #ffffff; margin: 0; font-size: 28px;">🎉 Benvingut a Abasta!</h1>
                                                </td>
                                            </tr>
                
                                            <!-- Body -->
                                            <tr>
                                                <td style="padding: 40px 30px;">
                                                    <h2 style="color: #333333; margin-top: 0;">Hola %s!</h2>
                                                    <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                                        El teu compte ha estat verificat amb èxit. Ara pots accedir a totes les funcionalitats de la plataforma.
                                                    </p>
                
                                                    <!-- Button -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 30px 0;">
                                                        <tr>
                                                            <td align="center">
                                                                <a href="%s" style="display: inline-block; padding: 16px 40px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);">
                                                                    Anar al Tauler
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <p style="color: #666666; font-size: 14px; line-height: 1.6; margin-top: 30px;">
                                                        Estem emocionats de tenir-te amb nosaltres. Si tens alguna pregunta, no dubtis a contactar-nos.
                                                    </p>
                                                </td>
                                            </tr>
                
                                            <!-- Footer -->
                                            <tr>
                                                <td style="background-color: #f8f8f8; padding: 20px 30px; text-align: center; border-top: 1px solid #eeeeee;">
                                                    <p style="color: #999999; font-size: 12px; margin: 0;">
                                                        © 2025 Abasta. Tots els drets reservats.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </body>
                        </html>
                \s""".formatted(userName, dashboardLink);
    }

    /**
     * Construeix el cos HTML del correu de verificació d'email estàndard.
     *
     * <p>La plantilla HTML inclou:
     * <ul>
     *   <li>Capçalera amb el logo d'Abasta</li>
     *   <li>Missatge personalitzat amb el nom de l'usuari</li>
     *   <li>Botó destacat amb l'enllaç de verificació</li>
     *   <li>Advertència sobre l'expiració del token (24 hores)</li>
     *   <li>Nota de seguretat si no es va registrar</li>
     *   <li>Peu de pàgina amb l'enllaç alternatiu</li>
     * </ul>
     * </p>
     *
     * @param token    el token únic de verificació d'email
     * @param userName el nom de l'usuari per personalitzar el missatge
     * @return el cos HTML del correu com a String
     */
    private String buildEmailVerificationBody(String token, String userName) {
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        return """
                 <!DOCTYPE html>
                        <html lang="ca">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Verifica el teu Email</title>
                        </head>
                        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                                <tr>
                                    <td align="center">
                                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                            <!-- Header -->
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center;">
                                                    <h1 style="color: #ffffff; margin: 0; font-size: 28px;">Abasta</h1>
                                                </td>
                                            </tr>
                
                                            <!-- Body -->
                                            <tr>
                                                <td style="padding: 40px 30px;">
                                                    <h2 style="color: #333333; margin-top: 0;">Hola %s,</h2>
                                                    <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                                        Gràcies per registrar-te a Abasta. Per completar el teu registre, si us plau verifica la teva adreça de correu electrònic.
                                                    </p>
                
                                                    <!-- Button -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 30px 0;">
                                                        <tr>
                                                            <td align="center">
                                                                <a href="%s" style="display: inline-block; padding: 16px 40px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);">
                                                                    Verificar Email
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <!-- Timer Info -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 20px 0; background-color: #fff3cd; border-radius: 6px;">
                                                        <tr>
                                                            <td style="padding: 15px; text-align: center;">
                                                                <p style="margin: 0; color: #856404; font-size: 13px;">
                                                                    ⏱️ Aquest enllaç expirarà en <strong>24 hores</strong>
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <p style="color: #999999; font-size: 13px; line-height: 1.6; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eeeeee;">
                                                        Si no t'has registrat a Abasta, pots ignorar aquest correu de forma segura.
                                                    </p>
                                                </td>
                                            </tr>
                
                                            <!-- Footer -->
                                            <tr>
                                                <td style="background-color: #f8f8f8; padding: 20px 30px; text-align: center; border-top: 1px solid #eeeeee;">
                                                    <p style="color: #999999; font-size: 12px; margin: 0;">
                                                        © 2025 Abasta. Tots els drets reservats.
                                                    </p>
                                                    <p style="color: #999999; font-size: 11px; margin: 10px 0 0 0;">
                                                        Si el botó no funciona, copia i enganxa aquest enllaç al teu navegador:<br>
                                                        <span style="color: #667eea;">%s</span>
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </body>
                        </html>
                \s""".formatted(userName, verificationLink, verificationLink);
    }

    /**
     * Construeix el cos HTML del correu de verificació per a administradors d'empresa.
     *
     * <p>La plantilla HTML inclou:
     * <ul>
     *   <li>Capçalera amb missatge de benvinguda especial</li>
     *   <li>Missatge personalitzat amb nom d'usuari i empresa</li>
     *   <li>Caixa informativa amb privilegis d'administrador</li>
     *   <li>Botó destacat amb l'enllaç de verificació i activació</li>
     *   <li>Advertència sobre l'expiració del token (24 hores)</li>
     *   <li>Informació sobre el panell d'administració</li>
     *   <li>Peu de pàgina amb l'enllaç alternatiu</li>
     * </ul>
     * </p>
     *
     * @param token       el token únic de verificació
     * @param userName    el nom de l'administrador
     * @param companyName el nom de l'empresa a verificar i activar
     * @return el cos HTML del correu com a String
     */
    private String buildCompanyAdminVerificationBody(String token, String userName, String companyName) {
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        return """
                 <!DOCTYPE html>
                        <html lang="ca">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Verifica la teva Empresa a Abasta</title>
                        </head>
                        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                                <tr>
                                    <td align="center">
                                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                            <!-- Header -->
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center;">
                                                    <h1 style="color: #ffffff; margin: 0; font-size: 28px;">🎉 Benvingut a Abasta!</h1>
                                                </td>
                                            </tr>
                
                                            <!-- Body -->
                                            <tr>
                                                <td style="padding: 40px 30px;">
                                                    <h2 style="color: #333333; margin-top: 0;">Hola %s,</h2>
                
                                                    <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                                        Gràcies per registrar la teva empresa <strong style="color: #667eea;">%s</strong> a Abasta!
                                                    </p>
                
                                                    <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                                        Estem emocionats que formis part de la nostra plataforma. Per completar el registre de la teva empresa i poder començar a gestionar el teu equip, necessitem que verificis la teva adreça de correu electrònic.
                                                    </p>
                
                                                    <!-- Info Box -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 25px 0; background-color: #f0f4ff; border-radius: 8px; border-left: 4px solid #667eea;">
                                                        <tr>
                                                            <td style="padding: 20px;">
                                                                <p style="margin: 0; color: #555555; font-size: 14px; line-height: 1.6;">
                                                                    <strong>✓ Com a administrador, podràs:</strong><br>
                                                                    • Convidar membres al teu equip<br>
                                                                    • Gestionar usuaris i permisos<br>
                                                                    • Configurar la teva empresa<br>
                                                                    • Accedir a totes les funcionalitats
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <p style="color: #666666; font-size: 16px; line-height: 1.6; margin-top: 25px;">
                                                        Fes clic al botó per verificar el teu compte i activar la teva empresa:
                                                    </p>
                
                                                    <!-- Button -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 30px 0;">
                                                        <tr>
                                                            <td align="center">
                                                                <a href="%s" style="display: inline-block; padding: 16px 45px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);">
                                                                    Verificar i Activar Empresa
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <!-- Timer Info -->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 20px 0; background-color: #fff3cd; border-radius: 6px;">
                                                        <tr>
                                                            <td style="padding: 15px; text-align: center;">
                                                                <p style="margin: 0; color: #856404; font-size: 13px;">
                                                                    ⏱️ Aquest enllaç expirarà en <strong>24 hores</strong>
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </table>
                
                                                    <p style="color: #666666; font-size: 14px; line-height: 1.6; margin-top: 30px;">
                                                        Un cop verificat el teu compte, podràs accedir al teu panell d'administració i convidar els membres del teu equip.
                                                    </p>
                
                                                    <p style="color: #999999; font-size: 13px; line-height: 1.6; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eeeeee;">
                                                        Si no has registrat aquesta empresa a Abasta, pots ignorar aquest correu de forma segura.
                                                    </p>
                                                </td>
                                            </tr>
                
                                            <!-- Footer -->
                                            <tr>
                                                <td style="background-color: #f8f8f8; padding: 20px 30px; text-align: center; border-top: 1px solid #eeeeee;">
                                                    <p style="color: #999999; font-size: 12px; margin: 0;">
                                                        © 2025 Abasta. Tots els drets reservats.
                                                    </p>
                                                    <p style="color: #999999; font-size: 11px; margin: 10px 0 0 0;">
                                                        Si el botó no funciona, copia i enganxa aquest enllaç al teu navegador:<br>
                                                        <span style="color: #667eea;">%s</span>
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </body>
                        </html>
                \s""".formatted(userName, companyName, verificationLink, verificationLink);
    }
}