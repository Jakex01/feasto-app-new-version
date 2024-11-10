package org.restaurant.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class LocationNotFoundException extends RuntimeException{
    private final String errorMessages;
}
