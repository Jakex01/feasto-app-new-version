package org.basket.repository;

import org.basket.model.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, String> {

    Optional<Cart> findByUserEmail(String userEmail);
}
