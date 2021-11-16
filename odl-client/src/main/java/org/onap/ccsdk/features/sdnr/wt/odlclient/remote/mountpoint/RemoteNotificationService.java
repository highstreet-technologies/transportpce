/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.mountpoint;

import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.ws.FilteredNotificationWebsocketCallback;
import org.onap.ccsdk.features.sdnr.wt.odlclient.ws.SdnrWebsocketClient;
import org.opendaylight.mdsal.binding.api.NotificationService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ProblemNotification;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.NotificationListener;

public class RemoteNotificationService implements NotificationService {

    private final String nodeId;
    private final SdnrWebsocketClient wsClient;


    public RemoteNotificationService(SdnrWebsocketClient wsClient, String nodeId) {
        this.wsClient = wsClient;
        this.nodeId = nodeId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends NotificationListener> @NonNull ListenerRegistration<T> registerNotificationListener(
            @NonNull T listener) {
        return (@NonNull ListenerRegistration<T>) new WebsocketListenerRegistration(this.wsClient,this.nodeId);
    }

    public static class WebsocketListenerRegistration extends FilteredNotificationWebsocketCallback
            implements ListenerRegistration<NotificationListener> {

        private final SdnrWebsocketClient wsClient;
        private final NotificationListener instance;

        public WebsocketListenerRegistration(SdnrWebsocketClient wsClient,String nodeId) {
            super(nodeId);
            this.wsClient = wsClient;
            this.wsClient.registerWebsocketEventListener(this);
            this.instance = new NotificationListener() {
            };
        }

        @Override
        public @NonNull NotificationListener getInstance() {
            return this.instance;
        }

        @Override
        public void close() {
            this.wsClient.unregisterWebsocketEventListener(this);
        }

        @Override
        public void onFilteredNotificationReceived(ProblemNotification notification) {

        }

    }
}
