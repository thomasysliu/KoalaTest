package cc.nctu1210.childcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import cc.nctu1210.api.koala3x.KoalaService;
import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.api.koala3x.SensorEvent;
import cc.nctu1210.api.koala3x.SensorEventListener;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class AddNewDeviceActivity extends Activity implements View.OnClickListener, SensorEventListener {
    private EditText mEditTextName;
    private ImageView mImageViewPhoto;
    private TextView mTextViewDeviceAddress;
    private Button mButtonOk;
    private Button mButtonCancel;
    private KoalaServiceManager mKoalaServiceManager;
    private boolean mBookeanKoalaServiceCreated = false;
    private String deviceAddress;
    private String deviceRange;
    private int viewPosition;
    private File tmpFile = ApplicationContext.createImageFile(ApplicationContext.CHILD_PHOTO_FILE_PATH, "tmp.jpg");
    Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.add_new_child);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeKoalaServiceManager();
    }

    void init() {
        mEditTextName = (EditText) findViewById(R.id.editText_new_name);
        mImageViewPhoto = (ImageView) findViewById(R.id.image_new_user);
        mTextViewDeviceAddress = (TextView) findViewById(R.id.text_new_mac);
        mButtonOk = (Button) findViewById(R.id.button_new_ok);
        mButtonCancel = (Button) findViewById(R.id.button_new_cancel);
        mButtonOk.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        mKoalaServiceManager = new KoalaServiceManager(this);
        mKoalaServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_PEDOMETER);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        deviceAddress = bundle.getString(ApplicationContext.DEVICE_ADDRESS);
        deviceRange = bundle.getString(ApplicationContext.DEVICE_RANGE);
        viewPosition = bundle.getInt(ApplicationContext.LIST_VIEW_POSITION);
        //mEditTextName.setInputType(InputType.TYPE_NULL);
        mEditTextName.setInputType(InputType.TYPE_CLASS_TEXT);
        mTextViewDeviceAddress.setText(deviceAddress);
        mImageViewPhoto.setBackground(ApplicationContext.controlBitMap(this, R.drawable.default_user));
        mImageViewPhoto.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ApplicationContext.REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(Uri.fromFile(tmpFile));
                }
                break;
            case ApplicationContext.REQUEST_CODE_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    }
                }
                break;
            case ApplicationContext.REQUEST_CODE_CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        setHeadPicture(data);
                    }
                }
                break;
        }

    }

    private void startPhotoZoom(Uri uri) {
        /*
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, ApplicationContext.REQUEST_CODE_SCALE_PHOTO);
        */
        Uri dest = Uri.fromFile(new File(getCacheDir(),"cropped"));
        Crop.of(uri, dest).asSquare().start(this);
    }

    private void setHeadPicture(Intent picData) {
        try {
            /*
            Bundle extras = picData.getExtras();
            if (extras != null) {
                photo = extras.getParcelable("data");
                mImageViewPhoto.setImageBitmap(photo);
            }
            */
            Uri photoUri = Crop.getOutput(picData);
            Bitmap tmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            int rotation = picData.getIntExtra(ApplicationContext.EXTRA_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap tmp2 = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight(), matrix, true);
            photo = ApplicationContext.scaleBitmap(tmp2, 100,100);
            mImageViewPhoto.setImageBitmap(photo);
        } catch (Exception e) {

        }
    }

    void connectToDevice(String addr) {
        mKoalaServiceManager.connect(addr);
    }

    void disConnectTheDevices() {
        mKoalaServiceManager.disconnect();
    }

    void closeKoalaServiceManager() {
        mKoalaServiceManager.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.image_new_user:

                String[] items = { getString(R.string.take_photo), getString(R.string.choose_photo) };
                new AlertDialog.Builder(this).setTitle(getString(R.string.photo))
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT,
                                                Uri.fromFile(tmpFile));
                                        startActivityForResult(intentTakePhoto, ApplicationContext.REQUEST_CODE_TAKE_PHOTO);
                                        break;
                                    case 1:
                                        Intent intentChoosePhoto = new Intent("android.intent.action.PICK", null);
                                        intentChoosePhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(intentChoosePhoto, ApplicationContext.REQUEST_CODE_CHOOSE_PHOTO);
                                    default:
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.button_new_ok:
                if(ApplicationContext.checkInternetConnection(this)) {
                    final Intent intent = getIntent();
                    final Bundle bundle = new Bundle();
                    final String name = mEditTextName.getText().toString();
                    final String photoName = name + ".jpg";
                    final String addr = deviceAddress;
                    final String range = deviceRange;
                    final int position = viewPosition;
                    if (photo != null) {

                        /*
                        final File photoFile = ApplicationContext.createImageFile(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);
                        ApplicationContext.saveBitmap(photoFile, photo);
                        ApplicationContext.imageFileDelete(tmpFile);
                        */
                        ApplicationContext.addBitmapToMemoryCache(photoName, photo);
                        int mid;
                        if (ApplicationContext.mIsLogin)
                            mid = ApplicationContext.login_mid;
                        else
                            mid = ApplicationContext.signup_mid;
                        ApplicationContext.showProgressDialog(this);
                        ApplicationContext.new_child(mid, addr, name, photo, new CallBack() {
                            @Override
                            public void done(CallBackContent content) {
                                if (content != null) {
                                    ApplicationContext.dismissProgressDialog();
                                    ApplicationContext.cids = content.getCids();
                                    String cid = content.getChild().getCid();
                                    bundle.putString(ApplicationContext.CHILD_NAME, name);
                                    bundle.putString(ApplicationContext.DEVICE_ADDRESS, addr);
                                    bundle.putString(ApplicationContext.DEVICE_RANGE, range);
                                    bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
                                    bundle.putString(ApplicationContext.PHOTO_NAME, photoName);
                                    bundle.putString(ApplicationContext.CHILD_ID, cid);
                                    intent.putExtras(bundle);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(AddNewDeviceActivity.this, "add child fail!", Toast.LENGTH_LONG).show();
                                    ApplicationContext.dismissProgressDialog();
                                }
                            }
                        });
                        ApplicationContext.imageFileDelete(tmpFile);
                    } else {
                        photo = ApplicationContext.getBitMapById(this, R.drawable.default_user);
                        int mid;
                        if (ApplicationContext.mIsLogin)
                            mid = ApplicationContext.login_mid;
                        else
                            mid = ApplicationContext.signup_mid;
                        ApplicationContext.showProgressDialog(this);
                        ApplicationContext.new_child(mid, addr, name, photo, new CallBack() {
                            @Override
                            public void done(CallBackContent content) {
                                if (content != null) {
                                    ApplicationContext.dismissProgressDialog();
                                    ApplicationContext.cids = content.getCids();
                                    String cid = content.getChild().getCid();
                                    bundle.putString(ApplicationContext.CHILD_NAME, name);
                                    bundle.putString(ApplicationContext.DEVICE_ADDRESS, addr);
                                    bundle.putString(ApplicationContext.DEVICE_RANGE, range);
                                    bundle.putInt(ApplicationContext.LIST_VIEW_POSITION, position);
                                    bundle.putString(ApplicationContext.PHOTO_NAME, photoName);
                                    bundle.putString(ApplicationContext.CHILD_ID, cid);
                                    intent.putExtras(bundle);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(AddNewDeviceActivity.this, "add child fail!", Toast.LENGTH_LONG).show();
                                    ApplicationContext.dismissProgressDialog();
                                }
                            }
                        });
                    }
                }
                else
                    Toast.makeText(AddNewDeviceActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                break;
            case R.id.button_new_cancel:
                finish();
                break;
        }
    }

    @Override
    public void onSensorChange(SensorEvent e) {

    }

    @Override
    public void onPedometerServiceChange(int i) {
    }

    @Override
    public void onConnectionStatusChange(boolean status) {

    }

    @Override
    public void onRSSIChange(String addr, float rssi) {
        if (mEditTextName.getInputType()==InputType.TYPE_NULL) {
            //mEditTextName.setInputType(InputType.TYPE_CLASS_TEXT);
            //disConnectTheDevices();
        }
    }

    @Override
    public void onKoalaServiceStatusChanged(boolean status) {
        mBookeanKoalaServiceCreated = status;
        if (mBookeanKoalaServiceCreated) {
            //connectToDevice(deviceAddress);
        }
    }
}
