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

public class TeacherCreateActivity extends Activity  implements View.OnClickListener /*,TextWatcher */{
    TextView txvError,txvEmailError;
    ImageView imgError,imgEmailError;
    EditText  edtAccount,edtPassword,edtConfirm;
    Button btNext;
    private String type = "teacher";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_create);

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
        btNext = (Button) findViewById(R.id.bt_next);
        btNext.setEnabled(true);

        //set Listeners
        /*
        edtAccount.addTextChangedListener(this);
        edtPassword.addTextChangedListener(this);
        edtConfirm.addTextChangedListener(this);*/
        btNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String account=edtAccount.getText().toString();
        String password=edtPassword.getText().toString();
        String confirm=edtConfirm.getText().toString();


        if(password.equals("") || account.equals(""))
        {
            Toast.makeText(TeacherCreateActivity.this, getString(R.string.error_account_password_empty), Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(confirm))
        {
            Toast.makeText(TeacherCreateActivity.this, getString(R.string.error_password_confirm_not_same), Toast.LENGTH_LONG).show();
        }
        else {
            ApplicationContext.signUp_teacher(type, account, password, ApplicationContext.signup_mid, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        Toast.makeText(TeacherCreateActivity.this, "Sign Up Teacher success!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(TeacherCreateActivity.this, GatewayCreateActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showErrorMessage();
                    }
                }
            });
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
