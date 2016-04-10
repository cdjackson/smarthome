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
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.Properties.PropertiesChanged;
import org.freedesktop.DBus;
import org.freedesktop.DBus.Binding.Triplet;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UnixFD;
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
public class BluezBluetoothA2dp extends BluetoothA2dp {
    private static final Logger logger = LoggerFactory.getLogger(BluezBluetoothA2dp.class);

    private DBusConnection connection;
    private Media1 media1;
    private MediaPlayer1 mediaplayer1;
    private MediaControl1 mediacontrol1;
    private String dbusPath;
    DBus.Properties propertyReader;

    private MediaEndpoint1 endpoint1;

    BluezBluetoothA2dp(BluezBluetoothAdapter adapter) {
        dbusPath = adapter.getDbusPath();
        logger.debug("Creating BlueZ A2DP at '{}'", dbusPath);

        try {
            String dbusAddress = System.getProperty(BluezBluetoothConstants.BLUEZ_DBUS_CONFIGURATION);
            if (dbusAddress == null) {
                connection = DBusConnection.getConnection(DBusConnection.SESSION);
            } else {
                connection = DBusConnection.getConnection(dbusAddress);
            }
            logger.debug("BlueZ connection opened at {}", connection.getUniqueName());

            Map<String, Variant> properties = new HashMap<String, Variant>();
            properties.put("UUID", new Variant(BluetoothProfile.PROFILE_A2DP_SINK.toString()));
            properties.put("Codec", new Variant(BluezBluetoothConstants.SBC_CODEC));
            properties.put("Capabilities", new Variant(BluezBluetoothConstants.SBC_CAPABILITIES));

            String[] deviceAddress = dbusPath.split("/");
            String rootAddress = "/" + deviceAddress[1] + "/" + deviceAddress[2] + "/" + deviceAddress[3];
            media1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, rootAddress, Media1.class);
            if (media1 == null) {
                logger.error("BlueZ error creating Media1 at {}", dbusPath);
                return;
            }

            endpoint1 = new A2dpEndpoint();
            connection.exportObject(dbusPath, endpoint1);

            try {
                media1.RegisterEndpoint(endpoint1, properties);
            } catch (Exception x) {
                logger.warn("Error registering endpoint: {}", x.getMessage());
                logger.warn("Error registering endpoint: {}", x.getStackTrace());
            }

            // connection.addSigHandler(MediaItem1.class, this);

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

    class A2dpEndpoint implements MediaEndpoint1, DBusSigHandler {
        private String dbusPath = null;
        private MediaTransport1 mediaTransport1;

        @Override
        public boolean isRemote() {
            return false;
        }

        @Override
        public void SetConfiguration(DBusInterface path, Map<String, Variant> properties) {
            logger.debug("BlueZ A2DP SetConfiguration {} {}", path, properties);
            // dbusPath = path.toString().split(":")[2];

            // dbusPath = (String) properties.get(BluezBluetoothConstants.BLUEZ_DBUS_A2DP_PROPERTY_DEVICE).getValue();
            dbusPath = properties.get(BluezBluetoothConstants.BLUEZ_DBUS_A2DP_PROPERTY_DEVICE).getValue().toString();

            SbcConfiguration config = new SbcConfiguration(
                    (byte[]) properties.get(BluezBluetoothConstants.BLUEZ_DBUS_A2DP_PROPERTY_CONFIGURATION).getValue());
            logger.debug("Incoming audio connection request from {}",
                    properties.get(BluezBluetoothConstants.BLUEZ_DBUS_A2DP_PROPERTY_DEVICE).getValue());
            logger.debug("Codecs               {}",
                    properties.get(BluezBluetoothConstants.BLUEZ_DBUS_A2DP_PROPERTY_CODEC).getValue());
            logger.debug("Configuration        {}",
                    properties.get(BluezBluetoothConstants.BLUEZ_DBUS_A2DP_PROPERTY_CONFIGURATION).getValue());
            logger.debug("Channel Mode         {}", config.getChannelModeAsString());
            logger.debug("Sampling Frequencies {}", config.getSamplingFreqAsString());
            logger.debug("Allocation Method    {}", config.getAllocationAsString());
            logger.debug("Bitpool              {} <> {}", config.getBitpoolMin(), config.getBitpoolMax());
            logger.debug("State                {}",
                    properties.get(BluezBluetoothConstants.BLUEZ_DBUS_A2DP_PROPERTY_STATE).getValue());

            try {
                connection.addSigHandler(PropertiesChanged.class, this);
            } catch (DBusException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                logger.debug("Acquiring media transport {}", dbusPath);
                mediaTransport1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, dbusPath,
                        MediaTransport1.class);
                // mediaTransport1.Release();
                Triplet<UnixFD, UInt16, UInt16> response = mediaTransport1.Acquire();
                logger.debug("Acquired media transport {}: {} {} {}", dbusPath, response.a, response.b, response.c);
            } catch (DBusException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mediaTransport1 == null) {
                logger.error("BlueZ error creating MediaTransport1 at {}", dbusPath);
                return;
            }

        }

        @Override
        public Map<String, Variant> SelectConfiguration(byte[] capabilities) {
            logger.debug("BlueZ A2DP SelectConfiguration {}", capabilities);
            return null;
        }

        @Override
        public void ClearConfiguration(DBusInterface path) {
            logger.debug("BlueZ A2DP ClearConfiguration {}", path);
        }

        @Override
        public void Release() {
            logger.debug("BlueZ A2DP Release");
        }

        @Override
        public void handle(DBusSignal signal) {
            logger.debug("A2DP Signal: {}", signal);
            if (signal.getName().equals(BluezBluetoothConstants.BLUEZ_DBUS_SIGNAL_PROPERTIESCHANGED)) {
                // Make sure it's for us
                if (dbusPath.equals(signal.getPath()) == false) {
                    return;
                }
            }
        }
    }

    private class SbcConfiguration {
        int channelMode;
        int samplingFreq;
        int blockLength;
        int subBands;
        int allocation;
        int bitpoolMin;
        int bitpoolMax;

        public SbcConfiguration(byte[] config) {
            samplingFreq = (config[0] >> 4) & 0x0f;
            channelMode = (config[0] & 0x0f);
            allocation = (config[1] & 0x03);
            subBands = (config[1] >> 2) & 0x03;
            blockLength = (config[1] >> 4) & 0x0f;
            bitpoolMin = config[2];
            bitpoolMax = config[3];
        }

        public String getSamplingFreqAsString() {
            String response = "";
            if ((samplingFreq & 0x01) > 0) {
                response += "48.0k ";
            }
            if ((samplingFreq & 0x02) > 0) {
                response += "44.1k ";
            }
            if ((samplingFreq & 0x04) > 0) {
                response += "32.0k ";
            }
            if ((samplingFreq & 0x08) > 0) {
                response += "16.0k ";
            }

            return response;
        }

        public String getChannelModeAsString() {
            String response = "";
            if ((channelMode & 0x01) > 0) {
                response += "JOINT_STEREO ";
            }
            if ((channelMode & 0x02) > 0) {
                response += "STEREO ";
            }
            if ((channelMode & 0x04) > 0) {
                response += "DUAL_CHANNEL ";
            }
            if ((channelMode & 0x08) > 0) {
                response += "MONO ";
            }

            return response;
        }

        public int getBitpoolMin() {
            return bitpoolMin;
        }

        public int getBitpoolMax() {
            return bitpoolMax;
        }

        public String getAllocationAsString() {
            String response = "";
            if (allocation == 0x01) {
                response += "LOUDNESS ";
            }
            if (allocation == 0x02) {
                response += "SNR ";
            }

            return response;
        }
    }
}
