/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.ble.handler;

import org.eclipse.smarthome.binding.ble.BleBindingConstants;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattCharacteristic;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattDescriptor;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothGattService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BleGenericThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * Note that the FlowerPower needs to be paired before communicating
 *
 * @author Chris Jackson - Initial Contribution
 */
public class BleGenericThingHandler extends BleBaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(BleGenericThingHandler.class);

    boolean initialised = false;

    boolean characteristicDiscoveryRequested = false;

    public BleGenericThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();

        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    protected void handleConnectionStateChange(int status, int newState) {
        if (initialised == true) {
            return;
        }
    }

    @Override
    protected void handleInitialisation() {
        gattClient.connect();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    protected void handleServicesDiscovered(int status) {
        // Only do this discovery search once!
        if (characteristicDiscoveryRequested == false) {
            characteristicDiscoveryRequested = true;

            logger.debug("Bluetooth BLE device services discovered for {}\n{}", address, dumpServices());

            // Request all characteristics and descriptors
            for (BluetoothGattService service : gattClient.getServices()) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                        logger.debug("Requesting CHARACTERISTIC {} {}", address, characteristic.getUuid());
                        gattClient.readCharacteristic(characteristic);
                    }
                    for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                        logger.debug("Requesting DESCRIPTOR {} {}", address, descriptor.getUuid());
                        gattClient.readDescriptor(descriptor);
                    }
                }
            }
        }
    }

    @Override
    public void handleDescriptorRead(BluetoothGattDescriptor descriptor, int status) {
        discoveryComplete();
    }

    @Override
    public void handleCharacteristicRead(BluetoothGattCharacteristic characteristic, int status) {
        discoveryComplete();
    }

    private void discoveryComplete() {
        if (gattClient.getQueueLength() == 0) {
            logger.debug("Queue is empty.\n{}", dumpServices());

            // Check if everything has been received
            if (requestServices() == true) {
                return;
            }

            // Decide the new thing type

            // Change the thing type
            Configuration configuration = new Configuration();// ['providedspecific':'there']);
            changeThingType(BleBindingConstants.THING_TYPE_YEELIGHT_BLUE, configuration);
        }

    }
}
