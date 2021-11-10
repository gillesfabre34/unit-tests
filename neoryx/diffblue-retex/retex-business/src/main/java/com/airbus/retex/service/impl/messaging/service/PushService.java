package com.airbus.retex.service.impl.messaging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.airbus.retex.business.exception.FunctionalException;

@Component
public class PushService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    /**
     * send message to specific client
     *
     * @param message
     * @param destination
     * @param toUser
     * @throws FunctionalException
     */
    public void notify(Object message, String destination, String toUser){
        messagingTemplate.convertAndSendToUser(toUser, destination, message);
    }
}
