/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.ws;

public interface SdnrWebsocketCallback {

    void onMessageReceived(String msg);

    void onDisconnect(SdnrWtWebsocket socket);

    void onError(Throwable cause);
}