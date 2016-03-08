/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.blukii;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link BlukiiBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Chris Jackson - Initial contribution
 */
public class BlukiiBindingConstants {

    public static final String BINDING_ID = "blukii";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");

    public final static String THING_NAME_GENERIC = "Bluetooth Device";
    public final static ThingTypeUID THING_TYPE_GENERIC = new ThingTypeUID(BINDING_ID, "generic");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";

    public final static String PROPERTY_ADDRESS = "address";
    public final static String PROPERTY_MANUFACTURER = "manufacturer";

}
