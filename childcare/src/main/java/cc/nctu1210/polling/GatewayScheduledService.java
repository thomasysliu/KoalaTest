package cc.nctu1210.polling;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.nctu1210.childcare.GatewayLoginActivity;
import cc.nctu1210.childcare.MasterLoginMainFragment;
import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapter;
import cc.nctu1210.view.ChildrenListAdapterForGateway;

/**
 * Created by Yi-Ta_Chuang on 2016/5/5.
 */
public class GatewayScheduledService extends Service {
    private static final String TAG = GatewayScheduledService.class.getSimpleName();
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private int count;
    private List<ChildItem> mChildItems;
    private List<ChildProfile> mListChildren;
    private ChildrenListAdapterForGateway mChildListAdapter;
    private String [] cids;
    private int i;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "mIsServiceOn:" + ApplicationContext.mIsServiceOn);
        mChildItems = GatewayLoginActivity.mChildItems;
        mChildListAdapter = GatewayLoginActivity.mChildListAdapter;
        mListChildren = ApplicationContext.mListChildren;
        cids = ApplicationContext.cids.split(",");
        count = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count > 1) {
                    handler.post(new Runnable() {
                        public void run() {
                            final int num_of_children = cids.length;
                            //mListChildren.clear();
                            if (num_of_children != 0) {
                                //ApplicationContext.mSpinChildName.clear();
                                for (i = 0; i < num_of_children; i++) {
                                    ApplicationContext.gateway_show_child_by_id(cids[i], new CallBack() {
                                        @Override
                                        public void done(CallBackContent content) {
                                            if (content != null) {
                                                ChildProfile mChild = content.getChild();
                                                ApplicationContext.updateChildProfile(mChild);
                                                ApplicationContext.addANewChild(mChild);
                                            } else {
                                                Log.e("TAG", "show_child_by_id fail" + "\n");
                                            }
                                        }
                                    });
                                }
                                populateList();
                            }
                        }
                    });
                }
            }
        }, 0, 1 * 10 * 1000);//10 sec
    }


    private void populateList() {
        mChildListAdapter.getData().clear();
        Log.i("TAG", "Initializing ListView....." + mChildListAdapter.getData().size());
        for (int i = 0, size = mListChildren.size(); i < size; i++) {
            ChildItem object = new ChildItem(mListChildren.get(i).getName(),mListChildren.get(i).getStatus());
            object.photoName = mListChildren.get(i).getPhotoName();
            object.place = mListChildren.get(i).getPlace();
            object.flag = mListChildren.get(i).getFlag();
            object.cid = mListChildren.get(i).getCid();
            object.rssi = mListChildren.get(i).getRssi();
            mChildItems.add(object);
        }
        Log.i("TAG", "Initialized ListView....." + mChildListAdapter.getData().size());
        mChildListAdapter.notifyDataSetChanged();

    }
}
