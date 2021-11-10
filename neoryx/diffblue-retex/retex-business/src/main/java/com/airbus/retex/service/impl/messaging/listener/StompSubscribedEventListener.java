package com.airbus.retex.service.impl.messaging.listener;

import com.airbus.retex.service.impl.messaging.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Optional;

import static com.airbus.retex.service.impl.messaging.WebsocketUrl.THINGWORX_QUEUE;

@Component
@Slf4j
public class StompSubscribedEventListener implements ApplicationListener<SessionSubscribeEvent> {
    private static final String PRINCIPAL_USER_EMPTY = "Error : on application event, Principal is null";

    @Autowired
    private PushService pushService;
    /**
     * This function will be called when Websocket client subscribe to topic
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        Principal principal = Optional.ofNullable((Principal) headers.get("simpUser"))
                .orElseThrow(() -> new RuntimeException(PRINCIPAL_USER_EMPTY));

        log.info("--------------- ON SUBSCRIBE TO EVENT -------------");
        log.info("-> principal Name : " + principal.getName());
        log.info("---------------------------------------------------");

        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();

        // send session id to client
        pushService.notify(sessionId, THINGWORX_QUEUE, principal.getName());
    }
}
