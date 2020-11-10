/*
 * Copyright © 2020 Orange, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.lighty.controllers.tpce.exception;

<<<<<<< HEAD
public class TechnicalException extends RuntimeException {

    /**
     * serial id.
     */
    private static final long serialVersionUID = 1359762809539335449L;

    /**
=======
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "SE_NO_SERIALVERSIONID",
    justification = "https://github.com/rzwitserloot/lombok/wiki/WHY-NOT:-serialVersionUID")
public class TechnicalException extends RuntimeException {

    /**
>>>>>>> standalone/stable/aluminium
     * Default constructor.
     */
    public TechnicalException() {
        super();
    }

    /**
     * Constructor with message.
     * @param message error message
     * @param cause root cause
     */
    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
