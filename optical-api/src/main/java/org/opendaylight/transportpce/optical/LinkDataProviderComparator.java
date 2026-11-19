/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical;

import java.util.Comparator;
import java.util.List;
import org.opendaylight.transportpce.optical.model.DataproviderInputSortOrder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;

public class LinkDataProviderComparator<T extends Link> implements Comparator<T> {

    private final List<DataproviderInputSortOrder> sortOrders;

    public LinkDataProviderComparator(List<DataproviderInputSortOrder> sortOrders) {
        this.sortOrders = sortOrders;
    }

    @Override
    public int compare(Link o1, Link o2) {

        if (this.sortOrders == null || sortOrders.size() <= 0) {
            return 0;
        }
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }

        int tmpResult = 0;
        for (var o : this.sortOrders) {
            switch (o.getProperty()) {
                case "link-id": {
                    tmpResult = o1.getLinkId().getValue().compareTo(o2.getLinkId().getValue());
                    break;
                }

            }
            if (tmpResult != 0) {
                return tmpResult;
            }
        }
        return 0;

    }
}
