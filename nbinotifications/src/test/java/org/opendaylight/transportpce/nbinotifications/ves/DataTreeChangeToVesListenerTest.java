/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.nbinotifications.ves;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opendaylight.transportpce.nbinotifications.ves.VesEventSerializerTest.LINK_IID;

import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.binding.api.DataObjectModification.ModificationType;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.transportpce.nbinotifications.producer.Publisher;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.topology.rev180226.networks.network.Link;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;

@Disabled
public class DataTreeChangeToVesListenerTest {

    @SuppressWarnings("unchecked")
    private DataTreeModification<Link> mockModification(ModificationType type, Link before, Link after,
            DataObjectIdentifier<Link> path) {
        DataTreeModification<Link> modification = mock(DataTreeModification.class);
        DataObjectModification<Link> rootNode = mock(DataObjectModification.class);
        when(rootNode.modificationType()).thenReturn(type);
        when(rootNode.dataBefore()).thenReturn(before);
        when(rootNode.dataAfter()).thenReturn(after);
        when(modification.getRootNode()).thenReturn(rootNode);
        when(modification.path()).thenReturn(path);
        return modification;
    }

    @Test
    @SuppressWarnings("unchecked")
    void createdEventTest() {
        Publisher<VesEvent<?>> publisher = mock(Publisher.class);
        DataTreeChangeToVesListener<Link> listener =
                new DataTreeChangeToVesListener<>(publisher, "ves-topic", "networkmodel");
        Link link = mock(Link.class);
        DataObjectIdentifier<Link> path = LINK_IID;

        List<DataTreeModification<Link>> changes = List.of(
            mockModification(ModificationType.WRITE, null, link, path));

        listener.onDataTreeChanged(changes);

        verify(publisher, times(1)).sendEvent(any(VesEvent.class), eq("ves-topic"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void updatedEventTest() {
        Publisher<VesEvent<?>> publisher = mock(Publisher.class);
        DataTreeChangeToVesListener<Link> listener =
                new DataTreeChangeToVesListener<>(publisher, "ves-topic", "networkmodel");
        Link linkBefore = mock(Link.class);
        Link linkAfter = mock(Link.class);
        DataObjectIdentifier<Link> path = LINK_IID;

        List<DataTreeModification<Link>> changes = List.of(
            mockModification(ModificationType.WRITE, linkBefore, linkAfter, path));

        listener.onDataTreeChanged(changes);

        verify(publisher, times(1)).sendEvent(any(VesEvent.class), eq("ves-topic"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void deletedEventTest() {
        Publisher<VesEvent<?>> publisher = mock(Publisher.class);
        DataTreeChangeToVesListener<Link> listener =
                new DataTreeChangeToVesListener<>(publisher, "ves-topic", "networkmodel");
        Link link = mock(Link.class);
        DataObjectIdentifier<Link> path = LINK_IID;

        List<DataTreeModification<Link>> changes = List.of(
            mockModification(ModificationType.DELETE, link, null, path));

        listener.onDataTreeChanged(changes);

        verify(publisher, times(1)).sendEvent(any(VesEvent.class), eq("ves-topic"));
    }
}
