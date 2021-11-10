package com.airbus.retex.business.dto.media;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MediaDtoList {
    private Long entityId;
    private List<MediaDto> medias;
    private List<MediaDto> mediasThumbnails;
}
