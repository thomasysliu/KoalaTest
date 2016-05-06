package cc.nctu1210.childcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.GatewayListAdapter;
import cc.nctu1210.view.NewGatewayItem;

public class GatewayCreateActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private ImageView imgAdd;
    public static Button btNext;
    private ListView gatewayList;
    private List<NewGatewayItem> mGateways = new ArrayList<NewGatewayItem>();
    private GatewayListAdapter mAdapter;
    private int gatewayNum = 0;
    private String type = "gateway";
    private int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gateway_create);
        imgAdd = (ImageView)findViewById(R.id.image_add);
        imgAdd.setOnClickListener(this);
        gatewayList = (ListView)findViewById(R.id.new_gateway_list);
        mAdapter = new GatewayListAdapter(this, mGateways);
        gatewayList.setAdapter(mAdapter);
        gatewayList.setOnItemClickListener(this);

        btNext = (Button)findViewById(R.id.bt_next);
        btNext.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_GATEWAY_ADD:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String account = bundle.getString(ApplicationContext.GATEWAY_ACCOUNT);
                    final String password = bundle.getString(ApplicationContext.GATEWAY_PASSWORD);
                    final String confirm = bundle.getString(ApplicationContext.GATEWAY_CONFIRM);
                    final String place = bundle.getString(ApplicationContext.GATEWAY_PLACE);
                    final String near = bundle.getString(ApplicationContext.GATEWAY_NEAR);
                    final String far = bundle.getString(ApplicationContext.GATEWAY_FAR);
                    final String gid = bundle.getString(ApplicationContext.GATEWAY_ID);
                    gatewayNum++;
                    NewGatewayItem newGateway = new NewGatewayItem(account, password, confirm, place, near, far);
                    newGateway.setGid(gid);
                    newGateway.setCheck(1);
                    addANewGateway(newGateway);
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
                    NewGatewayItem object = mAdapter.getData().get(position);
                    object.setPlace(place);
                    object.setCheck(1);
                    object.setNear(near);
                    object.setFar(far);
                    mAdapter.notifyDataSetChanged();
                } else if (resultCode == ApplicationContext.RESULT_CODE_REMOVE) {
                    final int position = data.getIntExtra(ApplicationContext.LIST_VIEW_POSITION, -1);
                    gatewayNum--;
                    mAdapter.getData().remove(position);
                    ApplicationContext.mGateways.clear();
                    ApplicationContext.mGateways.addAll(mGateways);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }

    }
    private void addANewGateway(NewGatewayItem gateway) {
        mGateways.add(gateway);
        mAdapter.notifyDataSetChanged();
        gatewayList.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add:
                Bundle bundle = new Bundle();
                bundle.putInt(ApplicationContext.GATEWAY_NUMBER, gatewayNum);
                Intent intent = new Intent(GatewayCreateActivity.this, AddGatewayActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, ApplicationContext.REQUEST_CODE_GATEWAY_ADD);
                break;
            case R.id.bt_next:
                Intent intent_next = new Intent();
                intent_next.setClass(GatewayCreateActivity.this, ChildProfileCreateActivity.class);
                startActivity(intent_next);
                finish();
                //ApplicationContext.getInstance().saveGatewayPreferences();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewGatewayItem object = mAdapter.getData().get(position);
        final String account = object.getAccount();
        final String place = object.getPlace();
        final String near = object.getNear();
        final String far = object.getFar();
        final String title = object.getTitle();
        final String gid = object.getGid();
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationContext.GATEWAY_ACCOUNT, account);
        bundle.putString(ApplicationContext.GATEWAY_PLACE, place);
        bundle.putString(ApplicationContext.GATEWAY_NEAR, near);
        bundle.putString(ApplicationContext.GATEWAY_FAR, far);
        bundle.putString(ApplicationContext.GATEWAY_TITLE, title);
        bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
        bundle.putString(ApplicationContext.GATEWAY_ID, gid);
        Intent intent = new Intent(GatewayCreateActivity.this, EditGatewayActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ApplicationContext.REQUEST_CODE_GATEWAY_EDIT);
    }

    @Override
    public void onBackPressed() {
    }
}
