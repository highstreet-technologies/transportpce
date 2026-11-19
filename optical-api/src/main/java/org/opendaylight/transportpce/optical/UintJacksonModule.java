/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yangtools.yang.common.Uint16;
import org.opendaylight.yangtools.yang.common.Uint32;
import org.opendaylight.yangtools.yang.common.Uint64;
import org.opendaylight.yangtools.yang.common.Uint8;

/**
 * Jackson module teaching the ObjectMapper to serialize and deserialize yangtools unsigned integer wrapper types
 * ({@link Uint8}, {@link Uint16}, {@link Uint32}, {@link Uint64}) as plain JSON numbers instead of bean objects
 * ({"value": 5}).
 *
 * <p>Required for the dataprovider envelope of this API: its pagination
 * fields are typed with the wrapper classes but must appear on the wire as plain numbers, matching the odlux client
 * expectations.
 */
public final class UintJacksonModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public UintJacksonModule() {
        super("UintJacksonModule");
        addSerializer(Uint8.class, new JsonSerializer<Uint8>() {
            @Override
            public void serialize(Uint8 value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeNumber(value.toJava());
            }
        });
        addSerializer(Uint16.class, new JsonSerializer<Uint16>() {
            @Override
            public void serialize(Uint16 value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeNumber(value.toJava());
            }
        });
        addSerializer(Uint32.class, new JsonSerializer<Uint32>() {
            @Override
            public void serialize(Uint32 value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeNumber(value.toJava());
            }
        });
        addSerializer(Uint64.class, new JsonSerializer<>() {
            @Override
            public void serialize(Uint64 value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeString(value.toCanonicalString());
            }
        });
        addSerializer(Uri.class, new JsonSerializer<>() {
            @Override
            public void serialize(Uri value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.getValue());
            }
        });
        addDeserializer(Uint8.class, new JsonDeserializer<Uint8>() {
            @Override
            public Uint8 deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException {
                return Uint8.valueOf(p.getIntValue());
            }
        });
        addDeserializer(Uint16.class, new JsonDeserializer<Uint16>() {
            @Override
            public Uint16 deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException {
                return Uint16.valueOf(p.getIntValue());
            }
        });
        addDeserializer(Uint32.class, new JsonDeserializer<Uint32>() {
            @Override
            public Uint32 deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException {
                return Uint32.valueOf(p.getLongValue());
            }
        });
        addDeserializer(Uint64.class, new JsonDeserializer<Uint64>() {
            @Override
            public Uint64 deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException {
                return Uint64.valueOf(p.getValueAsString());
            }
        });
        addDeserializer(Uri.class, new JsonDeserializer<>() {
            @Override
            public Uri deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                return new Uri(p.getValueAsString());
            }
        });
    }

    @Override
    public String getModuleName() {
        return "UintJacksonModule";
    }
}
