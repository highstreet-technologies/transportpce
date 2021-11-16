/*
 * Copyright (C) 2021 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2021 highstreet technologies GmbH Intellectual Property.
 * All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.onap.ccsdk.features.sdnr.wt.yang.mapper.YangToolsMapper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.AttributeValueChangedNotification;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ObjectCreationNotification;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ObjectDeletionNotification;
import org.opendaylight.yangtools.yang.binding.Notification;
import org.opendaylight.yangtools.yang.common.QName;

public class SdnrNotificationMapper extends YangToolsMapper {

    private static final long serialVersionUID = 1L;

    private final Map<QName, Class<? extends Notification>> notificationClasses;

    public SdnrNotificationMapper() {
        super();
        this.notificationClasses = new HashMap<>();
        this.notificationClasses.put(ObjectCreationNotification.QNAME, ObjectCreationNotification.class);
        this.notificationClasses.put(ObjectDeletionNotification.QNAME, ObjectDeletionNotification.class);
        this.notificationClasses.put(AttributeValueChangedNotification.QNAME, AttributeValueChangedNotification.class);
    }

    public <T extends Notification> NotificationInput<T> readNotification(String value)
            throws JsonMappingException, JsonProcessingException {
        NotificationInputBase base = this.readValue(value, NotificationInputBase.class);
        for (Entry<QName, Class<? extends Notification>> entry : this.notificationClasses.entrySet()) {
            if (base.getType() != null && base.getType().equals(entry.getKey())) {
                @SuppressWarnings("unchecked")
                T notification = (T) super.readValue(base.getData().toString(), entry.getValue());
                return new NotificationInput<T>(base, notification);
            }
        }
        return null;
    }

    public <T extends Notification> NotificationInput<T> readNotification(File value)
            throws IOException {
        NotificationInputBase base = this.readValue(value, NotificationInputBase.class);
        for (Entry<QName, Class<? extends Notification>> entry : this.notificationClasses.entrySet()) {
            if (base.getType().equals(entry.getKey())) {
                @SuppressWarnings("unchecked")
                T notification = (T) super.readValue(base.getData().toString(), entry.getValue());
                return new NotificationInput<T>(base, notification);
            }
        }
        return null;
    }

    public void registerNotification(QName qname, Class<? extends Notification> clazz) {
        this.notificationClasses.put(qname, clazz);
    }

    public void unregisterNotification(QName qname) {
        this.notificationClasses.remove(qname);
    }
}
