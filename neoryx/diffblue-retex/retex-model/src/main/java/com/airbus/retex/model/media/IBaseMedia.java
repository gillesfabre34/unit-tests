package com.airbus.retex.model.media;

import com.airbus.retex.model.basic.IIdentifiedModel;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IBaseMedia extends IIdentifiedModel<UUID> {
    LocalDateTime getCreatedAt();
    UUID getUuid();
    String getFilename();
    Boolean getIsFromThingworx();
}
