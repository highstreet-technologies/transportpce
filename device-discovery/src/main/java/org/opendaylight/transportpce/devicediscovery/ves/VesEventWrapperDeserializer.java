/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.ves;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class VesEventWrapperDeserializer implements Deserializer<VesEventWrapper> {


    private final ObjectMapper mapper;

    public VesEventWrapperDeserializer() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new DefaultJacksonModule());
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public VesEventWrapper deserialize(String s, byte[] bytes) {
        try {
            String content = new String(bytes);
            return this.mapper.readValue(content, VesEventWrapper.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
