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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import cc.nctu1210.tool.BLESanner;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.DeviceItem;
import cc.nctu1210.view.DeviceListAdapter;
import cc.nctu1210.view.GatewayListAdapter;
import cc.nctu1210.view.GatewayListAdapterForMaster;
import cc.nctu1210.view.NewGatewayItem;
import cc.nctu1210.view.NewParentItem;
import cc.nctu1210.view.ParentCreateChildItem;
import cc.nctu1210.view.ParentListAdapter;
import cc.nctu1210.view.ParentListAdapterForMaster;


public class MasterLoginSettingFragment extends Fragment implements OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = MasterLoginSettingFragment.class.getSimpleName();
    private LinearLayout SetAlarmTime;
    private TextView textTime;
    private ImageView imgAddGateway;
    private ListView gatewayList;
    private List<NewGatewayItem> mGateways = new ArrayList<NewGatewayItem>();
    private GatewayListAdapterForMaster mGatewayAdapter;
    private int gatewayNum = 0;
    private ImageView imgAddParent;
    private ListView parentList;
    private List<NewParentItem> mParents = new ArrayList<NewParentItem>();
    private ParentListAdapterForMaster mParentAdapter;
    private int parentNum = 0;
    private ListView mListViewDevices;
    private DeviceListAdapter mDeviceListAdapter;
    private List<DeviceItem> mDeviceItems = new ArrayList<DeviceItem>();
    private ImageButton mImageButtonConnect;
    private List<ChildProfile> mListChildren;
    private HashMap<String,ChildProfile> mMapChildren;
    private Button btGatewayEditor;
    private Button btParentEditor;
    private Button btChildEditor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.master_login_setting_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGateways = ApplicationContext.mGateways;
        mParents = ApplicationContext.mParents;
        mListChildren = ApplicationContext.mListChildren;
        mMapChildren = ApplicationContext.mMapChildren;
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
       // populateList();
    }

    private void initView() {
        /*
        gatewayList = (ListView) this.getView().findViewById(R.id.list_set_gateway);
        mGatewayAdapter = new GatewayListAdapterForMaster(getActivity(), mGateways);
        gatewayList.setAdapter(mGatewayAdapter);
        gatewayList.setOnItemClickListener(this);
        parentList = (ListView) this.getView().findViewById(R.id.list_set_parent);
        mParentAdapter = new ParentListAdapterForMaster(getActivity(), mParents);
        parentList.setAdapter(mParentAdapter);
        parentList.setOnItemClickListener(this);
        imgAddGateway = (ImageView)this.getView().findViewById(R.id.image_add_gateway);
        imgAddGateway.setOnClickListener(this);
        imgAddParent = (ImageView)this.getView().findViewById(R.id.image_add_parent);
        imgAddParent.setOnClickListener(this);*/
        SetAlarmTime = (LinearLayout) this.getView().findViewById(R.id.setting_alarm);
        SetAlarmTime.setOnClickListener(this);
        textTime = (TextView) this.getView().findViewById(R.id.text_time);
        int showAlarmtime = ApplicationContext.alarmTime/60;
        textTime.setText(String.valueOf(showAlarmtime));
        ;/*
        mImageButtonConnect = (ImageButton) this.getView().findViewById(R.id.image_button_set_ble);
        mImageButtonConnect.setOnClickListener(this);
        mListViewDevices = (ListView) this.getView().findViewById(R.id.list_set_device);
        mDeviceListAdapter = new DeviceListAdapter(getActivity(), mDeviceItems);
        mListViewDevices.setAdapter(mDeviceListAdapter);
        mListViewDevices.setOnItemClickListener(this);
        ApplicationContext.ChildName = new String[ApplicationContext.mListChildren.size()];
        ApplicationContext.show_all_gateway(ApplicationContext.login_mid, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    ApplicationContext.mGateways = content.getShow_gateway();
                        populateGatewayList();
                } else {
                    Log.e(TAG, "show_child_by_id fail" + "\n");
                }
            }
        });
        ApplicationContext.show_all_parent(ApplicationContext.login_mid, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    ApplicationContext.mParents = content.getShow_parent();
                    populateParentList();
                } else {
                    Log.e(TAG, "show_child_by_id fail" + "\n");
                }
            }
        });
        populateList();*/
        btGatewayEditor = (Button) this.getView().findViewById(R.id.button_gateway_list_editor);
        btGatewayEditor.setOnClickListener(this);
        btParentEditor = (Button) this.getView().findViewById(R.id.button_parent_list_editor);
        btParentEditor.setOnClickListener(this);
        btChildEditor = (Button) this.getView().findViewById(R.id.button_child_profile_list_editor);
        btChildEditor.setOnClickListener(this);
    }
/*
    private void populateGatewayList() {
        mGatewayAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mGatewayAdapter.getData().size());
        for (int i = 0, size = ApplicationContext.mGateways.size(); i < size; i++) {
            NewGatewayItem object = ApplicationContext.mGateways.get(i);
            mGateways.add(object);
        }
        gatewayNum = mGateways.size();
        mGatewayAdapter.notifyDataSetChanged();
    }

    private void populateParentList() {
        mParentAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mParentAdapter.getData().size());
        for (int i = 0, size = ApplicationContext.mParents.size(); i < size; i++) {
            NewParentItem object = ApplicationContext.mParents.get(i);
            mParents.add(object);
        }
        parentNum = mParents.size();
        mParentAdapter.notifyDataSetChanged();
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
        ApplicationContext.ChildName = new String[ApplicationContext.mListChildren.size()];
        for(int i=0; i<ApplicationContext.mListChildren.size(); i++)
        {
            ApplicationContext.ChildName[i] = ApplicationContext.mListChildren.get(i).getName();
        }
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.setting_alarm:
                Bundle bundle_set_alarm_time = new Bundle();
                bundle_set_alarm_time.putInt(ApplicationContext.ALARM_TIME, ApplicationContext.alarmTime);
                Intent intent_set_alarm_time = new Intent(getActivity(), EditAlarmTimeActivity.class);
                intent_set_alarm_time.putExtras(bundle_set_alarm_time);
                startActivityForResult(intent_set_alarm_time, ApplicationContext.REQUEST_CODE_ALARM_TIME_EDIT);
                break;/*
            case R.id.image_add_gateway:
                Bundle bundle_add_gateway = new Bundle();
                bundle_add_gateway.putInt(ApplicationContext.GATEWAY_NUMBER, gatewayNum);
                Intent intent_add_gateway = new Intent(getActivity(), AddGatewayActivity.class);
                intent_add_gateway.putExtras(bundle_add_gateway);
                startActivityForResult(intent_add_gateway, ApplicationContext.REQUEST_CODE_ADD);
                break;
            case R.id.image_add_parent:
                Bundle bundle_add_parent = new Bundle();
                bundle_add_parent.putInt(ApplicationContext.PARENT_NUMBER, parentNum);
                Intent intent_add_parent = new Intent(getActivity(), AddParentActivity.class);
                intent_add_parent.putExtras(bundle_add_parent);
                startActivityForResult(intent_add_parent, ApplicationContext.REQUEST_CODE_PARENT_ADD);
                break;
            case R.id.image_button_set_ble:
                Intent intent = new Intent(getActivity(), DeviceDiscoveryActivity.class);
                startActivityForResult(intent, ApplicationContext.REQUEST_CODE_SCAN);
                break;*/
            case R.id.button_gateway_list_editor:
                Intent intent_gateway = new Intent();
                intent_gateway.setClass(getActivity(), GatewayListEditorActivity.class);
                startActivity(intent_gateway);
                break;
            case R.id.button_parent_list_editor:
                Intent intent_parent = new Intent();
                intent_parent.setClass(getActivity(),ParentListEditorActivity.class);
                startActivity(intent_parent);
                break;
            case R.id.button_child_profile_list_editor:
                Intent intent_child = new Intent();
                intent_child.setClass(getActivity(),ChildProfileListEditorActivity.class);
                startActivity(intent_child);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            /*
            case R.id.list_set_gateway:
                NewGatewayItem object_gateway = mGatewayAdapter.getData().get(position);
                final String account_gateway = object_gateway.getAccount();
                final String password_gateway = object_gateway.getPassword();
                final String confirm_gateway = object_gateway.getConfirm();
                final String place = object_gateway.getPlace();
                final String near = object_gateway.getNear();
                final String far = object_gateway.getFar();
                final String title_gateway = object_gateway.getTitle();
                final String gid = object_gateway.getGid();
                Bundle bundle_click_gateway = new Bundle();
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_ACCOUNT, account_gateway);
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_PASSWORD, password_gateway);
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_CONFIRM, confirm_gateway);
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_PLACE, place);
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_NEAR, near);
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_FAR, far);
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_TITLE, title_gateway);
                bundle_click_gateway.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
                bundle_click_gateway.putString(ApplicationContext.GATEWAY_ID, gid);
                Intent intent_click_gateway = new Intent(getActivity(), EditGatewayActivity.class);
                intent_click_gateway.putExtras(bundle_click_gateway);
                startActivityForResult(intent_click_gateway, ApplicationContext.REQUEST_CODE_GATEWAY_EDIT);
                break;
            case R.id.list_set_parent:
                //parentNum--;
                //mParentAdapter.getData().remove(position);
                //need to remove this parent
                //mParentAdapter.notifyDataSetChanged();
                NewParentItem object_parent = mParentAdapter.getData().get(position);
                final String account = object_parent.getAccount();
                final String password = object_parent.getPassword();
                final String confirm = object_parent.getConfirm();
                final String title = object_parent.getTitle();
                final String pid = object_parent.getPid();
                Bundle bundle_click_parent = new Bundle();
                bundle_click_parent.putString(ApplicationContext.PARENT_ACCOUNT, account);
                bundle_click_parent.putString(ApplicationContext.PARENT_PASSWORD, password);
                bundle_click_parent.putString(ApplicationContext.PARENT_CONFIRM, confirm);
                bundle_click_parent.putString(ApplicationContext.PARENT_TITLE, title);
                bundle_click_parent.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
                bundle_click_parent.putString(ApplicationContext.PARENT_ID, pid);
                int num_child = object_parent.getmChildList().size();
                if(num_child != 0)
                {
                    bundle_click_parent.putInt(ApplicationContext.PARENT_CREATE_CHILD_NUM,num_child);
                    for (int i = 0; i < num_child; i++) {
                        bundle_click_parent.putString(ApplicationContext.PARENT_CREATE_CHILD_ID + i, object_parent.getmChildList().get(i).getID());
                        int spinner_select;
                        spinner_select = ApplicationContext.findChildById(object_parent.getmChildList().get(i).getID());
                        Log.e("TAG",spinner_select+"\n") ;
                        Log.e("TAG",object_parent.getmChildList().get(i).getID()+"\n") ;
                        bundle_click_parent.putInt(ApplicationContext.PARENT_CREATE_CHILD_SPINNER_SELECT + i, spinner_select);
                    }
                }
                Intent intent_click_parent = new Intent(getActivity(), EditParentActivity.class);
                intent_click_parent.putExtras(bundle_click_parent);
                startActivityForResult(intent_click_parent, ApplicationContext.REQUEST_CODE_PARENT_EDIT);
                break;
            case R.id.list_set_device:
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
                Intent intent = new Intent(getActivity(), ChildProfleEditActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, ApplicationContext.REQUEST_CODE_EDIT);
                break;*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_ALARM_TIME_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    ApplicationContext.alarmTime = bundle.getInt(ApplicationContext.ALARM_TIME,300);
                    int showAlarmtime = ApplicationContext.alarmTime/60;
                    textTime.setText(String.valueOf(showAlarmtime));

                }
                break;
                /*
            case ApplicationContext.REQUEST_CODE_GATEWAY_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String account = bundle.getString(ApplicationContext.GATEWAY_ACCOUNT);
                    final String password = bundle.getString(ApplicationContext.GATEWAY_PASSWORD);
                    final String confirm = bundle.getString(ApplicationContext.GATEWAY_CONFIRM);
                    final String place = bundle.getString(ApplicationContext.GATEWAY_PLACE);
                    final String near = bundle.getString(ApplicationContext.GATEWAY_NEAR);
                    final String far = bundle.getString(ApplicationContext.GATEWAY_FAR);
                    ApplicationContext.signUp_gateway("gateway", account, password, place, ApplicationContext.login_mid, near, far, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                gatewayNum++;
                                NewGatewayItem newGateway = new NewGatewayItem(account, password, confirm, place, near, far);
                                newGateway.setCheck(1);
                                addANewGateway(newGateway);
                            } else {
                                Toast.makeText(getActivity(), "Sign Up Gateway fail!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case ApplicationContext.REQUEST_CODE_GATEWAY_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String place = bundle.getString(ApplicationContext.GATEWAY_PLACE);
                    final String near = bundle.getString(ApplicationContext.GATEWAY_NEAR);
                    final String far = bundle.getString(ApplicationContext.GATEWAY_FAR);
                    final int position = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
                    final String gid = bundle.getString(ApplicationContext.GATEWAY_ID);
                    ApplicationContext.update_gateway("gateway", gid, place, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                NewGatewayItem object = mGatewayAdapter.getData().get(position);
                                object.setPlace(place);
                                object.setCheck(1);
                                mGatewayAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "Sign Up Gateway fail!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    ApplicationContext.update_distance("distance", gid, near, far, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                NewGatewayItem object = mGatewayAdapter.getData().get(position);
                                object.setNear(near);
                                object.setFar(far);
                                mGatewayAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "Sign Up Gateway fail!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else if (resultCode == ApplicationContext.RESULT_CODE_REMOVE) {
                    final int position = data.getIntExtra(ApplicationContext.LIST_VIEW_POSITION, -1);
                    gatewayNum--;
                    String gid = mGatewayAdapter.getData().get(position).getGid();
                    ApplicationContext.delete("gateway",gid);
                    mGatewayAdapter.getData().remove(position);
                    mGatewayAdapter.notifyDataSetChanged();
                }
                break;
            case ApplicationContext.REQUEST_CODE_PARENT_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String account = bundle.getString(ApplicationContext.PARENT_ACCOUNT);
                    final String password = bundle.getString(ApplicationContext.PARENT_PASSWORD);
                    final String confirm = bundle.getString(ApplicationContext.PARENT_CONFIRM);
                    final String pid = bundle.getString(ApplicationContext.PARENT_ID);
                    parentNum++;
                    NewParentItem newParent = new NewParentItem(account,password,confirm);
                    newParent.setPid(pid);
                    newParent.setCheck(1);
                    String childIdlist = "";
                    int num_child =  bundle.getInt(ApplicationContext.PARENT_CREATE_CHILD_NUM, 0);
                    if(num_child !=0)
                    {
                        for(int i=0; i<num_child; i++) {
                            String child_Id = bundle.getString(ApplicationContext.PARENT_CREATE_CHILD_ID+i);
                            childIdlist = childIdlist + child_Id +",";
                            int child_spinner_select = bundle.getInt(ApplicationContext.PARENT_CREATE_CHILD_SPINNER_SELECT+i,0);
                            ParentCreateChildItem mChild = new ParentCreateChildItem(child_Id, child_spinner_select);
                            newParent.addChild(mChild);
                        }
                    }

                    addANewParent(newParent);
                }
                break;
            case ApplicationContext.REQUEST_CODE_PARENT_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final int position = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
                    final String pid = bundle.getString(ApplicationContext.PARENT_ID);
                    NewParentItem object = mParentAdapter.getData().get(position);
                    object.setCheck(1);
                    String childIdlist = "";
                    int num_child =  bundle.getInt(ApplicationContext.PARENT_CREATE_CHILD_NUM, 0);

                    if(num_child !=0)
                    {
                        object.clearmChildList();
                        object.setAddedChildNum(0);
                        for(int i=0; i<num_child; i++) {
                            String child_Id = bundle.getString(ApplicationContext.PARENT_CREATE_CHILD_ID+i);
                            childIdlist = childIdlist + child_Id +",";
                            int child_spinner_select = bundle.getInt(ApplicationContext.PARENT_CREATE_CHILD_SPINNER_SELECT+i,0);
                            ParentCreateChildItem mChild = new ParentCreateChildItem(child_Id, child_spinner_select);
                            object.getmChildList().add(mChild);
                        }
                        ApplicationContext.parent_child_define(pid,childIdlist);
                        Log.e("TAG",childIdlist+"\n");
                        mParentAdapter.notifyDataSetChanged();
                    }
                } else if (resultCode == ApplicationContext.RESULT_CODE_REMOVE) {
                    final int position = data.getIntExtra(ApplicationContext.LIST_VIEW_POSITION, -1);
                    parentNum--;
                    mParentAdapter.getData().remove(position);
                    String pid = mParentAdapter.getData().get(position).getPid();
                    ApplicationContext.delete("parent",pid);
                    mParentAdapter.notifyDataSetChanged();
                }

                break;
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
                break;*/
        }

    }

    private void addANewGateway(NewGatewayItem gateway) {
        mGateways.add(gateway);
        mGatewayAdapter.notifyDataSetChanged();
        gatewayList.setSelection(mGatewayAdapter.getCount() - 1);
    }

    private void addANewParent(NewParentItem parent) {
        mParents.add(parent);
        mParentAdapter.notifyDataSetChanged();
        parentList.setSelection(mParentAdapter.getCount() - 1);
    }

}
