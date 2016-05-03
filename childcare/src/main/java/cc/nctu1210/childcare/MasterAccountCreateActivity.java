package cc.nctu1210.childcare;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;

public class MasterAccountCreateActivity extends Activity  implements View.OnClickListener  /*,TextWatcher */{
    TextView txvError,txvEmailError;
    ImageView imgError,imgEmailError;
    EditText  edtAccount,edtPassword,edtConfirm,edtAlarmTime;
    Button btCancel, btNext;
    private String type = "admin";
    private int alarmTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_account_create);

        //initialize
        txvError = (TextView) findViewById(R.id.txv_error);
        imgError = (ImageView) findViewById(R.id.img_error);
        txvEmailError = (TextView) findViewById(R.id.txv_email_error);
        imgEmailError = (ImageView) findViewById(R.id.img_email_error);
        txvError.setVisibility(View.INVISIBLE);
        imgError.setVisibility(View.INVISIBLE);
        txvEmailError.setVisibility(View.INVISIBLE);
        imgEmailError.setVisibility(View.INVISIBLE);
        edtAccount = (EditText) findViewById(R.id.edt_account);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConfirm = (EditText)findViewById(R.id.edt_confirm);
        edtAlarmTime = (EditText)findViewById(R.id.edt_alarm_time);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        btNext = (Button) findViewById(R.id.bt_next);
        btNext.setEnabled(true);

        alarmTime = ApplicationContext.alarmTime/60; //in minutes
/*
        //set Listeners
        edtAccount.addTextChangedListener(this);
        edtPassword.addTextChangedListener(this);
        edtConfirm.addTextChangedListener(this);*/
        btCancel.setOnClickListener(this);
        btNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_cancel:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.cancel).setMessage(R.string.cancel_signup);
                dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }
                );
                dialog.setNegativeButton(R.string.no, null);
                dialog.show();
                break;
            case R.id.bt_next:
                String account=edtAccount.getText().toString();
                String password=edtPassword.getText().toString();
                String confirm=edtConfirm.getText().toString();

                String time=edtAlarmTime.getText().toString();
                final int alarmTimeInSecond;
                if(time.equals(""))
                    time = getString(R.string.five);
                alarmTime = Integer.valueOf(time);
                alarmTimeInSecond = alarmTime * 60;
                ApplicationContext.alarmTime = alarmTimeInSecond; //in seconds

                if(password.equals("") || account.equals(""))
                {
                    Toast.makeText(MasterAccountCreateActivity.this, getString(R.string.error_account_password_empty), Toast.LENGTH_LONG).show();
                }
                else if(!password.equals(confirm))
                {
                    Toast.makeText(MasterAccountCreateActivity.this, getString(R.string.error_password_confirm_not_same), Toast.LENGTH_LONG).show();
                }
                else {
                    ApplicationContext.signUp_master(type, account, password, String.valueOf(alarmTimeInSecond), new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.alarmTime = alarmTimeInSecond;
                                ApplicationContext.signup_mid = content.getMid();
                                Log.e("TAG", "test: " + ApplicationContext.signup_mid + "\n");
                                Toast.makeText(MasterAccountCreateActivity.this, "Sign Up Master success!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                intent.setClass(MasterAccountCreateActivity.this, TeacherCreateActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showErrorMessage();
                            }
                        }
                    });
                }
                break;
        }

    }

    public void showErrorMessage()
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("The account has been used!");
        dialog.setPositiveButton("Ok",null);
        dialog.show();
    }
    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
    }
}
