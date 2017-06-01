package org.eclipse.smarthome.io.transport.bluetooth.bluegiga;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.smarthome.io.transport.bluetooth.BluetoothAdapter;
import org.eclipse.smarthome.io.transport.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.io.transport.bluetooth.bluegiga.le.BlueGigaBluetoothLeScanner;
import org.eclipse.smarthome.io.transport.bluetooth.bluegiga.le.BlueGigaScanRecord;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothDeviceDiscoveredEvent;
import org.eclipse.smarthome.io.transport.bluetooth.events.BluetoothScanEvent;
import org.eclipse.smarthome.io.transport.bluetooth.le.BluetoothLeScanner;
import org.eclipse.smarthome.io.transport.bluetooth.le.ScanRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zsmartsystems.bluetooth.bluegiga.BlueGigaCommand;
import com.zsmartsystems.bluetooth.bluegiga.BlueGigaEventListener;
import com.zsmartsystems.bluetooth.bluegiga.BlueGigaResponse;
import com.zsmartsystems.bluetooth.bluegiga.BlueGigaSerialHandler;
import com.zsmartsystems.bluetooth.bluegiga.command.connection.BlueGigaDisconnectCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaDiscoverCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaDiscoverResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaEndProcedureCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaEndProcedureResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaScanResponseEvent;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaAddressGetCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaAddressGetResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaGetConnectionsCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaGetConnectionsResponse;
import com.zsmartsystems.bluetooth.bluegiga.eir.EirDataType;
import com.zsmartsystems.bluetooth.bluegiga.eir.EirPacket;
import com.zsmartsystems.bluetooth.bluegiga.enumeration.BgApiResponse;
import com.zsmartsystems.bluetooth.bluegiga.enumeration.GapDiscoverMode;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * Implementation of BluetoothAdapter for BlueGiga API
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class BlueGigaBluetoothAdapter extends BluetoothAdapter implements BlueGigaEventListener {

    private final Logger logger = LoggerFactory.getLogger(BlueGigaBluetoothAdapter.class);

    /**
     * Scan consolidation map. Consolidates different scan packets to get the max information
     */
    private Map<String, Map<EirDataType, Object>> deviceScanMap = new TreeMap<String, Map<EirDataType, Object>>();

    /**
     * The portName portName.
     */
    private static SerialPort serialPort;

    /**
     * The portName portName input stream.
     */
    private static InputStream inputStream;

    /**
     * The portName portName output stream.
     */
    private static OutputStream outputStream;

    private BlueGigaSerialHandler bleHandler;

    private boolean enabled = false;
    private boolean scanning = false;

    public BlueGigaBluetoothAdapter(String adapter) {
        logger.debug("Opening BlueGiga BLE adaptor on {}", adapter);

        openSerialPort(adapter);
        bleHandler = new BlueGigaSerialHandler(inputStream, outputStream);

        bleHandler.addEventListener(this);

        BlueGigaCommand command = new BlueGigaAddressGetCommand();
        BlueGigaAddressGetResponse addressResponse = (BlueGigaAddressGetResponse) bleHandler.sendTransaction(command);
        if (addressResponse != null) {
            address = addressResponse.getAddress();
        }

        // Stop scanning or other procedures so we know the state
        command = new BlueGigaEndProcedureCommand();
        bleHandler.sendTransaction(command);

        // Close all transactions
        command = new BlueGigaGetConnectionsCommand();
        BlueGigaGetConnectionsResponse connectionsResponse = (BlueGigaGetConnectionsResponse) bleHandler
                .sendTransaction(command);
        if (connectionsResponse != null) {
            for (int connection = 0; connection < connectionsResponse.getMaxconn(); connection++) {
                BlueGigaDisconnectCommand disconnectCommand = new BlueGigaDisconnectCommand();
                disconnectCommand.setConnection(connection);
                bleHandler.sendTransaction(disconnectCommand);
            }
        }

        leReady = true;
        enabled = true;
    }

    @Override
    public void finalize() {
        closeSerialPort();
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean startDiscovery() {
        // Don't start scanning if we're already scanning
        if (scanning) {
            return true;
        }

        BlueGigaDiscoverCommand command = new BlueGigaDiscoverCommand();
        command.setMode(GapDiscoverMode.GAP_DISCOVER_OBSERVATION);
        BlueGigaDiscoverResponse response = (BlueGigaDiscoverResponse) bleHandler.sendTransaction(command);
        if (response == null || response.getResult() != BgApiResponse.SUCCESS) {
            return false;
        }

        return true;
    }

    @Override
    public boolean cancelDiscovery() {
        // Don't stop scanning if we're not already scanning
        if (scanning == false) {
            return true;
        }

        BlueGigaCommand command = new BlueGigaEndProcedureCommand();
        BlueGigaEndProcedureResponse response = (BlueGigaEndProcedureResponse) bleHandler.sendTransaction(command);
        if (response == null || response.getResult() != BgApiResponse.SUCCESS) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isDiscovering() {
        return scanning;
    }

    @Override
    public boolean isLeReady() {
        return true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    // @Override
    // public BluetoothDevice getRemoteDevice(String address) {
    // return new BlueGigaBluetoothDevice(this, address);
    // }

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
        return new BlueGigaBluetoothLeScanner(this);
    }

    private void addDevice(String address) {
        BluetoothDevice bluetoothDevice;
        // Make sure we don't already know about this device
        if (bluetoothDevices.containsKey(address)) {
            bluetoothDevice = bluetoothDevices.get(address);
        } else {
            bluetoothDevice = new BlueGigaBluetoothDevice(this, address);
            bluetoothDevices.put(address, bluetoothDevice);
            logger.debug("BlueGiga device '{}' added Ok.", bluetoothDevice.getName());
        }

        // Send the notification even if we know about this device already.
        // Otherwise there's no way to notify of existing devices (??)
        notifyEventListeners(new BluetoothDeviceDiscoveredEvent(bluetoothDevice));
    }

    /**
     * Opens serial port.
     *
     * @param portName the port name
     * @param baudRate the baud rate
     */
    private void openSerialPort(String portName) {
        if (serialPort != null) {
            throw new RuntimeException("BlueGiga serial port already open.");
        }

        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            CommPort commPort = portIdentifier.open("org.eclipse.smarthome.io.transport.bluetooth.bluegiga", 2000);
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.enableReceiveThreshold(1);
            serialPort.enableReceiveTimeout(2000);
            serialPort.setFlowControlMode(gnu.io.SerialPort.FLOWCONTROL_RTSCTS_OUT);
            logger.debug("Starting receive thread");

            // RXTX serial port library causes high CPU load
            // Start event listener, which will just sleep and slow down event loop
            serialPort.notifyOnDataAvailable(true);

            logger.info("Serial port is initialized");
        } catch (NoSuchPortException e) {
            throw new IllegalArgumentException("BlueGiga Serial port not found");
        } catch (PortInUseException e) {
            throw new IllegalArgumentException("BlueGiga Serial port in use");
        } catch (UnsupportedCommOperationException e) {
            throw new IllegalArgumentException("BlueGiga Serial port unsupported operation");
        }

        try {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            logger.debug("Error getting serial streams", e);
        }
    }

    private void closeSerialPort() {
        try {
            if (serialPort != null) {
                serialPort.enableReceiveTimeout(1);

                inputStream.close();
                outputStream.flush();
                outputStream.close();

                serialPort.close();

                serialPort = null;
                inputStream = null;
                outputStream = null;

                logger.info("BlueGiga serial port [{}] is closed.", serialPort);
            }
        } catch (Exception e) {
            logger.debug("Exception closing BlueGiga serial port {}", e);
        }
    }

    @Override
    public void bluegigaEventReceived(BlueGigaResponse event) {
        if (event instanceof BlueGigaScanResponseEvent) {
            BlueGigaScanResponseEvent scanEvent = (BlueGigaScanResponseEvent) event;

            EirPacket eir = new EirPacket(((BlueGigaScanResponseEvent) event).getData());
            if (deviceScanMap.get(scanEvent.getSender()) != null) {
                deviceScanMap.get(scanEvent.getSender()).putAll(eir.getRecords());
            } else {
                deviceScanMap.put(scanEvent.getSender(), eir.getRecords());
            }

            ScanRecord record = new BlueGigaScanRecord(scanEvent, deviceScanMap.get(scanEvent.getSender()));

            notifyEventListeners(new BluetoothScanEvent(record));
        }
    }
}
