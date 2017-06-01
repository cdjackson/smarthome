/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.le;

import java.util.List;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothEventListener;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothDeviceDiscoveredEvent;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothEvent;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothScanEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This calls provides methods to scan for Bluetooth LE devices.
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public abstract class BluetoothLeScanner implements BluetoothEventListener {
    private static final Logger logger = LoggerFactory.getLogger(BluetoothLeScanner.class);

    protected List<ScanFilter> filters;
    protected ScanSettings settings;
    protected ScanCallback callback;

    /**
     * Flush any pending batch scan results stored in Bluetooth controller.
     *
     * @param callback
     */
    public void flushPendingScanResults(ScanCallback callback) {
    }

    /**
     * Start Bluetooth LE scan
     *
     * @param filters
     * @param settings
     * @param callback
     */
    public void startScan(List<ScanFilter> filters, ScanSettings settings, ScanCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback can not be null");
        }
        this.filters = filters;
        this.settings = settings;
        this.callback = callback;
    }

    /**
     * Start Bluetooth LE scan
     *
     * @param callback
     */
    public void startScan(ScanCallback callback) {
        startScan(null, null, callback);
    }

    /**
     * Stops a Bluetooth LE scan
     *
     * @param callback
     */
    public void stopScan(ScanCallback callback) {
        this.callback = null;
    }

    @Override
    public void handleBluetoothEvent(BluetoothEvent event) {
        if (event instanceof BluetoothScanEvent) {
            BluetoothScanEvent scanEvent = (BluetoothScanEvent) event;
            ScanResult result = new ScanResult(discoveryEvent.getDevice(), null, 0, 0);
            callback.onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, result);
        }

        if (event instanceof BluetoothDeviceDiscoveredEvent) {
            BluetoothDeviceDiscoveredEvent discoveryEvent = (BluetoothDeviceDiscoveredEvent) event;

            if (filters != null && filters.size() != 0) {
                // TODO: Handle filters
            }

            BluetoothDevice device = discoveryEvent.getDevice();

            // Check if we support the GATT profile.
            // (This is not a reliable method so remove for now!)
            // boolean supportsGatt = false;
            // UUID[] uuids = device.getUuids();
            // for (UUID uuid : uuids) {
            // if (BluezBluetoothConstants.BLUEZ_PROFILE_GATT.toString().equals(uuid.toString())) {
            // supportsGatt = true;
            // break;
            // }
            // }

            // if (supportsGatt == false) {
            // logger.debug("BLE Scanner: Device found that doesn't support BLE {} {}", device.getAddress(),
            // device.getName());
            // return;
            // }

            int callbackType = ScanSettings.CALLBACK_TYPE_ALL_MATCHES;

            ScanResult result = new ScanResult(discoveryEvent.getDevice(), null, 0, 0);

            logger.debug("BLE Scanner: New BLE device {} {}", device.getAddress(), device.getName());
            callback.onScanResult(callbackType, result);
        }
    }
}
