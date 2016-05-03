package cc.nctu1210.childcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import cc.nctu1210.polling.MasterScheduledService;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.TabManager;
import cc.nctu1210.view.TabItemView;

public class MasterLoginTabViewActivity extends FragmentActivity {
    private static final String TAG = MasterLoginTabViewActivity.class.getSimpleName();
    private long exitTime = 0L;
    private TabHost tabhost;
    private TabManager tabManager;
    private LinearLayout contentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_login_tab_view);
        init();
        Intent intent_service_start= new Intent(MasterLoginTabViewActivity.this, MasterScheduledService.class);
        MasterLoginTabViewActivity.this.startService(intent_service_start);
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
        addTabSpec("Main Page", MasterLoginMainFragment.class, -1, R.string.base_main);
        //addTabSpec("Setting Page", SettingFragment.class, R.drawable.base_setting, R.string.base_setting);
        addTabSpec("Setting Page", MasterLoginSettingFragment.class, -1, R.string.base_setting);
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
           // ApplicationContext.getInstance().savePreferences();
           // ApplicationContext.getInstance().saveGatewayPreferences();
           // ApplicationContext.getInstance().saveParentPreferences();
           // ApplicationContext.getInstance().saveTeacherPreferences();
            ApplicationContext.cancelNotificationService(this);
            exitPrograms();
        }
        return super.dispatchKeyEvent(event);
    }

    public void exitPrograms() {
        Intent intent_service_stop= new Intent(MasterLoginTabViewActivity.this, MasterScheduledService.class);
        MasterLoginTabViewActivity.this.stopService(intent_service_stop);
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        // set activity flag
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // exit app
        Process.killProcess(Process.myPid());
    }
}
