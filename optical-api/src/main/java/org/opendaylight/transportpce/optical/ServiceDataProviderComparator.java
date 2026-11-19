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
import org.opendaylight.transportpce.optical.model.DataproviderInputSortOrder.SortOrder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.service.rev250110.service.list.Services;

/**
 * Comparator sorting OpenROADM service list entries by dataprovider sort
 * criteria. Supports the same property names as
 * {@link org.opendaylight.transportpce.optical.model.ServiceMatchFilter},
 * each ascending or descending. Multiple sort criteria are applied in
 * order: the first property producing a difference decides. Null values
 * sort last regardless of direction; unknown properties are skipped.
 */
public class ServiceDataProviderComparator implements Comparator<Services> {

    private final List<DataproviderInputSortOrder> sortOrders;

    public ServiceDataProviderComparator(List<DataproviderInputSortOrder> sortOrders) {
        this.sortOrders = sortOrders;
    }

    @Override
    public int compare(Services o1, Services o2) {

        if (this.sortOrders == null || sortOrders.isEmpty()) {
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

        for (DataproviderInputSortOrder sortOrder : this.sortOrders) {
            String value1 = sortValue(o1, sortOrder.getProperty());
            String value2 = sortValue(o2, sortOrder.getProperty());

            int tmpResult = compareNullable(value1, value2);
            if (tmpResult != 0) {
                return sortOrder.getSortorder() == SortOrder.descending ? -tmpResult : tmpResult;
            }
        }
        return 0;

    }

    /**
     * Compares two nullable string values: null sorts last, remaining
     * natural string order.
     */
    private static int compareNullable(String value1, String value2) {
        if (value1 == null && value2 == null) {
            return 0;
        }
        if (value1 == null) {
            // null last
            return 1;
        }
        return value2 == null ? -1 : value1.compareTo(value2);
    }

    /**
     * Extracts the sortable wire-name string of a filter property from the
     * service. Returns null if the property is not present on the service.
     */
    private static String sortValue(Services service, String property) {
        switch (property) {
            case "service-name":
                return service.getServiceName();
            case "common-id":
                return service.getCommonId();
            case "connection-type":
                return service.getConnectionType() != null
                        ? service.getConnectionType().getName() : null;
            case "lifecycle-state":
                return service.getLifecycleState() != null
                        ? service.getLifecycleState().getName() : null;
            case "administrative-state":
                return service.getAdministrativeState() != null
                        ? service.getAdministrativeState().getName() : null;
            case "operational-state":
                return service.getOperationalState() != null
                        ? service.getOperationalState().getName() : null;
            case "service-a-end.node-id":
                return service.getServiceAEnd() != null && service.getServiceAEnd().getNodeId() != null
                        ? service.getServiceAEnd().getNodeId().getValue() : null;
            case "service-a-end.clli":
                return service.getServiceAEnd() != null
                        ? service.getServiceAEnd().getClli() : null;
            case "service-z-end.node-id":
                return service.getServiceZEnd() != null && service.getServiceZEnd().getNodeId() != null
                        ? service.getServiceZEnd().getNodeId().getValue() : null;
            case "service-z-end.clli":
                return service.getServiceZEnd() != null
                        ? service.getServiceZEnd().getClli() : null;
            default:
                return null; // unknown property: no influence on sort order
        }
    }

}
