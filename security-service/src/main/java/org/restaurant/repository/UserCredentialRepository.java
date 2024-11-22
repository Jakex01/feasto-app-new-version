package org.restaurant.repository;

import org.restaurant.model.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredentialEntity, Long> {
    Optional<UserCredentialEntity> findByEmail(String email);
    @Query("SELECT u.email FROM UserCredentialEntity u WHERE u.id = :userId")
    Optional<String> findEmailByUserId(@Param("userId") Long userId);
}
