package com.airbus.retex.model.messaging;

import com.airbus.retex.model.basic.IIdentifiedModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WebsocketIdentifier implements Serializable, IIdentifiedModel<Long> {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "ws_session_id")
    private String websocketSessionId;

    @Column(name = "connected")
    private boolean connected;

    @Override
    public Long getId() {
        return userId;
    }
}
