package com.airbus.retex.business.dto.drtPicture;

import com.airbus.retex.business.dto.media.MediaDto;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DrtPicturesDto {

    private Long id;
    private boolean activated;
    private Set<MediaDto> images = new HashSet<>();
}
