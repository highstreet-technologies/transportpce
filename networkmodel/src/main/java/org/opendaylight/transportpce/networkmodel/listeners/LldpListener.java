/*
 * Copyright © 2017 AT&T and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.transportpce.networkmodel.listeners;

import org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev161014.LldpNbrInfoChange;
import org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev161014.OrgOpenroadmLldpListener;
import org.opendaylight.yang.gen.v1.http.org.openroadm.resource.types.rev161014.ResourceNotificationType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LldpListener implements OrgOpenroadmLldpListener {

    private static final Logger LOG = LoggerFactory.getLogger(LldpListener.class);
    //private final R2RLinkDiscovery linkDiscovery;
    private final NodeId nodeId;

    public LldpListener(final String nodeId) {
        this.nodeId = new NodeId(nodeId);
    }

    /**
     * Callback for lldp-nbr-info-change.
     * @param notification LldpNbrInfoChange object
     */
    @Override
    public void onLldpNbrInfoChange(LldpNbrInfoChange notification) {
        LOG.info("Notification {} received {}", LldpNbrInfoChange.QNAME, notification);
        if (notification.getNotificationType().equals(ResourceNotificationType.ResourceCreation)) {
            /*linkDiscovery.createR2RLink(nodeId,notification.getResource(),
                                                notification.getNbrInfo().getRemoteSysName(),
                                                notification.getNbrInfo().getRemotePortId());*/
        } else if (notification.getNotificationType().equals(ResourceNotificationType.ResourceDeletion)) {
            /*linkDiscovery.deleteR2RLink(nodeId,notification.getResource(),
                                                notification.getNbrInfo().getRemoteSysName(),
                                                notification.getNbrInfo().getRemotePortId());*/
        }
    }
}
