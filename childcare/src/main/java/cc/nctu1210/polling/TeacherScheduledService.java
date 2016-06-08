package cc.nctu1210.polling;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.nctu1210.childcare.GatewayLoginActivity;
import cc.nctu1210.childcare.R;
import cc.nctu1210.childcare.TeacherCreateActivity;
import cc.nctu1210.childcare.TeacherLoginActivity;
import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapter;

/**
 * Created by User on 2016/5/2.
 */
public class TeacherScheduledService  extends Service{
    private final String TAG = TeacherScheduledService.class.getSimpleName();
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private int count;
    private List<ChildItem> mChildItems = new ArrayList<ChildItem>();
    private List<ChildProfile> mListChildren = new ArrayList<ChildProfile>();
    private ChildrenListAdapter mChildListAdapter;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        mChildItems = TeacherLoginActivity.mChildItems;
        mChildListAdapter = TeacherLoginActivity.mChildListAdapter;
        super.onCreate();
        count = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count > 1) {
                    handler.post(new Runnable() {
                        public void run() {
                            ApplicationContext.show_all_children(ApplicationContext.login_mid, false, -1, new CallBack() {
                                @Override
                                public void done(CallBackContent content) {
                                    if (content != null) {
                                        mListChildren.clear();
                                        int j=0;
                                        for(int i=0; i<content.getShow_children().size(); i++) {
                                            mListChildren.add(content.getShow_children().get(i));
                                            if(mListChildren.size() == content.getShow_children().size())
                                                populateList();
                                        }
                                        /*
                                        mListChildren = content.getShow_children();
                                        */
                                        //populateList();
                                    } else {
                                        Log.e(TAG, "show_child_by_id fail" + "\n");
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }, 0, 1 * 1 * 1000);//60 sec
    }


    private void populateList() {
        mChildListAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....." + mChildListAdapter.getData().size());
        Collections.sort(mListChildren);
        for (int i = 0, size = mListChildren.size(); i < size; i++) {
            ChildItem object = new ChildItem(mListChildren.get(i).getName(),mListChildren.get(i).getStatus());
            object.photoName = mListChildren.get(i).getPhotoName();
            object.place = mListChildren.get(i).getPlace();
            object.flag = mListChildren.get(i).getFlag();
            object.cid = mListChildren.get(i).getCid();
            object.rssi = mListChildren.get(i).getRssi();
            mChildItems.add(object);
           /* Log.e("TAG", mListChildren.get(i).getStatus());
            if(!mListChildren.get(i).getFlag().equals("1"))
                Log.e("TAG", object.getPlace());*/
            if(mListChildren.get(i).getFlag().equals("1"))
                notificationBuilder(i,0);
            else if(mListChildren.get(i).getStatus().equals("far"))
                notificationBuilder(i,1);
        }
        Log.i(TAG, "Initialized ListView....." + mChildListAdapter.getData().size());
        mChildListAdapter.notifyDataSetChanged();

    }

    private void notificationBuilder(int position,int type) {
        final int notifyID = position; // 通知的識別號碼
        final boolean autoCancel = true; // 點擊通知後是否要自動移除掉通知
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效

        final ChildProfile child = mListChildren.get(position);
        final String childName = child.getName();

        final int requestCode = notifyID; // PendingIntent的Request Code
        final Intent intent = TeacherLoginActivity.mActivity.getIntent(); // 目前Activity的Intent
        final int flags = PendingIntent.FLAG_UPDATE_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(TeacherLoginActivity.mActivity.getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) TeacherLoginActivity.mActivity.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務

        if(type == 0) {
            final Notification notification = new Notification.Builder(TeacherLoginActivity.mActivity.getApplicationContext()).setSmallIcon(R.drawable.base_main).setContentTitle(getString(R.string.notification_title)).setContentText(childName + " " + getString(R.string.is_miss)).setSound(soundUri).setContentIntent(pendingIntent).setAutoCancel(autoCancel).build(); // 建立通知
            notificationManager.notify(notifyID, notification);
        }
        else
        {
            final Notification notification = new Notification.Builder(TeacherLoginActivity.mActivity.getApplicationContext()).setSmallIcon(R.drawable.base_main).setContentTitle(getString(R.string.notification_title)).setContentText(childName + " away " + mListChildren.get(position).getPlace()).setSound(soundUri).setContentIntent(pendingIntent).setAutoCancel(autoCancel).build(); // 建立通知
            notificationManager.notify(notifyID, notification);
        }

    }

    @Override
    public void onDestroy() {
        timer.cancel();
        stopSelf();
        super.onDestroy();
    }
}

