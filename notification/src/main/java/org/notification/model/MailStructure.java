package org.notification.model;

import lombok.Data;

@Data
public class MailStructure {
private String subject;
private String message;

private byte[] pdfContent;
}
