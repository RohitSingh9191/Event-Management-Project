package com.mirai.service.whatsApp;

public interface WhatsAppService {
    void sendWhatsAppMessage(
            String whatsappNumber, String templateName, String broadcastName, String paramName, String paramValue);

    void sendQrWhatsAppMessage(String whatsappNumber, String userName, String imageUrl);

    void sendReminder(String whatsappNumber, String userName, String imageUrl);
}
