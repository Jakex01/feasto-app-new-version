package org.restaurant.repository;

import org.restaurant.model.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredentialEntity, Long> {
    Optional<UserCredentialEntity> findByEmail(String email);
}
