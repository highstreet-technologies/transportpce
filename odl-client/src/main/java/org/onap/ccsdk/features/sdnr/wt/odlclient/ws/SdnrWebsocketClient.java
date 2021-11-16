/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.ws;

import java.net.URI;
import java.net.URISyntaxException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.onap.ccsdk.features.sdnr.wt.odlclient.http.BaseHTTPClient;

public class SdnrWebsocketClient extends WebsocketWatchDog {

    private final URI url;
    private WebSocketClient wsClient;
    private final boolean trustAllCerts;

    private static final HostnameVerifier ALL_HOSTNAMES_VALID_VERIFIER = new HostnameVerifier() {
        @Override
        public boolean verify(String anyString, SSLSession sslSession) {
            return true;
        }
    };

    public SdnrWebsocketClient(String url, SdnrWebsocketCallback callback, boolean trustAllCerts)
            throws URISyntaxException {
        super(callback);
        this.url = new URI(url);
        this.trustAllCerts = trustAllCerts;
    }

    @Override
    void reconnect() throws Exception {

        SslContextFactory cf = new SslContextFactory.Client();
        cf.setSslContext(BaseHTTPClient.setupSsl(this.trustAllCerts));
        HttpClient httpClient = new HttpClient(cf);
        httpClient.start();
        this.wsClient = new WebSocketClient(httpClient);

        this.wsClient.start();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        this.wsClient.connect(new SdnrWebsocket(this), this.url, request);
    }
}
