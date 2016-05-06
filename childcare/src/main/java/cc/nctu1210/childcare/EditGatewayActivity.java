package cc.nctu1210.childcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.nctu1210.api.koala3x.KoalaService;
import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.api.koala3x.SensorEvent;
import cc.nctu1210.api.koala3x.SensorEventListener;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.NewParentItem;
import cc.nctu1210.view.ParentCreateChildItem;
import cc.nctu1210.view.ParentCreateChildrenListAdapter;
import cc.nctu1210.view.ParentListAdapter;


public class EditGatewayActivity extends Activity implements View.OnClickListener {

    private TextView gatewayTitle;
    private EditText editPlace;
    private EditText editNear;
    private EditText editFar;
    private int viewPosition;
    private Button mButtonOk;
    private Button mButtonRemove;
    private String gid = "";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.edit_gateway);
        progressDialog = new ProgressDialog(EditGatewayActivity.this);
        progressDialog.setTitle(getString(R.string.processing_title));
        progressDialog.setMessage(getString(R.string.processing_dialog));
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void init() {
        gatewayTitle = (TextView) findViewById(R.id.gateway_title);
        editPlace = (EditText) findViewById(R.id.edt_place);
        editNear = (EditText) findViewById(R.id.edt_near);
        editFar = (EditText) findViewById(R.id.edt_far);
        mButtonOk = (Button) findViewById(R.id.button_edit_ok);
        mButtonRemove = (Button) findViewById(R.id.button_edit_remove);
        mButtonOk.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String account = bundle.getString(ApplicationContext.GATEWAY_ACCOUNT);
        final String place = bundle.getString(ApplicationContext.GATEWAY_PLACE);
        final String near = bundle.getString(ApplicationContext.GATEWAY_NEAR);
        final String far = bundle.getString(ApplicationContext.GATEWAY_FAR);
        final String title = bundle.getString(ApplicationContext.GATEWAY_TITLE);
        gid = bundle.getString(ApplicationContext.GATEWAY_ID);
        gatewayTitle.setText(title);
        viewPosition = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
        editPlace.setText(place);
        editNear.setText(near);
        editFar.setText(far);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_edit_ok:
                if(ApplicationContext.checkInternetConnection(this)) {
                    final Intent intent = getIntent();
                    final Bundle bundle = new Bundle();
                    final String place = editPlace.getText().toString();
                    final String near = editNear.getText().toString();
                    final String far = editFar.getText().toString();
                    progressDialog.show();
                    ApplicationContext.update_gateway("gateway", gid, place, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                bundle.putString(ApplicationContext.GATEWAY_PLACE, place);
                                ApplicationContext.update_distance("distance", gid, near, far, new CallBack() {
                                    @Override
                                    public void done(CallBackContent content) {
                                        if (content != null) {
                                            progressDialog.dismiss();
                                            bundle.putString(ApplicationContext.GATEWAY_NEAR, near);
                                            bundle.putString(ApplicationContext.GATEWAY_FAR, far);
                                            bundle.putString(ApplicationContext.GATEWAY_ID, gid);
                                            int position = viewPosition;
                                            bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
                                            intent.putExtras(bundle);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        } else {
                                            Toast.makeText(EditGatewayActivity.this, "update Gateway distance fail!", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(EditGatewayActivity.this, "update Gateway place fail!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(EditGatewayActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                break;
            case R.id.button_edit_remove:
                if(ApplicationContext.checkInternetConnection(this)) {
                    final Intent intent_remove = getIntent();
                    final int position_remove = viewPosition;
                    ApplicationContext.delete("gateway", gid, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                progressDialog.dismiss();
                                intent_remove.putExtra(ApplicationContext.LIST_VIEW_POSITION, position_remove);
                                setResult(ApplicationContext.RESULT_CODE_REMOVE, intent_remove);
                                finish();
                            } else {
                                Toast.makeText(EditGatewayActivity.this, "Remove parent fail! ", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(EditGatewayActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                break;
        }
    }

}
