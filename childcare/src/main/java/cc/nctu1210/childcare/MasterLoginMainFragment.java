package cc.nctu1210.childcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.polling.MasterScheduledService;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;
import cc.nctu1210.view.ChildItem;
import cc.nctu1210.view.ChildrenListAdapter;

public class MasterLoginMainFragment extends Fragment implements View.OnClickListener {
    private final String TAG = MasterLoginMainFragment.class.getSimpleName();
    public static ChildrenListAdapter mChildListAdapter;
    public static List<ChildItem> mChildItems = new ArrayList<ChildItem>();
    private ListView mListViewChildren;
    private List<ChildProfile> mListChildren;
    private HashMap<String,ChildProfile> mMapChildren;
    private String [] cids;
    private int i;

    private TextView mTextViewStatus;
    private ImageView mImageViewKoala;
    private Intent mPollingIntent;

    //use timer task to periodically perform polling reuqests of children's statuses

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.master_login_main_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListChildren = ApplicationContext.mListChildren;
        mMapChildren = ApplicationContext.mMapChildren;
        cids = ApplicationContext.cids.split(",");
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "mIsServiceOn:"+ApplicationContext.mIsServiceOn);
        if (ApplicationContext.mIsServiceOn) {
            ApplicationContext.notificationServiceStartBuilder(getActivity(), ApplicationContext.MASTER_TYPE);
            mPollingIntent = new Intent(getActivity(), MasterScheduledService.class);
            getActivity().startService(mPollingIntent);
        } else {
            ApplicationContext.cancelNotificationService(getActivity());
            mPollingIntent = new Intent(getActivity(), MasterScheduledService.class);
            getActivity().stopService(mPollingIntent);
        }
        if (ApplicationContext.mIsLogin & ApplicationContext.mIsServiceOn) {
            ApplicationContext.showProgressDialog(getActivity());
            ApplicationContext.login_admin(ApplicationContext.mLoginFlag, ApplicationContext.mAccount, ApplicationContext.mPassword, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        ApplicationContext.login_mid = content.getMid();
                        ApplicationContext.gids = content.getGids();
                        ApplicationContext.cids = content.getCids();
                        ApplicationContext.pids = content.getPids();
                        ApplicationContext.mIsLogin = true;
                        ApplicationContext.clearChildrenList();
                        cids = ApplicationContext.cids.split(",");
                        initView();
                    } else {
                        Toast.makeText(getActivity(), "Login fail !", Toast.LENGTH_LONG).show();
                    }
                }
            });
            ApplicationContext.dismissProgressDialog();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ApplicationContext.mIsServiceOn = false;
        ApplicationContext.cancelNotificationService(getActivity());
        mPollingIntent = new Intent(getActivity(), MasterScheduledService.class);
        getActivity().stopService(mPollingIntent);
    }

    private void initView() {
        mChildListAdapter = new ChildrenListAdapter(getActivity(), mChildItems,1);
        mTextViewStatus = (TextView) this.getView().findViewById(R.id.text_master_status);
        if(ApplicationContext.mIsServiceOn) {
            mTextViewStatus.setText(getString(R.string.toggle_on));
        } else {
            mTextViewStatus.setText(getString(R.string.toggle_off));
        }
        mImageViewKoala = (ImageView) this.getView().findViewById(R.id.image_main_user);
        mImageViewKoala.setOnClickListener(this);

        mListViewChildren = (ListView) this.getView().findViewById(R.id.list_main_child);
        mListViewChildren.setAdapter(mChildListAdapter);
        final int num_of_children = cids.length;
        Log.i(TAG, "num of children:" + num_of_children + " mListChildren:" + mListChildren.size());
        ApplicationContext.showProgressDialog(getActivity());
        if (mListChildren.size() != num_of_children) {
            //ApplicationContext.mSpinChildName.clear();
            Log.i(TAG, "inside...");
            for (i=0; i<num_of_children; i++) {
                ApplicationContext.show_child_by_id(cids[i], new CallBack() {
                    @Override
                    public void done(CallBackContent content) {
                        if (content != null) {
                            ChildProfile mChild = content.getChild();
                            ApplicationContext.addANewChild(mChild);
                            if (mListChildren.size() == num_of_children)
                                populateList();
                        } else {
                            Log.e(TAG, "show_child_by_id fail" + "\n");
                        }
                    }
                });
            }
        }
        ApplicationContext.dismissProgressDialog();
    }


    public void populateList() {
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
    public void onClick(View v) {
        if (ApplicationContext.mIsServiceOn) {
            ApplicationContext.mIsServiceOn = false;
            mTextViewStatus.setText(getString(R.string.toggle_off));
            ApplicationContext.cancelNotificationService(getActivity());
            mPollingIntent = new Intent(getActivity(), MasterScheduledService.class);
            getActivity().stopService(mPollingIntent);
        } else {
            ApplicationContext.mIsServiceOn = true;
            mTextViewStatus.setText(getString(R.string.toggle_on));
            ApplicationContext.notificationServiceStartBuilder(getActivity(), ApplicationContext.MASTER_TYPE);
            mPollingIntent = new Intent(getActivity(), MasterScheduledService.class);
            getActivity().startService(mPollingIntent);
        }
    }
}
