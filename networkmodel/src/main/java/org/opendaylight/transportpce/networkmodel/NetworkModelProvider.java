/*
 * Copyright © 2016 Orange and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.networkmodel;

<<<<<<< HEAD
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.RemoteOpendaylightClient;
=======
>>>>>>> standalone/stable/aluminium
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.DataTreeIdentifier;
import org.opendaylight.mdsal.binding.api.RpcProviderService;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.common.InstanceIdentifiers;
import org.opendaylight.transportpce.common.NetworkUtils;
import org.opendaylight.transportpce.common.network.NetworkTransactionService;
import org.opendaylight.transportpce.networkmodel.util.TpceNetwork;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.networkutils.rev170818.TransportpceNetworkutilsService;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.concepts.ObjectRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkModelProvider {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkModelProvider.class);

    private final DataBroker dataBroker;
    private final RpcProviderService rpcProviderService;
    private final TransportpceNetworkutilsService networkutilsService;
    private final NetConfTopologyListener topologyListener;
<<<<<<< HEAD
    private final RemoteOpendaylightClient odlClient;
=======
>>>>>>> standalone/stable/aluminium
    private ListenerRegistration<NetConfTopologyListener> dataTreeChangeListenerRegistration;
    private ObjectRegistration<TransportpceNetworkutilsService> networkutilsServiceRpcRegistration;
    private TpceNetwork tpceNetwork;

<<<<<<< HEAD

    public NetworkModelProvider(NetworkTransactionService networkTransactionService, final DataBroker dataBroker,
            final RpcProviderService rpcProviderService, final TransportpceNetworkutilsService networkutilsService,
            final NetConfTopologyListener topologyListener, RemoteOpendaylightClient odlClient) {
=======
    public NetworkModelProvider(NetworkTransactionService networkTransactionService, final DataBroker dataBroker,
        final RpcProviderService rpcProviderService, final TransportpceNetworkutilsService networkutilsService,
        final NetConfTopologyListener topologyListener) {
>>>>>>> standalone/stable/aluminium
        this.dataBroker = dataBroker;
        this.rpcProviderService = rpcProviderService;
        this.networkutilsService = networkutilsService;
        this.topologyListener = topologyListener;
        this.tpceNetwork = new TpceNetwork(networkTransactionService);
<<<<<<< HEAD
        this.odlClient = odlClient;
=======
>>>>>>> standalone/stable/aluminium
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        LOG.info("NetworkModelProvider Session Initiated");
        tpceNetwork.createLayer(NetworkUtils.CLLI_NETWORK_ID);
        tpceNetwork.createLayer(NetworkUtils.UNDERLAY_NETWORK_ID);
        tpceNetwork.createLayer(NetworkUtils.OVERLAY_NETWORK_ID);
        tpceNetwork.createLayer(NetworkUtils.OTN_NETWORK_ID);
        dataTreeChangeListenerRegistration =
<<<<<<< HEAD
                !this.odlClient.isEnabled()
                        ? dataBroker.registerDataTreeChangeListener(
                                DataTreeIdentifier.create(LogicalDatastoreType.OPERATIONAL,
                                        InstanceIdentifiers.NETCONF_TOPOLOGY_II.child(Node.class)),
                                topologyListener)
                        : this.odlClient
                                .registerDataTreeChangeListener(
                                        DataTreeIdentifier.create(LogicalDatastoreType.OPERATIONAL,
                                                InstanceIdentifiers.NETCONF_TOPOLOGY_II.child(Node.class)),
                                        topologyListener);
        LOG.info("running transportPCE netconf functions remotely: {}", this.odlClient.isEnabled());
        if (this.odlClient.isEnabled()) {
            this.odlClient.registerDeviceConnectionChangeListener(topologyListener);
        }
        networkutilsServiceRpcRegistration = rpcProviderService
                .registerRpcImplementation(TransportpceNetworkutilsService.class, networkutilsService);
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
=======
            dataBroker.registerDataTreeChangeListener(DataTreeIdentifier.create(LogicalDatastoreType.OPERATIONAL,
                InstanceIdentifiers.NETCONF_TOPOLOGY_II.child(Node.class)), topologyListener);
        networkutilsServiceRpcRegistration =
            rpcProviderService.registerRpcImplementation(TransportpceNetworkutilsService.class, networkutilsService);
    }

        /**
         * Method called when the blueprint container is destroyed.
         */
>>>>>>> standalone/stable/aluminium
    public void close() {
        LOG.info("NetworkModelProvider Closed");
        if (dataTreeChangeListenerRegistration != null) {
            dataTreeChangeListenerRegistration.close();
        }
        if (networkutilsServiceRpcRegistration != null) {
            networkutilsServiceRpcRegistration.close();
        }
        this.odlClient.unregisterDeviceConnectionChangeListener(topologyListener);
    }
}
