/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data.serializer;

public class ObjectSerializer {

	public String convertPropertyName(String name) {
		return name.substring(1).replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
	}

}
