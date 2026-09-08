/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.ves;

import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventHeader;
import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventHeader.Domain;
import org.opendaylight.transportpce.devicediscovery.model.ves.Event;
import org.opendaylight.transportpce.devicediscovery.model.ves.NotificationFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience wrapper around the generated VES Event POJO.
 *
 * Provides type-safe access to the fields relevant for device discovery:
 * - reportingEntityId (controller UUID) from commonEventHeader
 * - changeIdentifier (device node ID) from notificationFields
 * - newState (connecting / connected / disconnected) from notificationFields
 */
public class VesEventWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(VesEventWrapper.class);

    private Event event;

    public VesEventWrapper(){

    }
    public VesEventWrapper(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    /**
     * Check whether this VES event is a notification-domain event.
     *
     * @return true if domain == NOTIFICATION
     */
    public boolean isNotification() {
        CommonEventHeader header = getHeader();
        if (header == null) {
            return false;
        }
        return header.getDomain() == Domain.NOTIFICATION;
    }

    /**
     * Validate that all fields required for device-discovery processing are present.
     *
     * @return true if the event can be processed
     */
    public boolean assertFields() {
        if (event == null) {
            LOG.warn("invalid ves event: event is null");
            return false;
        }
        CommonEventHeader header = getHeader();
        if (header == null) {
            LOG.warn("invalid ves event: commonEventHeader is null");
            return false;
        }
        if (header.getReportingEntityId() == null || header.getReportingEntityId().isBlank()) {
            LOG.warn("invalid ves event: reportingEntityId is null or empty");
            return false;
        }
        NotificationFields fields = event.getNotificationFields();
        if (fields == null) {
            LOG.warn("invalid ves event: notificationFields is null");
            return false;
        }
        if (fields.getChangeIdentifier() == null || fields.getChangeIdentifier().isBlank()) {
            LOG.warn("invalid ves event: changeIdentifier is null or empty");
            return false;
        }
        if (fields.getNewState() == null || fields.getNewState().isBlank()) {
            LOG.warn("invalid ves event: newState is null or empty");
            return false;
        }
        return true;
    }

    /**
     * Get the reportingEntityId (controller UUID) from the commonEventHeader.
     *
     * @return controller UUID or null
     */
    public String getControllerUuid() {
        CommonEventHeader header = getHeader();
        return header != null ? header.getReportingEntityId() : null;
    }

    /**
     * Get the changeIdentifier (device node ID) from notificationFields.
     *
     * @return device node ID or null
     */
    public String getNodeId() {
        NotificationFields fields = event != null ? event.getNotificationFields() : null;
        return fields != null ? fields.getChangeIdentifier() : null;
    }

    /**
     * Get the newState from notificationFields.
     *
     * @return newState string (connecting / connected / disconnected) or null
     */
    public String getNewState() {
        NotificationFields fields = event != null ? event.getNotificationFields() : null;
        return fields != null ? fields.getNewState() : null;
    }

    private CommonEventHeader getHeader() {
        return event != null ? event.getCommonEventHeader() : null;
    }
}
