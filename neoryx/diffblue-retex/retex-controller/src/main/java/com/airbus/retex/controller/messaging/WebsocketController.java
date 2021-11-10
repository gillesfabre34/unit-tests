package com.airbus.retex.controller.messaging;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static com.airbus.retex.service.impl.messaging.WebsocketUrl.THINGWORX_MEASURES_MESSAGING_RECEPTION;
import static com.airbus.retex.service.impl.messaging.WebsocketUrl.THINGWORX_MESSAGING_ERRORS;


@Controller
public class WebsocketController {

    /**
     * we can receive measures sent from client
     *
     * @param message
     * @param principal
     * @throws Exception
     */
    @MessageMapping(THINGWORX_MEASURES_MESSAGING_RECEPTION)
    public void sendMessageToClient(@Payload String message, Principal principal) {
        // in there do something, if we receive data from client across websocket
    }

    /**
     * client will listen to this endpoint to receive any errors
     *
     * @param exception
     * @return
     */
    @MessageExceptionHandler
    @SendToUser(THINGWORX_MESSAGING_ERRORS)
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}