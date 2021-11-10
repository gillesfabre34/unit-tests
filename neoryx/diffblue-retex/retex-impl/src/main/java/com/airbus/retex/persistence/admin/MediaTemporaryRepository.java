package com.airbus.retex.persistence.admin;

import com.airbus.retex.model.media.MediaTmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaTemporaryRepository extends JpaRepository<MediaTmp, UUID> {

    /**
     * Select old temporary media
     *
     * @return
     */
    @Query(value = "SELECT m FROM MediaTmp m WHERE created_at < :dateLimit")
    Optional<List<MediaTmp>> findTemporaryMedia(@Param("dateLimit") LocalDateTime dateLimit);

    Optional<MediaTmp> findByUuid(UUID uuid);
}
