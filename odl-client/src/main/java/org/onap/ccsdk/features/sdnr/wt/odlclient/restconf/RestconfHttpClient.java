/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.restconf;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;
import org.onap.ccsdk.features.sdnr.wt.odlclient.config.RemoteOdlConfig.AuthMethod;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.NotImplementedException;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlRpcObjectMapperXml;
import org.onap.ccsdk.features.sdnr.wt.odlclient.http.BaseHTTPClient;
import org.onap.ccsdk.features.sdnr.wt.odlclient.http.BaseHTTPResponse;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier.PathArgument;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestconfHttpClient extends BaseHTTPClient {

    private static final Logger LOG = LoggerFactory.getLogger(RestconfHttpClient.class);
    private static final int DEFAULT_TIMEOUT = 5000;
    private final Map<String, String> headers;
    private final OdlRpcObjectMapperXml mapper;
    private RequestCallback callback;

    public RestconfHttpClient(String base, boolean trustAllCerts, AuthMethod authMethod, String username,
            String password) throws NotImplementedException {
        super(base, trustAllCerts);
        if (authMethod == AuthMethod.TOKEN) {
            throw new NotImplementedException();
        }
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/xml");
        this.headers.put("Authorization", BaseHTTPClient.getAuthorizationHeaderValue(username, password));
        this.headers.put("Accept", "application/xml");
        this.mapper = new OdlRpcObjectMapperXml();

    }

    public <T extends DataObject> @NonNull FluentFuture<Optional<T>> read(LogicalDatastoreType storage,
            InstanceIdentifier<T> instanceIdentifier, String nodeId) throws IOException, ClassNotFoundException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final boolean isLeafList = isCompleteLeafListRequest(instanceIdentifier);
        final String uri = this.getRfc8040UriFromIif(storage, instanceIdentifier, nodeId, false, isLeafList);
        return FutureRestRequest.createFutureGetRequest(this, uri, (String) null, this.headers,
                instanceIdentifier.getTargetType(), isLeafList);
    }

    private <T extends DataObject> boolean isCompleteLeafListRequest(InstanceIdentifier<T> instanceIdentifier) {
        Iterable<PathArgument> iterable = instanceIdentifier.getPathArguments();
        Iterator<PathArgument> it = iterable.iterator();
        boolean isLeafList = false;
        PathArgument pa = null;
        while (it.hasNext()) {
            pa = it.next();
        }
        if (pa != null) {
            isLeafList = pa.getType().isAssignableFrom(List.class);
        }
        return isLeafList;
    }

    public <T extends DataObject> @NonNull FluentFuture<Optional<T>> read(LogicalDatastoreType store,
            InstanceIdentifier<T> instanceIdentifier) throws ClassNotFoundException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
        return this.read(store, instanceIdentifier, null);
    }

    protected <T extends DataObject> String getRfc8040UriFromIif(LogicalDatastoreType storage,
            InstanceIdentifier<T> instanceIdentifier, String nodeId, boolean isRpc) throws ClassNotFoundException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        return this.getRfc8040UriFromIif(storage, instanceIdentifier, nodeId, isRpc, false);
    }

    protected <T extends DataObject> String getRfc8040UriFromIif(LogicalDatastoreType storage,
            InstanceIdentifier<T> instanceIdentifier, String nodeId, boolean isRpc, boolean getParentOnlyWithFields)
            throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {

        LOG.debug("try to create rfc8040 uri for datastore {}, iid: {} and nodeId {}", storage, instanceIdentifier,
                nodeId);
        String uri = "";
        Iterable<PathArgument> iterable = instanceIdentifier.getPathArguments();
        Iterator<PathArgument> it = iterable.iterator();
        boolean isNewModule = true;
        String moduleName = null;
        while (it.hasNext()) {
            PathArgument pa = it.next();
            Class<?> cls = this.mapper.findClass(pa.getType().getName(), RestconfHttpClient.class);
            String key = getKeyOrNull(pa);
            Field qname = cls.getField("QNAME");
            QName value = (QName) qname.get(cls);
            if (!implementsChildOf(cls)) {
                continue;
            }
            if (isNewModule) {
                moduleName = value.getLocalName();
            }
            if (key == null) {
                uri += String.format("/%s%s", isNewModule ? (urlencode(moduleName) + ":") : "",
                        urlencode(value.getLocalName()));
            } else {
                uri += String.format("/%s=%s", urlencode(value.getLocalName()), urlencode(key));
            }
            isNewModule = false;
        }
        final String retValue = this.getRfc8040UriFromIif(storage, uri, nodeId, isRpc);
        LOG.debug("uri={}", retValue);
        return retValue;
    }

    private static final String urlencode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace(":","%3A");
        } catch (UnsupportedEncodingException ex) {
            LOG.warn("problem encode {}:", value, ex);
        }
        return value;
    }

    protected <T extends DataObject> String getRfc8040UriFromIif(LogicalDatastoreType storage, String postUri,
            String nodeId, boolean isRpc) {
        if (!postUri.startsWith("/")) {
            postUri = "/" + postUri;
        }
        final String sstore = isRpc ? "operations" : "data";
        return nodeId != null ? String.format(
                "/rests/%s/network-topology:network-topology/topology=topology-netconf/node=%s/yang-ext:mount%s",
                sstore, nodeId, postUri) : String.format("/rests/%s%s", sstore, postUri);
    }

    private static boolean implementsChildOf(Class<?> clazz) {

        Class<?>[] clazzes = clazz.getInterfaces();
        for (Class<?> cls : clazzes) {
            if (cls.equals(ChildOf.class)) {
                return true;
            }
        }
        return false;
    }

    private String getKeyOrNull(PathArgument pa) {
        try {
            Class<? extends PathArgument> clazz = pa.getClass();

            Field field = getDeclaredFieldOrNull(clazz,"key");
            if(field==null) {
                return null;
            }
            field.setAccessible(true);
            Object keydef = field.get(pa);
            if (keydef instanceof Identifier) {
                // ((Identifier)keydef).
                Field[] fields = keydef.getClass().getDeclaredFields();
                for (Field f : fields) {
                    if (f.getName().endsWith("serialVersionUID")) {
                        continue;
                    }
                    f.setAccessible(true);
                    Object key = f.get(keydef);
                    if (key instanceof Uri) {
                        return ((Uri) key).getValue();
                    } else if (key != null) {
                        return key.toString();
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOG.debug("key is not a field of class {}: ", pa.getClass(), e);
        }
        return null;
    }

    private static Field getDeclaredFieldOrNull(Class<?> clazz, String key) {
        Field[] fields = clazz.getDeclaredFields();
        for(Field f:fields) {
            if(f.getName()==key) {
                return f;
            }
        }
        return null;
    }

    private static String assembleExceptionMessage(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        StringBuffer buf = new StringBuffer();
        buf.append("Exception: ");
        buf.append(sw.toString());
        return buf.toString();
    }

    public <O extends DataObject, I extends DataObject> ListenableFuture<RpcResult<O>> executeRpc(String nodeId,
            String rpc, I input, Class<O> clazz) {
        RpcResultBuilder<O> result;
        try {

            BaseHTTPResponse response = this.sendRequest(
                    this.getRfc8040UriFromIif(LogicalDatastoreType.OPERATIONAL, rpc, nodeId, true), "POST",
                    input == null ? "" : this.mapper.writeValueAsString(input), this.headers, DEFAULT_TIMEOUT);
            if (response.isSuccess()) {
                O output = this.mapper.readValue(response.body, clazz);
                result = RpcResultBuilder.success(output);
            } else {
                result = RpcResultBuilder.failed();
            }
        } catch (IOException e) {
            LOG.info("Exception", e);
            result = RpcResultBuilder.failed();
            result.withError(ErrorType.APPLICATION, assembleExceptionMessage(e));
        }

        return result.buildFuture();
    }

    public FluentFuture<?> delete(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<?> instanceIdentifier, String nodeId) throws ClassNotFoundException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final String uri = this.getRfc8040UriFromIif(store, instanceIdentifier, nodeId, false, false);
        return FutureRestRequest.createFutureDeleteRequest(this, uri, (String) null, this.headers, false);
    }

    public <O extends DataObject> FluentFuture<Optional<O>> put(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<O> instanceIdentifier, @NonNull O data, String nodeId)
            throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        final String uri = this.getRfc8040UriFromIif(store, instanceIdentifier, nodeId, false, false);
        LOG.debug("serialize {}",data);
        final String strData = this.mapper.writeValueAsString(data,data.getClass());
        LOG.debug("putting data: {}", strData);
        return FutureRestRequest.createFuturePutRequest(this, uri, strData, this.headers, false);
    }

    public <O extends DataObject> FluentFuture<Optional<O>> merge(@NonNull LogicalDatastoreType store,
            @NonNull InstanceIdentifier<O> instanceIdentifier, @NonNull O data, String nodeId)
            throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        final String uri = this.getRfc8040UriFromIif(store, instanceIdentifier, nodeId, false, false);
        LOG.debug("serialize {}",data);
        final String strData = this.mapper.writeValueAsString(data,data.getClass());
        LOG.debug("merging data: {}", strData);
        return FutureRestRequest.createFuturePostRequest(this, uri, strData, this.headers,
                instanceIdentifier.getTargetType(), false);
    }

    public void registerRequestCallback(RequestCallback callback) {
        this.callback = callback;
    }
}
