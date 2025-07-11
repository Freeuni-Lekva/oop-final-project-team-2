package com.moviemood.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.security.SecureRandom;

public class EmailService {

    public int generateVerificationCode() {
        SecureRandom sr = new SecureRandom();
        return 100000 + sr.nextInt(900000);
    }

    public boolean sendVerificationEmail(String email, String code, String username) {
        if (!isValidInput(email, code, username)) {
            return false;
        }

        // Check if we are in test mode
        String testMode = System.getProperty("test.mode", "false");
        if (Boolean.parseBoolean(testMode)) {
            return true;
        }

        return sendRealEmail(email, code, username);
    }

    private boolean isValidInput(String email, String code, String username) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (!email.contains("@")) {
            return false;
        }

        if (email.contains(" ")) {
            return false;
        }

        if (email.endsWith("@")) {
            return false;
        }

        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    // Sending email
    private boolean sendRealEmail(String email, String code, String username) {

        String host = "smtp.gmail.com";
        int port = 587;
        final String usernameSMTP = "official.moviemood@gmail.com";
        final String passwordSMTP = "efmr qbuk ichb lnpy";

        String fromEmail = "official.moviemood@gmail.com";
        String toEmail = email;
        String subject = "MovieMood - Email Verification";
        String body =
                "Hello " + username + ",\n\n" +
                        "Welcome to MovieMood! Your verification code is: " + code + "\n\n" +
                        "Please enter this code to verify your email address.\n\n" +
                        "Best regards,\n" +
                        "The MovieMood Team";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usernameSMTP, passwordSMTP);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}