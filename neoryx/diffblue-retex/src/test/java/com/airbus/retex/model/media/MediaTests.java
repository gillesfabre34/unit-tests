package com.airbus.retex.model.media;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MediaTests {

    @Test
    public void testFilenameMustHaveExtension() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Media(null, "filename_without_extension");
        });
    }
}
