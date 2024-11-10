package org.restaurant.service;

import lombok.AllArgsConstructor;
import org.restaurant.exceptions.InvalidIdempotencyKeyException;
import org.restaurant.model.IdempotencyRecord;
import org.restaurant.repository.redis.IdempotencyRecordRepository;
import org.springframework.stereotype.Service;


import java.util.Set;

@Service
@AllArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;
    private static final String IDEMPOTENCY_KEY_EXISTS = "Idempotency-Key already used.";
    public void existsByIdempotencyKey(String idempotencyKey) {
        repository.findById(idempotencyKey).ifPresent(this::throwIdempotencyKeyExistsException);
    }
    public void saveIdempotencyKey(IdempotencyRecord idempotencyRecord) {
        repository.save(idempotencyRecord);
    }

    private void throwIdempotencyKeyExistsException(IdempotencyRecord idempotencyRecord) {
        throw new InvalidIdempotencyKeyException(Set.of(IDEMPOTENCY_KEY_EXISTS + idempotencyRecord.getId()));
    }
}
