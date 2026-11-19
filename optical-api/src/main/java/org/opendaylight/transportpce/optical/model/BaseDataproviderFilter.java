/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import java.util.HashSet;
import java.util.Set;
import org.opendaylight.yangtools.yang.common.Uint16;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDataproviderFilter {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDataproviderFilter.class);

    protected static boolean filterMatches(Uint16 value, DataproviderInputFilter filter) {
        if (value == null) {
            return false;
        }
        // no filter is a match
        if (isEmpty(filter)) {
            return true;
        }
        // single filter => matches or not
        if (filter.getFiltervalue() != null) {
            return filterMatches(value, filter.getFiltervalue());
        }
        // not empty and did not match
        return false;
    }


    protected static boolean filterMatches(String value, DataproviderInputFilter filter) {
        if (value == null) {
            return false;
        }
        // no filter is a match
        if (isEmpty(filter)) {
            return true;
        }
        // single filter => matches or not
        if (filter.getFiltervalue() != null) {
            return filterMatches(value, filter.getFiltervalue());
        }
        final var values = filter.getFiltervalues();
        // multiple filter: check for each => match one of
        if (values != null && !values.isEmpty()) {
            for (String f : filter.getFiltervalues()) {
                if (filterMatches(value, f)) {
                    return true;
                }
            }
        }
        // not empty and did not match
        return false;
    }

    protected static boolean isEmpty(DataproviderInputFilter filter) {
        if (filter == null) {
            return true;
        }
        Set<String> tmp = new HashSet<>();
        if (filter.getFiltervalue() != null && !filter.getFiltervalue().isBlank()) {
            tmp.add(filter.getFiltervalue());
        }
        final var values = filter.getFiltervalues();
        if (values != null && !values.isEmpty()) {
            tmp.addAll(values);
        }
        return tmp.size() <= 0;
    }

    protected static boolean filterMatches(String value, String filtervalue) {
        if (filtervalue.contains("*")) {
            filtervalue = filtervalue.replace("*", ".*");
        }
        if (filtervalue.contains("?")) {
            filtervalue = filtervalue.replace("?", ".");
        }
        /*Pattern pattern = Pattern.compile(filtervalue);
        return pattern.matcher(value).matches();*/
        return value.matches(filtervalue);
    }

    protected static boolean filterMatches(Uint16 value, String filtervalue) {
        if (filtervalue.startsWith(">=")) {
            try {
                return value.intValue() >= Integer.parseInt(filtervalue.substring(2));
            } catch (NumberFormatException e) {
                LOG.warn("bad numeric filter value {}", filtervalue);
            }
        } else if (filtervalue.startsWith(">")) {
            try {
                return value.intValue() > Integer.parseInt(filtervalue.substring(1));
            } catch (NumberFormatException e) {
                LOG.warn("bad numeric filter value {}", filtervalue);
            }
        } else if (filtervalue.startsWith("<=")) {
            try {
                return value.intValue() <= Integer.parseInt(filtervalue.substring(2));
            } catch (NumberFormatException e) {
                LOG.warn("bad numeric filter value {}", filtervalue);
            }
        } else if (filtervalue.startsWith("<")) {
            try {
                return value.intValue() < Integer.parseInt(filtervalue.substring(1));
            } catch (NumberFormatException e) {
                LOG.warn("bad numeric filter value {}", filtervalue);
            }
        }
        return filterMatches(String.valueOf(value.intValue()), filtervalue);
    }

}
