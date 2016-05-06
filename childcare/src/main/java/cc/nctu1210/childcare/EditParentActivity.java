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

public class EditParentActivity extends Activity implements View.OnClickListener {
    private TextView parentTitle;
    private ImageView addChild;
    private ListView new_child_list;
    private List<ParentCreateChildItem> mNewChild = new ArrayList<ParentCreateChildItem>();
    private ParentCreateChildrenListAdapter mAdapter;
    private int viewPosition;
    private Button mButtonOk;
    private Button mButtonRemove;
    private String pid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.edit_parent);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void init() {
        parentTitle = (TextView) findViewById(R.id.parent_title);
        addChild = (ImageView) findViewById(R.id.image_add_child);
        mButtonOk = (Button) findViewById(R.id.button_edit_ok);
        mButtonRemove = (Button) findViewById(R.id.button_edit_remove);
        new_child_list = (ListView) findViewById(R.id.new_child_list);
        mAdapter = new ParentCreateChildrenListAdapter(this,mNewChild);
        new_child_list.setAdapter(mAdapter);
        addChild.setOnClickListener(this);
        mButtonOk.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String account = bundle.getString(ApplicationContext.PARENT_ACCOUNT);
        final String password = bundle.getString(ApplicationContext.PARENT_PASSWORD);
        final String confirm = bundle.getString(ApplicationContext.PARENT_CONFIRM);
        final String title = bundle.getString(ApplicationContext.PARENT_TITLE);
        pid = bundle.getString(ApplicationContext.PARENT_ID);
        parentTitle.setText(title);
        viewPosition = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
        int num_child =  bundle.getInt(ApplicationContext.PARENT_CREATE_CHILD_NUM, 0);
        if(num_child !=0)
        {
            for(int i=0; i<num_child; i++) {
                String child_Id = bundle.getString(ApplicationContext.PARENT_CREATE_CHILD_ID+i);
                int child_spinner_select = bundle.getInt(ApplicationContext.PARENT_CREATE_CHILD_SPINNER_SELECT+i,0);
                ParentCreateChildItem mChild = new ParentCreateChildItem(child_Id, child_spinner_select);
                mNewChild.add(mChild);
            }
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.image_add_child:
                int num = mAdapter.getCount()+1;
                ParentCreateChildItem newChild = new ParentCreateChildItem(String.valueOf(num));
                mNewChild.add(newChild);
                mAdapter.notifyDataSetChanged();
                new_child_list.setSelection(mAdapter.getCount() - 1);
                break;
            case R.id.button_edit_ok:
                if(ApplicationContext.checkInternetConnection(this)) {
                    final Intent intent = getIntent();
                    final Bundle bundle = new Bundle();
                    bundle.putString(ApplicationContext.PARENT_ID, pid);
                    int position = viewPosition;
                    bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
                    final int num_child = mAdapter.getCount();
                    String childIdlist = "";
                    if(num_child!=0) {
                        for (int i = 0; i < num_child; i++) {
                            childIdlist = childIdlist + mNewChild.get(i).getID() + ",";
                        }
                        ApplicationContext.showProgressDialog(this);
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
                                    Toast.makeText(EditParentActivity.this, "Edit parent fail! ", Toast.LENGTH_LONG).show();
                                    ApplicationContext.dismissProgressDialog();
                                }
                            }
                        });
                    }
                }
                else
                    Toast.makeText(EditParentActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();

                break;
            case R.id.button_edit_remove:
                if(ApplicationContext.checkInternetConnection(this)) {
                    final Intent intent_remove = getIntent();
                    final int position_remove = viewPosition;
                    ApplicationContext.showProgressDialog(this);
                    ApplicationContext.delete("parent", pid, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.dismissProgressDialog();
                                intent_remove.putExtra(ApplicationContext.LIST_VIEW_POSITION, position_remove);
                                setResult(ApplicationContext.RESULT_CODE_REMOVE, intent_remove);
                                finish();
                            } else {
                                Toast.makeText(EditParentActivity.this, "Remove parent fail! ", Toast.LENGTH_LONG).show();
                                ApplicationContext.dismissProgressDialog();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(EditParentActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
