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

public class AddParentActivity extends Activity implements View.OnClickListener{
    private TextView parentTitle;
    private EditText editAccount;
    private EditText editPassword;
    private EditText editConfirm;
    private ImageView addChild;
    private ListView new_child_list;
    private List<ParentCreateChildItem> mNewChild = new ArrayList<ParentCreateChildItem>();
    private ParentCreateChildrenListAdapter mAdapter;

    private Button mButtonOk;
    private Button mButtonCancel;
    private String type = "parent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.add_parent);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void init() {
        parentTitle = (TextView) findViewById(R.id.parent_title);
        editAccount = (EditText) findViewById(R.id.edt_account);
        editPassword = (EditText) findViewById(R.id.edt_password);
        editConfirm = (EditText) findViewById(R.id.edt_confirm);
        addChild = (ImageView) findViewById(R.id.image_add_child);
        mButtonOk = (Button) findViewById(R.id.button_new_ok);
        mButtonCancel = (Button) findViewById(R.id.button_new_cancel);
        new_child_list = (ListView) findViewById(R.id.new_child_list);
        mAdapter = new ParentCreateChildrenListAdapter(this,mNewChild);
        new_child_list.setAdapter(mAdapter);
        addChild.setOnClickListener(this);
        mButtonOk.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int parentNum = bundle.getInt(ApplicationContext.PARENT_NUMBER, 0);
        String title = getString(R.string.parent) + String.valueOf(parentNum+1);
        parentTitle.setText(title);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.image_add_child:
                int num = mAdapter.getCount()+1;
                ParentCreateChildItem newChild = new ParentCreateChildItem(String.valueOf(num));
                newChild.setId(ApplicationContext.mListChildren.get(0).getCid());
                mNewChild.add(newChild);
                mAdapter.notifyDataSetChanged();
                new_child_list.setSelection(mAdapter.getCount() - 1);
                break;
            case R.id.button_new_ok:
                final Intent intent = getIntent();
                final Bundle bundle = new Bundle();
                final String account = editAccount.getText().toString();
                final String password = editPassword.getText().toString();
                final String confirm = editConfirm.getText().toString();
                if(password.equals(confirm))
                {
                    int mid;
                    if(ApplicationContext.mIsLogin)
                        mid = ApplicationContext.login_mid;
                    else
                        mid = ApplicationContext.signup_mid;
                    if(ApplicationContext.checkInternetConnection(this)) {
                        ApplicationContext.showProgressDialog(this);
                        ApplicationContext.signUp_parent(type, account, password, mid, new CallBack() {
                            @Override
                            public void done(CallBackContent content) {
                                if (content != null) {
                                    final String pid = content.getParent().getPid();
                                    bundle.putString(ApplicationContext.PARENT_ACCOUNT, account);
                                    bundle.putString(ApplicationContext.PARENT_PASSWORD, password);
                                    bundle.putString(ApplicationContext.PARENT_CONFIRM, confirm);
                                    bundle.putString(ApplicationContext.PARENT_ID, pid);
                                    String childIdlist = "";
                                    final int num_child = mAdapter.getCount();
                                    if (num_child != 0) {

                                        for (int i = 0; i < num_child; i++) {
                                            childIdlist = childIdlist + mNewChild.get(i).getID() + ",";
                                        }
                                        ApplicationContext.parent_child_define(pid, childIdlist, new CallBack() {
                                            @Override
                                            public void done(CallBackContent content) {
                                                if (content != null) {
                                                    bundle.putInt(ApplicationContext.PARENT_CREATE_CHILD_NUM, num_child);
                                                    for (int i = 0; i < num_child; i++) {
                                                        bundle.putString(ApplicationContext.PARENT_CREATE_CHILD_ID + i, mNewChild.get(i).getID());
                                                        bundle.putInt(ApplicationContext.PARENT_CREATE_CHILD_SPINNER_SELECT + i, mNewChild.get(i).getSpinnerSelect());
                                                    }
                                                    ApplicationContext.dismissProgressDialog();
                                                    intent.putExtras(bundle);
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(AddParentActivity.this, "Sign up parent succeed, but child define fail! ", Toast.LENGTH_LONG).show();
                                                    ApplicationContext.dismissProgressDialog();
                                                    intent.putExtras(bundle);
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }
                                            }
                                        });
                                    } else {
                                        ApplicationContext.dismissProgressDialog();
                                        intent.putExtras(bundle);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(AddParentActivity.this, "add parent fail!", Toast.LENGTH_LONG).show();
                                    ApplicationContext.dismissProgressDialog();
                                }
                            }
                        });
                    }
                    else
                        Toast.makeText(AddParentActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(AddParentActivity.this, getString(R.string.error_password_confirm_not_same), Toast.LENGTH_LONG).show();
                break;
            case R.id.button_new_cancel:
                finish();
                break;
        }
    }
}
