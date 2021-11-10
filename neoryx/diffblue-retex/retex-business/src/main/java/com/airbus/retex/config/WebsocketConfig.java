package com.airbus.retex.config;

import com.airbus.retex.service.impl.messaging.WebsocketUrl;
import com.airbus.retex.service.impl.messaging.interceptor.WebsocketHandshakeInterceptor;
import com.airbus.retex.service.impl.messaging.interceptor.RmeSessionChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * WEBSOCKET_TOPIC_URL : is a topic where client can subscribe and receive all messages in that topic (ex :/thingworx/measurements)
     *
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(WebsocketUrl.WEBSOCKET_TOPIC_URL_NAME, WebsocketUrl.WEBSOCKET_QUEUE_URL_NAME);
        config.setApplicationDestinationPrefixes(WebsocketUrl.WEBSOCKET_DESTINATION_PREFIXE);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WebsocketUrl.WEBSOCKET) // endpoint to allow client to connect and establish connection.
                .addInterceptors(httpSessionIdHandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(rmeSessionChannelInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(rmeSessionChannelInterceptor());
    }

    @Bean
    public WebsocketHandshakeInterceptor httpSessionIdHandshakeInterceptor() {
        return new WebsocketHandshakeInterceptor();
    }

    @Bean
    public RmeSessionChannelInterceptor rmeSessionChannelInterceptor() {
        return new RmeSessionChannelInterceptor();
    }
}
