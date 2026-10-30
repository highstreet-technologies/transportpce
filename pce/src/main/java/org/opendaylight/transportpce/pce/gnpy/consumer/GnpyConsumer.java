/*
 * Copyright © 2021 Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.pce.gnpy.consumer;


import org.opendaylight.yang.gen.v1.gnpy.gnpy.path.computation.rev250120.Responses;
import org.opendaylight.yang.gen.v1.urn.gnpy.api.rev250613.Api;


public interface GnpyConsumer {

    /**
     * Check if api is available or not.
     * @return true os available, false otherwise.
     */
    boolean isAvailable();

    /**
     * Path computation request.
     * @param request GnpyApi top-level container.
     * @return Responses the result of path computation.
     */
    Responses computePaths(Api request);

}
