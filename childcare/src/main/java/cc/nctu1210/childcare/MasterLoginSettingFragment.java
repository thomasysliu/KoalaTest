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


public class MasterLoginSettingFragment extends Fragment implements OnClickListener {
    private static final String TAG = MasterLoginSettingFragment.class.getSimpleName();
    private LinearLayout SetAlarmTime;
    private TextView textTime;
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
        initView();
    }


    private void initView() {
        SetAlarmTime = (LinearLayout) this.getView().findViewById(R.id.setting_alarm);
        SetAlarmTime.setOnClickListener(this);
        textTime = (TextView) this.getView().findViewById(R.id.text_time);
        double showAlarmtime = (double)ApplicationContext.alarmTime/60;
        textTime.setText(String.valueOf(showAlarmtime));
        btGatewayEditor = (Button) this.getView().findViewById(R.id.button_gateway_list_editor);
        btGatewayEditor.setOnClickListener(this);
        btParentEditor = (Button) this.getView().findViewById(R.id.button_parent_list_editor);
        btParentEditor.setOnClickListener(this);
        btChildEditor = (Button) this.getView().findViewById(R.id.button_child_profile_list_editor);
        btChildEditor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_alarm:
                Bundle bundle_set_alarm_time = new Bundle();
                bundle_set_alarm_time.putInt(ApplicationContext.ALARM_TIME, ApplicationContext.alarmTime);
                Intent intent_set_alarm_time = new Intent(getActivity(), EditAlarmTimeActivity.class);
                intent_set_alarm_time.putExtras(bundle_set_alarm_time);
                startActivityForResult(intent_set_alarm_time, ApplicationContext.REQUEST_CODE_ALARM_TIME_EDIT);
                break;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_ALARM_TIME_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    ApplicationContext.alarmTime = bundle.getInt(ApplicationContext.ALARM_TIME, 300);
                    double showAlarmtime = (double)ApplicationContext.alarmTime / 60;
                    textTime.setText(String.valueOf(showAlarmtime));
                }
                break;
        }
    }
}
