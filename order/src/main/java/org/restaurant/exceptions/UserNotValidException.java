package org.restaurant.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class UserNotValidException extends RuntimeException{

    private final String errorMessages;
}