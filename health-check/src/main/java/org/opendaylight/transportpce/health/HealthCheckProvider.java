/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.health;

import io.lighty.server.LightyJettyServerProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.opendaylight.aaa.web.ServletDetails;
import org.opendaylight.aaa.web.WebContext;
import org.opendaylight.yangtools.concepts.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers a /ready endpoint on the same Jetty server that serves RESTCONF.
 *
 * Responds with HTTP 200 once TransportPCE has fully started up, and HTTP 503
 * during startup. Used by container orchestration (Docker/K8s) for readiness probes.
 */
public class HealthCheckProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckProvider.class);
    private static final String READY_PATH = "/ready";

    private final LightyJettyServerProvider serverProvider;
    private final AtomicBoolean ready = new AtomicBoolean(false);
    private Registration webContextRegistration;

    public HealthCheckProvider(LightyJettyServerProvider serverProvider) {
        this.serverProvider = serverProvider;
        LOG.info("HealthCheckProvider created");
    }

    /**
     * Register the /ready servlet on the Jetty server.
     */
    public void start() {
        ReadyServlet servlet = new ReadyServlet(ready);

        WebContext webContext = WebContext.builder()
                .name("HealthCheck")
                .contextPath(READY_PATH)
                .supportsSessions(false)
                .addServlet(ServletDetails.builder()
                        .servlet(servlet)
                        .addUrlPattern("/*")
                        .build())
                .build();

        try {
            webContextRegistration = serverProvider.getServer().registerWebContext(webContext);
            LOG.info("Health check endpoint registered at {} on RESTCONF server", READY_PATH);
        } catch (ServletException e) {
            LOG.error("Failed to register health check web context", e);
        }
    }

    /**
     * Mark TransportPCE as ready. Called after startup is complete.
     */
    public void setReady() {
        ready.set(true);
        LOG.info("Health check: TransportPCE is ready, /ready will return HTTP 200");
    }

    @Override
    public void close() {
        ready.set(false);
        if (webContextRegistration != null) {
            webContextRegistration.close();
            LOG.info("Health check endpoint unregistered");
        }
    }

    /**
     * Simple servlet that returns 200 or 503 based on readiness state.
     */
    static class ReadyServlet extends HttpServlet {

        private final AtomicBoolean ready;

        ReadyServlet(AtomicBoolean ready) {
            this.ready = ready;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            if (ready.get()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/plain");
                try (OutputStream out = resp.getOutputStream()) {
                    out.write("ready".getBytes(StandardCharsets.UTF_8));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                resp.setContentType("text/plain");
                try (OutputStream out = resp.getOutputStream()) {
                    out.write("starting".getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }
}
