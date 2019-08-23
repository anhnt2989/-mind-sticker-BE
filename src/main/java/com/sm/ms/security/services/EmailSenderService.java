package com.sm.ms.security.services;

import com.sm.ms.model.ConfirmationToken;
import com.sm.ms.model.User;
import com.sm.ms.repository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class EmailSenderService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    private JavaMailSender javaMailSender;
//
//    @Autowired
//    public EmailSenderService(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public void sendEmailCreateUser(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("anhnt2989@gmail");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/api/auth/confirm-account?token=" + confirmationToken.getConfirmationToken());

        sendEmail(mailMessage);
    }

    public void sendEmailForgotPassword(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Password Reset!");
        mailMessage.setFrom("anhnt2989@gmail");
        mailMessage.setText("To complete the password reset process, please click here : "
                + "http://localhost:8080/api/auth/confirm-reset?token=" + confirmationToken.getConfirmationToken());

        sendEmail(mailMessage);
    }
}