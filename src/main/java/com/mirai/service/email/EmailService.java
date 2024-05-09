package com.mirai.service.email;

import com.mirai.data.entities.Users;

public interface EmailService {

    void sentMessageToEmail(Users users, String toMail, String toCC);

    void sentMessageToAdmin(Users users, String toMail, String toCC);

    void sendEmailWithQRCode(Users users, String subject, String text, byte[] qrCodeImage);
}
