/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.InvalidObjectException;
import java.util.List;
import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventHeader;
import org.opendaylight.transportpce.devicediscovery.model.ves.Event;
import org.opendaylight.transportpce.devicediscovery.model.ves.NotificationFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience wrapper around the generated VES Event POJO.
 * <p>
 * Provides type-safe access to the fields relevant for device discovery: - reportingEntityId (controller UUID) from
 * commonEventHeader - changeIdentifier (device node ID) from notificationFields - newState (connecting / connected /
 * disconnected) from notificationFields
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VesEventWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(VesEventWrapper.class);
    public static final String NOTIFICATION_CHANGETYPE_CONNECTIONSTATE = "ConnectionState";
    public static final String NOTIFICATION_CHANGETYPE_CREATED = "created";
    public static final String NOTIFICATION_CHANGETYPE_UPDATED = "updated";
    public static final String NOTIFICATION_CHANGETYPE_DELETED = "deleted";
    public static final String NOTIFICATION_CHANGETYPE_UUIDGEN = "uuidgen";
    private static final List<String> ENTITYCHANGE_CHANGETYPES = List.of(NOTIFICATION_CHANGETYPE_CREATED,
            NOTIFICATION_CHANGETYPE_UPDATED, NOTIFICATION_CHANGETYPE_DELETED);
    private Event event;

    public VesEventWrapper() {

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
    @JsonIgnore
    public boolean isPnfRegistration() {
        return this.isDomain(CommonEventHeader.Domain.PNF_REGISTRATION);
    }

    @JsonIgnore
    public boolean isNotification() {
        return this.isDomain(CommonEventHeader.Domain.NOTIFICATION);
    }


    @JsonIgnore
    public boolean isConnectionStateChangeNotification() {
        if (!isNotification()) {
            return false;
        }
        var fields = this.getEvent().getNotificationFields();
        var changeType = fields != null ? fields.getChangeType() : null;
        return NOTIFICATION_CHANGETYPE_CONNECTIONSTATE.equals(changeType);
    }

    @JsonIgnore
    public boolean isEntityNotification() {
        final var fields = this.event.getNotificationFields();
        final var changeType = fields != null ? fields.getChangeType() : null;
        if (changeType == null) {
            return false;
        }
        return ENTITYCHANGE_CHANGETYPES.contains(changeType);
    }

    @JsonIgnore
    public boolean isEntityNotification(String expectedChangeType) {
        final var fields = this.event.getNotificationFields();
        final var changeType = fields != null ? fields.getChangeType() : null;
        return expectedChangeType.equals(changeType);
    }

    @JsonIgnore
    public boolean isUuidGenNotification() {
        if (!isNotification()) {
            return false;
        }
        final var fields = this.event.getNotificationFields();
        final var changeType = fields != null ? fields.getChangeType() : null;
        return NOTIFICATION_CHANGETYPE_UUIDGEN.equals(changeType);
    }

    @JsonIgnore
    private boolean isDomain(CommonEventHeader.Domain domain) {
        final var ev = this.getEvent();
        if (ev == null) {
            LOG.warn("invalid ves object: event is null");
            return false;
        }
        final var header = ev.getCommonEventHeader();
        if (header == null) {
            LOG.warn("invalid ves object: no event header found");
            return false;
        }
        return header.getDomain() == domain;
    }

    /**
     * Validate that all fields required for device-discovery processing are present.
     *
     * @return true if the event can be processed
     */
    @JsonIgnore
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
        if (this.isNotification()) {

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
        }
        return true;
    }

    /**
     * Get the reportingEntityId (controller UUID) from the commonEventHeader.
     *
     * @return controller UUID or null
     */
    @JsonIgnore
    public String getControllerUuid() {
        CommonEventHeader header = getHeader();
        return header != null ? header.getReportingEntityId() : null;
    }

    /**
     * Get the changeIdentifier (device node ID) from notificationFields.
     *
     * @return device node ID or null
     */
    @JsonIgnore
    public String getNodeId() {
        NotificationFields fields = event != null ? event.getNotificationFields() : null;
        return fields != null ? fields.getChangeIdentifier() : null;
    }

    /**
     * Get the newState from notificationFields.
     *
     * @return newState string (connecting / connected / disconnected) or null
     */
    @JsonIgnore
    public String getNewState() {
        NotificationFields fields = event != null ? event.getNotificationFields() : null;
        return fields != null ? fields.getNewState() : null;
    }

    @JsonIgnore
    private CommonEventHeader getHeader() {
        return event != null ? event.getCommonEventHeader() : null;
    }

    @JsonIgnore
    public UuidGenData getUuidGenData() throws InvalidObjectException {
        var notificationFields = this.event.getNotificationFields();
        var obj = notificationFields != null ? notificationFields.getAdditionalFields() : null;
        if (obj == null) {
            throw new InvalidObjectException("unable to find notification fields -> additionalFields");
        }
        if (!obj.hasNonNull("unm-network-topology:uuid")) {
            throw new InvalidObjectException("unable to detect uuid");
        }
        var uuid = obj.get("unm-network-topology:uuid").asText();
        var props = obj.properties();
        String key = null;
        String value = null;
        for (var prop : props) {
            if ("unm-network-topology:uuid".equals(prop.getKey())) {
                continue;
            }
            key = prop.getKey();
            value = prop.getValue().asText();
            break;
        }
        if (key == null || value == null) {
            throw new InvalidObjectException("unable to detect key value pair");
        }
        return new UuidGenData(uuid, key, value);
    }

    public boolean changeIdentifierIs(String changeIdentifier) {
        NotificationFields fields = event != null ? event.getNotificationFields() : null;
        var ci = fields != null ? fields.getChangeIdentifier() : null;
        return changeIdentifier.equals(ci);
    }

    public static class UuidGenData {

        private final String uuid;
        private final String key;
        private final String value;

        public UuidGenData(String uuid, String key, String value) {
            this.uuid = uuid;
            this.key = key;
            this.value = value;
        }

        public String getUuid() {
            return uuid;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
