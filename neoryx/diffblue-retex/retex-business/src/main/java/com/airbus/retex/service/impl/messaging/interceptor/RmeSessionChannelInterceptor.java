package com.airbus.retex.service.impl.messaging.interceptor;

import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.persistence.messaging.WebsocketIdentifierRepository;
import com.airbus.retex.service.impl.messaging.WebsocketConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Optional;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;

public class RmeSessionChannelInterceptor implements ChannelInterceptor {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebsocketIdentifierRepository websocketIdentifierRepository;
    private StompHeaderAccessor accessor;

    /**
     * in there make control to prevent a message from being processed in the down channel
     * downstream. To prevent this return null
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        // we intercept only CONNECT request
        if (headers.get(MESSAGE_TYPE_HEADER, SimpMessageType.class).equals(SimpMessageType.CONNECT)) {

            MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
            setHeaderAccessor(message);

            // we retrieve airbus user ID only on connection
            String airbusUserID = Optional.ofNullable(multiValueMap.getFirst(WebsocketConstants.AIRBUS_USER_ID_HEADER_KEY))
                    .orElseThrow(() -> new RuntimeException("User id was not informed"));

            // airbusUserID is mandatory
            if (StringUtils.isEmpty(airbusUserID)) {
                return null;
            }

            // check if we have already user with given airbusUser ID
            Optional<User> userOptional = userRepository.findById(Long.valueOf(airbusUserID));
            if (userOptional.isEmpty()) {
                return null;
            }
            // set the user to the accessor to make it accessible from sub chain
            accessor.setUser(() -> userOptional.get().getId().toString());
        }

        // This function will be called when client unsubscribe from websocket server
        // Update the state of the user in database
        if (headers.get(MESSAGE_TYPE_HEADER, SimpMessageType.class).equals(SimpMessageType.DISCONNECT)) {

            Principal principal = Optional.ofNullable((Principal) headers.get("simpUser"))
                    .orElseThrow(() -> new RuntimeException("Error : on application event, Principal is null"));

            Optional<WebsocketIdentifier> identifierOptional =  websocketIdentifierRepository.findByUserId(Long.valueOf(principal.getName()));

            if(identifierOptional.isPresent()) {
                WebsocketIdentifier identifier= identifierOptional.get();
                // update state of the user in database
                identifier.setConnected(false);
                websocketIdentifierRepository.save(identifier);
            }
        }
        return message;
    }

    private void setHeaderAccessor(Message message){
        accessor = Optional.ofNullable(MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class))
                .orElseThrow(() -> new RuntimeException("Error: StompHeader Accessor is null"));
    }
}