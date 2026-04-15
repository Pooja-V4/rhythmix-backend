package com.musicapp.musicapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base.url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String name, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verify your Rhythmix account");

            String verifyLink = baseUrl + "/verify-email?token=" + token;

            // Beautiful HTML email
            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <style>
                    body { font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; padding: 0; }
                    .container { max-width: 520px; margin: 40px auto; background: #1a1a2e; border-radius: 16px; overflow: hidden; }
                    .header { background: #1a1a2e; padding: 40px 40px 20px; text-align: center; }
                    .logo { font-size: 32px; font-weight: bold; color: #1DB954; letter-spacing: -1px; }
                    .body { padding: 32px 40px; }
                    h2 { color: #ffffff; font-size: 22px; margin: 0 0 12px; }
                    p { color: #aaaaaa; font-size: 15px; line-height: 1.6; margin: 0 0 20px; }
                    .btn { display: inline-block; background: #1DB954; color: #000000 !important;
                           font-weight: bold; font-size: 16px; padding: 14px 36px;
                           border-radius: 50px; text-decoration: none; margin: 8px 0; }
                    .footer { padding: 20px 40px 32px; text-align: center; }
                    .footer p { color: #555555; font-size: 12px; margin: 0; }
                    .link { color: #1DB954; word-break: break-all; font-size: 13px; }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <div class="logo">🎵 Rhythmix</div>
                    </div>
                    <div class="body">
                      <h2>Welcome, %s!</h2>
                      <p>Thanks for signing up. Please verify your email address to activate your Rhythmix account and start discovering music.</p>
                      <div style="text-align:center; margin: 28px 0;">
                        <a href="%s" class="btn">Verify Email Address</a>
                      </div>
                      <p>This link expires in <strong style="color:#fff">24 hours</strong>.</p>
                      <p>If the button doesn't work, paste this link in your browser:</p>
                      <p><a href="%s" class="link">%s</a></p>
                      <p>If you didn't sign up for Rhythmix, you can safely ignore this email.</p>
                    </div>
                    <div class="footer">
                      <p>© 2026 Rhythmix · Built with ❤️</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(name, verifyLink, verifyLink, verifyLink);

            helper.setText(html, true); // true = HTML
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    public void sendPasswordChangedEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your Rhythmix password was changed");

            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <style>
                    body { font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; }
                    .container { max-width: 520px; margin: 40px auto; background: #1a1a2e; border-radius: 16px; overflow: hidden; }
                    .header { padding: 40px 40px 20px; text-align: center; }
                    .logo { font-size: 28px; font-weight: bold; color: #1DB954; }
                    .body { padding: 32px 40px; }
                    h2 { color: #fff; font-size: 20px; margin: 0 0 12px; }
                    p { color: #aaa; font-size: 15px; line-height: 1.6; margin: 0 0 16px; }
                    .footer { padding: 20px 40px 32px; text-align: center; }
                    .footer p { color: #555; font-size: 12px; }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header"><div class="logo">🎵 Rhythmix</div></div>
                    <div class="body">
                      <h2>Password Changed</h2>
                      <p>Hi %s,</p>
                      <p>Your Rhythmix password was successfully changed.</p>
                      <p>If you didn't make this change, please contact support immediately.</p>
                    </div>
                    <div class="footer"><p>© 2026 Rhythmix</p></div>
                  </div>
                </body>
                </html>
                """.formatted(name);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Could not send password changed email: " + e.getMessage());
        }
    }
    public void sendPasswordResetEmail(String toEmail, String name, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Reset your Rhythmix password");

            String resetLink = baseUrl + "/reset-password?token=" + token;

            String html = """
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body { font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; }
                .container { max-width: 520px; margin: 40px auto; background: #1a1a2e; border-radius: 16px; overflow: hidden; }
                .header { padding: 40px 40px 20px; text-align: center; }
                .logo { font-size: 28px; font-weight: bold; color: #1DB954; }
                .body { padding: 32px 40px; }
                h2 { color: #fff; font-size: 22px; margin: 0 0 12px; }
                p { color: #aaa; font-size: 15px; line-height: 1.6; margin: 0 0 20px; }
                .btn { display: inline-block; background: #1DB954; color: #000 !important;
                       font-weight: bold; font-size: 16px; padding: 14px 36px;
                       border-radius: 50px; text-decoration: none; }
                .footer { padding: 20px 40px 32px; text-align: center; }
                .footer p { color: #555; font-size: 12px; margin: 0; }
                .link { color: #1DB954; word-break: break-all; font-size: 13px; }
                .warning { background: #2a1a1a; border-left: 3px solid #e94560;
                           padding: 12px 16px; border-radius: 4px; margin-top: 20px; }
                .warning p { color: #e94560; font-size: 13px; margin: 0; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header"><div class="logo">🎵 Rhythmix</div></div>
                <div class="body">
                  <h2>Reset your password</h2>
                  <p>Hi %s,</p>
                  <p>We received a request to reset your Rhythmix password. Click the button below to choose a new password.</p>
                  <div style="text-align:center; margin: 28px 0;">
                    <a href="%s" class="btn">Reset Password</a>
                  </div>
                  <p>This link expires in <strong style="color:#fff">1 hour</strong>.</p>
                  <p>If the button doesn't work, paste this link in your browser:</p>
                  <p><a href="%s" class="link">%s</a></p>
                  <div class="warning">
                    <p>If you didn't request a password reset, you can safely ignore this email. Your password will not change.</p>
                  </div>
                </div>
                <div class="footer"><p>© 2026 Rhythmix</p></div>
              </div>
            </body>
            </html>
            """.formatted(name, resetLink, resetLink, resetLink);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }
}