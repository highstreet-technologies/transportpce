/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.remote.transactions;

import com.google.common.util.concurrent.FutureCallback;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.onap.ccsdk.features.sdnr.wt.odlclient.restconf.RestconfHttpClient;
import org.opendaylight.mdsal.common.api.CommitInfo;

public class RemoteTransaction {

    public static final long COMMIT_DELAY = 2000;

    protected final List<FutureCallback<CommitInfo>> callbacks;
    protected final RestconfHttpClient client;
    protected final String nodeId;
    public RemoteTransaction(RestconfHttpClient remoteOdlClient, String nodeId) {
        this.callbacks = new ArrayList<>();
        this.client = remoteOdlClient;
        this.nodeId = nodeId;
    }
    public void addCallback(FutureCallback<CommitInfo> cb, ScheduledExecutorService scheduledExecutorService) {
        this.callbacks.add(cb);
    }
    protected void pushFailure(Throwable t) {
        for(FutureCallback<CommitInfo> cb:this.callbacks) {
            cb.onFailure(t);
        }
    }
    protected void pushSuccess(@Nullable CommitInfo result) {
        for(FutureCallback<CommitInfo> cb:this.callbacks) {
            cb.onSuccess(result);
        }
    }

    protected void pushSuccess(final @Nullable CommitInfo result, long delayMillis) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {

                }
                RemoteTransaction.this.pushSuccess(result);
            }

        }).start();
    }
    public static String whoCalledMe() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[3];
        String classname = caller.getClassName();
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        return classname + "." + methodName + ":" + lineNumber;
    }

    public static String whoCalledMeAll() {
        return whoCalledMeAll(Arrays
                .asList("org.opendaylight.transportpce.common.device.DeviceTransactionManagerImpl.getDataFromDevice"));
    }
    public static String whoCalledMeAll(final List<String> ignoreIfContains) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        new Throwable().printStackTrace(pw);
        final String exception = sw.toString();
        if(ignoreIfContains!=null) {
            for(String ignore:ignoreIfContains) {
                if(exception.contains(ignore)) {
                    return "";
                }
            }
        }
        return exception;
    }
}