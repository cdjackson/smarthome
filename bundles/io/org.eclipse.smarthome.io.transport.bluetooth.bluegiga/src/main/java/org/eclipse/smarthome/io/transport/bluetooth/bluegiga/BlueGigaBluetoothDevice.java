/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluegiga;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGatt;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattCallback;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothDeviceBondingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothDevice for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaBluetoothDevice extends BluetoothDevice {
    private BlueGigaBluetoothAdapter adapter;

    private final Logger logger = LoggerFactory.getLogger(BlueGigaBluetoothDevice.class);

    public BlueGigaBluetoothDevice(BlueGigaBluetoothAdapter adapter, String address) {
        this.adapter = adapter;

    }

    @Override
    public void finalize() {

    }

    @Override
    public BluetoothGatt connectGatt(boolean autoConnect, BluetoothGattCallback callback, int transport) {
        // If the device supports the GENERIC_ATTRIBUTE profile, then create a GATT class
        // if (!uuid.contains(BluezBluetoothConstants.BLUEZ_PROFILE_GATT)) {
        // logger.debug("{} attempted to connect to GATT when profile isn't supported", dbusPath);
        // return null;
        // }

        return new BlueGigaBluetoothGatt(this, autoConnect, callback, transport);
    }

    @Override
    public boolean createBond() {
        if (bondState == BOND_BONDED) {
            return false;
        }

        // try {
        // device1.Pair();
        // } catch (DBusExecutionException e) {
        // return false;
        // }

        bondState = BOND_BONDING;
        adapter.notifyEventListeners(new BluetoothDeviceBondingEvent(this, bondState));

        return true;
    }

    protected void Connect() {
    }

    protected void Disconnect() {
    }
}
