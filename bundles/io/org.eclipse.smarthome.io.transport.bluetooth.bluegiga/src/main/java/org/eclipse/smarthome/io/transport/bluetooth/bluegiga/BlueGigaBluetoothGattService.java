/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluegiga;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothGattService for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaBluetoothGattService extends BluetoothGattService {
    private final Logger logger = LoggerFactory.getLogger(BluetoothGattService.class);

    public BlueGigaBluetoothGattService(String servicePath) {
        super();

    }

    @Override
    public void finalize() {
    }
}
