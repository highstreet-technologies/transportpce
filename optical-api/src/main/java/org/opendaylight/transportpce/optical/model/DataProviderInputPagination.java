/*
 * Copyright © 2026 Highstreet Technologies GmbH and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.transportpce.optical.model;

import org.opendaylight.yangtools.yang.common.Uint32;
import org.opendaylight.yangtools.yang.common.Uint64;

public class DataProviderInputPagination {

    Uint64 page;
    Uint32 size;

    public Uint64 getPage() {
        return page;
    }

    public void setPage(Uint64 page) {
        this.page = page;
    }

    public Uint32 getSize() {
        return size;
    }

    public void setSize(Uint32 size) {
        this.size = size;
    }
}
