package org.restaurant.validators;

import lombok.RequiredArgsConstructor;
import org.restaurant.request.PostMenuItemRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuItemValidator {
    private final ObjectsValidator<PostMenuItemRequest> validator;
    public void validateRequest(PostMenuItemRequest request) {
        validator.validate(request);
    }
}
