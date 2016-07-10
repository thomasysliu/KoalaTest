package cc.nctu1210.api.koala3x;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yi-Ta_Chuang on 2016/1/31.
 */
public class KoalaServiceManager {
    private final static String TAG = KoalaServiceManager.class.getSimpleName();
    private final List<SparseArray<SensorEventListener>> eventListeners = new ArrayList<SparseArray<SensorEventListener>>();
    private int enableService = SensorEvent.TYPE_PEDOMETER;
    private Activity mActivity;
    private KoalaService mBluetoothLeService; // the main service to control the ble device

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((KoalaService.LocalBinder) service)
                    .getService();
            Log.i(TAG, "Initializing Bluetooth.....");
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            } else {
                Log.i(TAG, "Success!");
                for (int i=0, size=eventListeners.size(); i<size; i++) {
                    if (eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER) != null) {
                        SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER);
                        l.onKoalaServiceStatusChanged(true);
                    }
                }
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            for (int i=0, size=eventListeners.size(); i<size; i++) {
                if (eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER) != null) {
                    SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER);
                    l.onKoalaServiceStatusChanged(false);
                }
            }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (KoalaService.ACTION_GATT_CONNECTED.equals(action)) {
                final String addr = intent.getStringExtra(KoalaService.EXTRA_NAME);
                startReadRssi(addr);
                //setSamplingRate(addr, KoalaService.MOTION_WRITE_RATE_50);
                //startToReadPDRData(addr);
                //startToReadData(addr);
                //setGSensor(addr);
                //getSportInformation(addr);
                //setToFactoryMode(addr);
                for (int i=0, size=eventListeners.size(); i<size; i++) {
                    if (eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER) != null) {
                        SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER);
                        l.onConnectionStatusChange(true);
                    }
                }
            } else if (KoalaService.ACTION_GATT_DISCONNECTED.equals(action)) {
                final String addr = intent.getStringExtra(KoalaService.EXTRA_NAME);
                //fire a disconnected event
                for (int i=0, size=eventListeners.size(); i<size; i++) {
                    if (eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER) != null) {
                        SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER);
                        l.onConnectionStatusChange(false);
                    }
                }
            }  else if (KoalaService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                final String addr = intent.getStringExtra(KoalaService.EXTRA_NAME);
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED! mac Address:" + addr);
                //startToReadData(addr);
                readPDRMode(addr);
            } else if (KoalaService.ACTION_GATT_RSSI.equals(action)) {
                final String addr = intent.getStringExtra(KoalaService.EXTRA_NAME);
                float rssi = Float.valueOf(intent.getStringExtra(KoalaService.EXTRA_DATA));
                //Log.d(TAG, "mac Address:" + addr + " rssi:" + rssi);
                //fire a rssi event
                for (int i=0, size=eventListeners.size(); i<size; i++) {
                    if (eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER) != null) {
                        SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER);
                        l.onRSSIChange(addr, rssi);
                    }
                }
                startReadRssi(addr);
            } else if (KoalaService.ACTION_PDR_DATA_AVAILABLE.equals(action)) {
                final String addr = intent.getStringExtra(KoalaService.EXTRA_NAME);
                final float values [] = intent.getFloatArrayExtra(KoalaService.EXTRA_DATA);
                Log.i(TAG, "ACTION_PDR_DATA_AVAILABLE received!!");
                //fire a pdr data event
                BluetoothGatt gattServer = mBluetoothLeService.getGattbyAddr(addr);
                if (gattServer != null) {
                    BluetoothDevice device = gattServer.getDevice();
                    SensorEvent e = new SensorEvent(SensorEvent.TYPE_PEDOMETER, device, 5);
                    e.values[0] = values[0];
                    e.values[1] = values[1];
                    e.values[2] = values[2];
                    e.values[3] = values[3];
                    e.values[4] = values[4];
                    for (int i=0, size=eventListeners.size(); i<size; i++) {
                        if (eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER) != null) {
                            SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_PEDOMETER);
                            l.onSensorChange(e);
                        }
                    }
                }
            } else if (KoalaService.ACTION_SLEEP_DATA_AVAILABLE.equals(action)) {
                final String addr = intent.getStringExtra(KoalaService.EXTRA_NAME);
                final int values [] = intent.getIntArrayExtra(KoalaService.EXTRA_DATA);
                Log.i(TAG, "ACTION_SLEEP_DATA_AVAILABLE received!!");
                //fire a pdr data event
                BluetoothGatt gattServer = mBluetoothLeService.getGattbyAddr(addr);
                if (gattServer != null) {
                    BluetoothDevice device = gattServer.getDevice();
                    SensorEvent e = new SensorEvent(SensorEvent.TYPE_SLEEP_MONITOR, device, 2);
                    e.sleepValues[0] = values[0];
                    e.sleepValues[1] = values[1];
                    for (int i=0, size=eventListeners.size(); i<size; i++) {
                        if (eventListeners.get(i).get(SensorEvent.TYPE_SLEEP_MONITOR) != null) {
                            SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_SLEEP_MONITOR);
                            l.onSensorChange(e);
                        }
                    }
                }
            } else if (KoalaService.ACTION_STATUS_DATA_AVAILABLE.equals(action)) {
                final String addr = intent.getStringExtra(KoalaService.EXTRA_NAME);
                final int value = intent.getIntExtra(KoalaService.EXTRA_DATA, Pedometer.PDR_MODE);
                Log.i(TAG, "ACTION_SLEEP_DATA_AVAILABLE received!!");
                //fire a pdr data event
                BluetoothGatt gattServer = mBluetoothLeService.getGattbyAddr(addr);
                if (gattServer != null) {
                    BluetoothDevice device = gattServer.getDevice();
                    SensorEvent e = new SensorEvent(SensorEvent.TYPE_STATUS, device, 1);
                    e.modeValue = value;
                    for (int i=0, size=eventListeners.size(); i<size; i++) {
                        if (eventListeners.get(i).get(SensorEvent.TYPE_STATUS) != null) {
                            SensorEventListener l = eventListeners.get(i).get(SensorEvent.TYPE_STATUS);
                            l.onSensorChange(e);
                        }
                    }
                }
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(KoalaService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(KoalaService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(KoalaService.ACTION_GATT_RSSI);
        intentFilter.addAction(KoalaService.ACTION_GATT_SERVICES_DISCOVERED);
        //intentFilter.addAction(KoalaService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(KoalaService.ACTION_PDR_DATA_AVAILABLE);
        intentFilter.addAction(KoalaService.ACTION_SLEEP_DATA_AVAILABLE);
        intentFilter.addAction(KoalaService.ACTION_STATUS_DATA_AVAILABLE);
        return intentFilter;
    }

    public KoalaServiceManager(Activity act) {
        this.mActivity = act;
        Log.i(TAG, "Starting Koala service!!");
        Intent gattServiceIntent = new Intent(this.mActivity, KoalaService.class);
        this.mActivity.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        this.mActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.i(TAG, "Koala service started!!");
    }

    public void connect(final String addr) {
        Log.i(TAG, "Connect to device: " + addr);
        mBluetoothLeService.connect(addr);
    }

    public void disconnect() {
        mBluetoothLeService.disconnect();
    }

    public void disconnect(String addr) {
        mBluetoothLeService.disconnect(addr);
    }

    public void close() {
        mBluetoothLeService.close();
        this.mActivity.unregisterReceiver(mGattUpdateReceiver);
        unbindService();
    }

    public void unbindService() {
        if (mServiceConnection != null) {
            this.mActivity.unbindService(mServiceConnection);
        }
    }

    public void registerSensorEventListener(SensorEventListener listener, final int type) {
        enableService = type;
        SparseArray<SensorEventListener> e = new SparseArray<SensorEventListener>();
        e.put(type, listener);
        this.eventListeners.add(e);
    }

    public void unRegisterSensorEventListener(SensorEventListener listener, final int type) {
        for (int i=0, size=this.eventListeners.size(); i<size; i++) {
            SensorEventListener l = this.eventListeners.get(i).get(type);
            if (l.equals(listener)) {
                this.eventListeners.remove(i);
                return;
            }
        }
    }

    public void resetPDRData(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.resetPedometer(addr);
            }
        }.start();
    }

    public void softResetPDRData(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.softResetPedometer(addr);
            }
        }.start();
    }

    public void startToReadPDRData(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.enablePedometerService(addr);
            }
        }.start();
    }

    public void stopReadingPDRData(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.disablePedometerService(addr);
            }
        }.start();
    }

    public void readPDRMode(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.readPedometerMode(addr);
            }
        }.start();
    }

    public void setPDRMode(final String addr, final int mode) {
        new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.setPDRMode(addr, mode);
            }
        }.start();
    }


    public void setToFactoryMode(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.setToFactoryMode(addr);
            }
        }.start();
    }

    public void getSportInformation(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.getSportInformation(addr);
            }
        }.start();
    }

    public void startReadRssi(final String addr) {
        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.readRssi(addr);
            }
        }.start();
    }



}
