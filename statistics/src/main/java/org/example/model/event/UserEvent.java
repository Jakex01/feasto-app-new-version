package org.example.model.event;

public record UserEvent(
        String email,
        Long userId
) {
}
