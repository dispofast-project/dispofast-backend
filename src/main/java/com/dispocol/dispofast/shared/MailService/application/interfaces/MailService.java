package com.dispocol.dispofast.shared.MailService.application.interfaces;

public interface MailService {
    
    void send(String to, String subject, String body);

    void sendWithAttchment(
        String to,
        String subject,
        String body,
        byte[] attachment,
        String attachmentName,
        String attachmentType
    );
}
