package org.restaurant.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class MessageNotification {

    private String email;
    private String pdfContent;
}
