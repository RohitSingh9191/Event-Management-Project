package com.mirai.service.email.Impl;

import com.mirai.data.entities.Users;
import com.mirai.service.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
                + "We have received your registration for MIRAI™, the exclusive CXO conclave focused on transforming businesses using Artificial Intelligence.\n\n"
                + "As this is an exclusive invite only event, our team is carefully reviewing your profile and will soon confirm your participation via email.\n\n"
                + "In the meanwhile, if you have any questions or require further assistance, please feel free to reach out to us at +91 "
                + number
                + ".\n"
                + "Looking forward to seeing you!\n\n"
                + "Best regards,\n"
                + "Team MIRAI";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("Registration Confirmation for Mirai Events");
        simpleMailMessage.setFrom(emailUsername);
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setCc(toCC);
        simpleMailMessage.setText(sendMessage);
        try {
               javaMailSender.send(simpleMailMessage);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
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
        try {
              javaMailSender.send(simpleMailMessage);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendEmailWithQRCode(Users users, String subject, String text, byte[] qrCodeImage) {
        String number = env.getProperty("phoneNumber");
        String fromUser = env.getProperty("spring.mail.username");

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(users.getEmail());
            helper.setSubject(subject);
            helper.setFrom(fromUser);

            // Constructing the email content
            String content = "Hi " + users.getName() + ",<br><br>"
                    + "We have reviewed and confirmed your participation for MIRAI™, the exclusive CXO conclave focused on transforming businesses using Artificial Intelligence.<br><br>"
                    + "We're thrilled to have you join us for this exciting event. Get ready for an enriching experience filled with learning, networking, and fun! Please find below the event details for your reference:<br><br>"
                    + "<b>·</b> Date: 5th July 2024</span><br>"
                    + "<b>·</b> Time: 10 am – 6 pm<br>"
                    + "<b>·</b> Venue: The Lalit, Central Delhi<br>"
                    + "<b>·</b> Event Topic: Unlocking Tomorrow - Transforming Businesses using Generative AI, ML and Deep Learning<br>"
                    + "<b>·</b> Included: Tea, Lunch & Open networking opportunities<br>"
                    + "<b>·</b> Event website: <a href=\"https://mirai.events/\">https://mirai.events/</a><br>"
                    + "<b>·</b> Reporting time: 09:30 am<br><br>"
                    + "At the registration desk you will need to show the QR code attached to this email.<br><br>"
                    + "If you have any questions or require further assistance, please feel free to reach out to us at +91 "
                    + number + ".<br><br>"
                    + "See you there!<br><br>";

            // HTML content with embedded image
            String htmlContent = "<html><body><p>" + content + "</p><img src=\"cid:qrCode\"></body></html>";
            helper.setText(htmlContent, true);

            // Add the QR code as an inline resource
            ByteArrayResource qrCodeResource = new ByteArrayResource(qrCodeImage);
            helper.addInline("qrCode", qrCodeResource, "image/png");

             javaMailSender.send(message);
            System.out.println("Email with QR code sent successfully.");
        } catch (MessagingException e) {
            System.err.println("Failed to send email with QR code: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRejectionEmail(Users users) {
        String emailUsername = env.getProperty("spring.mail.username");
        String toMail = users.getEmail();
        String toCC = env.getProperty("toCCAdmin");
        String sendMessage = "Hi " + users.getName() + ",\n\n"
                + "Thank you for showing your interest in attending MIRAI™, the exclusive CXO conclave focused on transforming businesses using Artificial Intelligence.\n\n"
                + "We sincerely appreciate your effort to register. However, we regret to inform you that we are unable to proceed with your registration at this time. As this is an exclusive CXO event, your designation at this time does not meet our criteria.\n\n"
                + "We genuinely value your association and hope for future opportunities to collaborate.\n\n"
                + "Best regards,\n"
                + "Team MIRAI";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("Registration Status Update");
        simpleMailMessage.setFrom(emailUsername);
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setCc(toCC);
        simpleMailMessage.setText(sendMessage);
        try {
             javaMailSender.send(simpleMailMessage);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendReminderMail(Users users, byte[] qrCodeImage) {
        String fromUser = env.getProperty("spring.mail.username");
        LocalDate targetDate = LocalDate.of(2024, 7, 5);
        LocalDate currentDate = LocalDate.now();
        long day = ChronoUnit.DAYS.between(currentDate, targetDate);
        String subject = "Only " + day + " days to go!";
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(users.getEmail());
            helper.setSubject(subject);
            helper.setFrom(fromUser);

            String content = "Dear " + users.getName() + ",<br><br>"
                    + "We are excited to host you at the upcoming MIRAI™24, please find details below:<br>"
                    + "<b>·</b> Date: 5th July 2024</span><br>"
                    + "<b>·</b> Time: 10 am – 6 pm<br>"
                    + "<b>·</b> Venue: The Lalit, Central Delhi (<a href=\"https://maps.app.goo.gl/dechhB2Z8TQejjbx9\">https://maps.app.goo.gl/dechhB2Z8TQejjbx9</a>)<br>"
                    + "<b>·</b> Reporting time: 09:30 am<br>"
                    + "<b>·</b> Entry strictly with pre-registration only<br><br>"
                    + "Show your QR code at the registration desk!<br><br>"
                    + "Best regards,<br>"
                    + "Team MIRAI™24<br><br>";

            // HTML content with embedded image
            String htmlContent = "<html><body><p>" + content + "</p><img src=\"cid:qrCode\"></body></html>";
            helper.setText(htmlContent, true);

            // Add the QR code as an inline resource
            ByteArrayResource qrCodeResource = new ByteArrayResource(qrCodeImage);
            helper.addInline("qrCode", qrCodeResource, "image/png");

             javaMailSender.send(message);
            System.out.println("Email with QR code sent successfully.");
        } catch (MessagingException e) {
            System.err.println("Failed to send email with QR code: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
