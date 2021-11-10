package com.airbus.retex.service.impl.messaging;

public class WebsocketUrl {

    // the main entry point to connect to websocket server
    public static final String WEBSOCKET = "/ws";

    public static final String WEBSOCKET_DESTINATION_PREFIXE = "/messaging";

    // topic used when we send data for all listening clients
    public static final String WEBSOCKET_TOPIC_URL_NAME = "/topic/";

    // queue used when we want to send data to specific user
    public static final String WEBSOCKET_QUEUE_URL_NAME = "/queue/";

    public static final String THINGWORX_TOPIC_NAME = "thingworx/measurements";

    // server send data to specific user (is our destination)
    public static final String THINGWORX_QUEUE = WEBSOCKET_QUEUE_URL_NAME + THINGWORX_TOPIC_NAME;

    // all errors will be sent to this topic (so client be aware of what happening if websocket error is)
    public static final String THINGWORX_MESSAGING_ERRORS = "/thingworx/errors";

    // this endpoint can be used from controller (on @MessageMapping) to receive all data sent from client across websocket
    public static final String THINGWORX_MEASURES_MESSAGING_RECEPTION = "/thingworx/reception";

	/**
	 * private contructor
	 */
	private WebsocketUrl() {

	}
}
