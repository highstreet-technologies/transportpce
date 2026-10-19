/*
 * Copyright © 2021 Nokia, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.nbinotifications.utils;

import java.util.HashMap;
import java.util.Map;
import org.opendaylight.transportpce.common.converter.JsonStringConverter;
import org.opendaylight.transportpce.nbinotifications.listener.NbiNotificationsHandler;
import org.opendaylight.transportpce.nbinotifications.producer.Publisher;
import org.opendaylight.transportpce.nbinotifications.serialization.ConfigConstants;
import org.opendaylight.transportpce.nbinotifications.serialization.NotificationAlarmServiceSerializer;
import org.opendaylight.transportpce.nbinotifications.serialization.NotificationServiceSerializer;
import org.opendaylight.transportpce.nbinotifications.serialization.TapiNotificationSerializer;
import org.opendaylight.transportpce.nbinotifications.ves.VesEvent;
import org.opendaylight.transportpce.nbinotifications.ves.VesEventSerializer;
import org.opendaylight.yang.gen.v1.nbi.notifications.rev230728.NotificationAlarmService;
import org.opendaylight.yang.gen.v1.nbi.notifications.rev230728.NotificationProcessService;
import org.opendaylight.yang.gen.v1.nbi.notifications.rev230728.NotificationTapiService;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.data.codec.spi.BindingDOMCodecServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TopicManager {

    private static final Logger LOG = LoggerFactory.getLogger(TopicManager.class);
    private static TopicManager instance = new TopicManager();

    private Map<String, Publisher<VesEvent<? extends DataObject>>> vesPublisherMap = new HashMap<>();
    private String vesReportingEntityId = "";
    private String vesReportingEntityName = "transportpce";

    private Map<String, Publisher<NotificationTapiService>> tapiPublisherMap = new HashMap<>();
    private String publisherServer;
    private JsonStringConverter<NotificationTapiService> tapiConverter;
    private NbiNotificationsHandler nbiNotificationsListener;
    private Map<String, Publisher<NotificationAlarmService>> alarmPublisherMap = new HashMap<>();
    private Map<String, Publisher<NotificationProcessService>> processPublisherMap = new HashMap<>();
    private JsonStringConverter<NotificationProcessService> processConverter;
    private JsonStringConverter<NotificationAlarmService> alarmConverter;

    private TopicManager() {
    }

    public static TopicManager getInstance() {
        return instance;
    }

    public void setNbiNotificationsListener(NbiNotificationsHandler nbiNotificationsListener) {
        this.nbiNotificationsListener = nbiNotificationsListener;
    }

    public void setProcessConverter(JsonStringConverter<NotificationProcessService> processConverter) {
        this.processConverter = processConverter;
    }

    public void setAlarmConverter(JsonStringConverter<NotificationAlarmService> alarmConverter) {
        this.alarmConverter = alarmConverter;
    }

    public void setTapiConverter(JsonStringConverter<NotificationTapiService> tapiConverter) {
        this.tapiConverter = tapiConverter;
    }

    public void setPublisherServer(String publisherServer) {
        this.publisherServer = publisherServer;
    }

    public void addProcessTopic(String topic) {
        LOG.info("Adding process topic: {}", topic);
        processPublisherMap.put(topic, new Publisher<>(topic, publisherServer, processConverter,
            NotificationServiceSerializer.class));
        if (this.nbiNotificationsListener != null) {
            this.nbiNotificationsListener.setPublishersServiceMap(processPublisherMap);
        }
    }

    public void addAlarmTopic(String topic) {
        LOG.info("Adding alarm topic: {}", topic);
        alarmPublisherMap.put(topic, new Publisher<>(topic, publisherServer, alarmConverter,
                NotificationAlarmServiceSerializer.class));
        if (this.nbiNotificationsListener != null) {
            this.nbiNotificationsListener.setPublishersAlarmMap(alarmPublisherMap);
        }
    }

    public void addTapiTopic(String topic) {
        if (tapiPublisherMap.containsKey(topic)) {
            LOG.info("Tapi topic: {} already exists", topic);
            return;
        }
        LOG.info("Adding new tapi topic: {}", topic);
        tapiPublisherMap.put(topic, new Publisher<>(topic, publisherServer, tapiConverter,
            TapiNotificationSerializer.class));
        if (this.nbiNotificationsListener != null) {
            this.nbiNotificationsListener.setTapiPublishersMap(tapiPublisherMap);
        }
    }

    public void deleteTapiTopic(String topic) {
        if (!tapiPublisherMap.containsKey(topic)) {
            LOG.info("Tapi topic: {} doesnt exist", topic);
            return;
        }
        LOG.info("Deleting tapi topic: {}", topic);
        tapiPublisherMap.remove(topic);
        if (this.nbiNotificationsListener != null) {
            this.nbiNotificationsListener.setTapiPublishersMap(tapiPublisherMap);
        }
    }

    public Map<String, Publisher<NotificationTapiService>> getTapiTopicMap() {
        return this.tapiPublisherMap;
    }

    public Map<String, Publisher<NotificationAlarmService>> getAlarmTopicMap() {
        return this.alarmPublisherMap;
    }

    public Map<String, Publisher<NotificationProcessService>> getProcessTopicMap() {
        return this.processPublisherMap;
    }

    public void setVesReportingEntityId(String vesReportingEntityId) {
        this.vesReportingEntityId = vesReportingEntityId;
    }

    public void setVesReportingEntityName(String vesReportingEntityName) {
        this.vesReportingEntityName = vesReportingEntityName;
    }

    public void addVesTopic(String topic, BindingDOMCodecServices codecServices) {
        if (vesPublisherMap.containsKey(topic)) {
            LOG.info("Ves topic: {} already exists", topic);
            return;
        }
        LOG.info("Adding new ves topic: {}", topic);
        Map<String, Object> serializerConfigs = Map.of(
            ConfigConstants.CONVERTER, codecServices,
            ConfigConstants.REPORTING_ENTITY_ID, vesReportingEntityId,
            ConfigConstants.REPORTING_ENTITY_NAME, vesReportingEntityName);
        vesPublisherMap.put(topic, new Publisher<>(topic, publisherServer, codecServices,
            VesEventSerializer.class, serializerConfigs));
    }

    public Map<String, Publisher<VesEvent<? extends DataObject>>> getVesTopicMap() {
        return this.vesPublisherMap;
    }
}
