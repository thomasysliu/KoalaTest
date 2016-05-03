package cc.nctu1210.tool;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.nctu1210.api.koala3x.KoalaDevice;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class BLESanner {
    private static final String TAG = BLESanner.class.getSimpleName();
    private static Handler mDeviceListHandler = null;
    private static Handler mMonitorListHandler = null;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int ADV_DATA_FLAG = 0X01;
    private static final int LIMITED_AND_GENERAL_DISC_MASK = 0x03;
    public static final int MODE_SCAN_NEW_DEVICE = 0x01;
    public static final int MODE_SCAN_MONITOR = 0x01>>2;
    public static final int MODE_NO_SCAN = 0x01>>3;
    public static final int SCAN_A_DEVICE = 0x01;
    public static final String EXTRA_RSSI = "EXTRA_RSSI";
    public static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    private static int mMode;
    /******** for SDK version > 21 **********/
    private BluetoothLeScanner mBLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    /******** for SDK version > 21 **********/
    private static long scanTime = 0L;

    public BLESanner(BluetoothAdapter bluetoothAdapter) {
        this.mBluetoothAdapter = bluetoothAdapter;
        if (Build.VERSION.SDK_INT >= 21) {
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
            Message msg = new Message();
        }
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void setDeviceListHandler(Handler handler) {
        mDeviceListHandler = handler;
    }

    public void setMonitorListHandler(Handler handler) {
        mMonitorListHandler = handler;
    }

    public void scanLeDevice(boolean scanFlag) {
        if (Build.VERSION.SDK_INT < 21) {
            if (scanFlag)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            else
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
        } else {
            if (scanFlag)
                mBLEScanner.startScan(mScanCallback);
            else
                mBLEScanner.stopScan(mScanCallback);
        }
    }

    public static double getDistancebyRSSI(int rssi) {
        return Math.exp((rssi+78.781)*(-1/6.274));
    }

    private boolean checkIfBroadcastMode(byte[] scanRecord) {
        int offset = 0;
        while (offset < (scanRecord.length - 2)) {
            int len = scanRecord[offset++];
            if (len == 0)
                break;
            int type = scanRecord[offset++];
            switch (type) {
                case ADV_DATA_FLAG:
                    if (len > 2) {
                        byte falg = scanRecord[offset++];
                        return (falg & LIMITED_AND_GENERAL_DISC_MASK) <= 0;
                    } else if (len == 1) {
                        continue;
                    }
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return false;
    }

    /**
     * The event callback to handle the found of near le devices
     * For SDK version < 21.
     *
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            switch (mMode) {
                case MODE_SCAN_NEW_DEVICE:
                    if (!checkIfBroadcastMode(scanRecord)) {
                        Bundle bundle = new Bundle();
                        Message message = Message.obtain(
                                mDeviceListHandler, SCAN_A_DEVICE);
                        bundle.putInt(EXTRA_RSSI, rssi);
                        bundle.putParcelable("android.bluetooth.device.extra.DEVICE",
                                device);
                        message.setData(bundle);
                        message.sendToTarget();
                        if (scanTime == 0L) {
                            scanTime = System.currentTimeMillis();
                        } else {
                            long current = System.currentTimeMillis();
                            Log.i(TAG, "Scan for new device: "+device.getAddress()+" rssi:"+rssi+" scan time: "+(current - scanTime)/100+" seconds");
                            scanTime = current;
                        }
                    }
                    break;
                case MODE_SCAN_MONITOR:
                    if (!checkIfBroadcastMode(scanRecord)) {
                        Bundle bundle = new Bundle();
                        Message message = Message.obtain(
                                mMonitorListHandler, SCAN_A_DEVICE);
                        bundle.putInt(EXTRA_RSSI, rssi);
                        bundle.putParcelable("android.bluetooth.device.extra.DEVICE",
                                device);
                        message.setData(bundle);
                        message.sendToTarget();
                        if (scanTime == 0L) {
                            scanTime = System.currentTimeMillis();
                        } else {
                            long current = System.currentTimeMillis();
                            Log.i(TAG, "Scan for monitoring: " + device.getAddress() + " rssi:" + rssi + " scan time: " + (current - scanTime) / 100 + " seconds");
                            scanTime = current;
                        }
                    }
                    break;
            }
        }
    };

    /**
     * The event callback to handle the found of near le devices
     * For SDK version >= 21.
     *
     */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            final ScanResult scanResult = result;
            final byte[] scanRecord = scanResult.getScanRecord().getBytes();
            final BluetoothDevice device = scanResult.getDevice();
            switch (mMode) {
                case MODE_SCAN_NEW_DEVICE:
                    if (!checkIfBroadcastMode(scanRecord)) {
                        Bundle bundle = new Bundle();
                        Message message = Message.obtain(
                                mDeviceListHandler, SCAN_A_DEVICE);
                        bundle.putInt(EXTRA_RSSI, result.getRssi());
                        bundle.putParcelable("android.bluetooth.device.extra.DEVICE",
                                device);
                        message.setData(bundle);
                        message.sendToTarget();
                        if (scanTime == 0L) {
                            scanTime = System.currentTimeMillis();
                        } else {
                            long current = System.currentTimeMillis();
                            Log.i(TAG, "Scan for new device: "+device.getAddress()+" rssi:"+result.getRssi()+" scan time: "+(current - scanTime)/100+" seconds");
                            scanTime = current;
                        }
                    }
                    break;
                case MODE_SCAN_MONITOR:
                    if (!checkIfBroadcastMode(scanRecord)) {
                        Bundle bundle = new Bundle();
                        Message message = Message.obtain(
                                mMonitorListHandler, SCAN_A_DEVICE);
                        bundle.putInt(EXTRA_RSSI, result.getRssi());
                        bundle.putParcelable("android.bluetooth.device.extra.DEVICE",
                                device);
                        message.setData(bundle);
                        message.sendToTarget();
                        if (scanTime == 0L) {
                            scanTime = System.currentTimeMillis();
                        } else {
                            long current = System.currentTimeMillis();
                            Log.i(TAG, "Scan for monitoring: "+device.getAddress()+" rssi:"+result.getRssi()+" scan time: "+(current - scanTime)/100+" seconds");
                            scanTime = current;
                        }
                    }
                    break;
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

}
