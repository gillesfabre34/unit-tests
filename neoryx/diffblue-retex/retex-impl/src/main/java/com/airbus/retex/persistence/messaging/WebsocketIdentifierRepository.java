package com.airbus.retex.persistence.messaging;

import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebsocketIdentifierRepository extends JpaRepository<WebsocketIdentifier, Long>, CustomJpaRepository<WebsocketIdentifier> {

    Optional<WebsocketIdentifier> findByUserId(Long userId);

}