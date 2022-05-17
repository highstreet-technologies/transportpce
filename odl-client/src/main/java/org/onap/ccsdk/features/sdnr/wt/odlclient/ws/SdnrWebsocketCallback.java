/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotificationInput;

public interface SdnrWebsocketCallback {

    void onConnect(Session lsession);

    void onMessageReceived(String msg);

    void onDisconnect(int statusCode, String reason);

    void onError(Throwable cause);

    void onNotificationReceived(NotificationInput<?> notification);

}
