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
        String number = env.getProperty("phoneNumber");

        String sendMessage = "Hi " + users.getName() + ",\n\n"
                + "This email is to confirm that your registration for Mirai Events has been successfully processed.\n"
                + "We're thrilled to have you join us for this exciting event. Get ready for an enriching experience filled with learning, networking, and fun!\n\n"
                + "If you have any questions or require further assistance, feel free to reach out to us at +" + number
                + ".\n"
                + "Looking forward to seeing you!\n\n"
                + "Best regards,\n"
                + "Mirai Team";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("Registration Confirmation for Mirai Events");
        simpleMailMessage.setFrom(emailUsername);
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setCc(toCC);
        simpleMailMessage.setText(sendMessage);
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sentMessageToAdmin(Users users, String toMail, String toCC) {
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
