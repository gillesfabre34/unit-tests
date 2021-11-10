package com.airbus.retex.service.impl.messaging.listener;

import static com.airbus.retex.service.impl.messaging.WebsocketUrl.THINGWORX_QUEUE;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.persistence.messaging.WebsocketIdentifierRepository;
import com.airbus.retex.service.impl.messaging.service.PushService;

@Component
public class StompConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {
    private static final String PRINCIPAL_NULL = "Error : on application event, Principal is null";
    private static final String USER_NOT_FOUND = "Error on application Event: User not found";
    private static final String EMPTY_MESSAGE_TYPE_HEADER = "Error on application Event: SessionConnectedEvent empty message type";

    @Autowired
    private PushService pushService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebsocketIdentifierRepository websocketIdentifierRepository;

    /**
     * This function will be called once websocket client connection established
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        if (event.getUser() != null) {
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = sha.getSessionId();
            MessageHeaders headers = event.getMessage().getHeaders();

            Principal principal = Optional.ofNullable((Principal) headers.get("simpUser"))
                    .orElseThrow(() -> new RuntimeException(PRINCIPAL_NULL));

            User loggedUser = userRepository.findById(Long.valueOf(principal.getName()))
                    .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

            WebsocketIdentifier identifier = new WebsocketIdentifier();
            identifier.setUserId(loggedUser.getId());
            identifier.setWebsocketSessionId(sessionId);

            SimpMessageType simpMessageType = Optional.ofNullable(headers
                    .get(MESSAGE_TYPE_HEADER, SimpMessageType.class)).orElseThrow(() -> new RuntimeException(EMPTY_MESSAGE_TYPE_HEADER));

            identifier.setConnected(simpMessageType.equals(SimpMessageType.CONNECT) || simpMessageType.equals(SimpMessageType.CONNECT_ACK));

            identifier = websocketIdentifierRepository.save(identifier);

            pushService.notify(identifier, THINGWORX_QUEUE, loggedUser.getId().toString());

        }
    }
}
