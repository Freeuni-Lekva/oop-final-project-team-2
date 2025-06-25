package com.moviemood.services;


/**
 * This is test class for EmailService (Maybe temporary).
 */
public class TestEmailService {
    public static void main(String[] args) {
        EmailService emailService = new EmailService();

        int testCode = emailService.generateVerificationCode();
        System.out.println("Generated code: " + testCode);

        boolean success = emailService.sendVerificationEmail(
                "giorgi@giorgi.com",
                testCode,
                "giorgiTestUser"
        );

        if (success) {
            System.out.println("Email sent successfully!");
        } else {
            System.out.println("Email failed to send.");
        }
    }
}
