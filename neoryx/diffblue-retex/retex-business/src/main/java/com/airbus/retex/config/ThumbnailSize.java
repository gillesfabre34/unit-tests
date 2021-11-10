package com.airbus.retex.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ThumbnailSize {

    private int width;
    private int height;

    /**
     * @param width
     * @param height
     */
    public ThumbnailSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * @return
     */
    public String getName() {
        return String.valueOf(width) + "-" + height;
    }
}
