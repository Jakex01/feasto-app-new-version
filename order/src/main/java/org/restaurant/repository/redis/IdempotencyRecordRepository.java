package org.restaurant.repository.redis;

import org.restaurant.model.IdempotencyRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyRecordRepository extends CrudRepository<IdempotencyRecord, String> {
}
