/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluez;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothA2dp;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothProfile;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.Media1;
import org.freedesktop.DBus;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation class for the A2DP Bluetooth Profile for BlueZ
 *
 * @author Chris Jackson - Initial Contribution
 */
public class BluezBluetoothA2dp extends BluetoothA2dp implements DBusSigHandler, DBusInterface {
    private static final Logger logger = LoggerFactory.getLogger(BluezBluetoothDevice.class);

    private BluezBluetoothAdapter adapter;
    private DBusConnection connection;
    private Media1 media1;
    private String dbusPath;
    DBus.Properties propertyReader;
    private String address;

    BluezBluetoothA2dp(BluezBluetoothAdapter adapter, String address) {
        dbusPath = adapter.getDbusPath();
        this.address = address;
        logger.debug("Creating BlueZ A2DP at '{}'", dbusPath);

        try {
            String dbusAddress = System.getProperty(BluezBluetoothConstants.BLUEZ_DBUS_CONFIGURATION);
            if (dbusAddress == null) {
                connection = DBusConnection.getConnection(DBusConnection.SYSTEM);
            } else {
                connection = DBusConnection.getConnection(dbusAddress);
            }
            logger.debug("BlueZ connection opened at {}", connection.getUniqueName());

            Map<String, Variant> properties = new HashMap<String, Variant>();
            properties.put("UUID", new Variant(BluetoothProfile.PROFILE_A2DP_SOURCE.toString()));
            properties.put("Codec", new Variant(BluezBluetoothConstants.SBC_CODEC));
            properties.put("Capabilities", new Variant(BluezBluetoothConstants.SBC_CAPABILITIES));

            connection.requestBusName("org.eclipse.smarthome.binding.bluetooth");

            media1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, dbusPath, Media1.class);
            if (media1 == null) {
                logger.error("BlueZ error creating Media1 at {}", dbusPath);
                return;
            }

            try {
                media1.RegisterEndpoint(this, properties);
            } catch (Exception x) {
                logger.warn("Error registering endpoint: {}", x.getMessage());
            }

        } catch (DBusException | DBusExecutionException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public int getConnectionState(BluetoothDevice device) {
        return STATE_DISCONNECTED;
    }

    /**
     * Returns a list of A2DP connected devices
     *
     * @return
     */
    @Override
    public List<BluetoothDevice> getConnectedDevices() {
        return Collections.<BluetoothDevice> emptyList();
    }

    /**
     * Check if A2DP is playing music
     *
     * @param device
     * @return
     */
    @Override
    public boolean isA2dpPlaying(BluetoothDevice device) {
        return false;
    }

    /**
     * Connect to devices A2DP server.
     *
     * @return true, if the connection attempt was initiated successfully
     */
    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean isRemote() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void handle(DBusSignal arg0) {
        // TODO Auto-generated method stub

    }

}
