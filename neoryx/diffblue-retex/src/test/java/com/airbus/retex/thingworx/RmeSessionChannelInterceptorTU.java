package com.airbus.retex.thingworx;

import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.service.impl.messaging.interceptor.RmeSessionChannelInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;

@ExtendWith(MockitoExtension.class)
public class RmeSessionChannelInterceptorTU {
    @InjectMocks
    private RmeSessionChannelInterceptor rmeSessionChannelInterceptor;
    @Mock
    private Message message;
    @Mock
    private MessageHeaders messageHeaders;
    @Mock
    private MessageChannel channel;
    @Spy
    private MultiValueMap<String, String> multiValueMap;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SimpMessageHeaderAccessor accessor;
    @InjectMocks
    private MessageHeaderAccessor messageHeaderAccessor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void preSend_simpMessage_connect_stomp_header_null() throws Exception {
        lenient().when(message.getHeaders()).thenReturn(messageHeaders);
        lenient().when(messageHeaders.get(MESSAGE_TYPE_HEADER, SimpMessageType.class)).thenReturn(SimpMessageType.CONNECT);
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            rmeSessionChannelInterceptor.preSend(message, channel)
        );
        assertEquals("Error: StompHeader Accessor is null", thrown.getMessage());
    }

}
