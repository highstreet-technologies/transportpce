/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

public class DataproviderInputSortOrder {

    private String property;
    private SortOrder sortorder;

    public DataproviderInputSortOrder() {

    }

    public DataproviderInputSortOrder(String property, SortOrder sortorder) {
        this.property = property;
        this.sortorder = sortorder;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public SortOrder getSortorder() {
        return sortorder;
    }

    public void setSortorder(SortOrder sortorder) {
        this.sortorder = sortorder;
    }

    public enum SortOrder {
        ascending, descending
    }
}
