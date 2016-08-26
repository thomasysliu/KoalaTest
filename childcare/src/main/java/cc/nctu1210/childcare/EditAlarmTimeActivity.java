package cc.nctu1210.childcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Double2;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cc.nctu1210.tool.ApplicationContext;

public class EditAlarmTimeActivity extends Activity implements View.OnClickListener {

    private EditText editAlarmTime;
    private Button mButtonOk;
    private Button mButtonCancel;
    private String type = "time";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.edit_alarm_time);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void init() {
        editAlarmTime = (EditText) findViewById(R.id.edt_alam_time);
        mButtonOk = (Button) findViewById(R.id.button_edit_ok);
        mButtonCancel = (Button) findViewById(R.id.button_edit_cancel);
        mButtonOk.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final int time = bundle.getInt(ApplicationContext.ALARM_TIME, 0);
        double showAlarmtime = (double)time/60;
        editAlarmTime.setText(String.valueOf(showAlarmtime));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_edit_ok:

                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                final String t = editAlarmTime.getText().toString();
                double time = Double.parseDouble(t);
                time *= 60;
                int alarmtime = (int)Math.round(time);
                ApplicationContext.update_time(type,ApplicationContext.login_mid ,String.valueOf(alarmtime));
                bundle.putInt(ApplicationContext.ALARM_TIME, Integer.valueOf(alarmtime));
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.button_edit_cancel:
                finish();
                break;
        }
    }
}
