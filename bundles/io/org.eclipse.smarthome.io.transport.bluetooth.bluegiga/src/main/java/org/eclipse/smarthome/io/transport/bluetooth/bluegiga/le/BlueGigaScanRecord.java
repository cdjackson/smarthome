package org.eclipse.smarthome.io.transport.bluetooth.bluegiga.le;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.eclipse.smarthome.io.transport.bluetooth.le.ScanRecord;

import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaScanResponseEvent;
import com.zsmartsystems.bluetooth.bluegiga.eir.EirDataType;

/**
 * Represents a scan record from Bluetooth LE scan.
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaScanRecord extends ScanRecord {

    /**
     * Creates a scan record from a Blue Giga scan response packet
     *
     * @param packet {@link BlueGigaScanResponseEvent} the latest scan packet
     * @param dataMap the map of properties consolidated in multiple scan packets
     */
    public BlueGigaScanRecord(BlueGigaScanResponseEvent packet, Map<EirDataType, Object> dataMap) {
        if (packet.getData().length != 0) {
            byte[] data = new byte[packet.getData().length];

            int cnt = 0;
            for (int val : packet.getData()) {
                data[cnt++] = (byte) (val & 0xff);
            }
        }

        if (dataMap.containsKey(EirDataType.EIR_TXPOWER)) {
            txPower = (int) dataMap.get(EirDataType.EIR_TXPOWER);
        }

        if (dataMap.containsKey(EirDataType.EIR_NAME_LONG)) {
            deviceName = (String) dataMap.get(EirDataType.EIR_NAME_LONG);
        } else if (dataMap.containsKey(EirDataType.EIR_NAME_SHORT)) {
            deviceName = (String) dataMap.get(EirDataType.EIR_NAME_SHORT);
        }

        serviceUuids = new ArrayList<UUID>();
        if (dataMap.containsKey(EirDataType.EIR_SVC_UUID16_COMPLETE)) {
            serviceUuids.addAll((Collection<UUID>) dataMap.get(EirDataType.EIR_SVC_UUID16_COMPLETE));
        }
        if (dataMap.containsKey(EirDataType.EIR_SVC_UUID32_COMPLETE)) {
            serviceUuids.addAll((Collection<UUID>) dataMap.get(EirDataType.EIR_SVC_UUID32_COMPLETE));
        }
        if (dataMap.containsKey(EirDataType.EIR_SVC_UUID128_COMPLETE)) {
            serviceUuids.addAll((Collection<UUID>) dataMap.get(EirDataType.EIR_SVC_UUID128_COMPLETE));
        }
        if (dataMap.containsKey(EirDataType.EIR_SVC_UUID16_INCOMPLETE)) {
            serviceUuids.addAll((Collection<UUID>) dataMap.get(EirDataType.EIR_SVC_UUID16_INCOMPLETE));
        }
        if (dataMap.containsKey(EirDataType.EIR_SVC_UUID32_INCOMPLETE)) {
            serviceUuids.addAll((Collection<UUID>) dataMap.get(EirDataType.EIR_SVC_UUID32_INCOMPLETE));
        }
        if (dataMap.containsKey(EirDataType.EIR_SVC_UUID128_INCOMPLETE)) {
            serviceUuids.addAll((Collection<UUID>) dataMap.get(EirDataType.EIR_SVC_UUID128_INCOMPLETE));
        }
    }
}
