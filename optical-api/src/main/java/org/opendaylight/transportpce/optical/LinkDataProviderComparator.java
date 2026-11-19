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
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;

/**
 * Comparator sorting network topology links by dataprovider sort criteria.
 *
 * <p>Supports the same property names as {@link org.opendaylight.transportpce.optical.model
 * .LinkMatchFilter} (link-id, device-discovery:uuid, source/destination
 * endpoints, and the org-openroadm-common-network attributes), each
 * ascending or descending. Multiple sort criteria are applied in order:
 * the first property producing a difference decides. Null values sort
 * last regardless of direction; unknown properties are skipped.
 */
public class LinkDataProviderComparator implements Comparator<Link> {

    private final List<DataproviderInputSortOrder> sortOrders;

    public LinkDataProviderComparator(List<DataproviderInputSortOrder> sortOrders) {
        this.sortOrders = sortOrders;
    }

    @Override
    public int compare(Link o1, Link o2) {

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
     * link. Returns null if the property is not present on the link.
     */
    private static String sortValue(Link link, String property) {
        switch (property) {
            case "link-id":
                return link.getLinkId() != null ? link.getLinkId().getValue() : null;
            case "device-discovery:uuid": {
                var uuidAug = link.augmentation(
                        org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery
                                .rev260907.Link1.class);
                return uuidAug != null && uuidAug.getUuid() != null
                        ? uuidAug.getUuid().getValue() : null;
            }
            case "source.source-node":
                return link.getSource() != null && link.getSource().getSourceNode() != null
                        ? link.getSource().getSourceNode().getValue() : null;
            case "source.source-tp":
                return link.getSource() != null && link.getSource().getSourceTp() != null
                        ? link.getSource().getSourceTp().getValue() : null;
            case "destination.dest-node":
                return link.getDestination() != null && link.getDestination().getDestNode() != null
                        ? link.getDestination().getDestNode().getValue() : null;
            case "destination.dest-tp":
                return link.getDestination() != null && link.getDestination().getDestTp() != null
                        ? link.getDestination().getDestTp().getValue() : null;
            default:
                return commonNetworkValue(link, property);
        }
    }

    /**
     * Sort values of org-openroadm-common-network link attributes, using the
     * same wire names as in the JSON representation (enum names via
     * getName()).
     */
    private static String commonNetworkValue(Link link, String property) {
        var commonNetwork = link.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev250110.Link1.class);
        switch (property) {
            case "org-openroadm-common-network:link-type":
            case "link-type":
                return commonNetwork != null && commonNetwork.getLinkType() != null
                        ? commonNetwork.getLinkType().getName() : null;
            case "org-openroadm-common-network:operational-state":
            case "operational-state":
                return commonNetwork != null && commonNetwork.getOperationalState() != null
                        ? commonNetwork.getOperationalState().getName() : null;
            case "org-openroadm-common-network:administrative-state":
            case "administrative-state":
                return commonNetwork != null && commonNetwork.getAdministrativeState() != null
                        ? commonNetwork.getAdministrativeState().getName() : null;
            case "org-openroadm-common-network:opposite-link":
            case "opposite-link":
                return commonNetwork != null && commonNetwork.getOppositeLink() != null
                        ? commonNetwork.getOppositeLink().getValue() : null;
            default:
                return null; // unknown property: no influence on sort order
        }
    }

}
