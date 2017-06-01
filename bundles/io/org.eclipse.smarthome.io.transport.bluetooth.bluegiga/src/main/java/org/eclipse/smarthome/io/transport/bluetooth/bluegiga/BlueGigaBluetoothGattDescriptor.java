/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluegiga;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGatt;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattCallback;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattCharacteristic;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothGattDescriptor for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaBluetoothGattDescriptor extends BluetoothGattDescriptor {
    private final Logger logger = LoggerFactory.getLogger(BluetoothGattCharacteristic.class);

    public BlueGigaBluetoothGattDescriptor(BluetoothGattCharacteristic characteristic, String descriptorPath) {
        super();

        this.characteristic = characteristic;

        // logger.debug("Creating BlueZ GATT characteristic at '{}'", descriptorPath);

    }

    @Override
    public void finalize() {
    }

    @Override
    public boolean setValue(byte[] newValue) {
        value = newValue;

        return true;
    }

    public void read(BluetoothGatt gatt, BluetoothGattCallback callback) {
        ReadThread reader = new ReadThread(gatt, callback, this);

        reader.run();
    }

    private class ReadThread extends Thread {
        private BluetoothGattCallback callback;
        private BluetoothGattDescriptor descriptor;
        private BluetoothGatt gatt;

        ReadThread(BluetoothGatt gatt, BluetoothGattCallback callback, BluetoothGattDescriptor descriptor) {
            this.callback = callback;
            this.gatt = gatt;
            this.descriptor = descriptor;
        }

        @Override
        public void run() {
            boolean success = false;
            List<Byte> value = null;

            if (success == false) {
                // callback.onDescriptorRead(gatt, descriptor, BluetoothGatt.GATT_FAILURE);
                return;
            }

            // byte[] newValue = new byte[value.size()];
            // int cnt = 0;
            // for (byte b : value) {
            // newValue[cnt++] = b;
            // }
            // descriptor.setValue(newValue);

            gatt.processQueueResponse(descriptor.getUuid());
            callback.onDescriptorRead(gatt, descriptor, BluetoothGatt.GATT_SUCCESS);
        }
    }

    public void write(BluetoothGatt gatt, BluetoothGattCallback callback) {
        WriteThread reader = new WriteThread(gatt, callback, this, this.value);

        reader.run();
    }

    private class WriteThread extends Thread {
        private BluetoothGattCallback callback;
        private BluetoothGattDescriptor descriptor;
        private BluetoothGatt gatt;
        private List<Byte> value;

        WriteThread(BluetoothGatt gatt, BluetoothGattCallback callback, BluetoothGattDescriptor descriptor,
                byte[] value) {
            this.callback = callback;
            this.gatt = gatt;
            this.descriptor = descriptor;

            this.value = new ArrayList<Byte>(value.length);
            int cnt = 0;
            for (byte b : value) {
                this.value.add(value[cnt++]);
            }
        }

        @Override
        public void run() {
            boolean success = false;

            if (success == false) {
                callback.onDescriptorWrite(gatt, descriptor, BluetoothGatt.GATT_FAILURE);
                return;
            }

            gatt.processQueueResponse(characteristic.getUuid());
            callback.onDescriptorWrite(gatt, descriptor, BluetoothGatt.GATT_SUCCESS);
        }
    }

}
