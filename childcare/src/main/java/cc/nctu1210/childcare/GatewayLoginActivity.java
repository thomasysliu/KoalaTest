package cc.nctu1210.childcare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.polling.GatewayScheduledService;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.BLESanner;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapterForGateway;
import cc.nctu1210.view.ScanItem;
import cc.nctu1210.view.ScanListAdapter;

public class GatewayLoginActivity extends Activity implements View.OnClickListener {
    private final String TAG = GatewayLoginActivity.class.getSimpleName();
    private long exitTime = 0L;

    private BluetoothAdapter mBtAdapter = null;
    private BLESanner mBLEScanner = null;
    public static ChildrenListAdapterForGateway mChildListAdapter;
    public static List<ChildItem> mChildItems = new ArrayList<ChildItem>();
    private ListView mListViewChildren;
    private List<ChildProfile> mListChildren;
    public static List<ChildProfile> mListScan = new ArrayList<ChildProfile>();

    private static final int REQUEST_ENABLE_BT = 1;

    private TextView mTextViewStatus;
    private TextView mTextViewPlace;
    private String place;
    private ImageView mImageViewKoala;

    private Intent mGatewayPollingIntent;

    private List<ChildProfile> mListDevices = new ArrayList<ChildProfile>();
    private HashMap<String, ChildProfile> mMapDevices = new HashMap<String, ChildProfile>();
    private ScanListAdapter mScanListAdapter;
    private List<ScanItem> mScanItems = new ArrayList<ScanItem>();

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BLESanner.SCAN_A_DEVICE:
                    final int position, tmpPosition;
                    final String name, addr;
                    final int rssi = msg.getData().getInt(BLESanner.EXTRA_RSSI);
                    final BluetoothDevice device = msg.getData().getParcelable(BLESanner.EXTRA_DEVICE);
                    String range="miss";
                    if (rssi>-60) {
                        range = "near";
                    } else if (rssi<=-60 && rssi>=-90) {
                        range = "mediate";
                    } else if (rssi <- 90) {
                        range = "far";
                    }
                    if ((position = ApplicationContext.getInstance().findChild(device.getAddress())) != -1) {
                        ChildProfile child = mListChildren.get(position);
                        String status="?";
                        if(!range.equals("miss"))
                            status = String.valueOf(range);
                        child.setStatus(status);
                        child.setRssi(String.valueOf(rssi));
                        int unixTime = (int) (System.currentTimeMillis() / 1000L);
                        Log.i(TAG, "scan a device:" + device.getAddress() + " name:" + child.getName() + " rssi:" + rssi + " time:" + unixTime);
                        synchronized (mListScan) {
                            if (!mListScan.contains(child)) {
                                mListScan.add(child);
                                //ApplicationContext.gateway_upload(ApplicationContext.mGid,child.getCid(),child.getRssi(),String.valueOf(unixTime));
                            }
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gateway_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();

        Log.i(TAG, "getPackageManager");
        if (!GatewayLoginActivity.this.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(GatewayLoginActivity.this, GatewayLoginActivity.this.getResources().getText(R.string.BLE_not_support), Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        final BluetoothManager mBluetoothManager = (BluetoothManager) GatewayLoginActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(GatewayLoginActivity.this,  GatewayLoginActivity.this.getResources().getText(R.string.BLE_not_support), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mBLEScanner = new BLESanner(mBtAdapter);
            mBLEScanner.setMonitorListHandler(mHandler);
            ApplicationContext.mBluetoothAdapter = mBtAdapter;
            ApplicationContext.mBLEScanner = mBLEScanner;
            if (ApplicationContext.mIsServiceOn) {
                Log.i(TAG, "onResume start service...");
                ApplicationContext.notificationServiceStartBuilder(this, ApplicationContext.GATEWAY_TYPE);
                startMonitoring();
                mGatewayPollingIntent = new Intent(this, GatewayScheduledService.class);
                this.startService(mGatewayPollingIntent);
            } else {
                ApplicationContext.cancelNotificationService(this);
                stopMonitoring();
                mGatewayPollingIntent = new Intent(this, GatewayScheduledService.class);
                this.stopService(mGatewayPollingIntent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ApplicationContext.mIsServiceOn) {
            ApplicationContext.mIsServiceOn = false;
            mGatewayPollingIntent = new Intent(this, GatewayScheduledService.class);
            this.stopService(mGatewayPollingIntent);
        }
        ApplicationContext.cancelNotificationService(this);
        if (mBLEScanner != null)
            stopMonitoring();
    }

    private void initView() {
        mTextViewStatus = (TextView) findViewById(R.id.text_gateway_status);
        if(ApplicationContext.mIsServiceOn) {
            mTextViewStatus.setText(getString(R.string.toggle_on));
        } else {
            mTextViewStatus.setText(getString(R.string.toggle_off));
        }
        mTextViewPlace = (TextView) findViewById(R.id.text_place);
        mTextViewPlace.setText(ApplicationContext.mPlace);
        mImageViewKoala = (ImageView) findViewById(R.id.image_gateway_user);
        mImageViewKoala.setOnClickListener(this);

        mChildListAdapter = new ChildrenListAdapterForGateway(GatewayLoginActivity.this, mChildItems);
        mListViewChildren = (ListView) findViewById(R.id.list_main_child);
        mListViewChildren.setAdapter(mChildListAdapter);
        ApplicationContext.showProgressDialog(this);
        ApplicationContext.show_all_children(ApplicationContext.login_mid, false, -1, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    mListChildren = content.getShow_children();
                    populateList();
                } else {
                    Log.e(TAG, "show_child_by_id fail" + "\n");
                }
            }
        });
        ApplicationContext.dismissProgressDialog();
    }

    private void startMonitoring() {
        ApplicationContext.mIsScan = true;
        mBLEScanner.setMode(BLESanner.MODE_SCAN_MONITOR);
        mBLEScanner.scanLeDevice(true);
    }

    private void stopMonitoring() {
        ApplicationContext.mIsScan = false;
        mBLEScanner.setMode(BLESanner.MODE_NO_SCAN);
        mBLEScanner.scanLeDevice(false);
    }

    private void populateList() {
        mChildListAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mChildListAdapter.getData().size());
        Collections.sort(mListChildren);
        for (int i = 0, size = mListChildren.size(); i < size; i++) {
            ChildItem object = new ChildItem(mListChildren.get(i).getName(),mListChildren.get(i).getStatus());
            object.photoName = mListChildren.get(i).getPhotoName();
            object.place = mListChildren.get(i).getPlace();
            object.flag = mListChildren.get(i).getFlag();
            object.cid = mListChildren.get(i).getCid();
            object.rssi = mListChildren.get(i).getRssi();
            mChildItems.add(object);
        }
        Log.i(TAG, "Initialized ListView....." + mChildListAdapter.getData().size());
        mChildListAdapter.notifyDataSetChanged();
    }

    private int findDevice(String addr) {
        if (mMapDevices.containsKey(addr)) {
            ChildProfile child = mMapDevices.get(addr);
            int position = mListDevices.indexOf(child);
            return position;
        } else {
            return -1;
        }
    }

    private void addANewDevice(ChildProfile child) {
        if (findDevice(child.getDeviceAddress()) == -1) {
            mMapDevices.put(child.getDeviceAddress(), child);
            mListDevices.add(child);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000L) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.finish_system),
                        Toast.LENGTH_LONG).show();
                this.exitTime = System.currentTimeMillis();
                return true;
            }
            ApplicationContext.cancelNotificationService(this);
            if (ApplicationContext.mIsServiceOn) {
                ApplicationContext.mIsServiceOn = false;
                mGatewayPollingIntent = new Intent(this, GatewayScheduledService.class);
                this.stopService(mGatewayPollingIntent);
            }
            exitPrograms();
        }
        return super.dispatchKeyEvent(event);
    }

    public void exitPrograms() {
        stopMonitoring();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        // set activity flag
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // exit app
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onClick(View v) {
        if (ApplicationContext.mIsServiceOn) {
            ApplicationContext.mIsServiceOn = false;
            mTextViewStatus.setText(getString(R.string.toggle_off));
            ApplicationContext.cancelNotificationService(this);
            stopMonitoring();
            mGatewayPollingIntent = new Intent(this, GatewayScheduledService.class);
            this.stopService(mGatewayPollingIntent);
        } else {
            ApplicationContext.mIsServiceOn = true;
            mTextViewStatus.setText(getString(R.string.toggle_on));
            ApplicationContext.notificationServiceStartBuilder(this, ApplicationContext.GATEWAY_TYPE);
            startMonitoring();
            mGatewayPollingIntent = new Intent(this, GatewayScheduledService.class);
            this.startService(mGatewayPollingIntent);
        }
    }
}