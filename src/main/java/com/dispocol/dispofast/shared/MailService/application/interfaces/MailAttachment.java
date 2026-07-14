package com.dispocol.dispofast.shared.MailService.application.interfaces;

public record MailAttachment(byte[] content, String name, String contentType) {}
