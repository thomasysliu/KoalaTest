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

public class GatewayListEditorActivity extends Activity  implements OnClickListener, AdapterView.OnItemClickListener{
    private static final String TAG = GatewayListEditorActivity.class.getSimpleName();
    private ImageView imgAddGateway;
    private ListView gatewayList;
    private List<NewGatewayItem> mGateways = new ArrayList<NewGatewayItem>();
    private GatewayListAdapterForMaster mGatewayAdapter;
    private int gatewayNum = 0;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.gateway_list_editor);
        mGateways = ApplicationContext.mGateways;
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateGatewayList();
    }

    private void initView() {
        btnClose = (Button) findViewById(R.id.bt_close);
        btnClose.setOnClickListener(this);
        gatewayList = (ListView) findViewById(R.id.list_set_gateway);
        mGatewayAdapter = new GatewayListAdapterForMaster(this, mGateways);
        gatewayList.setAdapter(mGatewayAdapter);
        gatewayList.setOnItemClickListener(this);
        imgAddGateway = (ImageView)findViewById(R.id.image_add_gateway);
        imgAddGateway.setOnClickListener(this);
        ApplicationContext.showProgressDialog(this);
        ApplicationContext.show_all_gateway(ApplicationContext.login_mid, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    ApplicationContext.mGateways = content.getShow_gateway();
                    populateGatewayList();
                } else {
                    Log.e(TAG, "show_all_gateway fail" + "\n");
                }
            }
        });
        ApplicationContext.dismissProgressDialog();
    }

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add_gateway:
                Bundle bundle_add_gateway = new Bundle();
                bundle_add_gateway.putInt(ApplicationContext.GATEWAY_NUMBER, gatewayNum);
                Intent intent_add_gateway = new Intent(this, AddGatewayActivity.class);
                intent_add_gateway.putExtras(bundle_add_gateway);
                startActivityForResult(intent_add_gateway, ApplicationContext.REQUEST_CODE_GATEWAY_ADD);
                break;
            case R.id.bt_close:
                    finish();
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        Intent intent_click_gateway = new Intent(this, EditGatewayActivity.class);
        intent_click_gateway.putExtras(bundle_click_gateway);
        startActivityForResult(intent_click_gateway, ApplicationContext.REQUEST_CODE_GATEWAY_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_GATEWAY_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String account = bundle.getString(ApplicationContext.GATEWAY_ACCOUNT);
                    final String password = bundle.getString(ApplicationContext.GATEWAY_PASSWORD);
                    final String confirm = bundle.getString(ApplicationContext.GATEWAY_CONFIRM);
                    final String place = bundle.getString(ApplicationContext.GATEWAY_PLACE);
                    final String near = bundle.getString(ApplicationContext.GATEWAY_NEAR);
                    final String far = bundle.getString(ApplicationContext.GATEWAY_FAR);
                    gatewayNum++;
                    ApplicationContext.signUp_gateway("gateway", account, password, place, ApplicationContext.login_mid, near, far, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                NewGatewayItem newGateway = new NewGatewayItem(account, password, confirm, place, near, far);
                                newGateway.setGid(content.getmGid());
                                newGateway.setCheck(1);
                                addANewGateway(newGateway);
                            } else {
                                Toast.makeText(GatewayListEditorActivity.this, "Sign Up Gateway fail!", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(GatewayListEditorActivity.this, "update Gateway place fail!", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(GatewayListEditorActivity.this, "update Gateway pdistance fail!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if (resultCode == ApplicationContext.RESULT_CODE_REMOVE) {
                    final int position = data.getIntExtra(ApplicationContext.LIST_VIEW_POSITION, -1);
                    gatewayNum--;
                    String gid = mGatewayAdapter.getData().get(position).getGid();
                    ApplicationContext.delete("gateway", gid);

                    mGatewayAdapter.getData().remove(position);
                    ApplicationContext.mGateways.clear();
                    ApplicationContext.mGateways.addAll(mGateways);
                    mGatewayAdapter.notifyDataSetChanged();
                }
                break;
        }

    }

    private void addANewGateway(NewGatewayItem gateway) {
        mGateways.add(gateway);
        ApplicationContext.mGateways.clear();
        ApplicationContext.mGateways.addAll(mGateways);
        mGatewayAdapter.notifyDataSetChanged();
        gatewayList.setSelection(mGatewayAdapter.getCount() - 1);
    }

}
