package cc.nctu1210.childcare;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.polling.ParentScheduledService;
import cc.nctu1210.polling.TeacherScheduledService;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapter;

public class ParentLoginActivity extends Activity implements View.OnClickListener {
    private final String TAG = ParentLoginActivity.class.getSimpleName();
    public static ChildrenListAdapter mChildListAdapter;
    public static List<ChildItem> mChildItems = new ArrayList<ChildItem>();
    private ListView mListViewChildren;
    public static Activity mActivity;
    private List<ChildProfile> mListChildren;
    private HashMap<String,ChildProfile> mMapChildren;
    private long exitTime = 0L;
    private String [] cids;
    private int i;

    private TextView mTextViewStatus;
    private ImageView mImageViewKoala;
    private Intent mParentPollingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_login);
        mListChildren = ApplicationContext.mListChildren;
        mMapChildren = ApplicationContext.mMapChildren;
        cids = ApplicationContext.cids.split(",");
        mActivity = ParentLoginActivity.this;
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ApplicationContext.mIsServiceOn) {
            ApplicationContext.notificationServiceStartBuilder(this, ApplicationContext.PARENT_TYPE);
            mParentPollingIntent = new Intent(ParentLoginActivity.this, ParentScheduledService.class);
            ParentLoginActivity.this.startService(mParentPollingIntent);
        } else {
            ApplicationContext.cancelNotificationService(this);
            mParentPollingIntent = new Intent(ParentLoginActivity.this, ParentScheduledService.class);
            ParentLoginActivity.this.stopService(mParentPollingIntent);
        }
        if (ApplicationContext.mIsLogin) {
            ApplicationContext.showProgressDialog(this);
            ApplicationContext.login_admin(ApplicationContext.mLoginFlag, ApplicationContext.mAccount, ApplicationContext.mPassword, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        ApplicationContext.login_mid = content.getMid();
                        ApplicationContext.cids = content.getCids();
                        ApplicationContext.mPid = content.getmPid();
                        ApplicationContext.mIsLogin = true;
                        ApplicationContext.mListChildren.clear();
                        ApplicationContext.mMapChildren.clear();
                        initView();
                    } else {
                        Toast.makeText(ParentLoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                    }
                }
            });
            ApplicationContext.dismissProgressDialog();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationContext.mIsServiceOn=false;
        ApplicationContext.cancelNotificationService(this);
        mParentPollingIntent = new Intent(ParentLoginActivity.this, ParentScheduledService.class);
        ParentLoginActivity.this.stopService(mParentPollingIntent);
    }

    private void initView() {
        mTextViewStatus = (TextView) findViewById(R.id.text_parent_status);
        if(ApplicationContext.mIsServiceOn) {
            mTextViewStatus.setText(getString(R.string.toggle_on));
        } else {
            mTextViewStatus.setText(getString(R.string.toggle_off));
        }
        mImageViewKoala = (ImageView) findViewById(R.id.image_parent_user);
        mImageViewKoala.setOnClickListener(this);

        mChildListAdapter = new ChildrenListAdapter(ParentLoginActivity.this, mChildItems,0);
        mListViewChildren = (ListView) findViewById(R.id.list_main_child);
        mListViewChildren.setAdapter(mChildListAdapter);
        final int num_of_children = cids.length;
        if (mListChildren.size() != num_of_children) {
            ApplicationContext.mShowChildCount = 0;
            for (i=0; i<num_of_children; i++) {
                ApplicationContext.show_child_by_id(cids[i], new CallBack() {
                    @Override
                    public void done(CallBackContent content) {
                        if (content != null) {
                            ApplicationContext.mShowChildCount++;
                            ChildProfile mChild = content.getChild();
                            ApplicationContext.addANewChild(mChild);
                            if (ApplicationContext.mShowChildCount == num_of_children)
                                populateList();
                        } else {
                            Log.e(TAG, "show_child_by_id fail" + "\n");
                        }
                    }
                });
            }
        }
    }

    private void populateList() {
        mChildListAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mChildListAdapter.getData().size());
        for (int i = 0, size = mListChildren.size(); i < size; i++) {
            ChildItem object = new ChildItem(mListChildren.get(i).getName(),mListChildren.get(i).getStatus());
            object.photoName = mListChildren.get(i).getPhotoName();
            object.place = mListChildren.get(i).getPlace();
            object.flag = mListChildren.get(i).getFlag();
            object.cid = mListChildren.get(i).getCid();
            object.rssi = mListChildren.get(i).getRssi();
            mChildItems.add(object);
        }
        Log.i(TAG, "Initialized ListView....." + mChildListAdapter.getData().size());
        mChildListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000L) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.finish_system),
                        Toast.LENGTH_LONG).show();
                this.exitTime = System.currentTimeMillis();
                return true;
            }
            ApplicationContext.cancelNotificationService(this);
            if (ApplicationContext.mIsServiceOn) {
                ApplicationContext.mIsServiceOn = false;
                mParentPollingIntent = new Intent(this, ParentScheduledService.class);
                this.stopService(mParentPollingIntent);
            }
            exitPrograms();
        }
        return super.dispatchKeyEvent(event);
    }

    public void exitPrograms() {
        Intent intent_service_stop= new Intent(ParentLoginActivity.this, ParentScheduledService.class);
        ParentLoginActivity.this.stopService(intent_service_stop);
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        // set activity flag
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // exit app
        android.os.Process.killProcess(Process.myPid());
    }

    @Override
    public void onClick(View v) {
        if (ApplicationContext.mIsServiceOn) {
            mTextViewStatus.setText(getString(R.string.toggle_off));
            ApplicationContext.mIsServiceOn=false;
            ApplicationContext.cancelNotificationService(this);
            mParentPollingIntent = new Intent(ParentLoginActivity.this, ParentScheduledService.class);
            ParentLoginActivity.this.stopService(mParentPollingIntent);
        } else {
            mTextViewStatus.setText(getString(R.string.toggle_on));
            ApplicationContext.mIsServiceOn=true;
            ApplicationContext.notificationServiceStartBuilder(this, ApplicationContext.PARENT_TYPE);
            mParentPollingIntent = new Intent(ParentLoginActivity.this, ParentScheduledService.class);
            ParentLoginActivity.this.startService(mParentPollingIntent);
        }
    }
}
