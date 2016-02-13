/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.bluetooth.internal.discovery;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothAdapter;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothEventListener;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothManager;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothManufacturer;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothProfile;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothDeviceDiscoveredEvent;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BluetoothDiscoveryService} handles searching for new Bluetooth adapters
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BluetoothDiscoveryService extends AbstractDiscoveryService implements BluetoothEventListener {
    private final Logger logger = LoggerFactory.getLogger(BluetoothDiscoveryService.class);

    private final static int SEARCH_TIME = 15;

    private BluetoothAdapter adapter = null;

    // List<BluetoothDevice> devicesDiscovered = new ArrayList<BluetoothDevice>();

    public BluetoothDiscoveryService() {
        super(SEARCH_TIME);

        logger.debug("Creating Bluetooth adapter discovery service");
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return Collections.singleton(BluetoothBindingConstants.THING_UID_AUDIO);
    }

    @Override
    public void startScan() {
        if (adapter != null) {
            // logger.debug("Adapter not null - scan already in progress");
            // return;
        }

        logger.debug("Start Bluetooth adapter LE scan");
        if (adapter == null) {
            adapter = BluetoothManager.getDefaultAdapter();
        }
        if (adapter == null) {
            logger.info("No default bluetooth adapter found");
            return;
        }

        if (adapter.isEnabled() == false) {
            logger.info("Bluetooth adapter is disabled");
            return;
        }

        // If the adapter is enabled and supports BLE then start a scan
        if (adapter.isEnabled() == false || adapter.isLeReady() == false) {
            logger.info("Bluetooth adapter is disabled or not BLE capable");
            return;
        }

        adapter.addEventListener(this);
        adapter.startDiscovery();
    }

    @Override
    public void stopScan() {
        if (adapter == null) {
            return;
        }

        adapter.cancelDiscovery();
    }

    void addDiscoveryResultThing(String thingType, String address, String label, String manufacturer) {
        // Sanatise the address
        String thingId = address.toLowerCase().replaceAll("[^a-z0-9_/]", "");

        // Create the new thing
        ThingTypeUID thingTypeUID = new ThingTypeUID(thingType);
        logger.info("Creating new Bluetooth thing {} {}", thingTypeUID, thingId);
        ThingUID thingUID = new ThingUID(thingTypeUID, thingId);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put(BluetoothBindingConstants.PROPERTY_ADDRESS, address);
        // if (manufacturer != null) {
        // properties.put(BluetoothBindingConstants.PROPERTY_MANUFACTURER, manufacturer);
        // }
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withLabel(label).build();

        thingDiscovered(discoveryResult);
    }

    @Override
    public void handleBluetoothEvent(BluetoothEvent event) {
        if (!(event instanceof BluetoothDeviceDiscoveredEvent)) {
            return;
        }

        BluetoothDeviceDiscoveredEvent discoveryEvent = (BluetoothDeviceDiscoveredEvent) event;
        BluetoothDevice device = discoveryEvent.getDevice();

        // Check for devices that support the profiles we want
        // UUID[] deviceUuids = device.getUuids();
        List<UUID> deviceUuids = Arrays.asList(device.getUuids());
        // boolean found = false;
        // if (deviceUuids.contains(BluetoothProfile.PROFILE_HFP)) {
        // logger.debug("Includes HFP");
        // found = true;
        // }
        for (UUID uuid : deviceUuids) {
            logger.debug("{}: Device UUID: {}", device.getAddress(), uuid);
        }

        if (deviceUuids.contains(BluetoothProfile.PROFILE_A2DP_SINK) == false) {
            logger.debug("Device not supported");
            return;
        }

        String label = BluetoothBindingConstants.THING_NAME_AUDIO;
        String thingTypeUID = BluetoothBindingConstants.THING_UID_AUDIO.getAsString();

        String manufacturerName = null;
        BluetoothManufacturer manufacturer = BluetoothManufacturer.getManufacturer(device.getManufacturer());
        if (manufacturer != null) {
            label += " by " + manufacturer.getLabel();
            manufacturerName = manufacturer.getLabel();
        }

        if (device.getName() != null) {
            label += " (" + device.getName() + ")";
        }
        addDiscoveryResultThing(thingTypeUID, device.getAddress(), label, manufacturerName);
    }
}
