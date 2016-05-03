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

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.ParentCreateChildItem;
import cc.nctu1210.view.ParentListAdapter;
import cc.nctu1210.view.NewParentItem;

public class ParentCreateActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView imgAdd;
    public static Button btFinish;
    private ListView parentList;
    private List<NewParentItem> mParents = new ArrayList<NewParentItem>();
    private ParentListAdapter mAdapter;
    private int parentNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_create);
        imgAdd = (ImageView)findViewById(R.id.image_add);
        imgAdd.setOnClickListener(this);
        parentList = (ListView)findViewById(R.id.new_parent_list);
        mAdapter = new ParentListAdapter(this,mParents);
        parentList.setAdapter(mAdapter);
        parentList.setOnItemClickListener(this);
/*
        ApplicationContext.ChildName = new String[ApplicationContext.mListChildren.size()];
        for(int i=0; i<ApplicationContext.mListChildren.size(); i++)
        {
            ApplicationContext.ChildName[i] = ApplicationContext.mListChildren.get(i).getName();
            Log.e("TAG","test "+ApplicationContext.mListChildren.get(i).getCid()+"\n");
        }*/
        for(int i=0; i<ApplicationContext.mListChildren.size(); i++)
        {
            ApplicationContext.show_child_by_id(ApplicationContext.mListChildren.get(i).getCid(), new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        ;
                    } else {
                        Log.e("TAG", "show_child_by_id fail" + "\n");
                    }
                }
            });
        }

        btFinish = (Button)findViewById(R.id.bt_finish);
        btFinish.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_PARENT_ADD:
                if (resultCode == RESULT_OK) {
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
                        ApplicationContext.parent_child_define(pid,childIdlist);
                        Log.e("TAG", "TEST childList: " + childIdlist + "\n");
                    }
                    addANewParent(newParent);
                }
                break;
            case ApplicationContext.REQUEST_CODE_PARENT_EDIT:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final int position = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
                    final String pid = bundle.getString(ApplicationContext.PARENT_ID);
                    NewParentItem object = mAdapter.getData().get(position);
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
                        mAdapter.notifyDataSetChanged();
                    }
                } else if (resultCode == ApplicationContext.RESULT_CODE_REMOVE) {
                    final int position = data.getIntExtra(ApplicationContext.LIST_VIEW_POSITION, -1);
                    parentNum--;
                    String pid = mAdapter.getData().get(position).getPid();
                    mAdapter.getData().remove(position);
                    ApplicationContext.delete("parent", pid);
                    mAdapter.notifyDataSetChanged();

                }

                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add:
                Bundle bundle = new Bundle();
                bundle.putInt(ApplicationContext.PARENT_NUMBER, parentNum);
                Intent intent = new Intent(ParentCreateActivity.this, AddParentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, ApplicationContext.REQUEST_CODE_PARENT_ADD);
                break;
            case R.id.bt_finish:
                ApplicationContext.getInstance().saveParentPreferences();
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewParentItem object = mAdapter.getData().get(position);
        final String account = object.getAccount();
        final String password = object.getPassword();
        final String confirm = object.getConfirm();
        final String title = object.getTitle();
        final String pid = object.getPid();
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationContext.PARENT_ACCOUNT, account);
        bundle.putString(ApplicationContext.PARENT_PASSWORD, password);
        bundle.putString(ApplicationContext.PARENT_CONFIRM, confirm);
        bundle.putString(ApplicationContext.PARENT_TITLE, title);
        bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
        bundle.putString(ApplicationContext.PARENT_ID, pid);
        int num_child = object.getmChildList().size();
        if(num_child != 0)
        {
            bundle.putInt(ApplicationContext.PARENT_CREATE_CHILD_NUM,num_child);
            for (int i = 0; i < num_child; i++) {
                bundle.putString(ApplicationContext.PARENT_CREATE_CHILD_ID + i, object.getmChildList().get(i).getID());
                bundle.putInt(ApplicationContext.PARENT_CREATE_CHILD_SPINNER_SELECT + i, object.getmChildList().get(i).getSpinnerSelect());
            }
        }
        Intent intent = new Intent(ParentCreateActivity.this, EditParentActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ApplicationContext.REQUEST_CODE_PARENT_EDIT);
    }

    private void addANewParent(NewParentItem parent) {
        mParents.add(parent);
        mAdapter.notifyDataSetChanged();
        parentList.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    public void onBackPressed() {
    }
}
