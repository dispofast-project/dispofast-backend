package com.dispocol.dispofast.shared.MailService.application.impl;

import org.springframework.stereotype.Service;

import com.dispocol.dispofast.shared.MailService.application.interfaces.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private String from; 

    @Override
    public void send(String to, String subject, String body) {
        //TODO: Implementar el envío de correo utilizando JavaMailSender o cualquier otra biblioteca de correo electrónico.
    }

    @Override
    public void sendWithAttchment(
        String to,
        String subject,
        String body,
        byte[] attachment,
        String attachmentName,
        String attachmentType
    ) {
       //TODO: Implementar el envío de correo con adjunto utilizando JavaMailSender o cualquier otra biblioteca de correo electrónico.
    }

}