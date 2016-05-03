package cc.nctu1210.childcare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.DeviceItem;
import cc.nctu1210.view.DeviceListAdapter;
import cc.nctu1210.view.GatewayListAdapterForMaster;
import cc.nctu1210.view.NewGatewayItem;
import cc.nctu1210.view.NewParentItem;
import cc.nctu1210.view.ParentCreateChildItem;
import cc.nctu1210.view.ParentListAdapterForMaster;

public class ChildProfileListEditorActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = ChildProfileListEditorActivity.class.getSimpleName();

    private ListView mListViewDevices;
    private DeviceListAdapter mDeviceListAdapter;
    private List<DeviceItem> mDeviceItems = new ArrayList<DeviceItem>();
    private ImageButton mImageButtonConnect;
    private List<ChildProfile> mListChildren;
    private HashMap<String,ChildProfile> mMapChildren;
    private Button btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_profile_list_editor);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        mListChildren = ApplicationContext.mListChildren;
        mMapChildren = ApplicationContext.mMapChildren;
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }

    private void initView() {
        btnClose = (Button) findViewById(R.id.bt_close);
        btnClose.setOnClickListener(this);
        mImageButtonConnect = (ImageButton) findViewById(R.id.image_button_set_ble);
        mImageButtonConnect.setOnClickListener(this);
        mListViewDevices = (ListView)findViewById(R.id.list_set_device);
        mDeviceListAdapter = new DeviceListAdapter(this, mDeviceItems);
        mListViewDevices.setAdapter(mDeviceListAdapter);
        mListViewDevices.setOnItemClickListener(this);
        populateList();

    }
    private void populateList() {
        mDeviceListAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mDeviceListAdapter.getData().size());
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
        switch (v.getId()) {
            case R.id.image_button_set_ble:
                Intent intent = new Intent(this, DeviceDiscoveryActivity.class);
                startActivityForResult(intent, ApplicationContext.REQUEST_CODE_SCAN);
                break;
            case R.id.bt_close:
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
            Intent intent = new Intent(this, ChildProfleEditActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, ApplicationContext.REQUEST_CODE_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_EDIT:
                if (resultCode == Activity.RESULT_OK) {
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
                    final String photoName = mDeviceListAdapter.getData().get(position).photoName;
                    File photoFile = new File(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);
                    Log.i(TAG, "Delete filePath:" + ApplicationContext.CHILD_PHOTO_FILE_PATH + " fileName:" + photoName);
                    ApplicationContext.imageFileDelete(photoFile);
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

}
