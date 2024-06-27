package com.mirai.service.whatsApp.impl;

import com.mirai.service.whatsApp.WhatsAppService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
@Slf4j
public class WhatsAppServiceImpl implements WhatsAppService {

    private Environment env;

    public void sendWhatsAppMessage(
            String whatsappNumber, String templateName, String broadcastName, String paramName, String paramValue) {
        RestTemplate restTemplate = new RestTemplate();

        String url =
                "https://live-mt-server.wati.io/315238/api/v1/sendTemplateMessage?whatsappNumber=" + whatsappNumber;

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("template_name", templateName);
        requestBody.put("broadcast_name", broadcastName);

        List<Map<String, String>> parameters = new ArrayList<>();

        Map<String, String> parameter = new HashMap<>();
        parameter.put("name", paramName);
        parameter.put("value", paramValue);
        parameters.add(parameter);
        requestBody.put("parameters", parameters);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = env.getProperty("wpToken");
        headers.set("Authorization", "Bearer " + token);

        // Create request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        System.out.println("Request URL: " + url);
        System.out.println("Request Body: " + requestBody);
        // Send POST request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println("Response Status Code: " + responseEntity.getStatusCode());
        System.out.println("Response Body: " + responseEntity.getBody());

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();

        } else {
            // Handle error
        }
    }

    public void sendQrWhatsAppMessage(String whatsappNumber, String userName, String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();

        String url =
                "https://live-mt-server.wati.io/315238/api/v1/sendTemplateMessage?whatsappNumber=" + whatsappNumber;

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("template_name", "confirmuser");
        requestBody.put("broadcast_name", "confirm_user");

        List<Map<String, String>> parameters = new ArrayList<>();

        Map<String, String> nameParameter = new HashMap<>();
        nameParameter.put("name", "name");
        nameParameter.put("value", userName);
        parameters.add(nameParameter);

        Map<String, String> productImageParameter = new HashMap<>();
        productImageParameter.put("name", "product_image_url");
        productImageParameter.put("value", imageUrl);
        parameters.add(productImageParameter);

        requestBody.put("parameters", parameters);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = env.getProperty("wpToken");
        headers.set("Authorization", "Bearer " + token);

        // Create request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        System.out.println("Request URL: " + url);
        System.out.println("Request Body: " + requestBody);
        // Send POST request
        try {
            // Send POST request
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            System.out.println("Response Status Code: " + responseEntity.getStatusCode());
            System.out.println("Response Body: " + responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                System.out.println("Message sent successfully: " + responseBody);
            } else {
                System.err.println("Failed to send message. HTTP Status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while sending WhatsApp message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendReminder(String whatsappNumber, String userName, String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();

        String url =
                "https://live-mt-server.wati.io/315238/api/v1/sendTemplateMessage?whatsappNumber=" + whatsappNumber;

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("template_name", "mirai_reminder");
        requestBody.put("broadcast_name", "mirai_reminder");

        List<Map<String, String>> parameters = new ArrayList<>();

        Map<String, String> nameParameter = new HashMap<>();
        nameParameter.put("name", "name");
        nameParameter.put("value", userName);
        parameters.add(nameParameter);

        Map<String, String> productImageParameter = new HashMap<>();
        productImageParameter.put("name", "qr_image_url");
        productImageParameter.put("value", imageUrl);
        parameters.add(productImageParameter);

        requestBody.put("parameters", parameters);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = env.getProperty("wpToken");
        headers.set("Authorization", "Bearer " + token);

        // Create request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        System.out.println("Request URL: " + url);
        System.out.println("Request Body: " + requestBody);
        // Send POST request
        try {
            // Send POST request
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            System.out.println("Response Status Code: " + responseEntity.getStatusCode());
            System.out.println("Response Body: " + responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                System.out.println("Message sent successfully: " + responseBody);
            } else {
                System.err.println("Failed to send message. HTTP Status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while sending WhatsApp message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
