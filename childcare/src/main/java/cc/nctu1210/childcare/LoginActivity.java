package cc.nctu1210.childcare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;

public class LoginActivity extends Activity implements View.OnClickListener{
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btLogin,btNewGarden;
    private Spinner login_type;
    private int type = 0;  // 0: master,  1: teacher , 2: parent , 3: gateway
    private EditText edtAccount,edtPassword;
    private final String [] LOGINTYPE= {"admin","teacher","parent","gateway"};

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyStoragePermissions(this);
            verifyCoaseLocationPermissions(this);
        }
        edtAccount = (EditText) findViewById(R.id.edt_account);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btLogin = (Button) findViewById(R.id.bt_logIn);
        btNewGarden = (Button) findViewById(R.id.bt_new_garden);
        btLogin.setOnClickListener(this);
        btNewGarden.setOnClickListener(this);
        login_type = (Spinner)findViewById(R.id.login_type);
        login_type.setOnItemSelectedListener(spnOnItemSelected);
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity current activity
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

    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            type = position;
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    };
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_logIn:
                ApplicationContext.mLoginType = type;
                ApplicationContext.mLoginFlag = LOGINTYPE[type];
                String account=edtAccount.getText().toString();
                String password=edtPassword.getText().toString();
                final Intent intent_login = new Intent();
                ApplicationContext.showProgressDialog(this);
                if(type == ApplicationContext.MASTER_TYPE) {
                    intent_login.setClass(LoginActivity.this, MasterLoginTabViewActivity.class);
                    ApplicationContext.login_admin(LOGINTYPE[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.gids = content.getGids();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.pids = content.getPids();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(type == ApplicationContext.TEACHER_TYPE)
                {
                    intent_login.setClass(LoginActivity.this, TeacherLoginActivity.class);
                    ApplicationContext.login_teacher(LOGINTYPE[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(type == ApplicationContext.PARENT_TYPE)
                {
                    intent_login.setClass(LoginActivity.this, ParentLoginActivity.class);
                    ApplicationContext.login_parent(LOGINTYPE[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.mPid = content.getmPid();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(type == ApplicationContext.GATEWAY_TYPE)
                {
                    intent_login.setClass(LoginActivity.this, GatewayLoginActivity.class);
                    ApplicationContext.login_gateway(LOGINTYPE[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.mGid = content.getmGid();
                                ApplicationContext.mPlace = content.getPlace();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                ApplicationContext.dismissProgressDialog();
                break;
            case R.id.bt_new_garden:
                Intent intent_new_garden = new Intent();
                intent_new_garden.setClass(LoginActivity.this, MasterAccountCreateActivity.class);
                startActivity(intent_new_garden);
                break;
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
}
