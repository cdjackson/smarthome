/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.bluetooth;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link bluetoothBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Chris Jackson - Initial contribution
 */
public class BluetoothBindingConstants {

    public static final String BINDING_ID = "bluetooth";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_UID_AUDIO = new ThingTypeUID(BINDING_ID, "audio");

    public final static String THING_NAME_AUDIO = "Bluetooth Audio";

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";

    public static final String PROPERTY_ADDRESS = "address";

    public static final String THING_NAME_HFP = "Audio (HFP)";

}
