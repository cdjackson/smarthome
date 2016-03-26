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

    static protected final int A2DP_CODEC_SBC = 0x00;
    static protected final int A2DP_CODEC_MPEG12 = 0x01;
    static protected final int A2DP_CODEC_MPEG24 = 0x02;
    static protected final int A2DP_CODEC_ATRAC = 0x03;

    static protected final int SBC_SAMPLING_FREQ_16000 = (1 << 3);
    static protected final int SBC_SAMPLING_FREQ_32000 = (1 << 2);
    static protected final int SBC_SAMPLING_FREQ_44100 = (1 << 1);
    static protected final int SBC_SAMPLING_FREQ_48000 = 1;

    static protected final int SBC_CHANNEL_MODE_MONO = (1 << 3);
    static protected final int SBC_CHANNEL_MODE_DUAL_CHANNEL = (1 << 2);
    static protected final int SBC_CHANNEL_MODE_STEREO = (1 << 1);
    static protected final int SBC_CHANNEL_MODE_JOINT_STEREO = 1;

    static protected final int SBC_BLOCK_LENGTH_4 = (1 << 3);
    static protected final int SBC_BLOCK_LENGTH_8 = (1 << 2);
    static protected final int SBC_BLOCK_LENGTH_12 = (1 << 1);
    static protected final int SBC_BLOCK_LENGTH_16 = 1;

    static protected final int SBC_SUBBANDS_4 = (1 << 1);
    static protected final int SBC_SUBBANDS_8 = 1;

    static protected final int SBC_ALLOCATION_SNR = (1 << 1);
    static protected final int SBC_ALLOCATION_LOUDNESS = 1;

    static protected final int MPEG_CHANNEL_MODE_MONO = (1 << 3);
    static protected final int MPEG_CHANNEL_MODE_DUAL_CHANNEL = (1 << 2);
    static protected final int MPEG_CHANNEL_MODE_STEREO = (1 << 1);
    static protected final int MPEG_CHANNEL_MODE_JOINT_STEREO = 1;

    static protected final int MPEG_LAYER_MP1 = (1 << 2);
    static protected final int MPEG_LAYER_MP2 = (1 << 1);
    static protected final int MPEG_LAYER_MP3 = 1;

    static protected final int MPEG_SAMPLING_FREQ_16000 = (1 << 5);
    static protected final int MPEG_SAMPLING_FREQ_22050 = (1 << 4);
    static protected final int MPEG_SAMPLING_FREQ_24000 = (1 << 3);
    static protected final int MPEG_SAMPLING_FREQ_32000 = (1 << 2);
    static protected final int MPEG_SAMPLING_FREQ_44100 = (1 << 1);
    static protected final int MPEG_SAMPLING_FREQ_48000 = 1;

    static protected final int MAX_BITPOOL = 64;
    static protected final int MIN_BITPOOL = 2;

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
