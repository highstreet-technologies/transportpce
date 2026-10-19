/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.nbinotifications.ves;

import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.transportpce.nbinotifications.producer.Publisher;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic DataTreeChangeListener publishing MD-SAL data tree changes as
 * ONAP VES notification events.
 *
 * <p>Instead of defining dedicated YANG notifications per event type, the
 * original data objects exactly as stored in MD-SAL are wrapped into
 * {@link VesEvent} and forwarded to a Kafka publisher using
 * {@link VesEventSerializer}. Modification types are mapped to VES
 * changeTypes as follows:
 *
 * <ul>
 *   <li>WRITE on a new object -&gt; created</li>
 *   <li>WRITE / SUBTREE_MODIFIED on an existing object -&gt; updated</li>
 *   <li>DELETE -&gt; deleted</li>
 * </ul>
 *
 * @param <T> type of the observed data object
 */
public class DataTreeChangeToVesListener<T extends DataObject> implements DataTreeChangeListener<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DataTreeChangeToVesListener.class);

    private final Publisher<VesEvent<? extends DataObject>> publisher;
    private final String topic;
    private final String sourceName;

    /**
     * Listener publishing all observed changes of the observed data tree to
     * the given topic.
     *
     * @param publisher publisher to send the events to
     * @param topic kafka topic
     * @param sourceName value of the VES commonEventHeader.sourceName
     */
    public DataTreeChangeToVesListener(Publisher<VesEvent<? extends DataObject>> publisher, String topic,
            String sourceName) {
        this.publisher = publisher;
        this.topic = topic;
        this.sourceName = sourceName;
    }

    @Override
    public void onDataTreeChanged(@NonNull List<DataTreeModification<T>> changes) {
        for (DataTreeModification<T> change : changes) {
            DataObjectModification<T> rootNode = change.getRootNode();
            VesEvent<T> event = toEvent(change.path(), rootNode);
            if (event == null) {
                continue;
            }
            LOG.info("Publishing VES event '{}' for object '{}'", event.getChangeType(),
                    event.getDataObject().implementedInterface().getSimpleName());
            publisher.sendEvent((VesEvent) event, topic);
        }
    }

    private VesEvent<T> toEvent(DataObjectIdentifier<T> path, DataObjectModification<T> rootNode) {
        T dataBefore = rootNode.dataBefore();
        T dataAfter = rootNode.dataAfter();
        VesEvent.ChangeType changeType;
        T dataObject;
        switch (rootNode.modificationType()) {
            case WRITE:
                if (dataBefore == null) {
                    changeType = VesEvent.ChangeType.CREATED;
                    dataObject = dataAfter;
                } else {
                    changeType = VesEvent.ChangeType.UPDATED;
                    dataObject = dataAfter;
                }
                break;
            case SUBTREE_MODIFIED:
                changeType = VesEvent.ChangeType.UPDATED;
                dataObject = dataAfter;
                break;
            case DELETE:
                changeType = VesEvent.ChangeType.DELETED;
                dataObject = dataBefore;
                break;
            default:
                LOG.debug("Ignoring modification type {}", rootNode.modificationType());
                return null;
        }
        if (dataObject == null) {
            LOG.warn("Data object of modification is null, skipping event");
            return null;
        }
        return new VesEvent<>(changeType, path, dataObject, sourceName);
    }
}
