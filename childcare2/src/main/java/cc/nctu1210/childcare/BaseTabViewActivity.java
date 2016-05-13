package cc.nctu1210.childcare;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.TabManager;
import cc.nctu1210.view.TabItemView;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class BaseTabViewActivity extends FragmentActivity {
    private static final String TAG = BaseTabViewActivity.class.getSimpleName();
    private long exitTime = 0L;
    private TabHost tabhost;
    private TabManager tabManager;
    private LinearLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyStoragePermissions(this);
            verifyCoaseLocationPermissions(this);
        }
        init();
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, ApplicationContext.PERMISSIONS[1]);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    ApplicationContext.PERMISSIONS,
                    ApplicationContext.REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void verifyCoaseLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, ApplicationContext.PERMISSIONS[2]);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    ApplicationContext.PERMISSIONS,
                    ApplicationContext.REQUEST_COARSE_LOCATION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ApplicationContext.REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "coarse location permission granted");
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Functionality limited");
                        builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }

                        });
                        builder.show();
                    }
                } else {
                    Log.w(TAG, "no permission granted!!");
                }
                break;
            case ApplicationContext.REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "coarse location permission granted");
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Functionality limited");
                        builder.setMessage("Since storage access has not been granted, this app will not be able to store any images.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }

                        });
                        builder.show();
                    }
                } else {
                    Log.w(TAG, "no permission granted!!");
                }
                break;
        }
    }



    private void init() {
        //Use contentLayout to set background
        //contentLayout = (LinearLayout)findViewById(R.id.content_layout);
        //contentLayout.setBackground(ApplicationContext.getInstance().ControlBitMap(BaseTabViewActivity.this, R.drawable.bg_commen));

        tabhost = (TabHost) findViewById(R.id.tabhost);
        tabManager = new TabManager(this, tabhost, android.R.id.tabcontent);
        tabhost.setCurrentTab(0);
        tabhost.setOnTabChangedListener(tabManager);
        tabhost.setup();
        //addTabSpec("Main Page", MainFragment.class, R.drawable.base_main, R.string.base_main);
        addTabSpec("Main Page", MainFragment.class, -1, R.string.base_main);
        //addTabSpec("Setting Page", SettingFragment.class, R.drawable.base_setting, R.string.base_setting);
        addTabSpec("Setting Page", SettingFragment.class, -1, R.string.base_setting);
    }

    private void addTabSpec(String str, Class<?> paramClass, int i, int j) {
        TabHost.TabSpec tabSpec = tabhost.newTabSpec(str);
        tabSpec.setContent(new Intent(this, paramClass));
        tabSpec.setIndicator(new TabItemView(this, i, j));
        tabManager.addTab(tabSpec, paramClass, null);
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
            ApplicationContext.getInstance().savePreferences();
            cancelNotificationService();
            exitPrograms();
        }
        return super.dispatchKeyEvent(event);
    }

    public void exitPrograms() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        // set activity flag
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // exit app
        Process.killProcess(Process.myPid());
    }

    //cancel the service notification
    private void cancelNotificationService() {
        final int notifyID = ApplicationContext.NOTIFY_SERVICE_ID;
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        notificationManager.cancel(notifyID);
    }
}
