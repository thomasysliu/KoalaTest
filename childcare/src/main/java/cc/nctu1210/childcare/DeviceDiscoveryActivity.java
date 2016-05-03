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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.BLESanner;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ScanItem;
import cc.nctu1210.view.ScanListAdapter;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class DeviceDiscoveryActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private String TAG = DeviceDiscoveryActivity.class.getSimpleName();
    private TextView mEmptyList;
    private ListView mListViewScan;
    private Button mCancelButton;
    private ScanListAdapter mScanListAdapter;
    private List<ScanItem> mScanItems = new ArrayList<ScanItem>();
    // for scan devices purpose
    private List<ChildProfile> mListDevices = new ArrayList<ChildProfile>();
    private HashMap<String, ChildProfile> mMapDevices = new HashMap<String, ChildProfile>();
    private List<ChildProfile> mListChildren;
    private HashMap<String, ChildProfile> mMapChildren;

    private static BluetoothAdapter mBtAdapter = null;
    private static BLESanner mBLEScanner = null;
    private static final int REQUEST_ENABLE_BT = ApplicationContext.REQUEST_ENABLE_BT;

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
                    if ((position = findDevice(device.getAddress())) == -1) {
                        if ((tmpPosition = ApplicationContext.getInstance().findChild(device.getAddress()))!=-1)
                            name = mListChildren.get(tmpPosition).getName();
                        else
                            name = "new";
                        Log.i(TAG, "1 name:"+name+" device mac:"+device.getAddress());
                        addr = device.getAddress();
                        ChildProfile newDevice = new ChildProfile(name, addr);
                        newDevice.setStatus(range);
                        addANewDevice(newDevice);
                        populateList();
                    } else {
                        if ((tmpPosition = ApplicationContext.getInstance().findChild(device.getAddress()))!=-1)
                            name = mListChildren.get(tmpPosition).getName();
                        else
                            name = "new";
                        Log.i(TAG, "2 name:"+name+" device mac:"+device.getAddress());
                        ChildProfile child = mListDevices.get(position);
                        ScanItem item = mScanItems.get(position);
                        child.setStatus(range);
                        item.name = name;
                        item.range = range;
                        mScanListAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    private void populateList() {
        mScanListAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mScanListAdapter.getData().size());
        for (int i = 0, size = mListDevices.size(); i < size; i++) {
            ScanItem object = new ScanItem(mListDevices.get(i).getName(), mListDevices.get(i).getDeviceAddress(),mListDevices.get(i).getStatus());
            mScanItems.add(object);
        }
        Log.i(TAG, "Initialized ListView....." + mScanListAdapter.getData().size());
        mScanListAdapter.notifyDataSetChanged();
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

    private ChildProfile removeADevice(String addr) {
        if (findDevice(addr) != -1) {
            ChildProfile child = mMapDevices.get(addr);
            mListDevices.remove(child);
            mMapDevices.remove(child);
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ApplicationContext.mBluetoothAdapter == null) {
            Log.i(TAG, "getPackageManager");
            if (!getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, getResources().getText(R.string.BLE_not_support), Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
            final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBtAdapter = mBluetoothManager.getAdapter();
            if (mBtAdapter == null) {
                Toast.makeText(this,  getResources().getText(R.string.BLE_not_support), Toast.LENGTH_SHORT)
                        .show();
                finish();
                return;
            }
        }
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.device_discovery);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mBLEScanner = new BLESanner(mBtAdapter);
            mBLEScanner.setDeviceListHandler(mHandler);
            ApplicationContext.mBluetoothAdapter = mBtAdapter;
            ApplicationContext.mBLEScanner = mBLEScanner;
            startScaning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBLEScanner != null) {
            stopScaning();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Bluetooth not enabled.
                    Toast.makeText(this, getResources().getText(R.string.BLE_not_support), Toast.LENGTH_SHORT)
                            .show();
                    finish();
                    return;
                }
                break;
            case ApplicationContext.REQUEST_CODE_ADD:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String name = bundle.getString(ApplicationContext.CHILD_NAME);
                    final String addr = bundle.getString(ApplicationContext.DEVICE_ADDRESS);
                    final String range = bundle.getString(ApplicationContext.DEVICE_RANGE);
                    final String photoName = bundle.getString(ApplicationContext.PHOTO_NAME);
                    final int viewPosition = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
                    final String cid = bundle.getString(ApplicationContext.CHILD_ID);
                    ScanItem object = mScanListAdapter.getData().get(viewPosition);
                    object.name = name;
                    object.range = range;
                    ChildProfile child = new ChildProfile(name, addr);
                    child.setStatus(range);
                    child.setPhotoName(photoName);
                    child.setCid(cid);
                    ChildProfile dChild = mListDevices.get(viewPosition);
                    dChild.setName(name);
                    dChild.setStatus(range);
                    dChild.setPhotoName(photoName);
                    dChild.setCid(cid);
                    ApplicationContext.addANewChild(child);
                    mScanListAdapter.notifyDataSetChanged();
                }
                break;
        }

    }

    private void startScaning() {
        ApplicationContext.mIsScan = true;
        mBLEScanner.setMode(BLESanner.MODE_SCAN_NEW_DEVICE);
        mBLEScanner.scanLeDevice(true);
    }

    private void stopScaning() {
        ApplicationContext.mIsScan = false;
        mBLEScanner.setMode(BLESanner.MODE_NO_SCAN);
        mBLEScanner.scanLeDevice(false);
    }

    private void init() {
        Log.i(TAG, "init()");
        mListChildren = ApplicationContext.mListChildren;
        mMapChildren = ApplicationContext.mMapChildren;
        mEmptyList = (TextView) findViewById(R.id.empty);
        mListViewScan = (ListView) findViewById(R.id.list_scan_child);
        mScanListAdapter = new ScanListAdapter(this, mScanItems);
        mListViewScan.setAdapter(mScanListAdapter);
        mListViewScan.setOnItemClickListener(this);
        mCancelButton = (Button) findViewById(R.id.button_scan_cancel);
        mCancelButton.setOnClickListener(this);
        populateList();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ScanItem object = mScanListAdapter.getData().get(position);
        final String addr = object.addr;
        final String range = object.range;
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationContext.DEVICE_ADDRESS, addr);
        bundle.putString(ApplicationContext.DEVICE_RANGE, range);
        bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
        Intent intent = new Intent(this, AddNewDeviceActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ApplicationContext.REQUEST_CODE_ADD);
    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }


}
