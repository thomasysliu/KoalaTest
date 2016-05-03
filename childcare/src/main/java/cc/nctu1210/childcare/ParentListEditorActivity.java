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

public class ParentListEditorActivity extends Activity implements OnClickListener, AdapterView.OnItemClickListener{
    private static final String TAG = ParentListEditorActivity.class.getSimpleName();
    private ImageView imgAddParent;
    private ListView parentList;
    private List<NewParentItem> mParents = new ArrayList<NewParentItem>();
    private ParentListAdapterForMaster mParentAdapter;
    private int parentNum = 0;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.parent_list_editor);
        mParents = ApplicationContext.mParents;
        initView();
    }


    @Override
    public void onResume() {
        super.onResume();
        populateParentList();
    }

    private void initView() {
        btnClose = (Button) findViewById(R.id.bt_close);
        btnClose.setOnClickListener(this);
        parentList = (ListView)findViewById(R.id.list_set_parent);
        mParentAdapter = new ParentListAdapterForMaster(this, mParents);
        parentList.setAdapter(mParentAdapter);
        parentList.setOnItemClickListener(this);
        imgAddParent = (ImageView)findViewById(R.id.image_add_parent);
        imgAddParent.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add_parent:
                Bundle bundle_add_parent = new Bundle();
                bundle_add_parent.putInt(ApplicationContext.PARENT_NUMBER, parentNum);
                Intent intent_add_parent = new Intent(this, AddParentActivity.class);
                intent_add_parent.putExtras(bundle_add_parent);
                startActivityForResult(intent_add_parent, ApplicationContext.REQUEST_CODE_PARENT_ADD);
                break;
            case R.id.bt_close:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
        Intent intent_click_parent = new Intent(this, EditParentActivity.class);
        intent_click_parent.putExtras(bundle_click_parent);
        startActivityForResult(intent_click_parent, ApplicationContext.REQUEST_CODE_PARENT_EDIT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
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
        }

    }

    private void addANewParent(NewParentItem parent) {
        mParents.add(parent);
        mParentAdapter.notifyDataSetChanged();
        parentList.setSelection(mParentAdapter.getCount() - 1);
    }

}
