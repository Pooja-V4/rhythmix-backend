package com.musicapp.musicapp.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${gmail.client.id}")
    private String clientId;

    @Value("${gmail.client.secret}")
    private String clientSecret;

    @Value("${gmail.refresh.token}")
    private String refreshToken;

    @Value("${gmail.from.email}")
    private String fromEmail;

    @Value("${app.base.url}")
    private String baseUrl;

    // Build Gmail service using OAuth2
    private Gmail buildGmailService() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // Build credential using refresh token
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);

        // Refresh access token automatically
        credential.refreshToken();

        return new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Rhythmix")
                .build();
    }

    // Create MimeMessage and convert to Gmail Message
    private Message createMessage(String to, String subject, String htmlBody)
            throws MessagingException, Exception {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmail, "Rhythmix"));
        email.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setContent(htmlBody, "text/html; charset=utf-8");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes);

        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    // Core send method
    private void sendEmail(String to, String subject, String html) {
        try {
            System.out.println("📧 Sending email to: " + to);
            Gmail service = buildGmailService();
            Message message = createMessage(to, subject, html);
            service.users().messages().send("me", message).execute();
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Gmail API error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Verification email
    public void sendVerificationEmail(String toEmail, String name, String token) {
        String verifyLink = baseUrl + "/verify-email?token=" + token;

        System.out.println("🔗 Verify link: " + verifyLink);

        String html = "<!DOCTYPE html><html><head><style>"
                + "body{font-family:Arial,sans-serif;background:#f5f5f5;margin:0}"
                + ".wrap{max-width:520px;margin:40px auto;background:#1a1a2e;border-radius:16px;overflow:hidden}"
                + ".top{padding:32px 40px 16px;text-align:center}"
                + ".logo{font-size:26px;font-weight:bold;color:#1DB954}"
                + ".body{padding:24px 40px}"
                + "h2{color:#fff;font-size:22px;margin:0 0 12px}"
                + "p{color:#aaa;font-size:15px;line-height:1.6;margin:0 0 16px}"
                + ".btn{display:inline-block;background:#1DB954;color:#000 !important;"
                + "font-weight:bold;font-size:16px;padding:14px 36px;border-radius:50px;text-decoration:none}"
                + ".lnk{color:#1DB954;word-break:break-all;font-size:13px}"
                + ".foot{padding:16px 40px 28px;text-align:center}"
                + ".foot p{color:#555;font-size:12px;margin:0}"
                + "</style></head><body>"
                + "<div class='wrap'>"
                + "<div class='top'><div class='logo'>🎵 Rhythmix</div></div>"
                + "<div class='body'>"
                + "<h2>Welcome, " + name + "!</h2>"
                + "<p>Thanks for signing up! Click the button below to verify your email and start listening.</p>"
                + "<div style='text-align:center;margin:24px 0'>"
                + "<a href='" + verifyLink + "' class='btn'>Verify Email Address</a>"
                + "</div>"
                + "<p>This link expires in <strong style='color:#fff'>24 hours</strong>.</p>"
                + "<p>If the button doesn't work, paste this link in your browser:</p>"
                + "<p><a href='" + verifyLink + "' class='lnk'>" + verifyLink + "</a></p>"
                + "<p style='color:#666;font-size:13px'>Didn't sign up? You can safely ignore this email.</p>"
                + "</div>"
                + "<div class='foot'><p>© 2026 Rhythmix · Your personal music experience</p></div>"
                + "</div></body></html>";

        sendEmail(toEmail, "Verify your Rhythmix account", html);
    }

    // Password reset email
    public void sendPasswordResetEmail(String toEmail, String name, String token) {
        String resetLink = baseUrl + "/reset-password?token=" + token;

        System.out.println("🔗 Reset link: " + resetLink);

        String html = "<!DOCTYPE html><html><head><style>"
                + "body{font-family:Arial,sans-serif;background:#f5f5f5;margin:0}"
                + ".wrap{max-width:520px;margin:40px auto;background:#1a1a2e;border-radius:16px;overflow:hidden}"
                + ".top{padding:32px 40px 16px;text-align:center}"
                + ".logo{font-size:26px;font-weight:bold;color:#1DB954}"
                + ".body{padding:24px 40px}"
                + "h2{color:#fff;font-size:20px;margin:0 0 12px}"
                + "p{color:#aaa;font-size:15px;line-height:1.6;margin:0 0 16px}"
                + ".btn{display:inline-block;background:#1DB954;color:#000 !important;"
                + "font-weight:bold;font-size:16px;padding:14px 36px;border-radius:50px;text-decoration:none}"
                + ".lnk{color:#1DB954;word-break:break-all;font-size:13px}"
                + ".warn{background:#2a1a1a;border-left:3px solid #e94560;padding:12px 16px;border-radius:4px}"
                + ".warn p{color:#e94560;font-size:13px;margin:0}"
                + ".foot{padding:16px 40px 28px;text-align:center}"
                + ".foot p{color:#555;font-size:12px;margin:0}"
                + "</style></head><body>"
                + "<div class='wrap'>"
                + "<div class='top'><div class='logo'>🎵 Rhythmix</div></div>"
                + "<div class='body'>"
                + "<h2>Reset your password</h2>"
                + "<p>Hi " + name + ", we received a request to reset your Rhythmix password.</p>"
                + "<div style='text-align:center;margin:24px 0'>"
                + "<a href='" + resetLink + "' class='btn'>Reset Password</a>"
                + "</div>"
                + "<p>This link expires in <strong style='color:#fff'>1 hour</strong>.</p>"
                + "<p>Paste this link if the button doesn't work:</p>"
                + "<p><a href='" + resetLink + "' class='lnk'>" + resetLink + "</a></p>"
                + "<div class='warn'><p>Didn't request this? Ignore this email safely.</p></div>"
                + "</div>"
                + "<div class='foot'><p>© 2026 Rhythmix</p></div>"
                + "</div></body></html>";

        sendEmail(toEmail, "Reset your Rhythmix password 🔑", html);
    }

    // Password changed notification
    public void sendPasswordChangedEmail(String toEmail, String name) {
        String html = "<!DOCTYPE html><html><body style='font-family:Arial;background:#f5f5f5;margin:0'>"
                + "<div style='max-width:520px;margin:40px auto;background:#1a1a2e;border-radius:16px;padding:32px 40px'>"
                + "<div style='font-size:24px;font-weight:bold;color:#1DB954;margin-bottom:20px'>🎵 Rhythmix</div>"
                + "<h2 style='color:#fff;margin:0 0 12px'>Password Changed</h2>"
                + "<p style='color:#aaa;font-size:15px'>Hi " + name + ",</p>"
                + "<p style='color:#aaa;font-size:15px'>Your Rhythmix password was successfully changed.</p>"
                + "<p style='color:#aaa;font-size:15px'>If you didn't make this change, contact support immediately.</p>"
                + "<p style='color:#555;font-size:12px;margin-top:24px'>© 2026 Rhythmix</p>"
                + "</div></body></html>";

        sendEmail(toEmail, "Your Rhythmix password was changed", html);
    }
}