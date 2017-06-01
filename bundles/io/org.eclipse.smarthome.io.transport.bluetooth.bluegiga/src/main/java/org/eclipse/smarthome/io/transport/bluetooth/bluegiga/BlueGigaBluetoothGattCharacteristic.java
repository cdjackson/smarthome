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
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothCharacteristic for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaBluetoothGattCharacteristic extends BluetoothGattCharacteristic {
    private final Logger logger = LoggerFactory.getLogger(BluetoothGattCharacteristic.class);

    public BlueGigaBluetoothGattCharacteristic(BluetoothGattService service, String characteristicPath) {
        super();

        this.service = service;

        // logger.debug("Creating BlueZ GATT characteristic at '{}'", characteristicPath);

    }

    @Override
    public void finalize() {

    }

    public void read(BluetoothGatt gatt, BluetoothGattCallback callback) {
        new ReadThread(gatt, callback, this).run();
    }

    private class ReadThread extends Thread {
        private BluetoothGattCallback callback;
        private BluetoothGattCharacteristic characteristic;
        private BluetoothGatt gatt;

        ReadThread(BluetoothGatt gatt, BluetoothGattCallback callback, BluetoothGattCharacteristic characteristic) {
            this.callback = callback;
            this.gatt = gatt;
            this.characteristic = characteristic;
        }

        @Override
        public void run() {
            boolean success = false;
            List<Byte> value = null;

            // do stuff

            if (success == false) {
                // callback.onCharacteristicRead(gatt, characteristic, BluetoothGatt.GATT_FAILURE);
            } else {
                // byte[] newValue = new byte[value.size()];
                // int cnt = 0;
                // for (byte b : value) {
                // newValue[cnt++] = b;
                // }
                // characteristic.setValue(newValue);
                // callback.onCharacteristicRead(gatt, characteristic, BluetoothGatt.GATT_SUCCESS);
            }

            // Update the transmit queue
            gatt.processQueueResponse(characteristic.getUuid());
        }
    }

    public void write(BluetoothGatt gatt, BluetoothGattCallback callback) {
        new WriteThread(gatt, callback, this, this.value).run();
    }

    private class WriteThread extends Thread {
        private BluetoothGattCallback callback;
        private BluetoothGattCharacteristic characteristic;
        private BluetoothGatt gatt;
        private List<Byte> value;

        WriteThread(BluetoothGatt gatt, BluetoothGattCallback callback, BluetoothGattCharacteristic characteristic,
                byte[] value) {
            this.callback = callback;
            this.gatt = gatt;
            this.characteristic = characteristic;

            this.value = new ArrayList<Byte>(value.length);
            int cnt = 0;
            for (byte b : value) {
                this.value.add(value[cnt++]);
            }
        }

        @Override
        public void run() {
            boolean success = false;

            // do stuff

            if (success == false) {
                callback.onCharacteristicWrite(gatt, characteristic, BluetoothGatt.GATT_FAILURE);
                return;
            }

            gatt.processQueueResponse(characteristic.getUuid());
            callback.onCharacteristicWrite(gatt, characteristic, BluetoothGatt.GATT_SUCCESS);
        }
    }

    @Override
    public boolean setNotification(BluetoothGatt gatt, boolean enable) {
        super.setNotification(gatt, enable);

        return false;
    }
}
