/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import java.util.List;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.Destination;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.Source;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Link1;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Uuid;

/**
 * Matcher evaluating dataprovider filter criteria against network topology
 * links. Explicit, reflection-free property dispatch: the filter property
 * names mirror the RFC7951 wire names of the link fields.
 */
public class LinkMatchFilter extends BaseDataproviderFilter {

    public static boolean matchFilter(Link link, List<DataproviderInputFilter> filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        if (link == null) {
            //should never happen
            return true;
        }
        for (var f : filter) {
            if (!matchesProperty(link, f)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesProperty(Link link, DataproviderInputFilter filter) {
        String property = filter.getProperty();
        switch (property) {
            case "link-id":
                return filterMatches(link.getLinkId().getValue(), filter);
            case "device-discovery:uuid": {
                Uuid uuid = link.augmentation(Link1.class) != null
                        ? link.augmentation(Link1.class).getUuid() : null;
                return uuid != null && filterMatches(uuid.getValue(), filter);
            }
            case "source":
            case "source.source-node":
            case "source.source-tp":
                return filterMatches(link.getSource(), filter);
            case "destination":
            case "destination.dest-node":
            case "destination.dest-tp":
                return filterMatches(link.getDestination(), filter);
            default:
                return matchesCommonNetworkProperty(link, filter);
        }
    }
    /**
     * Filters on org-openroadm-common-network link attributes (link-type,
     * operational-state, administrative-state, opposite-link). If the link
     * does not carry the augmentation, only a filter on one of these
     * properties can match — none does (consistent with the uuid handling).
     */
    private static boolean matchesCommonNetworkProperty(Link link, DataproviderInputFilter filter) {
        var commonNetwork = link.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.common.network.rev250110.Link1.class);
        switch (filter.getProperty()) {
            case "org-openroadm-common-network:link-type":
            case "link-type":
                return commonNetwork != null
                        && commonNetwork.getLinkType() != null
                        && filterMatches(commonNetwork.getLinkType().getName(), filter);
            case "org-openroadm-common-network:operational-state":
            case "operational-state":
                return commonNetwork != null
                        && commonNetwork.getOperationalState() != null
                        && filterMatches(commonNetwork.getOperationalState().getName(), filter);
            case "org-openroadm-common-network:administrative-state":
            case "administrative-state":
                return commonNetwork != null
                        && commonNetwork.getAdministrativeState() != null
                        && filterMatches(commonNetwork.getAdministrativeState().getName(), filter);
            case "org-openroadm-common-network:opposite-link":
            case "opposite-link":
                return commonNetwork != null
                        && commonNetwork.getOppositeLink() != null
                        && filterMatches(commonNetwork.getOppositeLink().getValue(), filter);
            default:
                return true; // unknown property: no filtering
        }
    }

    private static boolean filterMatches(Source source, DataproviderInputFilter filter) {
        if (source == null) {
            return true;
        }

        switch (filter.getProperty()) {
            case "source.source-node": {
                if (!filterMatches(getUri(source.getSourceNode()), filter)) {
                    return false;
                }
                break;
            }
            case "source.source-tp": {
                if (!filterMatches(getUri(source.getSourceTp()), filter)) {
                    return false;
                }
                break;
            }
        }

        return true;
    }

    private static boolean filterMatches(Destination destination, DataproviderInputFilter filter) {
        if (destination == null) {
            return true;
        }

        switch (filter.getProperty()) {
            case "destination.dest-tp": {
                if (!filterMatches(getUri(destination.getDestTp()), filter)) {
                    return false;
                }
                break;
            }
            case "destination.dest-node": {
                if (!filterMatches(getUri(destination.getDestNode()), filter)) {
                    return false;
                }
                break;
            }
        }

        return true;
    }

    private static String getUri(Uri uri) {
        return uri != null ? uri.getValue() : null;
    }

}
