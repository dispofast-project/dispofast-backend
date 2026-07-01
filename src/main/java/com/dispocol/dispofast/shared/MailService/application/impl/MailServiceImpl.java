package com.dispocol.dispofast.shared.MailService.application.impl;

import com.dispocol.dispofast.shared.MailService.application.interfaces.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender javaMailSender;

  @Value("${app.mail.from}")
  private String from;

  @Override
  public void send(String[] to, String subject, String body) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);

      javaMailSender.send(message);
      log.info("Correo electrónico enviado a: {}", (Object) to);
    } catch (Exception e) {
      log.error("Error al enviar el correo electrónico a: {}", (Object) to, e);
      throw new RuntimeException("Error al enviar el correo electrónico", e);
    }
  }

  @Override
  public void sendWithAttchment(
      String[] to,
      String subject,
      String body,
      byte[] attachment,
      String attachmentName,
      String attachmentType) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);

      if (attachment != null && attachment.length > 0) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(attachment);

        if (attachmentType != null && !attachmentType.isBlank()) {
          helper.addAttachment(attachmentName, byteArrayResource, attachmentType);
        } else {
          helper.addAttachment(attachmentName, byteArrayResource);
        }
      }

      javaMailSender.send(message);
      log.info("Correo con adjunto [{}] enviado exitosamente a: {}", attachmentName, (Object) to);
    } catch (Exception e) {
      log.error("Error al enviar el correo electrónico con adjunto a: {}", to, e);
      throw new RuntimeException("Error al enviar el correo electrónico con adjunto", e);
    }
  }
}
