package org.restaurant.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class InvalidIdempotencyKeyException extends RuntimeException{

    private final Set<String> errorMessages;
}
