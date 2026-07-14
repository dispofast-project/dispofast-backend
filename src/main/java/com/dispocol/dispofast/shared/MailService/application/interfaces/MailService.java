package com.dispocol.dispofast.shared.MailService.application.interfaces;

import java.util.List;

public interface MailService {

  void send(String[] to, String subject, String body);

  void sendWithAttchment(
      String[] to,
      String subject,
      String body,
      byte[] attachment,
      String attachmentName,
      String attachmentType);

  void sendWithAttachments(
      String[] to, String subject, String body, List<MailAttachment> attachments);
}
