package org.example.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class StatisticNotFoundException extends RuntimeException{
    private final Set<String> errorMessages;

}
