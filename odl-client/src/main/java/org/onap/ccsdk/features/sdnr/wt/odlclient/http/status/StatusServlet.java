/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.http.status;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.status.InternalStatus;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.status.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO check if another solution such as using OSGi annotations would not be more indicated here
@SuppressFBWarnings(
    value = {"SE_BAD_FIELD"},
    justification =
       "This field is not Serializable but this class implements HttpServlet to delegate serialization."
       + "Thus instances of this class aren't serialized. SpotBugs does not recognize this.")
public class StatusServlet extends HttpServlet implements StatusService {

    private static final Logger LOG = LoggerFactory.getLogger(StatusServlet.class);
    private static final long serialVersionUID = 1L;
    private final InternalStatus status;

    public StatusServlet() {
        this.status = new InternalStatus();
    }

    @Override
    public void setWebsocketStatus(String connectionStatus) {
        this.status.setWebSocket(connectionStatus);

    }

    @Override
    public void addResponse(boolean success) {
        this.status.addResponse(success);

    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.sendJsonResponse(resp,this.status.toJSON());
    }

    private void sendJsonResponse(HttpServletResponse resp, String json) {
        try {
            resp.setHeader("Content-Type", "application/json");
            resp.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.warn("problem sending status: ",e);
        }
    }
}
