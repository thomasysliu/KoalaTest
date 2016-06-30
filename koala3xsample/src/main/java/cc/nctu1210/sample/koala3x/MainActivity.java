package cc.nctu1210.sample.koala3x;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.nctu1210.api.koala3x.KoalaDevice;
import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.api.koala3x.SensorEvent;
import cc.nctu1210.api.koala3x.SensorEventListener;
import cc.nctu1210.view.CustomAdapter;
import cc.nctu1210.view.ModelObject;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, SensorEventListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_COARSE_LOCATION = 0x01 << 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 0x01 << 2;

    private boolean startScan = false;
    private KoalaServiceManager mServiceManager;
    private boolean mBooleanServiceCreated = false;
    private BluetoothAdapter mBluetoothAdapter;
    /******** for SDK version > 21 **********/
    private BluetoothLeScanner mBLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    /******** for SDK version > 21 **********/
    public static ArrayList<KoalaDevice> mDevices = new ArrayList<KoalaDevice>();  // Manage the devices
    public static ArrayList<AtomicBoolean> mFlags = new ArrayList<AtomicBoolean>();

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 2000;
    public static final int REQUEST_CODE = 30;

    private Button btScan;
    private Button btDisconnect;

    /* ListView Related */
    private String DEVICE_NAME = "name";
    private String DEVICE_ADDRESS = "address";
    private String DEVICE_RSSI = "rssi";
    private String DEVICE_GX = "gX";
    private String DEVICE_GY = "gY";
    private String DEVICE_GZ = "gZ";
    private ListView listView;
    private List<ModelObject> mObjects = new ArrayList<ModelObject>();
    private CustomAdapter mAdapter;

    private long pastTime = 0;

    private void displayRSSI(final int position, final float rssi) {
        ModelObject object = mObjects.get(position);
        String range="";
        object.setRssi(rssi);

        if (rssi>-60) {
            range = "near";
        } else if (rssi<=-60 && rssi>=-90) {
            range = "mediate";
        } else if (rssi <- 90) {
            range = "far";
        }

        if (rssi <= -95) {
            notificationBuilder();
        }
        object.setRange(range);
        mAdapter.notifyDataSetChanged();
    }

    private void displayAccData(final int position, final double acc[]) {
        ModelObject object = mObjects.get(position);
        object.setAccelerometerData(acc[0], acc[1], acc[2]);
        mAdapter.notifyDataSetChanged();
    }

    private void displayPDRData(final int position, final float pdrData[]) {
        ModelObject object = mObjects.get(position);
        object.setPedometerData(pdrData[0], pdrData[1], pdrData[2], pdrData[3]);
        mAdapter.notifyDataSetChanged();
    }

    private void displayPDRData(final int position, final float step) {
        ModelObject object = mObjects.get(position);
        object.setPedometerData(step, 0, 0, 0);
        mAdapter.notifyDataSetChanged();
    }

    private void updateSamplingRate(final int position, float sampling) {
        ModelObject object = mObjects.get(position);
        object.setSampling(sampling);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyStoragePermissions(this);
            verifyCoaseLocationPermissions(this);
        }

        btScan = (Button) findViewById(R.id.bt_scan);
        btDisconnect = (Button) findViewById(R.id.bt_disconnect);
        listView = (ListView) findViewById(R.id.listView);

        mAdapter = new CustomAdapter(this, mObjects);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        btScan.setOnClickListener(scanListener);
        btDisconnect.setOnClickListener(disConnectListener);

        Log.i(TAG, "getPackageManager");
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        mServiceManager = new KoalaServiceManager(MainActivity.this);
        //mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_ACCELEROMETER);
        mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_PEDOMETER);

    }

    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity current activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, PERMISSIONS[1]);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void verifyCoaseLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, PERMISSIONS[2]);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_COARSE_LOCATION
            );
        }
    }

    private Button.OnClickListener scanListener = new Button.OnClickListener() {
        public void onClick(View v) {
            if (!startScan) {
                startScan = true;
                btScan.setText("Stop scan");
                mDevices.clear();
                mFlags.clear();
                // Start to scan the ble device
                scanLeDevice(startScan);
            } else {
                startScan = false;
                scanLeDevice(startScan);
                btScan.setText("Scan");
            }

        }
    };

    private void setupListView() {
        mAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....."+ mAdapter.getData().size());
        for (int i = 0, size = mDevices.size(); i < size; i++) {
            KoalaDevice d = mDevices.get(i);
            ModelObject object = new ModelObject(d.getDevice().getName(), d.getDevice().getAddress(), String.valueOf(d.getRssi()));
            mObjects.add(object);
        }
        Log.i(TAG, "Initialized ListView....."+ mAdapter.getData().size());

        mAdapter.notifyDataSetChanged();

    }

    private Button.OnClickListener disConnectListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mServiceManager.disconnect();
            mServiceManager.close();
            mDevices.clear();
            mFlags.clear();
            setupListView();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mBLEScanner =mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
        }
    }

    private void scanLeDevice(boolean scanFlag) {
        if (Build.VERSION.SDK_INT < 21) {
            if (scanFlag)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            else
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
        } else {
            if (scanFlag) {
                Log.d(TAG, "start scnning!!!");
                mBLEScanner.startScan(mScanCallback);
            }
            else {
                mBLEScanner.stopScan(mScanCallback);
            }
        }
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

            new Thread() {
                @Override
                public void run() {
                    if (device != null) {
                        KoalaDevice p = new KoalaDevice(device, rssi, scanRecord);
                        final int position = findKoalaDevice(device.getAddress());
                        if (position == -1) {
                            AtomicBoolean flag = new AtomicBoolean(false);
                            mDevices.add(p);
                            mFlags.add(flag);
                            Log.i(TAG, "Find device:"+p.getDevice().getAddress());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    setupListView();
                                }
                            });
                        } else {
                            //update RSSI
                            long time = System.currentTimeMillis();
                            if (pastTime > 0) {
                                Log.i(TAG, "Time elapsed:"+(time-pastTime)/1000+"seconds\n");
                            } else {
                                pastTime = time;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    displayRSSI(position, rssi);
                                }
                            });
                        }
                    }
                }
            }.start();
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
            final int rssi = scanResult.getRssi();
            final BluetoothDevice device = scanResult.getDevice();

            new Thread() {
                @Override
                public void run() {
                    if (device != null) {
                        KoalaDevice p = new KoalaDevice(device, scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                        final int position = findKoalaDevice(device.getAddress());
                        if (position == -1) {
                            AtomicBoolean flag = new AtomicBoolean(false);
                            mDevices.add(p);
                            mFlags.add(flag);
                            Log.i(TAG, "Find device:"+p.getDevice().getAddress());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    setupListView();
                                }
                            });
                        } else {
                            //update RSSI
                            long time = System.currentTimeMillis();
                            if (pastTime > 0) {
                                Log.i(TAG, "Time elapsed:"+(time-pastTime)/1000+"seconds\n");
                            } else {
                                pastTime = time;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    displayRSSI(position, rssi);
                                }
                            });
                        }
                    }
                }
            }.start();
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

    private int findKoalaDevice(String macAddr) {
        if (mDevices.size() == 0)
            return -1;
        for (int i=0; i<mDevices.size(); i++) {
            KoalaDevice tmpDevice = mDevices.get(i);
            if (macAddr.matches(tmpDevice.getDevice().getAddress()))
                return i;
        }
        return -1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i=0; i<mFlags.size(); i++) {
            AtomicBoolean flag = mFlags.get(i);
            flag.set(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.exit(0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        String macAddress = mAdapter.getData().get(position).getAddress();
        KoalaDevice d = mDevices.get(position);
        d.setConnectedTime();
        if (mBooleanServiceCreated) {
            Log.d(TAG, "Connecting to device:"+macAddress);
            mServiceManager.connect(macAddress);
        }
    }

    private void notificationBuilder() {
        final int notifyID = 1; // 通知的識別號碼
        final boolean autoCancel = true; // 點擊通知後是否要自動移除掉通知
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效

        final int requestCode = notifyID; // PendingIntent的Request Code
        final Intent intent = getIntent(); // 目前Activity的Intent
        final int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_launcher).setContentTitle("小孩監控").setContentText("有小孩走遠喔!!!").setSound(soundUri).setContentIntent(pendingIntent).setAutoCancel(autoCancel).build(); // 建立通知
        notificationManager.notify(notifyID, notification); // 發送通知
    }

    @Override
    public void onSensorChange(final SensorEvent e) {
        final int eventType = e.type;
        final double values [] = new double[3];
        switch (eventType) {
            case SensorEvent.TYPE_PEDOMETER:
                final int position2 = findKoalaDevice(e.device.getAddress());
                if (position2 != -1) {
                    Log.d(TAG, "time=" + System.currentTimeMillis() + "step counts:" + e.values[0] + "\n");
                    displayPDRData(position2, e.values[0]);
                }
                break;
        }
    }

    @Override
    public void onConnectionStatusChange(boolean status) {

    }

    @Override
    public void onPedometerServiceChange(int serviceType) {

    }

    @Override
    public void onRSSIChange(String addr, float rssi) {
        final int position = findKoalaDevice(addr);
        if (position != -1) {
            Log.d(TAG, "mac Address:"+addr+" rssi:"+rssi);

            displayRSSI(position, rssi);
        }
    }

    @Override
    public void onKoalaServiceStatusChanged(boolean status) {
        mBooleanServiceCreated = status;
    }
}
