package com.airbus.retex.business.dto.media;

import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto implements Dto {
    private String filename;
    private UUID uuid;
    private Boolean isFromThingworx;

    public MediaDto(String filename) {
        this.filename = filename;
        this.isFromThingworx = false;
    }

    public MediaDto(String filename, UUID uuid) {
        this.filename = filename;
        this.uuid = uuid;
        this.isFromThingworx = false;
    }
}
