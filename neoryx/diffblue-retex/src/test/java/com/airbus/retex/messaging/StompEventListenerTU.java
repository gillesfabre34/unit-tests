package com.airbus.retex.messaging;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.persistence.messaging.WebsocketIdentifierRepository;
import com.airbus.retex.service.impl.messaging.listener.StompConnectedEventListener;
import com.airbus.retex.service.impl.messaging.listener.StompSubscribedEventListener;
import com.airbus.retex.service.impl.messaging.service.PushService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StompEventListenerTU {
    private static final String PRINCIPAL_USER_EMPTY = "Error : on application event, Principal is null";
    private static final String USER_NOT_FOUND = "Error on application Event: User not found";
    private static final String EMPTY_MESSAGE_TYPE_HEADER = "Error on application Event: SessionConnectedEvent empty message type";
    private static final String THINGWORX_QUEUE = "/queue/thingworx/measurements";

    @InjectMocks
    private StompSubscribedEventListener stompSubscribedEventListener;
    @InjectMocks
    private StompConnectedEventListener stompConnectedEventListener;
    @Mock
    private PushService pushService;
    @Mock
    private SessionSubscribeEvent sessionSubscribeEvent;
    @Mock
    private SessionConnectedEvent sessionConnectedEvent;
    @Mock
    private Message message;
    @Mock
    private MessageHeaders messageHeaders;
    @Mock
    private Principal principal;
    @Mock
    private StompHeaderAccessor stompHeaderAccessor;
    @Mock
    private UserRepository userRepository;
    @Mock
    private User loggedUser;
    @Mock
    private WebsocketIdentifierRepository websocketIdentifierRepository;

    @Test
    public void onApplicationEvent_subscribe_ok() throws FunctionalException {
        createHeader("121512", principal, SimpMessageType.SUBSCRIBE);
        when(message.getHeaders()).thenReturn(messageHeaders);
        when(sessionSubscribeEvent.getMessage()).thenReturn(message);
        when(principal.getName()).thenReturn("1");
        ReflectionTestUtils.setField(stompSubscribedEventListener, "pushService", pushService);
        stompSubscribedEventListener.onApplicationEvent(sessionSubscribeEvent);
        verify(pushService, times(1)).notify("121512", THINGWORX_QUEUE, principal.getName());
    }

    @Test
    public void onApplicationEvent_subscribe_user_not_found() {
        createHeader("121512", null, SimpMessageType.SUBSCRIBE);
        try {
            when(sessionSubscribeEvent.getMessage()).thenReturn(message);
            when(message.getHeaders()).thenReturn(messageHeaders);
            stompSubscribedEventListener.onApplicationEvent(sessionSubscribeEvent);
            fail("error should occur");
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals(PRINCIPAL_USER_EMPTY, e.getMessage());
        }
    }

    @Test
    public void onApplicationEvent_connect_ok() throws FunctionalException {
        createHeader("121512", principal, SimpMessageType.CONNECT);
        when(sessionConnectedEvent.getUser()).thenReturn(principal);
        when(sessionConnectedEvent.getMessage()).thenReturn(message);
        when(message.getHeaders()).thenReturn(messageHeaders);
        when(principal.getName()).thenReturn("1");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(loggedUser));
        stompConnectedEventListener.onApplicationEvent(sessionConnectedEvent);

        ReflectionTestUtils.setField(stompConnectedEventListener, "pushService", pushService);
        verify(pushService, times(1)).notify(null, THINGWORX_QUEUE, "0");
    }

    @Test
    public void onApplicationEvent_connect_principal_not_found() {
        createHeader("121512", null, SimpMessageType.CONNECT);
        when(sessionConnectedEvent.getUser()).thenReturn(principal);
        when(sessionConnectedEvent.getMessage()).thenReturn(message);
        when(message.getHeaders()).thenReturn(messageHeaders);
        try {
            stompConnectedEventListener.onApplicationEvent(sessionConnectedEvent);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals(PRINCIPAL_USER_EMPTY, e.getMessage());
        }
    }

    @Test
    public void onApplicationEvent_connect_logged_user_empty() {
        createHeader("121512", principal, SimpMessageType.CONNECT);
        setMockObjects(Optional.empty());
        try {
            stompConnectedEventListener.onApplicationEvent(sessionConnectedEvent);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals(USER_NOT_FOUND, e.getMessage());
        }
    }

    @Test
    public void onApplicationEvent_connect_message_type_not_found() {
        createHeader("121512", principal, null);
        setMockObjects(Optional.of(loggedUser));
        try {
            stompConnectedEventListener.onApplicationEvent(sessionConnectedEvent);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals(EMPTY_MESSAGE_TYPE_HEADER, e.getMessage());
        }
    }

    private void setMockObjects(Optional<User> userOptional) {
        when(sessionConnectedEvent.getUser()).thenReturn(principal);
        when(sessionConnectedEvent.getMessage()).thenReturn(message);
        when(message.getHeaders()).thenReturn(messageHeaders);
        when(userRepository.findById(anyLong())).thenReturn(userOptional);
        when(principal.getName()).thenReturn("1");
    }

    private MessageHeaders createHeader(String sessionID, Principal simpUser, SimpMessageType simpMessageType) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("simpSessionId", "121512");
        headers.put("simpUser", simpUser);
        headers.put("simpMessageType", simpMessageType);
        messageHeaders = new MessageHeaders(headers);
        return messageHeaders;
    }
}
