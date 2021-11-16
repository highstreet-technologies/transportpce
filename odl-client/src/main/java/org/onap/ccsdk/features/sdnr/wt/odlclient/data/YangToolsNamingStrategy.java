/*
 * Copyright (C) 2020 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.data;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.KebabCaseStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YangToolsNamingStrategy extends KebabCaseStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(YangToolsNamingStrategy.class);
    private static final long serialVersionUID = 1L;
    private final List<String> exceptions;

    public YangToolsNamingStrategy() {
        this.exceptions = Arrays.asList("ifName", "adminStatus");
    }

    @Override
    public String translate(String input) {
        if (this.exceptions.contains(input)) {
            LOG.debug("translate not {}", input);
            return input;
        }
        String output = super.translate(input);
        LOG.debug("translate {} to {}", input, output);

        return output;
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        // TODO Auto-generated method stub
        return super.nameForSetterMethod(config, method, defaultName);
    }

}
