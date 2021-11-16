/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Arrays;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotificationInput;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.SdnrNotificationMapper;
import org.onap.ccsdk.features.sdnr.wt.websocketmanager.model.data.Scope;
import org.onap.ccsdk.features.sdnr.wt.websocketmanager.model.data.ScopeRegistration;
import org.onap.ccsdk.features.sdnr.wt.websocketmanager.model.data.ScopeRegistrationResponse;
import org.onap.ccsdk.features.sdnr.wt.websocketmanager.model.data.ScopeRegistrationResponse.Status;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.AttributeValueChangedNotification;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ObjectCreationNotification;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ObjectDeletionNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(maxTextMessageSize = 128 * 1024, maxIdleTime = Integer.MAX_VALUE)
public class SdnrWebsocket {

    private static final Logger LOG = LoggerFactory.getLogger(SdnrWebsocket.class);
    private static final ScopeRegistration SCOPE_REGISTRATION_OBJECT =
            ScopeRegistration.forNotifications(Scope.createList(Arrays.asList(ObjectCreationNotification.QNAME,
                    ObjectDeletionNotification.QNAME, AttributeValueChangedNotification.QNAME)));

    private final SdnrWebsocketCallback callback;
    private final SdnrNotificationMapper mappper;
    private Session session;

    public SdnrWebsocket(SdnrWebsocketCallback cb) {
        this.callback = cb;
        this.mappper = new SdnrNotificationMapper();
    }

    public boolean sendNotificationRegistration() {
        if (this.session != null && this.session.isOpen()) {
            LOG.debug("sending notification registration");
            try {
                final String scopeRegistration = this.mappper.writeValueAsString(SCOPE_REGISTRATION_OBJECT);
                this.session.getRemote().sendString(scopeRegistration);
                return true;
            } catch (IOException e) {
                LOG.warn("unable to send register message: ", e);
                return false;
            }
        }
        return false;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.info("Connection closed: {} - {}", statusCode, reason);
        this.session = null;
        this.callback.onDisconnect(statusCode, reason);
    }

    @OnWebSocketConnect
    public void onConnect(Session lsession) {
        LOG.info("Got connect: {}", lsession);
        this.session = lsession;
        this.sendNotificationRegistration();
        this.callback.onConnect(lsession);


    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        LOG.debug("Got msg: {}", msg);
        this.callback.onMessageReceived(msg);
        this.handleIncomingMessage(msg);
    }

    private void handleIncomingMessage(String msg) {
        NotificationInput<?> notification = null;
        ScopeRegistrationResponse scopeRegistrationResponse = null;
        try {
            notification = this.mappper.readNotification(msg);
        } catch (JsonProcessingException e) {
            LOG.warn("problem parsing incoming message '{}': ", msg, e);
        }
        if (notification != null) {
            this.callback.onNotificationReceived(notification);
        } else {
            try {
                scopeRegistrationResponse = this.mappper.readValue(msg, ScopeRegistrationResponse.class);
            } catch (JsonProcessingException e1) {
                LOG.warn("problem parsing incoming message '{}': ", msg, e1);
            }
            if (scopeRegistrationResponse == null) {
                LOG.warn("received unknown message: {}", msg);
                return;
            }
            if (scopeRegistrationResponse.getStatus() == Status.success) {
                LOG.info("successfully registered for scopes {}",scopeRegistrationResponse.getScopes());
            } else {
                LOG.warn("problem registering for scopes: {}", msg);
            }

        }
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        LOG.warn("WebSocket Error:", cause);
        this.callback.onError(cause);
    }


}
