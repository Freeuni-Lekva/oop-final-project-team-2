package com.moviemood.services;


import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;
import java.security.SecureRandom;


/**
 * EmailService is a service class (business logic).
 * It handles specific business operations (Sending email and Generating 6-digit random code).
 * It has 2 methods: generateVerificationCode() and sendVerificationEmail(email, code, username)
 * */
public class EmailService {


    public int generateVerificationCode() {
        SecureRandom sr = new SecureRandom();
        return 100000 + sr.nextInt(900000);
    }

    // This method sends email. For now, it uses MailTrap for testing.
    public boolean sendVerificationEmail(String email, String code, String username) {
        // Credentials (MailTrap)
        String host = "sandbox.smtp.mailtrap.io";
        int port = 2525;
        final String usernameSMTP = "0419a9ae82b7c2";
        final String passwordSMTP = "fe1da86c024c6e";

        // Email
        String fromEmail = "sender@sender.com";
        String toEmail = email;
        String subject = "Test email";
        String body = "Hello" +  username + ". Code: " + code;

        // Properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        //Session
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
