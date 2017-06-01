/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluegiga;

import java.util.Map;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothAdapter;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothTransportProvider;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothTransportProvider for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaTransportProvider implements BluetoothTransportProvider {
    private final Logger logger = LoggerFactory.getLogger(BluetoothTransportProvider.class);

    private final String CFG_DEFAULT_PORT = "defaultPort";

    private String defaultPort = null;

    @Override
    public BluetoothAdapter getDefaultAdapter() {
        if (defaultPort == null || defaultPort.length() == 0) {
            logger.debug("BlueGiga Bluetooth Transport Provider: defaultPort is not configured.");

            return null;
        }
        return new BlueGigaBluetoothAdapter(defaultPort);
    }

    protected void activate(ComponentContext cContext, Map<String, Object> properties) {
        logger.debug("BlueGiga Bluetooth Transport Provider: Activated.");

        if (properties == null || properties.isEmpty()) {
            return;
        }

        if (properties.get(CFG_DEFAULT_PORT) != null) {
            defaultPort = (String) properties.get(CFG_DEFAULT_PORT);
        }
    }

    protected void deactivate() {
        logger.debug("BlueGiga Bluetooth Transport Provider: Deactivated.");
    }
}
