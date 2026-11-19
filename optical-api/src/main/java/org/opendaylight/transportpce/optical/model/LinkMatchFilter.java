/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import java.util.List;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.Destination;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.link.Source;
import org.opendaylight.yang.gen.v1.urn.opendaylight.transportpce.device.discovery.rev260907.Link1;

public class LinkMatchFilter extends BaseDataproviderFilter {

    public static boolean matchFilter(Link link, List<DataproviderInputFilter> filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        if (link == null) {
            //should never happen
            return true;
        }
        var aug = link.augmentation(Link1.class);
        for (var f : filter) {
            switch (f.getProperty()) {
                case "link-id": {
                    if (!filterMatches(link.getLinkId().getValue(), f)) {
                        return false;
                    }
                    break;
                }
                case "device-discovery:uuid":
                    if (aug == null) {
                        return false;
                    }
                    if (!filterMatches(aug.getUuid().getValue(), f)) {
                        return false;
                    }
                    break;
                case "source": {
                    if (!filterMatches(link.getSource(), f)) {
                        return false;
                    }
                    break;
                }
                case "destination": {
                    if (!filterMatches(link.getDestination(), f)) {
                        return false;
                    }
                    break;
                }
            }
        }
        return true;

    }

    private static boolean filterMatches(Destination destination, DataproviderInputFilter filter) {
        return true;
    }

    private static boolean filterMatches(Source source, DataproviderInputFilter filter) {
        return true;
    }


}
