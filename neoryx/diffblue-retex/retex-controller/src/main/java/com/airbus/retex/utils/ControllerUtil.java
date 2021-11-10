package com.airbus.retex.utils;

import java.util.Collections;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.airbus.retex.exception.MessagesDto;

/**
 * 
 * @deprecated
 *
 */
@Component
@Deprecated(forRemoval = true)
public class ControllerUtil {
    @Autowired
    private MessageSource messageSource;

    public ResponseEntity exceptionNotFound(Locale locale, String code) {
        MessagesDto messagesDto = new MessagesDto();
        messagesDto.setMessages(Collections.singleton(messageSource.getMessage(
                code,
                null,
                locale)));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messagesDto);
    }
}
