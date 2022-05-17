/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotificationInput;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.SdnrNotificationMapper;
import org.onap.ccsdk.features.sdnr.wt.odlclient.ws.SdnrWebsocketCallback;
import org.onap.ccsdk.features.sdnr.wt.odlclient.ws.SdnrWebsocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWebsocketClient {
    private static final Logger LOG = LoggerFactory.getLogger(TestWebsocketClient.class);

    private static final String ATTRIBUTEVALUECHANGED_NOTIFICATION = "<?xml version=\"1.0\" "
            + "encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<AttributeValueChangedNotification>\n"
            + "    <counter>6</counter>\n" + "    <nodeName>SDN-Controller-0</nodeName>\n"
            + "    <objectId>ROADM-A</objectId>\n"
            + "    <timeStamp>2020-06-10T08:58:37.0Z</timeStamp>\n"
            + "    <attributeName>ConnectionStatus</attributeName>\n"
            + "    <newValue>connecting</newValue>\n" + "</AttributeValueChangedNotification>\n" + "";
    private static final String OBJECTCREATION_NOTIFICATION = "<?xml version=\"1.0\" "
            + "encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<ObjectCreationNotification>\n"
            + "    <counter>4</counter>\n" + "    <nodeName>SDN-Controller-0</nodeName>\n"
            + "    <objectId>ROADM-A</objectId>\n"
            + "    <timeStamp>2020-06-10T08:58:36.7Z</timeStamp>\n" + "</ObjectCreationNotification>";
    private static final String OBJECTDELETION_NOTIFICATION = "<?xml version=\"1.0\" "
            + "encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<ObjectDeletionNotification>\n"
            + "    <counter>5</counter>\n" + "    <nodeName>SDN-Controller-0</nodeName>\n"
            + "    <objectId>ROADM-A</objectId>\n"
            + "    <timeStamp>2020-06-10T08:58:36.7Z</timeStamp>\n" + "</ObjectDeletionNotification>";

    @Test
    public void test() throws URISyntaxException {

        SdnrWebsocketCallback callback = new SdnrWebsocketCallback() {

            @Override
            public void onMessageReceived(String msg) {
                LOG.info("on message: {}", msg);

            }

            @Override
            public void onError(Throwable cause) {
                LOG.info("on error", cause);
            }

            @Override
            public void onDisconnect(int statusCode, String reason) {
                LOG.info("disconnected");
            }

            @Override
            public void onConnect(Session lsession) {
                LOG.info("connected");
            }

            @Override
            public void onNotificationReceived(NotificationInput<?> notification) {
                LOG.info("notification: {}", notification);
            }
        };
        SdnrWebsocketClient wsClient = new SdnrWebsocketClient("wss://10.20.35.188:30267/websocket",
                callback, true);
        wsClient.start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOG.warn("sleep interrupted", e);
        }
        wsClient.stop();

        int downCounter = 5;
        while (!wsClient.isStopped() && downCounter-- > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.warn("sleep interrupted", e);
            }
        }
        LOG.info("finished test");

    }

    @Test
    public void testNotifications() throws JsonParseException, JsonMappingException, IOException {
        SdnrNotificationMapper mapper = new SdnrNotificationMapper();

        LOG.info("create={}", mapper.readNotification(OBJECTCREATION_NOTIFICATION));
        LOG.info("delete={}", mapper.readNotification(OBJECTDELETION_NOTIFICATION));
        LOG.info("value changed={}", mapper.readNotification(ATTRIBUTEVALUECHANGED_NOTIFICATION));

    }
}
