package com.mirai.service.email.Impl;

import com.mirai.data.entities.Users;
import com.mirai.service.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final Environment env;

    @Override
    public void sentMessageToEmail(Users users, String toMail, String toCC) {
        String emailUsername = env.getProperty("spring.mail.username");

        String sendMessage = "Hello !  A new User has registered:\n"
                + "Name: " + users.getName() + "\n"
                + "Email: " + users.getEmail() + "\n"
                + "Phone: " + users.getPhone() + "\n"
                + "Company: " + users.getCompany() + "\n"
                + "Designation: " + users.getDesignation() + "\n"
                + "Linkedin Profile: " + users.getLinkedInProfile() + "\n"
                + "Type: " + users.getType() + "\n\n"
                + "Please keep this information confidential and do not share it with anyone.";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("Mirai | Registration");
        simpleMailMessage.setFrom(emailUsername);
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setCc(toCC);
        simpleMailMessage.setText(sendMessage);
        javaMailSender.send(simpleMailMessage);
    }
}
