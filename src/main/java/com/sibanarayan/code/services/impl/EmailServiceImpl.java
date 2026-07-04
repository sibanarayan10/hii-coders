package com.sibanarayan.code.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // Linking email (manual signup conflict)
    public void sendLinkingEmail(String toEmail, String name, String token) {
        sendHtmlEmail(
                toEmail,
                "Link Your Account",
                buildLinkingEmailTemplate(name, token)
        );
    }

    // Standard password reset
    public void sendPasswordResetEmail(String toEmail, String name, String token) {
        sendHtmlEmail(
                toEmail,
                "Reset Your Password",
                buildPasswordResetTemplate(name, token)
        );
    }

    // OAuth user setting password for first time

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = isHtml

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


    private String buildLinkingEmailTemplate(String name, String token) {
        String link = backendUrl + "/api/v1/user/auth/link-account?token=" + token;

        return """
        <!DOCTYPE html>
        <html>
        <body style="margin:0; padding:0; background-color:#f4f4f4; font-family: Arial, sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="center" style="padding: 40px 0;">
                <table width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff; border-radius:8px;
                              box-shadow: 0 2px 8px rgba(0,0,0,0.1);">

                  <!-- Header -->
                  <tr>
                    <td style="background:#4F46E5; padding:30px;
                                border-radius:8px 8px 0 0; text-align:center;">
                      <h1 style="color:#ffffff; margin:0; font-size:24px;">
                        Link Your Account
                      </h1>
                    </td>
                  </tr>

                  <!-- Body -->
                  <tr>
                    <td style="padding: 40px 30px;">
                      <p style="color:#333; font-size:16px;">Hi %s,</p>
                      <p style="color:#555; font-size:15px; line-height:1.6;">
                        We noticed you tried to sign up manually with this email.
                        An account already exists via Google.
                      </p>
                      <p style="color:#555; font-size:15px; line-height:1.6;">
                        Click the button below to add password login to your account.
                      </p>

                      <!-- Button -->
                      <table cellpadding="0" cellspacing="0" style="margin: 30px auto;">
                        <tr>
                          <td align="center" style="background:#4F46E5;
                                          border-radius:6px; padding:14px 32px;">
                            <a href="%s"
                               style="color:#ffffff; text-decoration:none;
                                      font-size:16px; font-weight:bold;">
                              ✅ Verify & Link Account
                            </a>
                          </td>
                        </tr>
                      </table>

                      <p style="color:#999; font-size:13px; text-align:center;">
                        This link expires in 24 hours.
                        If you didn't request this, ignore this email.
                      </p>
                    </td>
                  </tr>

                  <!-- Footer -->
                  <tr>
                    <td style="background:#f9f9f9; padding:20px;
                                border-radius:0 0 8px 8px; text-align:center;">
                      <p style="color:#aaa; font-size:12px; margin:0;">
                        © 2025 YourApp. All rights reserved.
                      </p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(name, link);
    }
    private String buildPasswordResetTemplate(String name, String token) {
        String link = frontendUrl + "/auth/reset-password?token=" + token;

        return """
        <!DOCTYPE html>
        <html>
        <body style="margin:0; padding:0; background-color:#f4f4f4; font-family: Arial, sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="center" style="padding: 40px 0;">
                <table width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff; border-radius:8px;
                              box-shadow: 0 2px 8px rgba(0,0,0,0.1);">

                  <!-- Header -->
                  <tr>
                    <td style="background:#0EA5E9; padding:30px;
                                border-radius:8px 8px 0 0; text-align:center;">
                      <h1 style="color:#ffffff; margin:0; font-size:24px;">
                        Reset Your Password
                      </h1>
                    </td>
                  </tr>

                  <!-- Body -->
                  <tr>
                    <td style="padding: 40px 30px;">
                      <p style="color:#333; font-size:16px;">Hi %s,</p>
                      <p style="color:#555; font-size:15px; line-height:1.6;">
                        We received a request to reset your password.
                        Click the button below to set a new one.
                      </p>

                      <!-- Button -->
                      <table cellpadding="0" cellspacing="0" style="margin: 30px auto;">
                        <tr>
                          <td align="center" style="background:#0EA5E9;
                                          border-radius:6px; padding:14px 32px;">
                            <a href="%s"
                               style="color:#ffffff; text-decoration:none;
                                      font-size:16px; font-weight:bold;">
                              🔐 Reset Password
                            </a>
                          </td>
                        </tr>
                      </table>

                      <p style="color:#999; font-size:13px; text-align:center;">
                        This link expires in 1 hour.
                        If you didn't request this, ignore this email.
                      </p>
                    </td>
                  </tr>

                  <!-- Footer -->
                  <tr>
                    <td style="background:#f9f9f9; padding:20px;
                                border-radius:0 0 8px 8px; text-align:center;">
                      <p style="color:#aaa; font-size:12px; margin:0;">
                        © 2025 YourApp. All rights reserved.
                      </p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(name, link);
    }
}
