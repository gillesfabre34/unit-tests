package com.airbus.retex.config;

import com.airbus.retex.AbstractServiceIT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JacksonConfigurationIT extends AbstractServiceIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void localDateSerialize() throws JsonProcessingException {
        LocalDate localDate = LocalDate.of(2011, 3, 5);

        String serializedLocalDate = objectMapper.writeValueAsString(localDate);
        assertThat(serializedLocalDate, equalTo("\"2011-03-05\""));
    }

    @Test
    public void localDateDeserialize() throws IOException {
        String serializedLocalDate = "\"2011-03-05\"";

        LocalDate localDate = objectMapper.readValue(serializedLocalDate, LocalDate.class);
        assertThat(localDate, equalTo(LocalDate.of(2011, 3, 5)));
    }
}
