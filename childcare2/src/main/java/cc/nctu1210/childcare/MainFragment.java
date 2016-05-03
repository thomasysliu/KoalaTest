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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.BLESanner;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapter;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private final String TAG = MainFragment.class.getSimpleName();
    private BluetoothAdapter mBtAdapter = null;
    private BLESanner mBLEScanner = null;
    private TextView mTextTeacherName;
    private TextView mTextStatus;
    private ImageView mImageMonitor;
    private ChildrenListAdapter mChildListAdapter;
    private List<ChildItem> mChildItems = new ArrayList<ChildItem>();
    private ListView mListViewChildren;
    private List<ChildProfile> mListChildren;
    private HashMap<String,ChildProfile> mMapChildren;

    private static final int REQUEST_ENABLE_BT = 1;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BLESanner.SCAN_A_DEVICE:
                    final int position;
                    final int rssi = msg.getData().getInt(BLESanner.EXTRA_RSSI);
                    final BluetoothDevice device = msg.getData().getParcelable(BLESanner.EXTRA_DEVICE);
                    String range="miss";
                    final double distance = BLESanner.getDistancebyRSSI(rssi);
                    /*
                    if (rssi>-60) {
                        range = "near";
                    } else if (rssi<=-60 && rssi>=-90) {
                        range = "mediate";
                    } else if (rssi <- 90) {
                        range = "far";
                    }*/
                    if (distance < 2.0) {
                        range = "near";
                    } else if (distance >= 2.0 && distance <= 5.0) {
                        range = "mediate";
                    } else if (distance > 5.0) {
                        range = "far";
                    }
                    if ((position = ApplicationContext.getInstance().findChild(device.getAddress())) != -1) {
                        ChildProfile child = mListChildren.get(position);
                        ChildItem item = mChildItems.get(position);
                        child.setStatus(range);
                        item.status = range;
                        Log.i(TAG, child.getName()+" distance: "+distance);
                        mChildListAdapter.notifyDataSetChanged();
                        if (range.equals("far")) {
                            notificationBuilder(position);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "getPackageManager");
        if (!getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.BLE_not_support), Toast.LENGTH_SHORT)
                    .show();
            getActivity().finish();
        }

        final BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getActivity(),  getActivity().getResources().getText(R.string.BLE_not_support), Toast.LENGTH_SHORT)
                    .show();
            getActivity().finish();
            return;
        }
        mListChildren = ApplicationContext.mListChildren;
        mMapChildren = ApplicationContext.mMapChildren;
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mBLEScanner = new BLESanner(mBtAdapter);
            mBLEScanner.setMonitorListHandler(mHandler);
            ApplicationContext.mBluetoothAdapter = mBtAdapter;
            ApplicationContext.mBLEScanner = mBLEScanner;
            if (ApplicationContext.mIsScan) {
                mTextStatus.setText(getString(R.string.toggle_on));
                startMonitoring();
                notificationServiceStartBuilder();
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mBLEScanner != null) {
            ApplicationContext.mIsScan = false;
            stopMonitoring();
            cancelNotificationService();
        }
    }

    private void initView() {
        mChildListAdapter = new ChildrenListAdapter(getActivity(), mChildItems);
        mTextTeacherName = (TextView) this.getView().findViewById(R.id.text_main_user);
        mTextStatus = (TextView) this.getView().findViewById(R.id.text_main_status);
        mImageMonitor = (ImageView) this.getView().findViewById(R.id.image_main_user);
        mImageMonitor.setOnClickListener(this);
        //mTextTeacherName.setText(ApplicationContext.mTeacherName);
        mListViewChildren = (ListView) this.getView().findViewById(R.id.list_main_child);
        mListViewChildren.setAdapter(mChildListAdapter);
        populateList();
    }

    @Override
    public void onClick(View v) {
        if (ApplicationContext.mIsScan) {
            ApplicationContext.mIsScan = false;
            stopMonitoring();
            mTextStatus.setText(getString(R.string.toggle_off));
            cancelNotificationService();
        } else {
            ApplicationContext.mIsScan = true;
            startMonitoring();
            mTextStatus.setText(getString(R.string.toggle_on));
            notificationServiceStartBuilder();
        }
    }


    private void startMonitoring() {
        mBLEScanner.setMode(BLESanner.MODE_SCAN_MONITOR);
        mBLEScanner.scanLeDevice(true);
    }

    private void stopMonitoring() {
        mBLEScanner.setMode(BLESanner.MODE_NO_SCAN);
        mBLEScanner.scanLeDevice(false);
    }

    private void populateList() {
        mChildListAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mChildListAdapter.getData().size());
        for (int i = 0, size = mListChildren.size(); i < size; i++) {
            ChildItem object = new ChildItem(mListChildren.get(i).getName(),mListChildren.get(i).getStatus());
            object.photoName = mListChildren.get(i).getPhotoName();
            mChildItems.add(object);
        }
        Log.i(TAG, "Initialized ListView....." + mChildListAdapter.getData().size());
        mChildListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                getActivity().finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void notificationBuilder(int position) {
        final int notifyID = position; // 通知的識別號碼
        final boolean autoCancel = true; // 點擊通知後是否要自動移除掉通知
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效

        final ChildProfile child = mListChildren.get(position);
        final String childName = child.getName();

        final int requestCode = ApplicationContext.REQUEST_WARNING_NOTIFICATION; // PendingIntent的Request Code
        final Intent intent = getActivity().getIntent(); // 目前Activity的Intent
        final int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getActivity().getApplicationContext()).setSmallIcon(R.drawable.base_main).setContentTitle(getString(R.string.notification_title)).setContentText(childName+" "+getString(R.string.is_miss)).setSound(soundUri).setContentIntent(pendingIntent).setAutoCancel(autoCancel).build(); // 建立通知
        notificationManager.notify(notifyID, notification); // 發送通知
    }

    private void notificationServiceStartBuilder() {
        final int notifyID = ApplicationContext.NOTIFY_SERVICE_ID; // 通知的識別號碼
        final boolean autoCancel = false; // 點擊通知後是否要自動移除掉通知
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
        final int requestCode = ApplicationContext.REQUEST_NOTIFICATION_SERVICE; // PendingIntent的Request Code
        final Intent intent = getActivity().getIntent(); // 目前Activity的Intent
        intent.setClass(getActivity(), BaseTabViewActivity.class);
        final int flags = PendingIntent.FLAG_UPDATE_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getActivity().getApplicationContext()).setSmallIcon(R.drawable.base_main).setContentTitle(getString(R.string.notification_title)).setContentText(getString(R.string.toggle_on)).setSound(soundUri).setContentIntent(pendingIntent).setAutoCancel(autoCancel).build(); // 建立通知
        notification.flags = Notification.FLAG_ONGOING_EVENT; //將訊息常駐在status bar
        notificationManager.notify(notifyID, notification); // 發送通知
    }

    private void cancelNotificationService() {
        final int notifyID = ApplicationContext.NOTIFY_SERVICE_ID;
        final NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        notificationManager.cancel(notifyID);
    }

}
