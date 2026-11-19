/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DataproviderInput {
    @NotNull
    @Valid
    DataProviderInputPagination pagination;
    @NotNull
    @Valid
    List<DataproviderInputFilter> filter;
    @NotNull
    @Valid
    List<DataproviderInputSortOrder> sortorder;

    public DataProviderInputPagination getPagination() {
        return pagination;
    }

    public void setPagination(DataProviderInputPagination pagination) {
        this.pagination = pagination;
    }

    public List<DataproviderInputFilter> getFilter() {
        return filter;
    }

    public void setFilter(List<DataproviderInputFilter> filter) {
        this.filter = filter;
    }

    public List<DataproviderInputSortOrder> getSortorder() {
        return sortorder;
    }

    public void setSortorder(List<DataproviderInputSortOrder> sortorder) {
        this.sortorder = sortorder;
    }
}
