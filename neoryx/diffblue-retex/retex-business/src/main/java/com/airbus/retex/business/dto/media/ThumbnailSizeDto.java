package com.airbus.retex.business.dto.media;

import com.airbus.retex.business.annotation.Sized;
import lombok.Data;

@Data
@Sized()
public class ThumbnailSizeDto {
    private Integer width;
    private Integer height;
    public boolean isValid(){//TODO Make this method disappear from Swagger doc (Swagger shows a "valid" parameter)
        return width != null && height != null;
    }
}
