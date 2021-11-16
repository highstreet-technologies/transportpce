/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * ============LICENSE_START=======================================================
 * ONAP : ccsdk features
 * ================================================================================
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.onap.ccsdk.features.sdnr.wt.websocketmanager.model.data.ReducedSchemaInfo;
import org.onap.ccsdk.features.sdnr.wt.yang.mapper.YangToolsMapperHelper;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.DateAndTime;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.devicemanager.rev190109.ObjectCreationNotification;
import org.opendaylight.yangtools.yang.binding.Notification;

public class NotificationInput<T extends Notification> {

    private static final String CONTROLLER_NAMESPACE = ObjectCreationNotification.QNAME.getNamespace().toString();
    private DateAndTime eventTime;
    private T data;
    private String nodeId;
    private ReducedSchemaInfo type;


    public NotificationInput() {

    }

    public NotificationInput(NotificationInputBase base, T notification) {
        this.eventTime = base.getEventTime();
        this.nodeId = base.getNodeId();
        this.type = base.getType();
        this.data = notification;
    }

    public DateAndTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(DateAndTime eventTime) {
        this.eventTime = eventTime;
    }

    public T getData() {
        return data;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public ReducedSchemaInfo getType() {
        return this.type;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setType(ReducedSchemaInfo type) {
        this.type = type;
    }

    public NotificationInput(T notification, String nodeId, ReducedSchemaInfo type, DateAndTime eventTime) {
        this.data = notification;
        this.nodeId = nodeId;
        this.eventTime = eventTime;
        this.type = type;
    }

    @JsonIgnore
    public boolean isDataType(Class<?> clazz) {
        return this.data != null && YangToolsMapperHelper.implementsInterface(this.data.getClass(), clazz);
    }

    @JsonIgnore
    public boolean isControllerNotification() {
        return this.type != null && CONTROLLER_NAMESPACE.equals(this.type.getNamespace());
    }

}
