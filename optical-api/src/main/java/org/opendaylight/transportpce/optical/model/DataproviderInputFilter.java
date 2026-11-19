/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import java.util.Set;

public class DataproviderInputFilter {

    private String property;
    private String filtervalue;
    private Set<String> filtervalues;

    public DataproviderInputFilter() {

    }

    public DataproviderInputFilter(String property, String filtervalue, Set<String> filtervalues) {
        this.property = property;
        this.filtervalue = filtervalue;
        this.filtervalues = filtervalues;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getFiltervalue() {
        return filtervalue;
    }

    public void setFiltervalue(String filtervalue) {
        this.filtervalue = filtervalue;
    }

    public Set<String> getFiltervalues() {
        return filtervalues;
    }

    public void setFiltervalues(Set<String> filtervalues) {
        this.filtervalues = filtervalues;
    }
}
