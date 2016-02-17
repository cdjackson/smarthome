/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.bluetooth.handler;

import static org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants.CHANNEL_1;

import org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothA2dp;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothAdapter;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothManager;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothProfile;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothProfile.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BluetoothHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Chris Jackson - Initial contribution
 */
public class BluetoothHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(BluetoothHandler.class);

    protected BluetoothAdapter adapter;
    protected BluetoothDevice device;
    protected BluetoothA2dp a2dp;
    protected String address;

    public BluetoothHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_1)) {
            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNINITIALIZED);

        address = this.getThing().getProperties().get(BluetoothBindingConstants.PROPERTY_ADDRESS);
        if (address == null) {
            logger.error("Property 'Address' is not set for {}", getThing().getUID());
            return;
        }

        // Open the adapter
        adapter = BluetoothManager.getDefaultAdapter();
        if (adapter == null) {
            logger.error("Unable to get default Bluetooth adapter");
            return;
        }

        device = adapter.getRemoteDevice(address);
        if (device == null) {
            logger.error("Unable to get Bluetooth device {}", address);
            return;
        }

        // a2dp = (BluetoothA2dp) device.getProfile(BluetoothProfile.A2DP);
        // a2dp.connect()

        ProxyListener listener = new ProxyListener();
        adapter.getProfileProxy(listener, BluetoothProfile.A2DP);
        updateStatus(ThingStatus.OFFLINE);
    }

    class ProxyListener implements ServiceListener {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile != BluetoothProfile.A2DP) {
                return;
            }

            BluetoothA2dp a2dp = (BluetoothA2dp) proxy;
            // if(proxy.g)
        }

        @Override
        public void onServiceDisconnected(int profile) {
            // TODO Auto-generated method stub

        }

    }
}
