/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import org.eclipse.jdt.annotation.Nullable;
import org.opendaylight.yangtools.concepts.Builder;

public interface ClassFinder {
    public Class<?> findClass(String name) throws ClassNotFoundException;
    public Class<?> findClass(String name, Class<?> clazz) throws ClassNotFoundException;
    public @Nullable <T> Builder<T> getBuilder(Class<T> clazz, T value);
}
