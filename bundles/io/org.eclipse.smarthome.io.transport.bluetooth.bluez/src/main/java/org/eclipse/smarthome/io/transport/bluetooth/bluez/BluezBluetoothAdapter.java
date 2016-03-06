/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.bluez;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.Adapter1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.Device1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.ObjectManager;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.ObjectManager.InterfacesAdded;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.ObjectManager.InterfacesRemoved;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.Profile1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.ProfileManager1;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.internal.dbus.Properties.PropertiesChanged;
import org.eclipse.smarthome.io.transport.bluetooth.bluez.le.BluezBluetoothLeScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of BluetoothAdapter for BlueZ
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BluezBluetoothAdapter extends BluetoothAdapter implements DBusSigHandler {

    private static final Logger logger = LoggerFactory.getLogger(BluezBluetoothAdapter.class);

    private Adapter1 adapter1;
    private ProfileManager1 profileManager1;
    private DBusConnection connection;
    private DBus.Properties propertyReader;
    private String adapter;
    private boolean scanning;

    private String dbusPath;

    public BluezBluetoothAdapter(String adapter) {
        this.adapter = adapter;
        dbusPath = BluezBluetoothConstants.BLUEZ_DBUS_PATH + "/" + adapter;
        logger.debug("Creating BlueZ adapter at '{}'", dbusPath);

        try {
            String dbusAddress = System.getProperty(BluezBluetoothConstants.BLUEZ_DBUS_CONFIGURATION);
            if (dbusAddress == null) {
                connection = DBusConnection.getConnection(DBusConnection.SYSTEM);
            } else {
                connection = DBusConnection.getConnection(dbusAddress);
            }
            logger.debug("BlueZ connection opened at {}", connection.getUniqueName());

            ObjectManager objectManager = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, "/",
                    ObjectManager.class);
            Map<Path, Map<String, Map<String, Variant>>> managedObjects = objectManager.GetManagedObjects();

            // Notify our user(s) of any devices that already exist - otherwise they won't know about them!
            if (managedObjects != null) {
                for (Map<String, Map<String, Variant>> managedObject : managedObjects.values()) {
                    Map<String, Variant> deviceProperties = managedObject
                            .get(BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_DEVICE1);
                    if (deviceProperties == null) {
                        continue;
                    }

                    Variant adapterPath = deviceProperties
                            .get(BluezBluetoothConstants.BLUEZ_DBUS_DEVICE_PROPERTY_ADAPTER);
                    if (adapterPath == null) {
                        continue;
                    }

                    String adapterName = adapterPath.getValue().toString();
                    if (dbusPath.equals(adapterName)) {
                        addInterface(deviceProperties);
                    }
                }
            }

            propertyReader = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, dbusPath,
                    DBus.Properties.class);

            Map<String, Variant> properties = propertyReader
                    .GetAll(BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_ADAPTER1);
            updateProperties(properties);

            connection.addSigHandler(PropertiesChanged.class, this);
            connection.addSigHandler(InterfacesAdded.class, this);
            connection.addSigHandler(InterfacesRemoved.class, this);

            adapter1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, dbusPath, Adapter1.class);

            Map<String, Variant> scanProperties = new HashMap<String, Variant>(1);
            scanProperties.put(BluezBluetoothConstants.BLUEZ_DBUS_DEVICE_PROPERTY_RSSI,
                    new Variant(new Short((short) -125)));
            adapter1.SetDiscoveryFilter(scanProperties);

            profileManager1 = connection.getRemoteObject(BluezBluetoothConstants.BLUEZ_DBUS_SERVICE, dbusPath,
                    ProfileManager1.class);

            Profile1 profile1 = new BluezProfile1();
            connection.exportObject(dbusPath, profile1);

            try {
                profileManager1.RegisterProfile(profile1, "", properties);
            } catch (Exception x) {
                logger.warn("Error registering profile: {}", x.getMessage());
                logger.warn("Error registering profile: {}", x.getStackTrace());
            }

        } catch (DBusException e) {
            e.printStackTrace();
        } catch (DBusExecutionException e) {
            logger.debug("DBus method failed: {}", e.getMessage());
        }
    }

    @Override
    public void finalize() {
        try {
            if (connection != null) {
                connection.removeSigHandler(PropertiesChanged.class, this);
                connection.removeSigHandler(InterfacesAdded.class, this);
                connection.removeSigHandler(InterfacesRemoved.class, this);

                connection.disconnect();
            }
        } catch (DBusException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getState() {
        state = ((Boolean) propertyReader.Get(BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_ADAPTER1,
                BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_POWERED)) ? STATE_ON : STATE_OFF;

        return state;
    }

    @Override
    public boolean isEnabled() {
        return getState() == STATE_ON ? true : false;
    }

    @Override
    public boolean startDiscovery() {
        for (BluetoothDevice device : bluetoothDevices.values()) {
            notifyEventListeners(new BluetoothDeviceDiscoveredEvent(device));
        }

        // Now start the discovery - only if we're not already discovering though!
        if (isDiscovering() == false) {
            adapter1.StartDiscovery();
        }

        return true;
    }

    @Override
    public boolean cancelDiscovery() {
        // Only stop discovery if there is a discovery in progress
        // Otherwise this can cause an exception
        if (isDiscovering() == false) {
            return false;
        }

        try {
            adapter1.StopDiscovery();
            return true;
        } catch (DBusExecutionException e) {
            logger.debug("Cancel discovery failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isDiscovering() {
        scanning = (Boolean) propertyReader.Get(BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_ADAPTER1,
                BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_DISCOVERING);

        return scanning;
    }

    @Override
    public boolean isLeReady() {
        return leReady;
    }

    @Override
    public void enable() {
        propertyReader.Set(BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_ADAPTER1,
                BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_POWERED, true);
    }

    @Override
    public void disable() {
        propertyReader.Set(BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_ADAPTER1,
                BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_POWERED, false);
    }

    @Override
    public BluetoothDevice getRemoteDevice(String address) {
        if (bluetoothDevices.containsKey(address)) {
            return bluetoothDevices.get(address);
        }
        return new BluezBluetoothDevice(this, address, "");
    }

    @Override
    public void startBeaconAdvertising() {
    }

    @Override
    public void stopBeaconAdvertising() {
    }

    @Override
    public void setBeaconAdvertisingInterval(Integer min, Integer max) {
    }

    @Override
    public void setBeaconAdvertisingData(String uuid, Integer major, Integer minor, String companyCode, Integer txPower,
            boolean LELimited, boolean LEGeneral, boolean BR_EDRSupported, boolean LE_BRController, boolean LE_BRHost) {
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> pairedDevices = new HashSet<BluetoothDevice>();

        return pairedDevices;
    }

    @Override
    public BluetoothLeScanner getBluetoothLeScanner() {
        if (leReady == false) {
            return null;
        }
        return new BluezBluetoothLeScanner(this);
    }

    /**
     *
     * @param properties
     */
    private void addInterface(Map<String, Variant> properties) {
        for (Entry<String, Variant> entry : properties.entrySet()) {
            logger.trace("Property: {} is {}", entry.getKey(), entry.getValue());
        }

        String newAddress = properties.get(BluezBluetoothConstants.BLUEZ_DBUS_DEVICE_PROPERTY_ADDRESS).getValue()
                .toString();
        if (newAddress == null) {
            logger.debug("Address not known. Aborting addDevice.");
            return;
        }

        BluetoothDevice bluetoothDevice;
        // Make sure we don't already know about this device
        if (bluetoothDevices.containsKey(newAddress)) {
            bluetoothDevice = bluetoothDevices.get(newAddress);
        } else {
            bluetoothDevice = new BluezBluetoothDevice(this, properties);
            bluetoothDevices.put(newAddress, bluetoothDevice);
            logger.debug("Device '{}' added Ok.", bluetoothDevice.getName());
        }

        // Send the notification even if we know about this device already.
        // Otherwise there's no way to notify of existing devices (??)
        notifyEventListeners(new BluetoothDeviceDiscoveredEvent(bluetoothDevice));
    }

    /**
     *
     * @param device
     */
    private void removeDevice(InterfacesRemoved device) {
        logger.info("BlueZ removeDevice: {}", device.object_path);
        // TODO: Do something!
    }

    @Override
    public void handle(DBusSignal signal) {
        try {
            if (signal.getName().equals(BluezBluetoothConstants.BLUEZ_DBUS_SIGNAL_PROPERTIESCHANGED)) {
                // Make sure it's for us
                if (dbusPath.equals(signal.getPath()) == false) {
                    return;
                }

                PropertiesChanged propertiesChanged = (PropertiesChanged) signal;

                if (BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_ADAPTER1
                        .equals(propertiesChanged.interface_name) == false) {
                    return;
                }
                if (propertiesChanged.changed_properties.size() != 0) {
                    logger.debug("{}: Properties changed: {}", dbusPath, propertiesChanged.changed_properties);
                    updateProperties(propertiesChanged.changed_properties);
                }
                if (propertiesChanged.invalidated_properties.size() != 0) {
                    // TODO: Implement this
                    logger.debug("{}: Properties invalid: {}", dbusPath, propertiesChanged.invalidated_properties);
                }
            } else if (signal.getName().equals(BluezBluetoothConstants.BLUEZ_DBUS_SIGNAL_INTERFACESADDED)) {
                // Get the properties for this device
                // If this is not a BlueZ device, then this will return null
                Map<String, Variant> properties = ((InterfacesAdded) signal).interfaces_and_properties
                        .get(BluezBluetoothConstants.BLUEZ_DBUS_INTERFACE_DEVICE1);
                if (properties == null) {
                    return;
                }

                addInterface(properties);
            } else if (signal instanceof InterfacesRemoved) {
                removeDevice((InterfacesRemoved) signal);
            } else {
                logger.info("Unknown signal!!! {}", signal.getClass());
            }
        } catch (DBusExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the adapter configuration from the Bluez DBus properties
     *
     * @param changed_properties
     */
    private void updateProperties(Map<String, Variant> properties) {
        for (String property : properties.keySet()) {
            logger.trace("Adapter '{}' updated property: {} to {}", dbusPath, property,
                    properties.get(property).getValue());
            switch (property) {
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_NAME:
                    // Name can't change, so if it's already set, then ignore
                    // This allows the name to be used if the alias isn't set
                    if (name == null || name == "") {
                        name = (String) properties.get(BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_NAME)
                                .getValue();
                    }
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_ALIAS:
                    name = (String) properties.get(BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_ALIAS)
                            .getValue();
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_ADDRESS:
                    address = (String) properties.get(BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_ADDRESS)
                            .getValue();
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_DISCOVERING:
                    scanning = (Boolean) properties.get(BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_DISCOVERING)
                            .getValue();
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_POWERED:
                    state = ((Boolean) properties.get(BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_POWERED)
                            .getValue()) ? STATE_ON : STATE_OFF;
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_UUIDS:
                    // Check if we support the GATT profile. If so, then we're LE ready
                    leReady = false;
                    Vector<String> uuids = (Vector<String>) properties
                            .get(BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_UUIDS).getValue();
                    for (String uuid : uuids) {
                        if (BluetoothProfile.PROFILE_GATT.toString().equals(uuid)) {
                            leReady = true;
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void invalidateDeviceProperties(List<String> invalidated_properties) {
        for (String property : invalidated_properties) {
            logger.trace("GATT Service '{}' invalidated property: {}", dbusPath, property);
            switch (property) {
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_NAME:
                    // Name can't change, so if it's already set, then ignore
                    // This allows the name to be used if the alias isn't set
                    name = "";
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_ALIAS:
                    name = "";
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_ADDRESS:
                    address = "";
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_DISCOVERING:
                    scanning = false;
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_POWERED:
                    state = STATE_OFF;
                    break;
                case BluezBluetoothConstants.BLUEZ_DBUS_ADAPTER_PROPERTY_UUIDS:
                    // Check if we support the GATT profile. If so, then we're LE ready
                    leReady = false;
                    break;
                default:
                    break;
            }
        }
    }

    public String getDbusPath() {
        return dbusPath;
    }

    private class BluezProfile1 implements Profile1 {

        @Override
        public boolean isRemote() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void NewConnection(Device1 device, int fd, Map<String, Variant> properties) {
            // TODO Auto-generated method stub

        }

        @Override
        public void RequestDisconnection(Device1 device) {
            // TODO Auto-generated method stub

        }

        @Override
        public void Release() {
            // TODO Auto-generated method stub

        }

    }

}
