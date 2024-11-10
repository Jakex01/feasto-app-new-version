package org.restaurant.repository;

import org.restaurant.model.LocationEntity;
import org.restaurant.model.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<LocationEntity> findAllByUserCredentialEntity(UserCredentialEntity userCredential);
    LocationEntity findByCurrentAndUserCredentialEntity(boolean current, UserCredentialEntity userCredential);
}
