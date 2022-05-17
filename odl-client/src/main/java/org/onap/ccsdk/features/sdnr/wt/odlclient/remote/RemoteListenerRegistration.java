/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote;

import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;

public class RemoteListenerRegistration<L extends DataTreeChangeListener<T>, T extends Node>
        implements ListenerRegistration<L> {

    private @NonNull L instance;
    private CloseCallback<L, T> callback;

    public RemoteListenerRegistration(@NonNull L listener, CloseCallback<L, T> cb) {
        this.instance = listener;
        this.callback = cb;
    }

    @Override
    public @NonNull L getInstance() {
        return this.instance;
    }

    @Override
    public void close() {
        if (this.callback != null) {
            this.callback.onClose(this);
        }

    }

}
