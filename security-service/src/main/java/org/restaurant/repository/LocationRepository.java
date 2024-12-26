package org.restaurant.repository;

import org.restaurant.model.LocationEntity;
import org.restaurant.model.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<LocationEntity> findAllByUserCredentialEntity(UserCredentialEntity userCredential);
    Optional<LocationEntity> findByCurrentAndUserCredentialEntity(boolean current, UserCredentialEntity userCredential);
}
