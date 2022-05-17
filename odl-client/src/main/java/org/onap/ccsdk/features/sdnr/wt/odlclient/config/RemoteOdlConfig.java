/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteOdlConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteOdlConfig.class);
    private static final String FILENAME = "etc/remoteodl.properties";
    public static final String KEY_BASEURL = "baseurl";
    public static final String KEY_WSURL = "wsurl";
    public static final String KEY_AUTHMETHOD = "auth";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ENABLED = "enabled";
    public static final String KEY_TRUSTALL = "trustall";
    

    private static final String DEFAULT_BASEURL = "http://sdnr:8181";
    private static final String DEFAULT_WSURL = "ws://sdnr:8181/websocket";
    private static final String DEFAULT_AUTHMETHOD = AuthMethod.BASIC.name();
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    private static final boolean DEFAULT_ENABLED = false;
    private static final boolean DEFAULT_TRUSTALL = false;

    private static final String ENVVARIABLE = "${";
    private static final String REGEXENVVARIABLE = "(\\$\\{[A-Z0-9_-]+\\})";
    private static final Pattern PATTERN = Pattern.compile(REGEXENVVARIABLE);

    private final String baseUrl;
    private final String wsUrl;
    private final AuthMethod authMethod;
    private final String username;
    private final String password;
    private final boolean trustall;
    private final boolean enabled;
    

    public RemoteOdlConfig() {
        this(FILENAME);
    }

    public RemoteOdlConfig(String filename) {
        // try to load
        File file = new File(filename);
        Properties prop = null;
        if (file.exists() && file.isFile() && file.canRead()) {
            try (InputStream input = new FileInputStream(file)) {
                prop = new Properties();
                prop.load(input);

            } catch (IOException ex) {
                LOG.warn("problem loading config file {}: ", filename, ex);
            }
        }
        if (prop != null) {
            this.baseUrl = getProperty(prop, KEY_BASEURL, DEFAULT_BASEURL);
            this.wsUrl = getProperty(prop, KEY_WSURL, DEFAULT_WSURL);
            this.authMethod = AuthMethod.valueOf(getProperty(prop, KEY_AUTHMETHOD, DEFAULT_AUTHMETHOD));
            this.username = getProperty(prop, KEY_USERNAME, DEFAULT_USERNAME);
            this.password = getProperty(prop, KEY_PASSWORD, DEFAULT_PASSWORD);
            this.enabled = "true"
                    .equals(getProperty(prop, KEY_ENABLED, String.valueOf(DEFAULT_ENABLED)));
            this.trustall = "true"
                    .equals(getProperty(prop, KEY_TRUSTALL, String.valueOf(DEFAULT_TRUSTALL)));
        } else {
            this.baseUrl = DEFAULT_BASEURL;
            this.wsUrl = DEFAULT_WSURL;
            this.authMethod = AuthMethod.BASIC;
            this.username = DEFAULT_USERNAME;
            this.password = DEFAULT_PASSWORD;
            this.enabled = DEFAULT_ENABLED;
            this.trustall = DEFAULT_TRUSTALL;
            this.saveFile(filename);
        }
        LOG.info("loaded remote ODL config with enabled={}, remoteODL={}, wsUrl={} and trustall={}",
                this.enabled, this.baseUrl, this.wsUrl, this.trustall);
    }

    private void saveFile(String filename) {
        try (OutputStream output = new FileOutputStream(filename)) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty(KEY_BASEURL, this.baseUrl);
            prop.setProperty(KEY_WSURL, this.wsUrl);
            prop.setProperty(KEY_AUTHMETHOD, this.authMethod.name());
            prop.setProperty(KEY_USERNAME, "****");
            prop.setProperty(KEY_PASSWORD, "****");
            prop.setProperty(KEY_ENABLED, String.valueOf(this.enabled));
            prop.setProperty(KEY_TRUSTALL, String.valueOf(this.trustall));
            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            LOG.warn("problem writing property file to {}: ", filename, io);
        }

    }

    /**
     * get property for key. with ability to have env var expression as value like
     * mykey=${MYENV_VAR}
     *
     * @param prop     property object
     * @param key      property key
     * @param defValue default value
     * @return its value, if env var then the value of this or if not exists then
     *         defValue
     */
    private static String getProperty(final Properties prop, final String key, final String defValue) {
        String value = prop.getProperty(key, defValue);
        LOG.debug("try to get property for {} with def {}", key, defValue);

        // try to read env var
        if (value != null && value.contains(ENVVARIABLE)) {

            LOG.debug("try to find env var(s) for {}", value);
            final Matcher matcher = PATTERN.matcher(value);
            String tmp = new String(value);
            while (matcher.find() && matcher.groupCount() > 0) {
                final String mkey = matcher.group(1);
                if (mkey != null) {
                    try {
                        LOG.debug("match found for v={} and env key={}", tmp, mkey);
                        String env = System.getenv(mkey.substring(2, mkey.length() - 1));
                        tmp = tmp.replace(mkey, env == null ? "" : env);
                    } catch (SecurityException e) {
                        LOG.warn("unable to read env {}: {}", value, e);
                    }
                }
            }
            value = tmp;
        }
        return value;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getWebsocketUrl() {
        return this.wsUrl;
    }

    public AuthMethod getAuthenticationMethod() {
        return this.authMethod;
    }

    public String getCredentialUsername() {
        return this.username;
    }

    public String getCredentialPassword() {
        return this.password;
    }

    public enum AuthMethod {
        BASIC, TOKEN
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean trustAllCerts() {
        return this.trustall;
    }

}
