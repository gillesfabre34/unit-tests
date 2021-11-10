package com.airbus.retex.persistence.admin;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.airbus.retex.model.media.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    @Override
    Optional<Media> findById(UUID uuid);
}
