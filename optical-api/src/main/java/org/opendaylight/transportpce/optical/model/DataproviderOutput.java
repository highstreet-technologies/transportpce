/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Dataprovider response payload. The data entries are pre-serialized JSON
 * trees ({@link JsonNode}) so they can be embedded into the envelope without
 * further POJO conversion — allowing RFC7951 wire format for the entries
 * while keeping the Jackson-serialized envelope.
 */
public class DataproviderOutput {

    List<JsonNode> data;
    DataproviderOutputPagination pagination;

    public DataproviderOutput() {

    }

    public DataproviderOutput(List<JsonNode> data, DataproviderOutputPagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<JsonNode> getData() {
        return data;
    }

    public void setData(List<JsonNode> data) {
        this.data = data;
    }

    public DataproviderOutputPagination getPagination() {
        return pagination;
    }

    public void setPagination(DataproviderOutputPagination pagination) {
        this.pagination = pagination;
    }
}
