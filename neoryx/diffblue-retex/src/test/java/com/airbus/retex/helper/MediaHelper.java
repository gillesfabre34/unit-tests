package com.airbus.retex.helper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

import java.io.IOException;

public class MediaHelper {


    public static InputStreamSource loadStream() throws IOException {
        return new ClassPathResource("/media/h160.jpg");
    }

}
