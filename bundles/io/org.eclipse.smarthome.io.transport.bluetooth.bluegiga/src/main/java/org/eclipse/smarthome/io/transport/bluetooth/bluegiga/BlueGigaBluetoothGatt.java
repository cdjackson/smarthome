/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluegiga;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGatt;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattCallback;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattCharacteristic;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothGatt for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaBluetoothGatt extends BluetoothGatt {

    private final Logger logger = LoggerFactory.getLogger(BlueGigaBluetoothGatt.class);

    BlueGigaBluetoothGatt(BlueGigaBluetoothDevice device, boolean autoConnect, BluetoothGattCallback callback,
            int transport) {
        super(autoConnect, callback, transport);

        // Whether to directly connect to the remote device (false) or to automatically connect as soon as
        // the remote device becomes available (true).
        if (autoConnect == false) {
        }

        // Set callback to null to stop calling the service update event while we're adding them
        this.callback = null;

        // do stuff here

        // Restore the callback and add the event handlers
        this.callback = callback;
    }

    @Override
    public void finalize() {
    }

    @Override
    public boolean connect() {
        super.connect();

        return true;
    }

    @Override
    public void disconnect() {
        super.disconnect();

    }

    @Override
    public int getConnectionState(BluetoothDevice device) {
        // Not supported
        return STATE_DISCONNECTED;
    }

    @Override
    public void close() {

        super.close();
    }

    @Override
    protected void sendReadDescriptor(BluetoothGattDescriptor descriptor) {
        ((BlueGigaBluetoothGattDescriptor) descriptor).read(this, callback);
    }

    @Override
    protected void sendReadCharacteristic(BluetoothGattCharacteristic characteristic) {
        ((BlueGigaBluetoothGattCharacteristic) characteristic).read(this, callback);
    }

    @Override
    protected void sendWriteDescriptor(BluetoothGattDescriptor descriptor) {
        ((BlueGigaBluetoothGattDescriptor) descriptor).write(this, callback);
    }

    @Override
    protected void sendWriteCharacteristic(BluetoothGattCharacteristic characteristic) {
        ((BlueGigaBluetoothGattCharacteristic) characteristic).write(this, callback);
    }

    @Override
    public boolean discoverServices() {
        // TODO Auto-generated method stub
        return false;
    }
}
