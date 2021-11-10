package com.airbus.retex.messaging;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.persistence.messaging.WebsocketIdentifierRepository;
import com.airbus.retex.service.impl.messaging.service.PushService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PushServiceTU {
    @Mock
    private WebsocketIdentifierRepository websocketIdentifierRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @InjectMocks
    private PushService pushService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void notify_ok() throws FunctionalException {
        Long userIDToNotify = 1L;
        WebsocketIdentifier user = createUser(userIDToNotify);
        pushService.notify("anyMessage", "/url/topic", userIDToNotify.toString());
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(userIDToNotify.toString(), "/url/topic", "anyMessage");
    }

    @Test
    public void notify_user_not_subscribed_exception() throws FunctionalException {
        Long userIDToNotify = 1L;
        WebsocketIdentifier user = createUser(userIDToNotify);
        user.setConnected(false);
        pushService.notify("anyMessage", "/url/topic", userIDToNotify.toString());
    }

    public WebsocketIdentifier createUser(Long userIDToNotify) {
        WebsocketIdentifier user = new WebsocketIdentifier();
        user.setConnected(true);
        user.setWebsocketSessionId("anystring");
        user.setUserId(userIDToNotify);
        return user;
    }
}
