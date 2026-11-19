/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import java.util.List;
import org.opendaylight.yang.gen.v1.http.org.openroadm.service.rev250110.service.list.Services;

/**
 * Matcher evaluating dataprovider filter criteria against OpenROADM service
 * list entries. Reflection-free property dispatch; filter property names
 * mirror the RFC7951 wire names of the service fields.
 */
public class ServiceMatchFilter extends BaseDataproviderFilter {

    public static boolean matchFilter(Services service, List<DataproviderInputFilter> filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        if (service == null) {
            //should never happen
            return true;
        }
        for (var f : filter) {
            if (!matchesProperty(service, f)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesProperty(Services service, DataproviderInputFilter filter) {
        switch (filter.getProperty()) {
            case "service-name":
                return filterMatches(service.getServiceName(), filter);
            case "common-id":
                return filterMatches(service.getCommonId(), filter);
            case "connection-type":
                return service.getConnectionType() != null
                        && filterMatches(service.getConnectionType().getName(), filter);
            case "lifecycle-state":
                return service.getLifecycleState() != null
                        && filterMatches(service.getLifecycleState().getName(), filter);
            case "administrative-state":
                return service.getAdministrativeState() != null
                        && filterMatches(service.getAdministrativeState().getName(), filter);
            case "operational-state":
                return service.getOperationalState() != null
                        && filterMatches(service.getOperationalState().getName(), filter);
            case "service-a-end.node-id":
                return service.getServiceAEnd() != null
                        && service.getServiceAEnd().getNodeId() != null
                        && filterMatches(service.getServiceAEnd().getNodeId().getValue(), filter);
            case "service-a-end.clli":
                return service.getServiceAEnd() != null
                        && filterMatches(service.getServiceAEnd().getClli(), filter);
            case "service-z-end.node-id":
                return service.getServiceZEnd() != null
                        && service.getServiceZEnd().getNodeId() != null
                        && filterMatches(service.getServiceZEnd().getNodeId().getValue(), filter);
            case "service-z-end.clli":
                return service.getServiceZEnd() != null
                        && filterMatches(service.getServiceZEnd().getClli(), filter);
            default:
                return true; // unknown property: no filtering
        }
    }

}
