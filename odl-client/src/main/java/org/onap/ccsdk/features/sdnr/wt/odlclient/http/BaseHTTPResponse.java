/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.http;

public class BaseHTTPResponse {

    public static final int CODE404 = 404;
    public static final int CODE200 = 200;
    public static final BaseHTTPResponse UNKNOWN = new BaseHTTPResponse(-1, "");
    public final int code;
    public final String body;
    public final String contentType;

    public BaseHTTPResponse(int code, String body) {
        this(code, body, "plain/text");
    }

    public BaseHTTPResponse(int code, String body, String contentType) {
        this.code = code;
        this.body = body;
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "BaseHTTPResponse [code=" + code + ", body=" + body + ", contentType=" + contentType
                + "]";
    }

    public boolean isSuccess() {
        return this.code >= 200 && this.code < 300;
    }
}
