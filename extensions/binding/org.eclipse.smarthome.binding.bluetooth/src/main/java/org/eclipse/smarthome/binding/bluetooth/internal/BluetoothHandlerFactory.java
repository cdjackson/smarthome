/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.bluetooth.internal;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants;
import org.eclipse.smarthome.binding.bluetooth.handler.BluetoothHandler;
import org.eclipse.smarthome.binding.bluetooth.internal.discovery.BluetoothDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

/**
 * The {@link BleHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Chris Jackson - Initial contribution
 */
public class BluetoothHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .singleton(BluetoothBindingConstants.THING_UID_AUDIO);

    private BluetoothDiscoveryService adapterDiscoveryService;
    private ServiceRegistration<?> serviceReg;

    @Override
    protected void activate(ComponentContext componentContext) {
        super.activate(componentContext);

        // thingHandlers.put(BleBindingConstants.THING_TYPE_GENERIC, BleGenericThingHandler.class);
        // thingHandlers.put(BleBindingConstants.THING_TYPE_PARROT_FLOWERPOWER, ParrotFlowerPowerThingHandler.class);
        // thingHandlers.put(BleBindingConstants.THING_TYPE_WIT_ENERGY, WiTEnergyThingHandler.class);
        // thingHandlers.put(BleBindingConstants.THING_TYPE_YEELIGHT_BLUE, YeelightBlueThingHandler.class);

        // Start the discovery service
        adapterDiscoveryService = new BluetoothDiscoveryService();
        // adapterDiscoveryService.activate();

        // And register it as an OSGi service
        serviceReg = bundleContext.registerService(DiscoveryService.class.getName(), adapterDiscoveryService,
                new Hashtable<String, Object>());
    }

    @Override
    public void deactivate(ComponentContext componentContext) {
        super.activate(componentContext);

        // Remove the discovery service
        // adapterDiscoveryService.();
        serviceReg.unregister();
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(BluetoothBindingConstants.THING_UID_AUDIO)) {
            return new BluetoothHandler(thing);
        }

        return null;
    }
}
