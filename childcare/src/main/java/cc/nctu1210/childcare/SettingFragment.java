package cc.nctu1210.childcare;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.view.DeviceItem;
import cc.nctu1210.view.DeviceListAdapter;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class SettingFragment extends Fragment implements OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private TextView mTextTeacherName;
    private ListView mListViewDevices;
    private DeviceListAdapter mDeviceListAdapter;
    private List<DeviceItem> mDeviceItems = new ArrayList<DeviceItem>();
    private ImageButton mImageButtonConnect;
    private List<ChildProfile> mListChildren;
    private HashMap<String,ChildProfile> mMapChildren;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        mTextTeacherName = (TextView) this.getView().findViewById(R.id.text_set_user);
        mImageButtonConnect = (ImageButton) this.getView().findViewById(R.id.image_button_set_ble);
        mImageButtonConnect.setOnClickListener(this);
        mListViewDevices = (ListView) this.getView().findViewById(R.id.list_set_device);
        mDeviceListAdapter = new DeviceListAdapter(getActivity(), mDeviceItems);
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
            mDeviceItems.add(object);
        }
        Log.i(TAG, "Initialized ListView....." + mDeviceListAdapter.getData().size());
        mDeviceListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), DeviceDiscoveryActivity.class);
        startActivityForResult(intent, ApplicationContext.REQUEST_CODE_SCAN);
    }

    @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DeviceItem object = mDeviceListAdapter.getData().get(position);
        final String name = object.child_name;
        final String addr = object.device_addr;
        final String photoName = object.photoName;
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationContext.CHILD_NAME, name);
        bundle.putString(ApplicationContext.DEVICE_ADDRESS, addr);
        bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
        bundle.putString(ApplicationContext.PHOTO_NAME, photoName);
        Intent intent = new Intent(getActivity(), ChildProfleEditActivity.class);
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
