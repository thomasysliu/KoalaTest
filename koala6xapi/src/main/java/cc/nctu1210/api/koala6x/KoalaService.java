package cc.nctu1210.api.koala6x;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KoalaService extends Service {
    private final static String TAG = KoalaService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private List<Map<String, BluetoothGatt>> mBluetoothGattList = new ArrayList<Map<String, BluetoothGatt>>();

    /**
     * Provided actions
     */
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_RSSI = "ACTION_GATT_RSSI";
    public final static String ACTION_RAW_ACC_DATA_AVAILABLE = "ACTION_RAW_ACC_DATA_AVAILABLE";
    public final static String ACTION_RAW_GYRO_DATA_AVAILABLE = "ACTION_RAW_GYRO_DATA_AVAILABLE";
    public final static String ACTION_RAW_MAG_DATA_AVAILABLE = "ACTION_RAW_MAG_DATA_AVAILABLE";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";

    public final static String EXTRA_DATA = "EXTRA_DATA";
    public final static String EXTRA_DATA_SEQ = "EXTRA_DATA_SEQ";
    public final static String EXTRA_NAME = "EXTRA_NAME";

    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); //CLIENT_CHARACTERISTIC_CONFIG
    public static final UUID UUID_KOALA_MOTION_SERVICE = UUID.fromString(Koala6xGattAttributes.KOALA_MOTION_SERVICE_UUID);
    public static final UUID UUID_KOALA_MOTION_MEASUREMENT_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.KOALA_MOTION_MEASUREMENT_CHARACTERISTIC_UUID);
    public final static UUID UUID_KOALA_MOTION_RATE_CHANGE_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.KOALA_MOTION_RATE_CHANGE_CHARACTERISTIC_UUID);
    public final static UUID UUID_KOALA_MOTION_ACC_FSR_CHANGE_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.KOALA_MOTION_ACC_FSR_CHANGE_CHARACTERISTIC_UUID);
    public final static UUID UUID_KOALA_MOTION_GYRO_FSR_CHANGE_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.KOALA_MOTION_GYRO_FSR_CHANGE_CHARACTERISTIC_UUID);

    /*
    public static final byte MOTION_WRITE_RATE_OPCODE            =     0x02;
    public static final byte MOTION_WRITE_ACC_SCALE_OPCODE       =    0x03;
    public static final byte MOTION_WRITE_GYRO_SCALE_OPCODE      =     0x04;
    */

    public static final int MOTION_WRITE_RATE_40      =  0x00;
    public static final int MOTION_WRITE_RATE_20      =  0x01;
    public static final int MOTION_WRITE_RATE_10      =  0x02;
    public static final int MOTION_WRITE_RATE_5      =  0x03;

    public static final int MOTION_ACCEL_SCALE_2G      =     0x00;
    public static final int MOTION_ACCEL_SCALE_4G       =    0x01;
    public static final int MOTION_ACCEL_SCALE_8G      =     0x02;
    public static final int MOTION_ACCEL_SCALE_16G      =    0x03;

    public static final int MOTION_GYRO_SCALE_250      =     0x00;
    public static final int MOTION_GYRO_SCALE_500       =    0x01;
    public static final int MOTION_GYRO_SCALE_1000      =     0x02;
    public static final int MOTION_GYRO_SCALE_2000      =    0x03;

    private static int sensor_write_rate = MOTION_WRITE_RATE_10;
    private static int acc_fsr = MOTION_ACCEL_SCALE_2G;
    private static int gyro_fsr = MOTION_GYRO_SCALE_250;

    private boolean is_9x = false;
    public static final UUID UUID_NAXSEN_MOTION_SERVICE = UUID.fromString(Koala6xGattAttributes.NAXSEN_MOTION_SERVICE_UUID);
    public static final UUID UUID_NAXSEN_MOTION_MEASUREMENT_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.NAXSEN_MOTION_MEASUREMENT_CHARACTERISTIC_UUID);

    public static final UUID UUID_NAXSEN_MOTION_DMP_MEASUREMENT_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.NAXSEN_MOTION_DMP_MEASUREMENT_CHARACTERISTIC_UUID);
    public static final UUID UUID_NAXSEN_BATTERY = UUID.fromString(Koala6xGattAttributes.NAXSEN_BATTERY_UUID);
    public static final UUID UUID_NAXSEN_TX_POWER_SERVICE = UUID.fromString(Koala6xGattAttributes.NAXSEN_TX_POWER_SERVICE_UUID);
    public static final UUID UUID_NAXSEN_TX_POWER = UUID.fromString(Koala6xGattAttributes.NAXSEN_TX_POWER_UUID);
    public static final UUID UUID_NAXSEN_BATTERY_SERVICE = UUID.fromString(Koala6xGattAttributes.NAXSEN_BATTERY_SERVICE_UUID);
    public static final UUID UUID_NAXSEN_STORAGE_SERVICE = UUID.fromString(Koala6xGattAttributes.NAXSEN_STORAGE_SERVICE_UUID);
    public static final UUID UUID_NAXSEN_STORAGE_CMD = UUID.fromString(Koala6xGattAttributes.NAXSEN_STORAGE_CMD_CHARACTERISTIC_UUID);
    public static final UUID UUID_NAXSEN_STORAGE_READ = UUID.fromString(Koala6xGattAttributes.NAXSEN_STORAGE_READ_CHARACTERISTIC_UUID);
    public static final UUID UUID_NAXSEN_STORAGE_READ_CONFIG = UUID.fromString(Koala6xGattAttributes.NAXSEN_STORAGE_READ_CONFIG_UUID);
    /*public final static UUID UUID_NAXSEN_MOTION_RATE_CHANGE_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.NAXSEN_MOTION_RATE_CHANGE_CHARACTERISTIC_UUID);
    public final static UUID UUID_NAXSEN_MOTION_ACC_FSR_CHANGE_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.NAXSEN_MOTION_ACC_FSR_CHANGE_CHARACTERISTIC_UUID);
    public final static UUID UUID_NAXSEN_MOTION_GYRO_FSR_CHANGE_CHARACTERISTIC = UUID.fromString(Koala6xGattAttributes.NAXSEN_MOTION_GYRO_FSR_CHANGE_CHARACTERISTIC_UUID);
*/


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();



    public class LocalBinder extends Binder {
        public KoalaService getService() {
            return KoalaService.this;
        }
    }

    /**
     * The callback function to handle the connection stage of BLE
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String intentAction;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                if(gatt.getDevice().getName() != null){
                    is_9x = true;
                }
                Log.i(TAG, "is_9x = "+ is_9x);
                Log.i(TAG, "gatt.getDevice().getName() = "+ gatt.getDevice().getName());

                String addr = gatt.getDevice().getAddress();
                // dirty
                broadcastUpdate(intentAction, addr);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:"
                        + gatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                String addr = gatt.getDevice().getAddress();
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction, addr);
            }
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String addr = gatt.getDevice().getAddress();
                broadcastUpdate(ACTION_GATT_RSSI, addr, rssi);
            } else {
                Log.w(TAG, "onReadRemoteRssi received: " + status);
            }
        };

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String addr = gatt.getDevice().getAddress();
                _set_storage_notify(addr);
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, addr);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String addr = gatt.getDevice().getAddress();
                broadcastUpdate(ACTION_DATA_AVAILABLE, addr, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            String addr = gatt.getDevice().getAddress();
            broadcastUpdate(ACTION_DATA_AVAILABLE, addr, characteristic);
        }
    };

    private void broadcastUpdate(final String action, final String addr) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_NAME, String.valueOf(addr));
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String addr, int rssi) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_NAME, String.valueOf(addr));
        intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
        sendBroadcast(intent);
    }

    /**
     * Fire sensor event.
     * @param action
     * @param addr
     * @param characteristic
     */
    private void broadcastUpdate(final String action,
                                 final String addr,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_NAME, String.valueOf(addr));
        // This is special handling for the Motion Raw Data Measurement profile. Data
        if (UUID_KOALA_MOTION_MEASUREMENT_CHARACTERISTIC.equals(characteristic.getUuid()) || UUID_NAXSEN_MOTION_MEASUREMENT_CHARACTERISTIC.equals(characteristic.getUuid())) {
            final byte[] rx = characteristic.getValue();
            //fire sensor event
            fireSensorEvent(addr, rx);
            return;
        }

        sendBroadcast(intent);
    }

    private void fireSensorEvent(String addr, byte [] values) {
        int sequence = values[0];
        double accData[] = new double[3];
        double gyroData[] = new double[3];
        double magData[] = new double[3];
        double accel_x = 0.0;
        double accel_y = 0.0;
        double accel_z = 0.0;
        double gyro_x = 0.0;
        double gyro_y = 0.0;
        double gyro_z = 0.0;
        double mag_x = 0.0;
        double mag_y = 0.0;
        double mag_z = 0.0;
        short s_accel_x =0;
        short s_accel_y =0;
        short s_accel_z =0;
        short s_gyro_x = 0;
        short s_gyro_y =0;
        short s_gyro_z =0;
        short s_mag_x = 0;
        short s_mag_y = 0;
        short s_mag_z = 0;

        if(!is_9x) {
            s_accel_x = (short) ((short) values[1] * 256 + values[2]);
            s_accel_y = (short) ((short) values[3] * 256 + values[4]);
            s_accel_z = (short) ((short) values[5] * 256 + values[6]);
            s_gyro_x = (short) ((short) values[7] * 256 + values[8]);
            s_gyro_y = (short) ((short) values[9] * 256 + values[10]);
            s_gyro_z = (short) ((short) values[11] * 256 + values[12]);
            s_mag_x = (short) ((short) values[13] * 256 + values[13]);
            s_mag_y = (short) ((short) values[14] * 256 + values[15]);
            s_mag_z = (short) ((short) values[16] * 256 + values[17]);
        }else{
            s_accel_x = (short) ((short) values[0] * 256 + values[1]);
            s_accel_y = (short) ((short) values[2] * 256 + values[3]);
            s_accel_z = (short) ((short) values[4] * 256 + values[5]);
            s_gyro_x = (short) ((short) values[6] * 256 + values[7]);
            s_gyro_y = (short) ((short) values[8] * 256 + values[9]);
            s_gyro_z = (short) ((short) values[10] * 256 + values[11]);
            s_mag_x = (short) ((short) values[12] * 256 + values[13]);
            s_mag_y = (short) ((short) values[14] * 256 + values[15]);
            s_mag_z = (short) ((short) values[16] * 256 + values[17]);
        }

        switch (acc_fsr) {
            case MOTION_ACCEL_SCALE_2G:
                accel_x = (double) (s_accel_x * 4.0d / Math.pow(2,16)) ; // +-2g
                accel_y = (double) (s_accel_y * 4.0d / Math.pow(2,16)) ;
                accel_z = (double) (s_accel_z * 4.0d / Math.pow(2,16)) ;
                break;
            case MOTION_ACCEL_SCALE_4G:
                accel_x = (double) (s_accel_x * 8.0d / Math.pow(2,16)) ; // +-4g
                accel_y = (double) (s_accel_y * 8.0d / Math.pow(2,16)) ;
                accel_z = (double) (s_accel_z * 8.0d / Math.pow(2,16)) ;
                break;
            case MOTION_ACCEL_SCALE_8G:
                accel_x = (double) (s_accel_x * 16.0d / Math.pow(2,16)) ; // +-8g
                accel_y = (double) (s_accel_y * 16.0d / Math.pow(2,16)) ;
                accel_z = (double) (s_accel_z * 16.0d / Math.pow(2,16)) ;
                break;
            case MOTION_ACCEL_SCALE_16G:
                accel_x = (double) (s_accel_x * 32.0d / Math.pow(2,16)) ; // +-16g
                accel_y = (double) (s_accel_y * 32.0d / Math.pow(2,16)) ;
                accel_z = (double) (s_accel_z * 32.0d / Math.pow(2,16)) ;
                break;
        }

        accData[0] = accel_x;
        accData[1] = accel_y;
        accData[2] = accel_z;

        switch (gyro_fsr) {
            case MOTION_GYRO_SCALE_250:
                gyro_x = (double) (s_gyro_x  * 500.0d / Math.pow(2,16));  //+-250
                gyro_y = (double) (s_gyro_y  * 500.0d / Math.pow(2,16));
                gyro_z = (double) (s_gyro_z  * 500.0d / Math.pow(2,16)) ;
                break;
            case MOTION_GYRO_SCALE_500:
                gyro_x = (double) (s_gyro_x  * 1000.0d / Math.pow(2,16));  //+-500
                gyro_y = (double) (s_gyro_y  * 1000.0d / Math.pow(2,16));
                gyro_z = (double) (s_gyro_z  * 1000.0d / Math.pow(2,16)) ;
                break;
            case MOTION_GYRO_SCALE_1000:
                gyro_x = (double) (s_gyro_x  * 2000.0d / Math.pow(2,16));  //+-1000
                gyro_y = (double) (s_gyro_y  * 2000.0d / Math.pow(2,16));
                gyro_z = (double) (s_gyro_z  * 2000.0d / Math.pow(2,16)) ;
                break;
            case MOTION_GYRO_SCALE_2000:
                gyro_x = (double) (s_gyro_x  * 4000.0d / Math.pow(2,16));  //+-2000
                gyro_y = (double) (s_gyro_y  * 4000.0d / Math.pow(2,16));
                gyro_z = (double) (s_gyro_z  * 4000.0d / Math.pow(2,16)) ;
                break;
        }

        gyroData[0] = gyro_x;
        gyroData[1] = gyro_y;
        gyroData[2] = gyro_z;

        if(!is_9x){
            magData[0] = (double) (s_mag_x * 9824 / 65520);  //+-4800 effective for 14bits data
            magData[1] = (double) (s_mag_y * 9824 / 65520);
            magData[2] = (double) (s_mag_z * 9824 / 65520);
        }else{
            magData[0] = s_mag_x * 4912. / 32760.;
            magData[1] = s_mag_y * 4912. / 32760.;
            magData[2] = s_mag_z * 4912. / 32760.;
        }

        Intent intent_acc = new Intent(KoalaService.ACTION_RAW_ACC_DATA_AVAILABLE);
        intent_acc.putExtra(KoalaService.EXTRA_NAME, String.valueOf(addr));
        intent_acc.putExtra(KoalaService.EXTRA_DATA, accData);
        intent_acc.putExtra(KoalaService.EXTRA_DATA_SEQ, sequence);
        sendBroadcast(intent_acc);

        Intent intent_gyro = new Intent(KoalaService.ACTION_RAW_GYRO_DATA_AVAILABLE);
        intent_gyro.putExtra(KoalaService.EXTRA_NAME, String.valueOf(addr));
        intent_gyro.putExtra(KoalaService.EXTRA_DATA_SEQ, sequence);
        intent_gyro.putExtra(KoalaService.EXTRA_DATA, gyroData);
        sendBroadcast(intent_gyro);

        Intent intent_mag = new Intent(KoalaService.ACTION_RAW_MAG_DATA_AVAILABLE);
        intent_mag.putExtra(KoalaService.EXTRA_NAME, String.valueOf(addr));
        intent_mag.putExtra(KoalaService.EXTRA_DATA_SEQ, sequence);
        intent_mag.putExtra(KoalaService.EXTRA_DATA, magData);
        sendBroadcast(intent_mag);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     *
     *
     *  Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address
     *            The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The
     *         connection result is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (!this.mBluetoothGattList.isEmpty()) {
            for (int i=0; i<this.mBluetoothGattList.size(); i++) {
                if (this.mBluetoothGattList.get(i).containsKey(address)) {
                    BluetoothGatt tmpGatt = this.mBluetoothGattList.get(i).get(address);
                    Log.d(TAG,
                            "Trying to use an existing mBluetoothGatt for connection.");
                    if (tmpGatt.connect()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        Map<String, BluetoothGatt> map = new HashMap<String, BluetoothGatt>();
        String macAddr = address;
        BluetoothGatt tmpGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        map.put(macAddr, tmpGatt);
        this.mBluetoothGattList.add(map);

        return true;
    }

    /**
     * Get Gatt server by id
     */
    protected BluetoothGatt getGattbyAddr(String addr) {
        for (int i=0; i<this.mBluetoothGattList.size(); i++) {
            if (this.mBluetoothGattList.get(i).containsKey(addr)) {
                return this.mBluetoothGattList.get(i).get(addr);
            }

        }
        return null;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || this.mBluetoothGattList.isEmpty()) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        for (int i=0; i<this.mBluetoothGattList.size(); i++) {
            BluetoothGatt tmpGatt =  this.mBluetoothGattList.get(i).values().iterator().next();
            String addr = tmpGatt.getDevice().getAddress();
            disableMotionRawService(addr);
            tmpGatt.disconnect();
        }
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public void close() {
        if (this.mBluetoothGattList.isEmpty()) {
            return;
        }
        for (int i=0; i<this.mBluetoothGattList.size(); i++) {
            BluetoothGatt tmpGatt =  this.mBluetoothGattList.get(i).values().iterator().next();
            tmpGatt.close();
            tmpGatt = null;
        }
        this.mBluetoothGattList.clear();
    }

    public boolean readRssi(String addr) {
        if (mBluetoothAdapter == null || this.mBluetoothGattList.isEmpty()) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        BluetoothGatt tmpGatt = getGattbyAddr(addr);
        tmpGatt.readRemoteRssi();
        return true;
    }

    /**
     *
     * @param addr
     * @return return true if the operation is successful.
     */
    public boolean enableMotionRawService(String addr) {
        //Log.w(TAG, "not support yet!!");
        BluetoothGatt tmpGatt = getGattbyAddr(addr);
        BluetoothGattService motionService;
        if(!is_9x){
            motionService = tmpGatt.getService(UUID_KOALA_MOTION_SERVICE);
        }else{
            motionService = tmpGatt.getService(UUID_NAXSEN_MOTION_SERVICE);
        }
        if (motionService == null) {
            Log.w(TAG, "motion service not found!");
            return false;
        }
        BluetoothGattCharacteristic motionRawDataCharacteristic;
        if(!is_9x){
            motionRawDataCharacteristic = motionService.getCharacteristic(UUID_KOALA_MOTION_MEASUREMENT_CHARACTERISTIC);
        }else{
            motionRawDataCharacteristic = motionService.getCharacteristic(UUID_NAXSEN_MOTION_MEASUREMENT_CHARACTERISTIC);
        }
        if (motionRawDataCharacteristic == null) {
            Log.w(TAG, "motion characteristic not found!");
            return false;
        }
        tmpGatt.setCharacteristicNotification(motionRawDataCharacteristic,true);

        BluetoothGattDescriptor descriptor = motionRawDataCharacteristic.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        tmpGatt.writeDescriptor(descriptor);
        motionRawDataCharacteristic.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return true;
    }

    /**
     * Disable the notification of raw data services
     */
    public boolean disableMotionRawService(String addr) {
        BluetoothGatt tmpGatt = getGattbyAddr(addr);
        BluetoothGattService motionService;
        if(!is_9x){
            motionService = tmpGatt.getService(UUID_KOALA_MOTION_SERVICE);
        }else{

            motionService = tmpGatt.getService(UUID_NAXSEN_MOTION_SERVICE);
        }
        if (motionService == null) {
            Log.w(TAG, "motion service not found!");
            return false;
        }

        BluetoothGattCharacteristic motionRawDataCharacteristic;
        if(!is_9x){
            motionRawDataCharacteristic = motionService.getCharacteristic(UUID_KOALA_MOTION_MEASUREMENT_CHARACTERISTIC);
        }else{
            motionRawDataCharacteristic = motionService.getCharacteristic(UUID_NAXSEN_MOTION_MEASUREMENT_CHARACTERISTIC);
        }
        if (motionRawDataCharacteristic == null) {
            Log.w(TAG, "motion characteristic not found!");
            return false;
        }
        tmpGatt.setCharacteristicNotification(motionRawDataCharacteristic,false);

        BluetoothGattDescriptor descriptor = motionRawDataCharacteristic.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        tmpGatt.writeDescriptor(descriptor);
        motionRawDataCharacteristic.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        return true;
    }

    /**
     * Configuration the scale of accel sensor
     *
     * @param scale
     *            int 0 MOTION_ACCEL_SCALE_2G: 2g
     *            int 1 MOTION_ACCEL_SCALE_4G:4g
     *            int 2 MOTION_ACCEL_SCALE_8G: 8g
     *             int 3 MOTION_ACCEL_SCALE_16G  16g
     */
    public boolean setMotionAccelScale(String addr, int scale){
        BluetoothGatt tmpGatt = getGattbyAddr(addr);
        BluetoothGattService motionService;
        if(!is_9x){
            motionService = tmpGatt.getService(UUID_KOALA_MOTION_SERVICE);
        }else{
            motionService = tmpGatt.getService(UUID_NAXSEN_MOTION_SERVICE);
        }
        if (motionService == null) {
            Log.w(TAG, "motion service not found!");
            return false;
        }

        BluetoothGattCharacteristic motionParamCharacteristic = motionService.getCharacteristic(UUID_KOALA_MOTION_ACC_FSR_CHANGE_CHARACTERISTIC);
        if (motionParamCharacteristic == null) {
            Log.w(TAG, "motion characteristic not found!");
            return false;
        }

        byte[] value = new byte[]{ 0x00 };

        switch (scale) {
            case MOTION_ACCEL_SCALE_2G:
                value[0] = 0x00;
                break;
            case MOTION_ACCEL_SCALE_4G:
                value[0] = 0x01;
                break;
            case MOTION_ACCEL_SCALE_8G:
                value[0] = 0x02;
                break;
            case MOTION_ACCEL_SCALE_16G:
                value[0] = 0x03;
                break;
            default:
                value[0] = 0x00;
                break;
        }
        motionParamCharacteristic.setValue(value);
        boolean status = tmpGatt.writeCharacteristic(motionParamCharacteristic);
        Log.d(TAG, "setMotionAccelScale: status:" + status);
        return status;
    }


    /**
     * Configuration the data write rate of motion sensor
     *
     * @param rate
     *        int 0 MOTION_WRITE_RATE_50: 50Hz
     *        int 1 MOTION_WRITE_RATE_40: 40Hz
     *        int 2 MOTION_WRITE_RATE_20: 20Hz
     *
     */
    public boolean setMotionDataWriteRate(String addr, int rate){
        BluetoothGatt tmpGatt = getGattbyAddr(addr);
        BluetoothGattService motionService;
        if(!is_9x){
            motionService = tmpGatt.getService(UUID_KOALA_MOTION_SERVICE);
        }else{

            motionService = tmpGatt.getService(UUID_NAXSEN_MOTION_SERVICE);
        }
        if (motionService == null) {
            Log.w(TAG, "motion service not found!");
            return false;
        }

        BluetoothGattCharacteristic motionParamCharacteristic = motionService.getCharacteristic(UUID_KOALA_MOTION_RATE_CHANGE_CHARACTERISTIC);
        if (motionParamCharacteristic == null) {
            Log.w(TAG, "motion characteristic not found!");
            return false;
        }

        byte[] value = new byte[]{ 0x00 };
        switch (rate) {
            case MOTION_WRITE_RATE_40:
                value[0] = 0x00;
                break;
            case MOTION_WRITE_RATE_20:
                value[0] = 0x01;
                break;
            case MOTION_WRITE_RATE_10:
                value[0] = 0x02;
                break;
            case MOTION_WRITE_RATE_5:
                value[0] = 0x03;
                break;
            default:
                value[0] = 0x02;
                break;
        }
        motionParamCharacteristic.setValue(value);
        boolean status = tmpGatt.writeCharacteristic(motionParamCharacteristic);
        Log.d(TAG, "setMotionDataWriteRate: status:" + status);
        return status;
    }

    /**
     * Configuration the scale of gyro sensor
     *
     * @param scale
     *            int 0 MOTION_GYRO_SCALE_250: 250
     *            int 1 MOTION_GYRO_SCALE_500:500
     *            int 2 MOTION_GYRO_SCALE 1000: 1000
     *            int3MOTION_GYRO_SCALE_2000  2000
     */
    public boolean setMotionGyroScale(String addr, int scale){
        BluetoothGatt tmpGatt = getGattbyAddr(addr);
        BluetoothGattService motionService;
        if(!is_9x){
            motionService = tmpGatt.getService(UUID_KOALA_MOTION_SERVICE);
        }else{

            motionService = tmpGatt.getService(UUID_NAXSEN_MOTION_SERVICE);
        }
        if (motionService == null) {
            Log.w(TAG, "motion service not found!");
            return false;
        }


        if(!is_9x) {
            BluetoothGattCharacteristic motionParamCharacteristic = motionService.getCharacteristic(UUID_KOALA_MOTION_GYRO_FSR_CHANGE_CHARACTERISTIC);
            if (motionParamCharacteristic == null) {
                Log.w(TAG, "motion characteristic not found!");
                return false;
            }

            byte[] value = new byte[]{0x00};
            switch (scale) {
                case MOTION_GYRO_SCALE_250:
                    value[0] = 0x00;
                    break;
                case MOTION_GYRO_SCALE_500:
                    value[0] = 0x01;
                    break;
                case MOTION_GYRO_SCALE_1000:
                    value[0] = 0x02;
                    break;
                case MOTION_GYRO_SCALE_2000:
                    value[0] = 0x03;
                    break;
                default:
                    value[0] = 0x00;
                    break;
            }
            motionParamCharacteristic.setValue(value);
            boolean status = tmpGatt.writeCharacteristic(motionParamCharacteristic);
            Log.d(TAG, "setMotionGyroScale: status:" + status);
            return status;
        }
        else {

            return true;
        }
    }

    /**
     * Configuration the scale of accel sensor
     *
     * @param acc_scale
     *            int 0 MOTION_ACCEL_SCALE_2G: 2g
     *            int 1 MOTION_ACCEL_SCALE_4G:4g
     *            int 2 MOTION_ACCEL_SCALE_8G: 8g
     *             int 3 MOTION_ACCEL_SCALE_16G  16g
     */
    public boolean setMotionData9x_FSR_Rate(String addr, final int acc_scale, final int gyro_scale, final int rate){
        BluetoothGatt tmpGatt = getGattbyAddr(addr);
        BluetoothGattService motionService;
        byte[] value = new byte[]{0x45, 0x02, 0x02, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        switch (acc_scale) {
            case MOTION_ACCEL_SCALE_2G:
                value[1] = 0x00;
                break;
            case MOTION_ACCEL_SCALE_4G:
                value[1] = 0x01;
                break;
            case MOTION_ACCEL_SCALE_8G:
                value[1] = 0x02;
                break;
            case MOTION_ACCEL_SCALE_16G:
                value[1] = 0x03;
                break;
            default:
                value[1] = 0x00;
                break;
        }
        switch (gyro_scale) {
            case MOTION_ACCEL_SCALE_2G:
                value[2] = 0x00;
                break;
            case MOTION_ACCEL_SCALE_4G:
                value[2] = 0x01;
                break;
            case MOTION_ACCEL_SCALE_8G:
                value[2] = 0x02;
                break;
            case MOTION_ACCEL_SCALE_16G:
                value[2] = 0x03;
                break;
            default:
                value[2] = 0x00;
                break;
        }
        Log.d(TAG, "setMotionData9x_FSR_Rate !!"+ formatData(value));
        _send_cmd(value, addr);
        return true;
    }
    public static void setSensorWriteRate(int rate) {
        sensor_write_rate = rate;
    }

    public static void setAccFSR(int scale) {
        acc_fsr = scale;
    }

    public static void setGyroFSR(int scale) {
        gyro_fsr = scale;
    }

    private void _return_status(Boolean status) {
        /*if (status) {
            broadcastUpdate(STATUS_DONE);
        } else {
            broadcastUpdate(STATUS_FAIL);
        }*/
    }

    static public String formatData(byte[] data) {
        if (data == null) {
            return "";
        }
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format("%02X", byteChar));
        return stringBuilder.toString();
    }

    private void _send_cmd(byte[] value, String addr) {
        //check mBluetoothGatt is available
        BluetoothGatt mBluetoothGatt = getGattbyAddr(addr);
        if (mBluetoothGatt == null) {
            Log.e(TAG, "lost connection");
            //return false;
            _return_status(false);
            return;
        }
        BluetoothGattService Service = mBluetoothGatt.getService(UUID_NAXSEN_STORAGE_SERVICE);
        if (Service == null) {
            Log.e(TAG, "service not found!");
            _return_status(false);
            return;
        }
        BluetoothGattCharacteristic charac = Service
                .getCharacteristic(UUID_NAXSEN_STORAGE_CMD);
        if (charac == null) {
            Log.e(TAG, "char not found!");
            _return_status(false);
            return;
        }
        charac.setValue(value);
        if (mBluetoothGatt == null) {
            Log.e(TAG, "lost connection");
            //return false;
            _return_status(false);
            return;
        }
        boolean status = mBluetoothGatt.writeCharacteristic(charac);

        Log.e(TAG, "send_cmd status:" + status);
        //Log.e(TAG, "status:"+status);
        _return_status(status);
    }

    private void _set_storage_notify(String addr) {
        enable_notify(UUID_NAXSEN_STORAGE_SERVICE, UUID_NAXSEN_STORAGE_READ, addr);
    }

    private void enable_notify(UUID service_uuid, UUID char_uuid, String addr) {
        BluetoothGatt mBluetoothGatt = getGattbyAddr(addr);
        //check mBluetoothGatt is available
        if (mBluetoothGatt == null) {
            Log.e(TAG, "lost connection");
            _return_status(false);
            return;
        }
        BluetoothGattService Service = mBluetoothGatt.getService(service_uuid);
        if (Service == null) {
            Log.e(TAG, "service not found!");
            _return_status(false);
            return;
        }
        BluetoothGattCharacteristic charac = Service
                .getCharacteristic(char_uuid);
        if (charac == null) {
            Log.e(TAG, "char not found!");
            _return_status(false);
            return;
        }
        //setStorageNotification(charac, true);

        mBluetoothGatt.setCharacteristicNotification(charac, true);

        BluetoothGattDescriptor descriptor = charac.getDescriptor(CCCD);
        if(descriptor == null){
            Log.e(TAG, "descriptor not found!");
            _return_status(false);
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        charac.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        _return_status(true);
    }
}