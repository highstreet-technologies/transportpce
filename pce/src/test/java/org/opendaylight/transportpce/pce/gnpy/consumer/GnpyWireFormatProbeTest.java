/*
 * Copyright © 2021 Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.pce.gnpy.consumer;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.opendaylight.transportpce.common.converter.JsonStringConverter;
import org.opendaylight.transportpce.test.AbstractTest;
import org.opendaylight.yang.gen.v1.urn.gnpy.api.rev250613.Api;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** TEMPORARY probe: compare RFC7951 vs DRAFT_LHOTKA wire encoding. Will be deleted. */
class GnpyWireFormatProbeTest extends AbstractTest {
    private static final Logger LOG = LoggerFactory.getLogger(GnpyWireFormatProbeTest.class);

    @Test
    void probeEncodings() throws Exception {
        JsonStringConverter<Api> conv = new JsonStringConverter<>(
                AbstractTest.getDataStoreContextUtil().getBindingDOMCodecServices());
        Api request = conv.createDataObjectFromJsonString(
                YangInstanceIdentifier.of(Api.QNAME),
                Files.readString(Path.of("src/test/resources/gnpy/gnpy_request.json")),
                JSONCodecFactorySupplier.DRAFT_LHOTKA_NETMOD_YANG_JSON_02);

        String rfc7951 = conv.createJsonStringFromDataObject(
                DataObjectIdentifier.builder(Api.class).build(), request, JSONCodecFactorySupplier.RFC7951);
        String lhotka = conv.createJsonStringFromDataObject(
                DataObjectIdentifier.builder(Api.class).build(), request,
                JSONCodecFactorySupplier.DRAFT_LHOTKA_NETMOD_YANG_JSON_02);

        LOG.info("RFC7951 : {}", rfc7951.substring(0, Math.min(600, rfc7951.length())));
        LOG.info("DRAFT   : {}", lhotka.substring(0, Math.min(600, lhotka.length())));
        Files.writeString(Path.of("target/probe_rfc7951.json"), rfc7951);
        Files.writeString(Path.of("target/probe_lhotka.json"), lhotka);
    }
}
