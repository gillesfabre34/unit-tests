package com.airbus.retex.messaging;

import com.airbus.retex.service.impl.messaging.interceptor.WebsocketHandshakeInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebsocketHandshakeInterceptorTU {

    @InjectMocks
    private WebsocketHandshakeInterceptor websocketHandshakeInterceptor;
    @Mock
    private ServletServerHttpRequest servletServerHttpRequest;
    @Mock
    private ServerHttpResponse response;
    @Mock
    private WebSocketHandler wsHandler;
    @Mock
    private HttpSession httpSession;
    @Mock
    private ServerHttpRequest httpRequest;
    @Mock
    private HttpServletRequest httpServletRequest;
    private Map<String, Object> attributes = new HashMap<>();

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void beforeHandshake_instance_of_servletServerHttpRequest() throws Exception{
        when(servletServerHttpRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(servletServerHttpRequest.getServletRequest().getSession()).thenReturn(httpSession);
        when(httpSession.getId()).thenReturn("sessionID");
        websocketHandshakeInterceptor.beforeHandshake(servletServerHttpRequest,response, wsHandler, attributes);
        assertTrue(attributes.containsKey("sessionId"));
    }

    @Test
    public void beforeHandshake_not_instance_of_servletServerHttpRequest() throws Exception{
        websocketHandshakeInterceptor.beforeHandshake(httpRequest,response, wsHandler, attributes);
        assertTrue(attributes.isEmpty());
    }
}
