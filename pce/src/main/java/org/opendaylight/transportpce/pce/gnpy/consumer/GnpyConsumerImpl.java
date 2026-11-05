/*
 * Copyright © 2021 Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.pce.gnpy.consumer;

import java.io.IOException;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.opendaylight.transportpce.common.converter.JsonStringConverter;
import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.Responses;
import org.opendaylight.yang.gen.v1.urn.gnpy.api.rev250613.Api;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(configurationPid = "org.opendaylight.transportpce.pce")
public class GnpyConsumerImpl implements GnpyConsumer {

    @ObjectClassDefinition
    public @interface Configuration {
        @AttributeDefinition
        String url() default "http://127.0.0.1:8080";
        @AttributeDefinition
        String username() default "gnpy";
        @AttributeDefinition
        String password() default "gnpy";
    }

    private static final Logger LOG = LoggerFactory.getLogger(GnpyConsumerImpl.class);
    private static final String STATUS_ENDPOINT = "/api/v1.0/status";
    private static final String PATH_REQUEST_ENDPOINT = "/api/v1.0/path-request";
    private final HttpClient client;
    private final URI statusUri;
    private final URI pathRequestUri;
    private final JsonStringConverter<Api> requestConverter;
    private final JsonStringConverter<Responses> resultConverter;
    private final GnpyEquipmentLoader equipmentLoader;

    @Activate
    public GnpyConsumerImpl(final Configuration configuration,
            @Reference BindingDOMCodecServices bindingDOMCodecServices) {
        this(configuration.url(), configuration.username(), configuration.password(), bindingDOMCodecServices);
    }

    public GnpyConsumerImpl(String baseUrl, String username, String password,
            BindingDOMCodecServices bindingDOMCodecServices) {
        LOG.info("baseUrl: {}, username: {}, password: {}", baseUrl, username, password);
        this.client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                username,
                                password.toCharArray());
                    }
                }).build();
        this.statusUri = URI.create(baseUrl + STATUS_ENDPOINT);
        this.pathRequestUri = URI.create(baseUrl + PATH_REQUEST_ENDPOINT);
        requestConverter = new JsonStringConverter<>(bindingDOMCodecServices);
        resultConverter = new JsonStringConverter<>(bindingDOMCodecServices);
        equipmentLoader = new GnpyEquipmentLoader(
                new JsonStringConverter<>(bindingDOMCodecServices));
    }

    @Deactivate
    public void close() {
        LOG.info("Closing client {}", this.client);
    }

    @Override
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(statusUri)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            LOG.info("GNPy available {}", response.statusCode() == 200);
            return response.statusCode() == 200;
        } catch (ConnectException e) {
            LOG.info("Connection to GNPy refused or server simply not available: {}", e.getMessage());
            return false;
        } catch (IOException e) {
            LOG.info("GNPy is not available ", e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public Responses computePaths(final Api request) {
        try {
            String requestBody = toGnpyWireFormat(requestConverter
                    .createJsonStringFromDataObject(
                            DataObjectIdentifier.builder(Api.class).build(),
                            withDefaultEquipment(request),
                            JSONCodecFactorySupplier.DRAFT_LHOTKA_NETMOD_YANG_JSON_02));
            LOG.info("requestbody = {}", requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(pathRequestUri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpRequest.headers().map().forEach((k, v) -> LOG.info("header {}: {}", k, v));
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            LOG.info("response = {}", response);

            if (response.statusCode() == 201) {
                LOG.info("response body = {}", response.body());
                // The official gnpy-api returns the content of the responses grouping
                // ({"response": [...]}) without the surrounding "responses" container.
                // Wrap the body so that the JSON parser can resolve it against the schema.
                String wrappedBody = "{\"gnpy-path-computation:responses\":" + response.body() + "}";
                return resultConverter.createDataObjectFromJsonString(
                        YangInstanceIdentifier.of(Responses.QNAME),
                        wrappedBody,
                        JSONCodecFactorySupplier.DRAFT_LHOTKA_NETMOD_YANG_JSON_02);
            } else {
                LOG.info("Error response: {}", response.body());
                return null;
            }
        } catch (IOException e) {
            LOG.info("Something went wrong while requesting GNPy ", e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Converts the JSON produced by the ODL codec into the wire format expected
     * by the official gnpy-api, which validates the payload with libyang:
     * decimal64 values must be RFC7951-encoded (quoted), and identity values
     * must carry no module namespace prefix. Both deviations of the ODL
     * draft codec are corrected with targeted textual replacements; container
     * names keep their namespace prefix and are not matched because they are
     * never followed by a number.
     */
    private String toGnpyWireFormat(String requestBody) {
        return requestBody
                .replace("\"gnpy-network-topology:", "\"")
                .replace("\"gnpy-path-computation:route-", "\"route-")
                // the ODL codec renders the topology augmentation container without its
                // module prefix when further augmentations are present; the gnpy server
                // expects the fully-qualified container name
                .replace("{\"topology\":", "{\"gnpy-network-topology:topology\":")
                .replaceAll(":(-?\\d+\\.\\d+)", ":\"$1\"");
    }

    /**
     * The official gnpy-api requires the equipment library to be part of the
     * path-request. If the caller did not provide any equipment, the default
     * library bundled in this module is merged into the request on binding
     * level, mirroring the hard-coded server-side library of the former
     * gnpy4tpce server.
     */
    private Api withDefaultEquipment(Api request) {
        if (request.augmentation(
                org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library.rev250526.Api1.class) != null
                && request.augmentation(
                        org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library.rev250526.Api1.class)
                        .getEquipment() != null) {
            return request;
        }
        org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library.rev250526.Equipment equipment =
                equipmentLoader.loadDefaultEquipment();
        if (equipment == null) {
            LOG.warn("No equipment library found in request and default library could not be loaded");
            return request;
        }
        org.opendaylight.yang.gen.v1.urn.gnpy.api.rev250613.ApiBuilder builder =
                new org.opendaylight.yang.gen.v1.urn.gnpy.api.rev250613.ApiBuilder();
        // carry over all existing augmentations (services, topology, ...)
        request.augmentations().values().forEach(builder::addAugmentation);
        builder.addAugmentation(new org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library
                .rev250526.Api1Builder()
                .setEquipment(GnpyEquipmentLoader.toEquipmentEntry(equipment))
                .build());
        return builder.build();
    }
}
