package com.airbus.retex.exception;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler implements AccessDeniedHandler {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        MessagesDto messages = errorMessages(accessDeniedException, request.getLocale());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, objectMapper.writeValueAsString(messages));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {AccessDeniedException.class, ForbiddenException.class})
    public MessagesDto handleForbidden(Exception exception, Locale locale) {
        MessagesDto messagesDto = new MessagesDto();
        List<String> messages = new ArrayList<>();
        messages.add(messageSource.getMessage("retex.error.access.denied",null,locale));
        messagesDto.setMessages(messages);
        return messagesDto;
    }

    //FIXME find a way to keep some default error handling while customizing response
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {FunctionalException.class, HttpMessageConversionException.class, MethodArgumentNotValidException.class, BindException.class})
    public MessagesDto handleBadRequest(Exception exception, Locale locale) {
        return errorMessages(exception, locale);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {NotFoundException.class})
    public MessagesDto handleNotFound(Exception exception, Locale locale) { return errorMessages(exception, locale);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Throwable.class})
    public MessagesDto handleInternalServerError(Throwable exception, Locale locale) {
        return errorMessages(exception, locale);
    }

    private MessagesDto errorMessages(Throwable throwable, Locale locale) {
        MessagesDto result = new MessagesDto();

        if (throwable instanceof FunctionalException) {
            log.info("Functional error", throwable);
            result.setMessages(buildFunctionnalMessages((FunctionalException) throwable, locale));
        } else if (throwable instanceof MethodArgumentNotValidException) {
            List<String> resolvedMessages = new ArrayList<>();
            for (FieldError fieldError : ((MethodArgumentNotValidException) throwable).getBindingResult().getFieldErrors()) {
                resolvedMessages.add(fieldError.getDefaultMessage());
            }
            result.setMessages(resolvedMessages);
        } else {
            log.error("Error with code " + throwable.getClass().getSimpleName() + "@" + throwable.hashCode(), throwable);
            result.setMessages(buildThrowableMessages(throwable, locale));
        }

        return result;
    }

    private Collection<String> buildFunctionnalMessages(FunctionalException exception, Locale locale) {
        if (exception.getCodeArgs().isEmpty()) {
            return buildThrowableMessages(exception, locale);
        } else {
            List<String> resolvedMessages = new ArrayList<>();
            for (var entry : exception.getCodeArgs().entrySet()) {
                resolvedMessages.add(messageSource.getMessage(entry.getKey(), entry.getValue(), locale));
            }
            return resolvedMessages;
        }
    }

    private Collection<String> buildThrowableMessages(Throwable throwable, Locale locale) {
        return Collections.singleton(messageSource.getMessage("retex.error.generic",
                new Object[]{throwable.getClass().getSimpleName(), String.valueOf(throwable.hashCode())}, locale));
    }
}
