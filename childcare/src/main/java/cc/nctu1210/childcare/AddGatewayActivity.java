package cc.nctu1210.childcare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;


public class AddGatewayActivity extends Activity implements View.OnClickListener{
    private TextView gatewayTitle;
    private EditText editAccount;
    private EditText editPassword;
    private EditText editConfirm;
    private EditText editPlace;
    private EditText editNear;
    private EditText editFar;

    private Button mButtonOk;
    private Button mButtonCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.add_gateway);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void init() {
        gatewayTitle = (TextView) findViewById(R.id.gateway_title);
        editAccount = (EditText) findViewById(R.id.edt_account);
        editPassword = (EditText) findViewById(R.id.edt_password);
        editConfirm = (EditText) findViewById(R.id.edt_confirm);
        editPlace = (EditText) findViewById(R.id.edt_place);
        editNear = (EditText) findViewById(R.id.edt_near);
        editFar = (EditText) findViewById(R.id.edt_far);
        mButtonOk = (Button) findViewById(R.id.button_new_ok);
        mButtonCancel = (Button) findViewById(R.id.button_new_cancel);
        mButtonOk.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int gatewayNum = bundle.getInt(ApplicationContext.GATEWAY_NUMBER, 0);
        String title = getString(R.string.gateway) + String.valueOf(gatewayNum+1);
        gatewayTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_new_ok:
                final Intent intent = getIntent();
                final Bundle bundle = new Bundle();
                final String account = editAccount.getText().toString();
                final String password = editPassword.getText().toString();
                final String confirm = editConfirm.getText().toString();
                final String place = editPlace.getText().toString();
                final String near = editNear.getText().toString();
                final String far = editFar.getText().toString();
                if(password.equals(confirm))
                {
                    int mid;
                    if(ApplicationContext.mIsLogin)
                        mid = ApplicationContext.login_mid;
                    else
                        mid = ApplicationContext.signup_mid;
                    if(ApplicationContext.checkInternetConnection(this)) {
                        ApplicationContext.showProgressDialog(this);
                        ApplicationContext.signUp_gateway("gateway", account, password, place, mid, near, far, new CallBack() {
                            @Override
                            public void done(CallBackContent content) {
                                if (content != null) {
                                    ApplicationContext.dismissProgressDialog();
                                    bundle.putString(ApplicationContext.GATEWAY_ACCOUNT, account);
                                    bundle.putString(ApplicationContext.GATEWAY_PASSWORD, password);
                                    bundle.putString(ApplicationContext.GATEWAY_CONFIRM, confirm);
                                    bundle.putString(ApplicationContext.GATEWAY_PLACE, place);
                                    bundle.putString(ApplicationContext.GATEWAY_NEAR, near);
                                    bundle.putString(ApplicationContext.GATEWAY_FAR, far);
                                    bundle.putString(ApplicationContext.GATEWAY_ID, content.getmGid());
                                    intent.putExtras(bundle);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    ApplicationContext.dismissProgressDialog();
                                    Toast.makeText(AddGatewayActivity.this, "Sign Up Gateway fail!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                        Toast.makeText(AddGatewayActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(AddGatewayActivity.this, getString(R.string.error_password_confirm_not_same), Toast.LENGTH_LONG).show();
                break;
            case R.id.button_new_cancel:
                finish();
                break;
        }
    }
}
