package org.basket.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class CartItemNotFoundException extends RuntimeException{
    private final String errorMessages;
}
