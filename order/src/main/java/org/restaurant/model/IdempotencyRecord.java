package org.restaurant.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Data
@RedisHash("Idempotency-key")
@AllArgsConstructor
public class IdempotencyRecord implements Serializable {
    @Id
    private String id;
    @TimeToLive(unit = TimeUnit.MINUTES)
    private long ttl = 10;
    @CreatedDate
    private LocalDateTime createDate;
}
