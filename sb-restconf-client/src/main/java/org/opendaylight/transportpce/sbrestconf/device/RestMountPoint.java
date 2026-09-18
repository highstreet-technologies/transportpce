/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.sbrestconf.device;

import java.util.Optional;
import java.util.concurrent.Executor;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.api.BindingService;
import org.opendaylight.mdsal.binding.api.MountPoint;
import org.opendaylight.mdsal.binding.api.NotificationService;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.Notification;
import org.opendaylight.yangtools.concepts.Registration;

public class RestMountPoint implements MountPoint {

    private final String nodeId;

    public RestMountPoint(String deviceId) {
        this.nodeId = deviceId;
    }

    @Override
    public @NonNull <T extends BindingService> Optional<T> getService(@NonNull Class<T> service) {
        if(service.equals(NotificationService.class)){
            return Optional.of((T)new RestNotificationService());
        }

        return Optional.empty();
    }

    @Override
    public @NonNull DataObjectIdentifier<?> getIdentifier() {
        return null;
    }

    private class RestNotificationService implements NotificationService{

        @Override
        public @NonNull <N extends Notification<N> & DataObject> Registration registerListener(Class<N> type,
                Listener<N> listener, Executor executor) {
            return () -> {

            };
        }

        @Override
        public @NonNull Registration registerCompositeListener(CompositeListener listener, Executor executor) {
            return () -> {

            };
        }
    }
}
