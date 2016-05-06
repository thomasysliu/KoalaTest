package cc.nctu1210.childcare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.BLESanner;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapter;
import cc.nctu1210.view.DeviceItem;
import cc.nctu1210.view.DeviceListAdapter;

public class ChildProfileCreateActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{
    private static final String TAG = ChildProfileCreateActivity.class.getSimpleName();
    private TextView mTextTeacherName;
    private ListView mListViewDevices;
    private DeviceListAdapter mDeviceListAdapter;
    private List<DeviceItem> mDeviceItems = new ArrayList<DeviceItem>();
    private ImageButton mImageButtonConnect;
    private ChildrenListAdapter mChildListAdapter;
    private List<ChildItem> mChildItems = new ArrayList<ChildItem>();
    private List<ChildProfile> mListChildren;
    private HashMap<String,ChildProfile> mMapChildren;
    private Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_profile_create);
        btNext = (Button)findViewById(R.id.bt_next);
        btNext.setOnClickListener(this);
        mListChildren = ApplicationContext.mListChildren;
        mMapChildren = ApplicationContext.mMapChildren;
        initView();
    }


    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initView() {
        mTextTeacherName = (TextView) findViewById(R.id.text_set_user);
        mImageButtonConnect = (ImageButton) findViewById(R.id.image_button_set_ble);
        mImageButtonConnect.setOnClickListener(this);
        mListViewDevices = (ListView)findViewById(R.id.list_set_device);
        mDeviceListAdapter = new DeviceListAdapter(ChildProfileCreateActivity.this, mDeviceItems);
        mListViewDevices.setAdapter(mDeviceListAdapter);
        mListViewDevices.setOnItemClickListener(this);
        populateList();
    }

    private void populateList() {
        mDeviceListAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mDeviceListAdapter.getData().size());
        Collections.sort(mListChildren);
        for (int i = 0, size = mListChildren.size(); i < size; i++) {
            DeviceItem object = new DeviceItem(mListChildren.get(i).getName());
            object.child_name = mListChildren.get(i).getName();
            object.device_addr = mListChildren.get(i).getDeviceAddress();
            object.photoName = mListChildren.get(i).getPhotoName();
            object.cid = mListChildren.get(i).getCid();
            mDeviceItems.add(object);
        }
        Log.i(TAG, "Initialized ListView....." + mDeviceListAdapter.getData().size());
        mDeviceListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_button_set_ble:
                Intent intent = new Intent(ChildProfileCreateActivity.this, DeviceDiscoveryActivity.class);
                startActivityForResult(intent, ApplicationContext.REQUEST_CODE_SCAN);
                break;
            case R.id.bt_next:
                Intent intent_next = new Intent(ChildProfileCreateActivity.this, ParentCreateActivity.class);
                startActivity(intent_next);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DeviceItem object = mDeviceListAdapter.getData().get(position);
        final String name = object.child_name;
        final String addr = object.device_addr;
        final String photoName = object.photoName;
        final String cid = object.cid;
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationContext.CHILD_NAME, name);
        bundle.putString(ApplicationContext.DEVICE_ADDRESS, addr);
        bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
        bundle.putString(ApplicationContext.PHOTO_NAME, photoName);
        bundle.putString(ApplicationContext.CHILD_ID, cid);
        Intent intent = new Intent(ChildProfileCreateActivity.this, ChildProfleEditActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ApplicationContext.REQUEST_CODE_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_EDIT:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String name = bundle.getString(ApplicationContext.CHILD_NAME);
                    final int position = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
                    final String photoName = bundle.getString(ApplicationContext.PHOTO_NAME);
                    DeviceItem object = mDeviceListAdapter.getData().get(position);
                    ChildProfile child = mListChildren.get(position);
                    child.setName(name);
                    child.setPhotoName(photoName);
                    object.child_name = name;
                    object.photoName = photoName;
                    mDeviceListAdapter.notifyDataSetChanged();
                } else if (resultCode == ApplicationContext.RESULT_CODE_REMOVE) {
                    final int position = data.getIntExtra(ApplicationContext.LIST_VIEW_POSITION, -1);
                    //final String photoName = mDeviceListAdapter.getData().get(position).photoName;
                    //File photoFile = new File(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);
                    // Log.i(TAG, "Delete filePath:" + ApplicationContext.CHILD_PHOTO_FILE_PATH + " fileName:" + photoName);
                    //ApplicationContext.imageFileDelete(photoFile);
                    mDeviceListAdapter.getData().remove(position);
                    mDeviceListAdapter.notifyDataSetChanged();
                }
                break;
            case ApplicationContext.REQUEST_CODE_SCAN:
                if (resultCode == Activity.RESULT_OK) {
                    populateList();
                }
                break;
        }

    }


    @Override
    public void onBackPressed() {
    }

}
