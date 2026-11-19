/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataproviderInputWrapper {

    @JsonProperty("data-provider:input")
    DataproviderInput input;

    public DataproviderInput getInput() {
        return input;
    }

    public void setInput(DataproviderInput input) {
        this.input = input;
    }
}
