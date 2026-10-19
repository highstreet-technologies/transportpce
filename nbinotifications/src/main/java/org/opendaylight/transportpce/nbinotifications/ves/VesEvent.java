/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.nbinotifications.ves;

import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;

/**
 * Envelope carrying an MD-SAL data object to be published as an ONAP VES
 * notification event.
 *
 * <p>Holds the original data object exactly as stored in MD-SAL (no extra
 * YANG mapping) plus the change type observed by a DataTreeChangeListener.
 *
 * @param <T> type of the wrapped data object
 */
public class VesEvent<T extends DataObject> {

    public enum ChangeType {
        CREATED("created"),
        UPDATED("updated"),
        DELETED("deleted");

        private final String value;

        ChangeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final ChangeType changeType;
    private final DataObjectIdentifier<T> identifier;
    private final T dataObject;
    private final String sourceName;

    public VesEvent(ChangeType changeType, DataObjectIdentifier<T> identifier, T dataObject, String sourceName) {
        this.changeType = changeType;
        this.identifier = identifier;
        this.dataObject = dataObject;
        this.sourceName = sourceName;
    }

    /**
     * Change type of the data tree modification (created / updated / deleted).
     *
     * @return the change type
     */
    public ChangeType getChangeType() {
        return changeType;
    }

    /**
     * Identifier of the data object within its data tree.
     *
     * @return the data object identifier
     */
    public DataObjectIdentifier<T> getIdentifier() {
        return identifier;
    }

    /**
     * The original data object as stored in MD-SAL (dataBefore for deleted
     * objects, dataAfter for created / updated objects).
     *
     * @return the data object
     */
    public T getDataObject() {
        return dataObject;
    }

    /**
     * Name of the entity that reported the change, e.g. the network element
     * or application holding the data. Used as VES sourceName.
     *
     * @return the source name
     */
    public String getSourceName() {
        return sourceName;
    }
}
