/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.transport.bluetooth.events;

import org.eclipse.smarthome.io.transport.bluetooth.le.ScanRecord;

/**
 * Bluetooth event notifying the a scan status event.
 *
 * @author Chris Jackson - Initial Implementation
 *
 */
public class BluetoothScanEvent extends BluetoothEvent {
    private ScanRecord record;

    public BluetoothScanEvent(ScanRecord record) {
        this.record = record;
    }

    public ScanRecord getScanRecord() {
        return record;
    }
}
