/*
 * Copyright © 2019 Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.tapi.utils;

<<<<<<< HEAD
import edu.umd.cs.findbugs.annotations.NonNull;
=======
import com.google.common.util.concurrent.FluentFuture;
>>>>>>> standalone/stable/aluminium
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
<<<<<<< HEAD
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.common.DataStoreContext;
import org.opendaylight.transportpce.common.converter.XMLDataObjectConverter;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev200429.Network;
=======
import java.util.concurrent.ExecutionException;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.transportpce.test.DataStoreContext;
import org.opendaylight.transportpce.test.converter.XMLDataObjectConverter;
import org.opendaylight.yang.gen.v1.http.org.opendaylight.transportpce.portmapping.rev200827.Network;
>>>>>>> standalone/stable/aluminium
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.network.rev180226.Networks;
import org.opendaylight.yang.gen.v1.urn.onf.otcc.yang.tapi.topology.rev181210.GetTopologyDetailsInput;
import org.opendaylight.yang.gen.v1.urn.onf.otcc.yang.tapi.topology.rev181210.GetTopologyDetailsInputBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TopologyDataUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyDataUtils.class);
    public static final String OPENROADM_TOPOLOGY_FILE = "src/test/resources/openroadm-topology2.xml";
    public static final String OTN_TOPOLOGY_FILE = "src/test/resources/otn-topology.xml";
<<<<<<< HEAD
=======
    public static final String OTN_TOPOLOGY_WITH_OTN_LINKS_FILE = "src/test/resources/otn-topology-with-otn-links.xml";
>>>>>>> standalone/stable/aluminium
    public static final String PORTMAPPING_FILE = "src/test/resources/portmapping-example.xml";

    public static GetTopologyDetailsInput buildGetTopologyDetailsInput(String topoName) {
        GetTopologyDetailsInputBuilder builtInput = new GetTopologyDetailsInputBuilder();
        builtInput.setTopologyIdOrName(topoName);
        return builtInput.build();
    }

    public static <T> void writeTopologyFromFileToDatastore(DataStoreContext dataStoreContextUtil, String file,
<<<<<<< HEAD
        InstanceIdentifier ii) {
=======
        InstanceIdentifier ii) throws InterruptedException, ExecutionException {
>>>>>>> standalone/stable/aluminium
        Networks networks = null;
        File topoFile = new File(file);
        if (topoFile.exists()) {
            String fileName = topoFile.getName();
            InputStream targetStream;
            try {
                targetStream = new FileInputStream(topoFile);
                Optional<NormalizedNode<?, ?>> transformIntoNormalizedNode = null;
                transformIntoNormalizedNode = XMLDataObjectConverter.createWithDataStoreUtil(dataStoreContextUtil)
                    .transformIntoNormalizedNode(targetStream);
                if (!transformIntoNormalizedNode.isPresent()) {
                    throw new IllegalStateException(String.format(
                        "Could not transform the input %s into normalized nodes", fileName));
                }
                Optional<DataObject> dataObject = XMLDataObjectConverter.createWithDataStoreUtil(dataStoreContextUtil)
                    .getDataObject(transformIntoNormalizedNode.get(), Networks.QNAME);
                if (!dataObject.isPresent()) {
                    throw new IllegalStateException("Could not transform normalized nodes into data object");
                } else {
                    networks = (Networks) dataObject.get();
                }
            } catch (FileNotFoundException e) {
                LOG.error("File not found : {} at {}", e.getMessage(), e.getLocalizedMessage());
            }
        } else {
            LOG.error("xml file {} not found at {}", topoFile.getName(), topoFile.getAbsolutePath());
        }
<<<<<<< HEAD
        writeTransaction(dataStoreContextUtil.getDataBroker(), ii, networks.getNetwork().get(0));
=======
        FluentFuture<? extends CommitInfo> commitFuture = writeTransaction(dataStoreContextUtil.getDataBroker(), ii,
                networks.nonnullNetwork().values().stream().findFirst().get());
        commitFuture.get();
>>>>>>> standalone/stable/aluminium
        LOG.info("extraction from {} stored with success in datastore", topoFile.getName());
    }

    @SuppressWarnings("unchecked")
<<<<<<< HEAD
    private static boolean writeTransaction(DataBroker dataBroker, InstanceIdentifier instanceIdentifier,
        DataObject object) {
        @NonNull
        WriteTransaction transaction = dataBroker.newWriteOnlyTransaction();
        transaction.put(LogicalDatastoreType.CONFIGURATION, instanceIdentifier, object);
        transaction.commit();
        return true;
    }

    public static void writePortmappingFromFileToDatastore(DataStoreContext dataStoreContextUtil) {
=======
    private static FluentFuture<? extends CommitInfo> writeTransaction(DataBroker dataBroker,
        InstanceIdentifier instanceIdentifier, DataObject object) {
        WriteTransaction transaction = dataBroker.newWriteOnlyTransaction();
        transaction.put(LogicalDatastoreType.CONFIGURATION, instanceIdentifier, object);
        return transaction.commit();
    }

    public static void writePortmappingFromFileToDatastore(DataStoreContext dataStoreContextUtil)
        throws InterruptedException, ExecutionException {
>>>>>>> standalone/stable/aluminium
        Network result = null;
        File portmappingFile = new File(PORTMAPPING_FILE);
        if (portmappingFile.exists()) {
            String fileName = portmappingFile.getName();
            InputStream targetStream;
            try {
                targetStream = new FileInputStream(portmappingFile);
                Optional<NormalizedNode<?, ?>> transformIntoNormalizedNode = null;
                transformIntoNormalizedNode = XMLDataObjectConverter.createWithDataStoreUtil(dataStoreContextUtil)
                    .transformIntoNormalizedNode(targetStream);
                if (!transformIntoNormalizedNode.isPresent()) {
                    throw new IllegalStateException(String.format(
                        "Could not transform the input %s into normalized nodes", fileName));
                }
                Optional<DataObject> dataObject = XMLDataObjectConverter.createWithDataStoreUtil(dataStoreContextUtil)
                    .getDataObject(transformIntoNormalizedNode.get(), Network.QNAME);
                if (!dataObject.isPresent()) {
                    throw new IllegalStateException("Could not transform normalized nodes into data object");
                } else {
                    result = (Network) dataObject.get();
                }
            } catch (FileNotFoundException e) {
                LOG.error("File not found : {} at {}", e.getMessage(), e.getLocalizedMessage());
            }
        } else {
            LOG.error("xml file {} not found at {}", portmappingFile.getName(), portmappingFile.getAbsolutePath());
        }
        InstanceIdentifier<Network> portmappingIID = InstanceIdentifier.builder(Network.class).build();
<<<<<<< HEAD
        writeTransaction(dataStoreContextUtil.getDataBroker(), portmappingIID, result);
=======
        FluentFuture<? extends CommitInfo> writeTransaction = writeTransaction(dataStoreContextUtil.getDataBroker(),
            portmappingIID, result);
        writeTransaction.get();
>>>>>>> standalone/stable/aluminium
        LOG.info("portmapping-example stored with success in datastore");
    }

    private TopologyDataUtils() {
    }

}
