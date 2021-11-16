/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotificationInput;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;


//FIXME Spotbugs issue here
@SuppressFBWarnings(
    value = {"URF_UNREAD_FIELD", "UPM_UNCALLED_PRIVATE_METHOD"},
    justification = "waiting for more update")
public class RemoteDataTreeChangeProvider<N extends Node, D extends DataTreeChangeListener<N>> {

    private final RestconfHttpClient client;
    private final List<RemoteListenerRegistration<D, N>> listeners;

    public RemoteDataTreeChangeProvider(RestconfHttpClient restClient) {
        this.client = restClient;
        this.listeners = new ArrayList<>();
    }

    private final CloseCallback<D, N> closeCallback = new CloseCallback<D, N>() {

        @Override
        public void onClose(RemoteListenerRegistration<D, N> reg) {
            listeners.remove(reg);
        }
    };

    public void onControllerNotification(NotificationInput<?> notification) {
//        for (RemoteListenerRegistration<D, N> reg : this.listeners) {
//            @NonNull
//            Collection<DataTreeModification<N>> changes = Arrays
//                    .asList(this.createModification(notification));
//            //reg.getInstance().onDataTreeChanged(changes);
//        }
    }

    private DataTreeModification<N> createModification(NotificationInput<?> notification) {
        return new RemoteDataTreeModification<N>(notification);
    }

    public @NonNull ListenerRegistration<D> register(@NonNull DataTreeIdentifier<N> treeId,
            @NonNull D listener) {
        RemoteListenerRegistration<D, N> reg = new RemoteListenerRegistration<>(listener,
                this.closeCallback);
        this.listeners.add(reg);
        return reg;
    }

}
