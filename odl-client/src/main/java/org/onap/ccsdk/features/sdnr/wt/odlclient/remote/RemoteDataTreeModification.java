/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotificationInput;
import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;


//FIXME this class skeleton is almost empty
@SuppressFBWarnings(
    value = {"URF_UNREAD_FIELD"},
    justification = "Auto-generated class waiting to be filled")
public class RemoteDataTreeModification<N extends Node> implements DataTreeModification<N> {

    private final NotificationInput<?> source;

    public RemoteDataTreeModification(NotificationInput<?> notification) {
        this.source = notification;
    }

    @Override
    public @NonNull DataTreeIdentifier<N> getRootPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull DataObjectModification<N> getRootNode() {
        return null;
    }

}
