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
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.MediaControl1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.MediaEndpoint1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.MediaPlayer1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.MediaTransport1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.Profile1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.ProfileManager1;
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
public class BluezBluetoothA2dp extends BluetoothA2dp implements DBusSigHandler {
    private static final Logger logger = LoggerFactory.getLogger(BluezBluetoothA2dp.class);

    private final int HF_NREC = 0x0001;
    private final int HF_3WAY = 0x0002;
    private final int HF_CLI = 0x0004;
    private final int HF_VOICE_RECOGNITION = 0x0008;
    private final int HF_REMOTE_VOL = 0x0010;
    private final int HF_ENHANCED_STATUS = 0x0020;
    private final int HF_ENHANCED_CONTROL = 0x0040;
    private final int HF_CODEC_NEGOTIATION = 0x0080;

    private final int AG_3WAY = 0x0001;
    private final int AG_NREC = 0x0002;
    private final int AG_VOICE_RECOGNITION = 0x0004;
    private final int AG_INBAND_RING = 0x0008;
    private final int AG_VOICE_TAG = 0x0010;
    private final int AG_REJECT_CALL = 0x0020;
    private final int AG_ENHANCED_STATUS = 0x0040;
    private final int AG_ENHANCED_CONTROL = 0x0080;
    private final int AG_EXTENDED_RESULT = 0x0100;
    private final int AG_CODEC_NEGOTIATION = 0x0200;

    private final int HF_FEATURES = (HF_3WAY | HF_CLI | HF_VOICE_RECOGNITION | HF_REMOTE_VOL | HF_ENHANCED_STATUS
            | HF_ENHANCED_CONTROL | HF_CODEC_NEGOTIATION);

    private DBusConnection connection;
    private Media1 media1;
    private MediaPlayer1 mediaplayer1;
    private MediaControl1 mediacontrol1;
    private String dbusPath;
    DBus.Properties propertyReader;

    private A2dpProfile profile1;

    BluezBluetoothA2dp(BluezBluetoothAdapter adapter) {
        dbusPath = adapter.getDbusPath();
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

            String[] deviceAddress = dbusPath.split("/");
            String rootAddress = "/" + deviceAddress[1] + "/" + deviceAddress[2] + "/" + deviceAddress[3];
            // connection.requestBusName("org.eclipse.smarthome.binding.bluetooth"); // TODO: Change this!!!
            media1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, rootAddress, Media1.class);
            if (media1 == null) {
                logger.error("BlueZ error creating Media1 at {}", dbusPath);
                return;
            }

            // MediaEndpoint1 endpoint1 = new A2dpEndpoint();
            // connection.exportObject(dbusPath, endpoint1);

            // try {
            // media1.RegisterEndpoint(endpoint1, properties);
            // } catch (Exception x) {
            // logger.warn("Error registering endpoint: {}", x.getMessage());
            // logger.warn("Error registering endpoint: {}", x.getStackTrace());
            // }

            ProfileManager1 profileManager1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE,
                    BluezBluetoothConstants.BLUEZ_DBUS_PATH, ProfileManager1.class);

            Map<String, Variant> options = new HashMap<String, Variant>();
            options.put("Version", new Variant(new Integer(0x0106)));
            options.put("Features", new Variant(new Integer(HF_FEATURES)));
            options.put("Name", new Variant("SmartHome"));
            options.put("AutoConnect", new Variant(true));
            options.put("Channel", new Variant(6));
            // options.put("Capabilities", new Variant(BluezBluetoothConstants.SBC_CAPABILITIES));

            profile1 = new A2dpProfile();
            connection.exportObject(dbusPath, profile1);

            profileManager1.RegisterProfile(profile1, BluetoothProfile.PROFILE_HFP.toString(), options);

            // mediaplayer1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, dbusPath,
            // MediaPlayer1.class);
            // if (mediaplayer1 == null) {
            // logger.debug("BlueZ error creating MediaPlayer1 at {}", dbusPath);
            // return;
            // }

            // mediacontrol1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, dbusPath,
            // MediaControl1.class);
            // if (mediacontrol1 == null) {
            // logger.debug("BlueZ error creating MediaControl1 at {}", dbusPath);
            // return;
            // }

            // device.Connect();

            // mediacontrol1.VolumeUp();

            // mediaplayer1.Pause();

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
    public void handle(DBusSignal arg0) {
        // TODO Auto-generated method stub

    }

    class A2dpProfile implements Profile1 {

        @Override
        public boolean isRemote() {
            // TODO Auto-generated method stub
            logger.debug("BlueZ A2dpProfile isRemote");
            return false;
        }

        @Override
        public void Release() {
            // TODO Auto-generated method stub
            logger.debug("BlueZ A2dpProfile Release");
        }

        @Override
        public void Cancel() {
            // TODO Auto-generated method stub
            logger.debug("BlueZ A2dpProfile Cancel");
        }

        @Override
        public void NewConnection(DBusInterface path, int fd, Map<String, Variant> properties) {
            // TODO Auto-generated method stub
            logger.debug("BlueZ A2dpProfile NewConnection {} {} {}", path, fd, properties);
        }

        @Override
        public void RequestDisconnection(DBusInterface path) {
            // TODO Auto-generated method stub
            logger.debug("BlueZ A2dpProfile RequestDisconnection {}", path);
        }

    }

    class A2dpEndpoint implements MediaEndpoint1 {
        @Override
        public boolean isRemote() {
            return false;
        }

        @Override
        public void SetConfiguration(MediaTransport1 transport, Map<String, Variant> properties) {
            logger.debug("BlueZ A2DP SetConfiguration {}", transport);
        }

        @Override
        public Map<String, Variant> SelectConfiguration(MediaTransport1 transport, Map<String, Variant> capabilities) {
            logger.debug("BlueZ A2DP SelectConfiguration {}", transport);
            return null;
        }

        @Override
        public void ClearConfiguration(MediaTransport1 transport) {
            logger.debug("BlueZ A2DP ClearConfiguration {}", transport);
        }

        @Override
        public void Release() {
            logger.debug("BlueZ A2DP Release");
        }
    }
}
