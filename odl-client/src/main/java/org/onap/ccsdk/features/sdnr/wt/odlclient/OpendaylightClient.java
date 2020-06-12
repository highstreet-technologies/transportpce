/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.api.Session;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig.AuthMethod;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.RemoteOpendaylightClient;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.SdnrNotification;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.RemoteDataBroker;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.RemoteDataTreeChangeProvider;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.RemoteDeviceDataBroker;
import org.onap.ccsdk.features.sdnr.wt.odlclient.remote.RemoteMountPoint;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.onap.ccsdk.features.sdnr.wt.odlclient.ws.SdnrWebsocketCallback;
import org.onap.ccsdk.features.sdnr.wt.odlclient.ws.SdnrWebsocketClient;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.MountPoint;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeConnectionStatus.ConnectionStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpendaylightClient implements AutoCloseable, RemoteOpendaylightClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpendaylightClient.class);
    private static final InstanceIdentifier<Topology> NETCONF_TOPO_IID = InstanceIdentifier
            .create(NetworkTopology.class).child(Topology.class,
                    new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())));
    private static final boolean TRUSTALLCERTS = false;
    private final RestconfHttpClient restClient;
    private final SdnrWebsocketClient wsClient;
    private final SdnrWebsocketCallback wsCallback = new SdnrWebsocketCallback() {

        @Override
        public void onMessageReceived(String msg) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(Throwable cause) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDisconnect(int statusCode, String reason) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onConnect(Session lsession) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNotificationReceived(SdnrNotification notification) {
            if(notification.isControllerNotification()) {
                dataTreeChangeProvider.onControllerNotification(notification);
            }

        }
    };
    private final DataBroker dataBroker;
    private final Map<String, DataBroker> deviceDataBrokers;
    private final RemoteDataTreeChangeProvider<Node> dataTreeChangeProvider;
    private final RemoteOdlConfig config;

    public OpendaylightClient() throws Exception {
        this.config = new RemoteOdlConfig();
        if (this.config.isEnabled()) {
            this.restClient = new RestconfHttpClient(this.config.getBaseUrl(),
                    this.config.trustAllCerts(), this.config.getAuthenticationMethod(),
                    this.config.getCredentialUsername(), this.config.getCredentialPassword());
            this.wsClient = this.config.getWebsocketUrl() == null ? null
                    : new SdnrWebsocketClient(this.config.getWebsocketUrl(), this.wsCallback);
            if (this.wsClient != null) {
                this.wsClient.start();

            }
            this.dataBroker = new RemoteDataBroker(this.restClient);
        } else {
            this.restClient = null;
            this.dataBroker = null;
            this.wsClient = null;

        }
        this.dataTreeChangeProvider = new RemoteDataTreeChangeProvider<>(this.restClient);
        this.deviceDataBrokers = new HashMap<>();
    }

    public OpendaylightClient(String baseUrl, @Nullable String wsUrl, AuthMethod authMethod,
            String username, String password) throws Exception {
        this.config = null;
        this.restClient = new RestconfHttpClient(baseUrl, TRUSTALLCERTS, authMethod, username, password);
        this.wsClient = wsUrl == null ? null : new SdnrWebsocketClient(wsUrl, this.wsCallback);
        this.dataBroker = new RemoteDataBroker(this.restClient);
        this.dataTreeChangeProvider = new RemoteDataTreeChangeProvider<>(this.restClient);
        this.deviceDataBrokers = new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        if (this.wsClient != null) {
            if (!this.wsClient.isStopped()) {
                this.wsClient.stop();
            }
        }
    }

    @Override
    public boolean isDevicePresent(String nodeId) {

        InstanceIdentifier<NetconfNode> iif = NETCONF_TOPO_IID
                .child(Node.class, new NodeKey(new NodeId(nodeId))).augmentation(NetconfNode.class);
        try {
            @NonNull
            Optional<NetconfNode> node = this.restClient.read(LogicalDatastoreType.OPERATIONAL, iif)
                    .get();
            return node.isPresent() ? node.get().getConnectionStatus() == ConnectionStatus.Connected
                    : false;
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException | IOException | InterruptedException
                | ExecutionException e) {
            LOG.warn("unable to read netconfnode {}:", nodeId, e);

        }
        return false;
    }

    @Override
    public DataBroker getRemoteDeviceDataBroker(String nodeId) {
        DataBroker broker = this.deviceDataBrokers.get(nodeId);
        if (broker == null) {
            broker = new RemoteDeviceDataBroker(this.restClient, nodeId);
            this.deviceDataBrokers.put(nodeId, broker);
        }
        return broker;
    }

    @Override
    public DataBroker getRemoteDataBroker() {
        return this.dataBroker;
    }

    @Override
    public MountPoint getMountPoint(String deviceId) {
        return new RemoteMountPoint(this.restClient, deviceId);
    }

    @Override
    public boolean isEnabled() {
        return this.config == null ? false : this.config.isEnabled();
    }

    @Override
    public <L extends DataTreeChangeListener<Node>> @NonNull ListenerRegistration<L> registerDataTreeChangeListener(
            @NonNull DataTreeIdentifier<Node> treeId, @NonNull L listener) {
        return this.dataTreeChangeProvider.register(treeId, listener);
    }



}
