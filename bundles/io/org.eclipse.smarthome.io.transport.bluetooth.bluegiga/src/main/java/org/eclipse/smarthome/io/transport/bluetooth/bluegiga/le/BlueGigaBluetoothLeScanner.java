/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluegiga.le;

import java.util.List;

import org.eclipse.smarthome.io.transport.bluetooth.bluegiga.BlueGigaBluetoothAdapter;
import org.eclipse.smarthome.io.transport.bluetooth.le.BluetoothLeScanner;
import org.eclipse.smarthome.io.transport.bluetooth.le.ScanCallback;
import org.eclipse.smarthome.io.transport.bluetooth.le.ScanFilter;
import org.eclipse.smarthome.io.transport.bluetooth.le.ScanSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothLeScanner for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaBluetoothLeScanner extends BluetoothLeScanner {
    private final Logger logger = LoggerFactory.getLogger(BluetoothLeScanner.class);

    private BlueGigaBluetoothAdapter adapter;

    public BlueGigaBluetoothLeScanner(BlueGigaBluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Start Bluetooth LE scan.
     *
     * @param filters
     * @param settings
     * @param callback
     */
    @Override
    public void startScan(List<ScanFilter> filters, ScanSettings settings, ScanCallback callback) {
        super.startScan(filters, settings, callback);

        if (adapter != null) {
            adapter.addEventListener(this);
            adapter.startDiscovery();
        }
    }

    /**
     * Stops an ongoing Bluetooth LE scan.
     *
     * @param callback
     */
    @Override
    public void stopScan(ScanCallback callback) {
        super.stopScan(callback);
        if (adapter != null) {
            adapter.cancelDiscovery();
            adapter.removeEventListener(this);
        }
    }

}
