package org.restaurant.validators;

import lombok.RequiredArgsConstructor;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantValidator {
    private final ObjectsValidator<CreateRestaurantRequestDuplicate> validator;

    public void validateRequest(CreateRestaurantRequestDuplicate request) {
        validator.validate(request);
    }
}
