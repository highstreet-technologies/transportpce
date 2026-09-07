/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.devicediscovery.ves;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventFormat3021ONAP;
import org.opendaylight.transportpce.devicediscovery.model.ves.CommonEventHeader;
import org.opendaylight.transportpce.devicediscovery.model.ves.Event;
import org.opendaylight.transportpce.devicediscovery.model.ves.NotificationFields;

class VesEventWrapperTest {

    @Test
    void testIsNotificationWithNotificationDomain() {
        Event event = createNotificationEvent("ctrl-uuid-1", "node-1", "connected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertTrue(wrapper.isNotification());
    }

    @Test
    void testIsNotificationWithFaultDomain() {
        Event event = new Event();
        CommonEventHeader header = new CommonEventHeader();
        header.setDomain(CommonEventHeader.Domain.FAULT);
        event.setCommonEventHeader(header);
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertFalse(wrapper.isNotification());
    }

    @Test
    void testIsNotificationWithNullEvent() {
        VesEventWrapper wrapper = new VesEventWrapper(null);
        assertFalse(wrapper.isNotification());
    }

    @Test
    void testIsNotificationWithNullHeader() {
        VesEventWrapper wrapper = new VesEventWrapper(new Event());
        assertFalse(wrapper.isNotification());
    }

    @Test
    void testAssertFieldsValid() {
        Event event = createNotificationEvent("ctrl-uuid-1", "node-1", "connected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertTrue(wrapper.assertFields());
    }

    @Test
    void testAssertFieldsNullEvent() {
        VesEventWrapper wrapper = new VesEventWrapper(null);
        assertFalse(wrapper.assertFields());
    }

    @Test
    void testAssertFieldsNullHeader() {
        VesEventWrapper wrapper = new VesEventWrapper(new Event());
        assertFalse(wrapper.assertFields());
    }

    @Test
    void testAssertFieldsMissingReportingEntityId() {
        Event event = createNotificationEvent(null, "node-1", "connected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertFalse(wrapper.assertFields());
    }

    @Test
    void testAssertFieldsBlankReportingEntityId() {
        Event event = createNotificationEvent("", "node-1", "connected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertFalse(wrapper.assertFields());
    }

    @Test
    void testAssertFieldsMissingChangeIdentifier() {
        Event event = createNotificationEvent("ctrl-uuid-1", null, "connected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertFalse(wrapper.assertFields());
    }

    @Test
    void testAssertFieldsMissingNewState() {
        Event event = createNotificationEvent("ctrl-uuid-1", "node-1", null);
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertFalse(wrapper.assertFields());
    }

    @Test
    void testAssertFieldsNullNotificationFields() {
        Event event = new Event();
        CommonEventHeader header = new CommonEventHeader();
        header.setDomain(CommonEventHeader.Domain.NOTIFICATION);
        header.setReportingEntityId("ctrl-uuid-1");
        event.setCommonEventHeader(header);
        // notificationFields not set
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertFalse(wrapper.assertFields());
    }

    @Test
    void testGetControllerUuid() {
        Event event = createNotificationEvent("ctrl-uuid-1", "node-1", "connected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertEquals("ctrl-uuid-1", wrapper.getControllerUuid());
    }

    @Test
    void testGetControllerUuidNullHeader() {
        VesEventWrapper wrapper = new VesEventWrapper(new Event());
        assertNull(wrapper.getControllerUuid());
    }

    @Test
    void testGetNodeId() {
        Event event = createNotificationEvent("ctrl-uuid-1", "node-1", "connected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertEquals("node-1", wrapper.getNodeId());
    }

    @Test
    void testGetNewState() {
        Event event = createNotificationEvent("ctrl-uuid-1", "node-1", "disconnected");
        VesEventWrapper wrapper = new VesEventWrapper(event);
        assertEquals("disconnected", wrapper.getNewState());
    }

    private Event createNotificationEvent(String reportingEntityId, String changeIdentifier, String newState) {
        Event event = new Event();
        CommonEventHeader header = new CommonEventHeader();
        header.setDomain(CommonEventHeader.Domain.NOTIFICATION);
        if (reportingEntityId != null) {
            header.setReportingEntityId(reportingEntityId);
        }
        event.setCommonEventHeader(header);

        NotificationFields fields = new NotificationFields();
        if (changeIdentifier != null) {
            fields.setChangeIdentifier(changeIdentifier);
        }
        if (newState != null) {
            fields.setNewState(newState);
        }
        event.setNotificationFields(fields);

        return event;
    }
}
