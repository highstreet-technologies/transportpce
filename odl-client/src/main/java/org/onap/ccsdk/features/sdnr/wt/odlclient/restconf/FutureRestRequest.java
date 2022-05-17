/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.restconf;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlObjectMapperXml;
import org.onap.ccsdk.features.sdnr.wt.odlclient.http.BaseHTTPClient;
import org.onap.ccsdk.features.sdnr.wt.odlclient.http.BaseHTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FutureRestRequest<T> implements ListenableFuture<Optional<T>> {

    private static final Logger LOG = LoggerFactory.getLogger(FutureRestRequest.class);

    public static <T> FluentFuture<Optional<T>> createFutureGetRequest(BaseHTTPClient client, String uri, String data,
            Map<String, String> headers, Class<T> clazz, boolean clearWrappingParent) {
        return FluentFuture
                .from(new FutureRestRequest<T>(client, uri, "GET", data, headers, clazz, clearWrappingParent));
    }

    public static <T> FluentFuture<Optional<T>> createFuturePutRequest(BaseHTTPClient client, String uri, String data,
            Map<String, String> headers, boolean clearWrappingParent) {
        return FluentFuture
                .from(new FutureRestRequest<T>(client, uri, "PUT", data, headers, null, clearWrappingParent));
    }

    public static <T> FluentFuture<Optional<T>> createFuturePostRequest(BaseHTTPClient client, String uri, String data,
            Map<String, String> headers, Class<T> clazz, boolean clearWrappingParent) {
        return FluentFuture
                .from(new FutureRestRequest<T>(client, uri, "POST", data, headers, clazz, clearWrappingParent));
    }

    public static <T> FluentFuture<Optional<T>> createFutureDeleteRequest(BaseHTTPClient client, String uri,
            String data, Map<String, String> headers, boolean clearWrappingParent) {
        return FluentFuture
                .from(new FutureRestRequest<T>(client, uri, "DELETE", data, headers, null, clearWrappingParent));
    }

    private final BaseHTTPClient client;
    private final String uri;
    private final String method;
    private final String data;
    private final Map<String, String> headers;
    private final Class<T> clazz;
    private boolean isDone;
    private boolean isCancelled;
    private boolean clearWrappingParent;
    private final RequestCallback callback;

    private FutureRestRequest(BaseHTTPClient client, String uri, String method, String data,
            Map<String, String> headers, Class<T> clazz, boolean clearWrappingParent) {
        this(client, uri, method, data, headers, clazz, clearWrappingParent, null);
    }
    private FutureRestRequest(BaseHTTPClient client, String uri, String method, String data,
            Map<String, String> headers, Class<T> clazz, boolean clearWrappingParent, RequestCallback callback) {
        this.client = client;
        this.uri = uri;
        this.method = method;
        this.data = data;
        this.headers = headers;
        this.clazz = clazz;
        this.isDone = false;
        this.isCancelled = false;
        this.clearWrappingParent = clearWrappingParent;
        this.callback = callback;
    }

    @Override
    public boolean cancel(boolean arg0) {
        this.isCancelled = arg0;
        return this.isCancelled;
    }

    @Override
    public Optional<T> get() throws InterruptedException, ExecutionException {
        try {
            return get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            LOG.warn("request timeout:", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
        BaseHTTPResponse response;
        try {
            long timeoutMillis = arg1.toMillis(arg0);
            response = this.client.sendRequest(this.uri, this.method, this.data, this.headers,
                    new AtomicLong(timeoutMillis).intValue());
            if (response.isSuccess()) {
                LOG.debug("request to {}", uri);
                LOG.debug("response({}):{}", response.code, response.body);
                if (this.clazz != null) {
                    OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
                    return Optional.ofNullable(mapper.readValue(response.body, this.clazz));
                }
            }
        } catch (IOException e) {
            LOG.warn("problem requesting data: ", e);
        }
        this.isDone = true;
        return Optional.empty();
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public boolean isDone() {
        return this.isDone;
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {

    }

}
