package com.GraphicalPasswordAuth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications, specifically for password reset functionality.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a password reset email to the specified user email address.
     *
     * @param toEmail   the recipient's email address
     * @param resetLink the password reset link to include in the email
     */
    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset - Graphical Auth App");
        message.setText("Click the link below to reset your password:\n\n" + resetLink + "\n\nThis link expires in 30 minutes.");
        message.setFrom("abc@gmail.com");  // Tip: consider externalizing this in application.properties
        mailSender.send(message);
    }
}
