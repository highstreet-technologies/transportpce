/*
 * Copyright © 2026 Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.pce.gnpy.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.opendaylight.transportpce.common.converter.JsonStringConverter;
import org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library.rev250526.Eqpt;
import org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library.rev250526.Equipment;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.codec.gson.JSONCodecFactorySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the default GNPy equipment library bundled with this module and
 * exposes it as a binding object, so that it can be merged into a
 * path-computation request programmatically.
 *
 * <p>The official gnpy-api requires the equipment library to be part of every
 * path-request. The previous gnpy4tpce server loaded a hard-coded library
 * server-side; with the official API the controller must send it along.
 * If a request does not carry any equipment, {@code GnpyConsumerImpl} merges
 * the library provided by this loader into the request before sending it.
 */
public final class GnpyEquipmentLoader {
    private static final Logger LOG = LoggerFactory.getLogger(GnpyEquipmentLoader.class);
    private static final String EQUIPMENT_RESOURCE = "/gnpy/eqpt_config_yang.json";

    private final JsonStringConverter<Equipment> converter;
    private volatile Equipment defaultEquipment;

    public GnpyEquipmentLoader(final JsonStringConverter<Equipment> converter) {
        this.converter = converter;
    }

    /**
     * Returns the default equipment library, loading it lazily from the
     * bundled resource on first access.
     *
     * @return the equipment library or {@code null} if it cannot be loaded
     */
    public Equipment loadDefaultEquipment() {
        Equipment equipment = this.defaultEquipment;
        if (equipment != null) {
            return equipment;
        }
        synchronized (this) {
            if (this.defaultEquipment == null) {
                this.defaultEquipment = readResource();
            }
            return this.defaultEquipment;
        }
    }

    private Equipment readResource() {
        try (InputStream input = GnpyEquipmentLoader.class.getResourceAsStream(EQUIPMENT_RESOURCE)) {
            if (input == null) {
                LOG.warn("Equipment library resource {} not found", EQUIPMENT_RESOURCE);
                return null;
            }
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            Equipment equipment = converter.createDataObjectFromJsonString(
                    YangInstanceIdentifier.of(Equipment.QNAME),
                    json,
                    JSONCodecFactorySupplier.DRAFT_LHOTKA_NETMOD_YANG_JSON_02);
            LOG.info("Default GNPy equipment library loaded from {}", EQUIPMENT_RESOURCE);
            return equipment;
        } catch (IOException | IllegalStateException e) {
            LOG.warn("Could not load default GNPy equipment library", e);
            return null;
        }
    }

    /**
     * Builds the augment value carrying an equipment library, ready to be
     * attached to the gnpy-api request container.
     *
     * @param equipment the library to wrap
     * @return the equipment entry for the api augmentation
     */
    public static org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library.rev250526.equipment.Equipment
            toEquipmentEntry(final Equipment equipment) {
        return new org.opendaylight.yang.gen.v1.urn.ns.gnpy.equipment_library.rev250526.equipment
                .EquipmentBuilder((Eqpt) equipment).build();
    }
}
