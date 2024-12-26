package org.restaurant.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationValidator {
    private final ObjectsValidator<Object> objectsValidator;

    public <T> void validateRequest(T request) {
        objectsValidator.validate(request);
    }
}