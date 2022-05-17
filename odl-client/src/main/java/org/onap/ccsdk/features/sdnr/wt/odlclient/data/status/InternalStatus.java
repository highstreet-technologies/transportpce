/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data.status;

public class InternalStatus {

    private static final String JSON_FORMAT = "{\"websocket\":\"%s\",\"responses\":%s}";
    private static final String JSON_FORMAT_RESPONSES = "{\"succeeded\":%d,\"failed\":%d}";

    private String webSocket;

    private ResponseStatus responses;

    public InternalStatus() {
        this.webSocket = "unknown";
        this.responses = new ResponseStatus();
    }

    public void setWebSocket(String status) {
        this.webSocket = status;
    }
    public void addResponse(boolean succeeded) {
        if(succeeded) {
            this.responses.succeeded++;
        }
        else {
            this.responses.failed++;
        }
    }

    public String toJSON() {
        return String.format(JSON_FORMAT, this.webSocket,this.responses.toJSON());
    }


    private class ResponseStatus {

        private int succeeded;
        private int failed;

        ResponseStatus(){
            this.succeeded = 0;
            this.failed = 0;
        }

        public String toJSON() {
            return String.format(JSON_FORMAT_RESPONSES, this.succeeded,this.failed);
        }
    }

}
