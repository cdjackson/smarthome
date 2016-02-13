/**
 * Copyright (c) 1997, 2015 by Huawei Technologies Co., Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth;

import java.util.Collections;
import java.util.List;

/**
 * Abstract class for the A2DP Bluetooth Profile
 *
 * @author Chris Jackson - Initial Contribution
 */
public abstract class BluetoothA2dp implements BluetoothProfile {

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
    public boolean isA2dpPlaying(BluetoothDevice device) {
        return false;
    }

    /**
     * Connect to devices A2DP server.
     *
     * @return true, if the connection attempt was initiated successfully
     */
    public boolean connect() {
        return false;
    }
}
