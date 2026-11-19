/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dataprovider response envelope. Non-generic: the payload
 * ({@link DataproviderOutput}) carries pre-serialized JSON trees.
 */
public class DataproviderOutputWrapper {

    @JsonProperty("data-provider:output")
    DataproviderOutput output;

    public DataproviderOutputWrapper() {

    }

    public DataproviderOutputWrapper(DataproviderOutput output) {
        this.output = output;
    }

    public DataproviderOutput getOutput() {
        return output;
    }

    public void setOutput(DataproviderOutput output) {
        this.output = output;
    }
}
