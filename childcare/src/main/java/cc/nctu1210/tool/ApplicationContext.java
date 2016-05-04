package cc.nctu1210.tool;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.childcare.BaseTabViewActivity;
import cc.nctu1210.childcare.R;
import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.view.NewGatewayItem;
import cc.nctu1210.view.NewParentItem;
import cc.nctu1210.view.ParentCreateChildItem;

/**
 * Created by Yi-Ta_Chuang on 2016/4/17.
 */
public class ApplicationContext extends Application {
    private static final String TAG = ApplicationContext.class.getSimpleName();
    public static HashMap<String, ChildProfile> mMapChildren;
    public static HashMap<String, ChildProfile> mMapChildrenCid;
    public static List<ChildProfile> mListChildren;
    public static List<NewGatewayItem> mGateways;
    public static List<NewParentItem> mParents;
    public static int alarmTime;
    public static List<BluetoothDevice> mDeviceList;
    public static KoalaServiceManager mKoalaManager;
    public static boolean mBooleanKoalaServiceCreated;
    public static String mTeacherName;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BLESanner mBLEScanner;
    public static boolean mIsScan;
    public static boolean mIsServiceOn; //for polling service

    public static int signup_mid;
    public static int login_mid;
    public static String gids;
    public static String cids;
    public static String pids;
    public static String mGid;
    public static String mPid;
    public static boolean mIsLogin;
    public static List<String> mSpinChildName;
    public static String mAccount;
    public static String mPassword;
    public static int mLoginType;  // 0: master,  1: teacher , 2: parent , 3: gateway


    private static ApplicationContext mInstance;
    //for image cache
    public static LruCache<String, Bitmap> mMemoryCache;

    public static final String APPLICATION_PREFERENCES = "APPLICATION_PREFERENCES";
    public static final String TEACHER_NAME_PREFERENCE = "TEACHER_NAME_PREFERENCE";
    public static final String NUM_OF_CHILDREN = "NUM_OF_CHILDREN";
    public static final String CHILD_NAME_PREFERENCE = "CHILD_NAME_PREFERENCE";
    public static final String CHILD_DEVICE_PREFERENCE = "CHILD_DEVICE_PREFERENCE";
    public static final String CHILD_PHOTO_PREFERENCE = "CHILD_PHOTO_PREFERENCE";

    public static final String ALARM_TIME_PREFERENCE = "ALARM_TIME_PREFERENCE";

    public static final String NUM_OF_GATEWAY = "NUM_OF_GATEWAY";
    public static final String GATEWAY_ACCOUNT_PREFERENCE = "GATEWAY_ACCOUNT_PREFERENCE";
    public static final String GATEWAY_PASSWORD_PREFERENCE = "GATEWAY_PASSWORD_PREFERENCE";
    public static final String GATEWAY_CONFIRM_PREFERENCE = "GATEWAY_CONFIRM_PREFERENCE";
    public static final String GATEWAY_PLACE_PREFERENCE = "GATEWAY_PLACE_PREFERENCE";
    public static final String GATEWAY_NEAR_PREFERENCE = "GATEWAY_NEAR_PREFERENCE";
    public static final String GATEWAY_FAR_PREFERENCE = "GATEWAY_FAR_PREFERENCE";

    public static final String NUM_OF_PARENT = "NUM_OF_PARENT";
    public static final String PARENT_ACCOUNT_PREFERENCE = "PARENT_ACCOUNT_PREFERENCE";
    public static final String PARENT_PASSWORD_PREFERENCE = "PARENT_PASSWORD_PREFERENCE";
    public static final String PARENT_CONFIRM_PREFERENCE = "PARENT_CONFIRM_PREFERENCE";
    public static final String PARENT_CHILDLIST_PREFERENCE = "PARENT_CHILDLIST_PREFERENCE";

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();

    public static final String CHILD_NAME = "CHILD_NAME";
    public static final String CHILD_ID = "CHILD_ID";
    public static final String DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String DEVICE_RANGE = "DEVICE_RANGE";
    public static final String PHOTO_NAME = "PHOTO_NAME";
    public static final String LIST_VIEW_POSITION = "LIST_VIEW_POSITION";
    public static final String GATEWAY_ID = "GATEWAY_ID";
    public static final String GATEWAY_ACCOUNT = "GATEWAY_ACCOUNT";
    public static final String GATEWAY_PASSWORD = "GATEWAY_PASSWORD";
    public static final String GATEWAY_CONFIRM = "GATEWAY_CONFIRM";
    public static final String GATEWAY_PLACE = "GATEWAY_PLACE";
    public static final String GATEWAY_NEAR = "GATEWAY_NEAR";
    public static final String GATEWAY_FAR = "GATEWAY_FAR";
    public static final String GATEWAY_NUMBER = "GATEWAY_NUMBER";
    public static final String GATEWAY_TITLE = "GATEWAY_TITLE";
    public static final String PARENT_ID = "PARENT_ID";
    public static final String PARENT_ACCOUNT = "PARENT_ACCOUNT";
    public static final String PARENT_PASSWORD = "PARENT_PASSWORD";
    public static final String PARENT_CONFIRM = "PARENT_CONFIRM";
    public static final String PARENT_CREATE_CHILD_NUM = "PARENT_CREATE_CHILD_NUM";
    public static final String PARENT_CREATE_CHILD_ID = "PARENT_CREATE_CHILD_ID";
    public static final String PARENT_CREATE_CHILD_SPINNER_SELECT = "PARENT_CREATE_CHILD_SPINNER_SELECT";
    public static final String PARENT_NUMBER = "PARENT_NUMBER";
    public static final String PARENT_TITLE = "PARENT_TITLE";
    public static final String ALARM_TIME = "ALARM_TIME";
    public static final int REQUEST_ENABLE_BT = 0x00;
    public static final int REQUEST_CODE_EDIT = 0x01;
    public static final int REQUEST_CODE_SCAN = 0x01 << 2;
    public static final int REQUEST_CODE_ADD = 0x01 << 3;
    public static final int REQUEST_CODE_TAKE_PHOTO = 0x01 << 4;
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 0x01 << 5;
    public static final int REQUEST_CODE_SCALE_PHOTO = 0x01 << 6;
    public static final int REQUEST_CODE_CROP_PHOTO = Crop.REQUEST_CROP;

    public static final int REQUEST_CODE_GATEWAY_ADD = 0x01 << 7;
    public static final int REQUEST_CODE_GATEWAY_EDIT = 0x01 << 8;
    public static final int REQUEST_CODE_PARENT_EDIT = 0x01 << 9;
    public static final int REQUEST_CODE_PARENT_ADD = 0x01 << 10;
    public static final int REQUEST_CODE_ALARM_TIME_EDIT = 0x01 << 11;
    public static final int REQUEST_COARSE_LOCATION = 0x01 << 12;
    public static final int REQUEST_EXTERNAL_STORAGE = 0x01 << 13;
    public static final int RESULT_CODE_REMOVE = 0x01;
    public static final int RESULT_CODE_CROP_ERROR = Crop.RESULT_ERROR;

    public static final int NOTIFY_SERVICE_ID = 100;
    public static final int REQUEST_NOTIFICATION_SERVICE = 0x01 << 14;

    public static final String EXTRA_ORIENTATION =  Crop.EXTRA_ORIENTATION;

    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    //for server request
    public static final String CHILD_PHOTO_FILE_PATH = ROOT_PATH+"/koala/childcare/";
    public static final String CHILD_PHOTO_FILE_URL = "http://";
    public static final String BACKEND_API_URL = "http://140.113.169.174/child_care/";
    public static final String SIGN_UP_URL = BACKEND_API_URL + "sign_up.php";
    public static final String LOGIN_URL = BACKEND_API_URL + "login.php";
    public static final String GATEWAY_UPLOAD_URL = BACKEND_API_URL + "gateway_upload.php";
    public static final String SHOW_CHILD_BY_ID_URL = BACKEND_API_URL + "show_child_by_id.php";
    public static final String PARENT_CHILD_DEFINE_URL = BACKEND_API_URL + "parent_child_define.php";
    public static final String NEW_CHILD_URL = BACKEND_API_URL + "new_child.php";
    public static final String DELETE_URL = BACKEND_API_URL + "delete.php";
    public static final String UPATE_URL = BACKEND_API_URL + "update.php";
    public static final String SHOW_ALL_PARENT_URL = BACKEND_API_URL + "show_all_parent.php";
    public static final String SHOW_ALL_GATEWAY_URL = BACKEND_API_URL + "show_all_gateway.php";
    public static final String CHILD_PHOTO_FILE_UPLOAD_URL = BACKEND_API_URL + "up_image.php";
    public static final String CHILD_PHOTO_FILE_DELETE_URL = BACKEND_API_URL + "del_image.php";

    public static ApplicationContext getInstance(){
        ApplicationContext mApplication = mInstance;
        if(mInstance == null){
            mInstance = new ApplicationContext();
        }
        return mInstance;
    }

    static {
        mMapChildren = new HashMap<String, ChildProfile>();
        mMapChildrenCid = new HashMap<String, ChildProfile>();
        mListChildren = new ArrayList<ChildProfile>();
        mGateways = new ArrayList<NewGatewayItem>();
        mParents = new ArrayList<NewParentItem>();
        mDeviceList = new ArrayList<BluetoothDevice>();
        mSpinChildName = new ArrayList<String>();
        mKoalaManager = null;
        mBooleanKoalaServiceCreated = false;
        mBluetoothAdapter = null;
        mBLEScanner = null;
        mTeacherName = "Teacher Name";
        mIsScan = false;
        mIsLogin = false;
        mIsServiceOn = false;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        } else {
            mMemoryCache.remove(key);
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
    }


    //Initialize lists
    private void init() {

        SharedPreferences mPref = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        String mTeacherName = mPref.getString(TEACHER_NAME_PREFERENCE, "Teacher Name");
    }

    public static void addANewChild(ChildProfile child) {
        if (findChild(child.getDeviceAddress()) == -1) {
            mMapChildren.put(child.getDeviceAddress(), child);
            mMapChildrenCid.put(child.getCid(), child);
            mListChildren.add(child);
        }
    }

    public static ChildProfile removeAChild(String addr) {
        if (findChild(addr) != -1) {
            ChildProfile child = mMapChildren.get(addr);
            mListChildren.remove(child);
            mMapChildren.remove(child);
            mMapChildrenCid.remove(child);
        }
        return null;
    }

    public static int findChild(String addr) {
        if (mMapChildren.containsKey(addr)) {
            ChildProfile child = mMapChildren.get(addr);
            int position = mListChildren.indexOf(child);
            return position;
        } else {
            return -1;
        }
    }

    public static int findChildById(String id) {
        if (mMapChildrenCid.containsKey(id)) {
            ChildProfile child = mMapChildrenCid.get(id);
            int position = mListChildren.indexOf(child);
            return position;
        } else {
            return -1;
        }
    }


    public void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        final int num_of_children = mListChildren.size();
        final String teacherName = mTeacherName;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEACHER_NAME_PREFERENCE, teacherName);
        editor.putInt(NUM_OF_CHILDREN, num_of_children);
        for (int i=0; i<num_of_children; i++) {
            Log.i(TAG, "save Child:" + i + " name:" + mListChildren.get(i).getName() + " address:" + mListChildren.get(i).getDeviceAddress() + " status:" + mListChildren.get(i).getStatus() + " photoName:" + mListChildren.get(i).getPhotoName());
            editor.putString(CHILD_NAME_PREFERENCE + i, mListChildren.get(i).getName());
            editor.putString(CHILD_DEVICE_PREFERENCE+i, mListChildren.get(i).getDeviceAddress());
            editor.putString(CHILD_PHOTO_PREFERENCE+i, mListChildren.get(i).getPhotoName());
        }
        editor.apply();
    }

    public void saveTimePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ALARM_TIME_PREFERENCE, alarmTime);
        editor.apply();
    }

    public void saveGatewayPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        final int num_of_gateway = mGateways.size();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NUM_OF_GATEWAY, num_of_gateway);
        for (int i=0; i<mGateways.size(); i++) {
            Log.i(TAG, "save Gateway:" + i + " account:" + mGateways.get(i).getAccount() + " password:" + mGateways.get(i).getPassword() + " place:" + mGateways.get(i).getPlace());
            editor.putString(GATEWAY_ACCOUNT_PREFERENCE + i, mGateways.get(i).getAccount());
            editor.putString(GATEWAY_PASSWORD_PREFERENCE+i, mGateways.get(i).getPassword());
            editor.putString(GATEWAY_CONFIRM_PREFERENCE + i, mGateways.get(i).getConfirm());
            editor.putString(GATEWAY_PLACE_PREFERENCE+i, mGateways.get(i).getPlace());
            editor.putString(GATEWAY_NEAR_PREFERENCE+i, mGateways.get(i).getNear());
            editor.putString(GATEWAY_FAR_PREFERENCE+i, mGateways.get(i).getFar());
        }
        editor.apply();
    }

    public void saveParentPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        final int num_of_parent = mParents.size();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NUM_OF_PARENT, num_of_parent);
        for (int i=0; i<num_of_parent; i++) {
            Log.i(TAG, "saveParent:" + i + " account:" + mParents.get(i).getAccount() + " password:" + mParents.get(i).getPassword());
            editor.putString(PARENT_ACCOUNT_PREFERENCE + i, mParents.get(i).getAccount());
            editor.putString(PARENT_PASSWORD_PREFERENCE+i, mParents.get(i).getPassword());
            editor.putString(PARENT_CONFIRM_PREFERENCE+i, mParents.get(i).getConfirm());
            int num_parent_child = mParents.get(i).getmChildList().size();
            String S = "";
            for(int j=0; j<num_parent_child; j++)
                S = S + String.valueOf(mParents.get(i).getmChildList().get(j).spinner_select)+",";
            S = S + ",";
            editor.putString(PARENT_CHILDLIST_PREFERENCE + i, S);
        }
        editor.apply();
    }

    public static void clearPhotoCache() {
        final int num_of_children = mListChildren.size();
        for (int i=0; i<num_of_children; i++) {
            File tmpFile = new File(CHILD_PHOTO_FILE_PATH, mListChildren.get(i).getPhotoName());
            Log.i(TAG, "file:" + CHILD_PHOTO_FILE_PATH + mListChildren.get(i).getPhotoName());
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    public static Drawable controlBitMap(Context context,int id){
        BitmapFactory.Options opt =new BitmapFactory.Options();
        //opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        try {
            InputStream is = context.getResources().openRawResource(id);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
            is.close();
            return new BitmapDrawable(context.getResources(),bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitMapById(Context context,int id){
        BitmapFactory.Options opt =new BitmapFactory.Options();
        //opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        try {
            InputStream is = context.getResources().openRawResource(id);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBitmap(File file, Bitmap bitmap) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
        } catch (Exception e) {
            // TODO: handle exception
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
        try {
            fout.flush();
            fout.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static String encodeImagetoString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, 0);
    }

    public static Bitmap getBitmapByFile(File file) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (OutOfMemoryError e) {
            //e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmapByFileName(String fileName) {
        File tmpFile;
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            tmpFile = createImageFile(CHILD_PHOTO_FILE_PATH, fileName);
            fis = new FileInputStream(tmpFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (OutOfMemoryError e) {
            //e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    public static File createImageFile(String filePath, String fileName) {
        File dir = new File(filePath);
        File imageFile;
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Log.i(TAG, "filePath:"+filePath+" fileName:"+fileName);
        imageFile = new File(filePath, fileName);
        if (imageFile == null) {
            Log.i(TAG, "filePath:"+filePath+" fileName:"+fileName+" is null!");
        }
        return imageFile;
    }

    public static boolean imageFileDelete(File file) {
        if (file.exists()) {
            if (file.delete())
                return true;
            else
                return false;
        }
        return false;
    }

    public static void signUp_master(String type, String account,String password,String time, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);
            json.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SIGN_UP_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());

                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            content.mid=Integer.parseInt(data.getString("mid"));
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void signUp_teacher(String type, String account,String password,int mid, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);
            json.put("mid", Integer.valueOf(mid));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SIGN_UP_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                            Log.i(TAG, response.toString());
                            CallBackContent content=new CallBackContent();
                            content.success_msg = response.toString();
                            callBack.done(content);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void signUp_parent(String type, String account,String password,int mid, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);
            json.put("mid", Integer.valueOf(mid));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SIGN_UP_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            String pid = data.getString("pid");
                            NewParentItem parent = new NewParentItem(pid);
                            content.parent = parent;
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void signUp_gateway(String type, String account,String password,String place,int mid, String near, String far, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);
            json.put("mid", Integer.valueOf(mid));
            json.put("place", place);
            json.put("near", near);
            json.put("far", far);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SIGN_UP_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, response.toString());
                        CallBackContent content=new CallBackContent();
                        content.success_msg = response.toString();
                        callBack.done(content);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    public static void login_admin(String type, final String account, final String password, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            mAccount = account;
                            mPassword = password;
                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            content.mid=Integer.parseInt(data.getString("mid"));
                            content.gids=data.getString("gids");
                            content.cids=data.getString("cids");
                            content.pids=data.getString("pids");
                            alarmTime=Integer.parseInt(data.getString("time"));
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void login_teacher(String type, final String account,final String password, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            mAccount = account;
                            mPassword = password;
                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            content.mid=Integer.parseInt(data.getString("mid"));
                            content.cids=data.getString("cids");
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void login_gateway(String type,final String account,final String password, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            mAccount = account;
                            mPassword = password;
                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            content.mid=Integer.parseInt(data.getString("mid"));
                            content.mGid=data.getString("gid");
                            content.cids=data.getString("cids");
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void login_parent(String type, final String account,final String password, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("account", account);
            json.put("passwd", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            mAccount = account;
                            mPassword = password;
                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            content.mid=Integer.parseInt(data.getString("mid"));
                            content.mPid=data.getString("pid");
                            content.cids=data.getString("cids");
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void gateway_upload(String gid, String cids,String status, String time) {
        JSONObject json = new JSONObject();
        try {
            json.put("gid", gid);
            json.put("cids", cids);
            json.put("status", status);
            json.put("time", time);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, GATEWAY_UPLOAD_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            Log.i(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void show_child_by_id(final String cid,final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("cid", cid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SHOW_CHILD_BY_ID_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            String name = data.getString("name");
                            String photo_url = data.getString("photo");
                            String mac =  data.getString("mac");
                            String gid = data.getString("gid");
                            String place = data.getString("place");
                            String rssi = data.getString("rssi");
                            String status = data.getString("status");
                            String flag = data.getString("flag");
                            mSpinChildName.add(name);
                            ChildProfile child = new ChildProfile(name,photo_url,mac,gid,place,rssi,status,flag);
                            child.setCid(cid);
                            content.child = child;
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void parent_child_define(String pid, String cids) {
        JSONObject json = new JSONObject();
        try {
            json.put("pid", pid);
            json.put("cids", cids);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, PARENT_CHILD_DEFINE_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void new_child(int mid, String mac, String name, Bitmap bitmap,final CallBack callBack) {
        String base64 = encodeImagetoString(bitmap);
        JSONObject json = new JSONObject();
        try {
            json.put("mid", String.valueOf(mid));
            json.put("mac", mac);
            json.put("name",name);
            json.put("photoBase64",base64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, NEW_CHILD_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            JSONObject data = response.getJSONObject("data");
                            CallBackContent content=new CallBackContent();
                            String cid = data.getString("cid");
                            content.cids = cids + cid +",";
                            ChildProfile child = new ChildProfile(cid);
                            content.child = child;
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                            callBack.done(null);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void delete(String type, final String id) {
        switch (type) {
            case "child":
                JSONObject json_delete_child = new JSONObject();
                try {
                    json_delete_child.put("type", type);
                    json_delete_child.put("cid", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsObjRequest_delete_child = new JsonObjectRequest
                        (Request.Method.POST, DELETE_URL, json_delete_child, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                    Log.i(TAG, response.toString());
                                    int pos = cids.indexOf(id);
                                    StringBuilder str = new StringBuilder(cids);
                                    str.delete(pos,pos+2);
                                    cids = str.toString();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i(TAG, error.toString());
                            }
                        });
                //no cache
                jsObjRequest_delete_child.setShouldCache(false);
                VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest_delete_child);
                break;
            case "gateway":
                JSONObject json_delete_gateway = new JSONObject();
                try {
                    json_delete_gateway.put("type", type);
                    json_delete_gateway.put("gid", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsObjRequest_delete_gateway = new JsonObjectRequest
                        (Request.Method.POST, DELETE_URL, json_delete_gateway, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i(TAG, response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i(TAG, error.toString());
                            }
                        });
                //no cache
                jsObjRequest_delete_gateway.setShouldCache(false);
                VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest_delete_gateway);
                break;
            case "parent":
                JSONObject json_delete_parent = new JSONObject();
                try {
                    json_delete_parent.put("type", type);
                    json_delete_parent.put("pid", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsObjRequest_delete_parent = new JsonObjectRequest
                        (Request.Method.POST, DELETE_URL, json_delete_parent, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i(TAG, response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i(TAG, error.toString());
                            }
                        });
                //no cache
                jsObjRequest_delete_parent.setShouldCache(false);
                VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest_delete_parent);
                break;
        }
    }


    public static void update_gateway(String type, String gid, String place,final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type",type);
            json.put("gid", gid);
            json.put("place", place);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, UPATE_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        CallBackContent content=new CallBackContent();
                        content.success_msg = response.toString();
                        callBack.done(content);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void update_child(String type, String cid, String name, Bitmap bitmap) {
        String base64 = encodeImagetoString(bitmap);
        JSONObject json = new JSONObject();
        try {
            json.put("type",type);
            json.put("cid", cid);
            json.put("name", name);
            json.put("photoBase64", base64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, UPATE_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void update_distance(String type, String gid, String near, String far,final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("type",type);
            json.put("gid", gid);
            json.put("near", near);
            json.put("far", far);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, UPATE_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        CallBackContent content=new CallBackContent();
                        content.success_msg = response.toString();
                        callBack.done(content);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                        callBack.done(null);
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void update_time(String type, int mid, String time) {
        JSONObject json = new JSONObject();
        try {
            json.put("type",type);
            json.put("mid", String.valueOf(mid));
            json.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, UPATE_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void show_all_parent( int mid,final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("mid", String.valueOf(mid));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SHOW_ALL_PARENT_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.i(TAG, response.toString());
                            CallBackContent content = new CallBackContent();
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                NewParentItem returnedParent = new NewParentItem(((JSONObject) data.get(i)).getString("pid"));
                                String[] cids = ((JSONObject) data.get(i)).getString("cids").split(",");
                                if (!((JSONObject) data.get(0)).getString("cids").equals("")) {
                                    for (int j = 0; j < cids.length; j++) {
                                        ParentCreateChildItem returnedChild = new ParentCreateChildItem(cids[j]);
                                        returnedParent.addChild(returnedChild);
                                    }
                                }
                                content.show_parent.add(returnedParent);
                            }
                            callBack.done(content);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void show_all_gateway( int mid, final CallBack callBack) {
        JSONObject json = new JSONObject();
        try {
            json.put("mid", String.valueOf(mid));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SHOW_ALL_GATEWAY_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.i(TAG, response.toString());
                            CallBackContent content=new CallBackContent();
                            JSONArray data = response.getJSONArray("data");
                            for(int i=0; i<data.length(); i++) {
                                String gid =  ((JSONObject) data.get(i)).getString("gid");
                                String place = ((JSONObject) data.get(i)).getString("place");
                                String near = ((JSONObject) data.get(i)).getString("near");
                                String far = ((JSONObject) data.get(i)).getString("far");
                                NewGatewayItem returnedGateway = new NewGatewayItem(gid,place,near,far);
                                content.show_gateway.add(returnedGateway);
                            }
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());
                    }
                });
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void notificationServiceStartBuilder(Activity activity) {
        final int notifyID = NOTIFY_SERVICE_ID; // 通知的識別號碼
        final boolean autoCancel = false; // 點擊通知後是否要自動移除掉通知
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
        final int requestCode = REQUEST_NOTIFICATION_SERVICE; // PendingIntent的Request Code
        final Intent intent = activity.getIntent(); // 目前Activity的Intent
        intent.setClass(activity, BaseTabViewActivity.class);
        final int flags = PendingIntent.FLAG_UPDATE_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(activity.getApplicationContext()).setSmallIcon(R.drawable.base_main).setContentTitle(activity.getString(R.string.notification_title)).setContentText(activity.getString(R.string.toggle_on)).setSound(soundUri).setContentIntent(pendingIntent).setAutoCancel(autoCancel).build(); // 建立通知
        notification.flags = Notification.FLAG_ONGOING_EVENT; //將訊息常駐在status bar
        notificationManager.notify(notifyID, notification); // 發送通知
    }

    public static void cancelNotificationService(Activity activity) {
        final int notifyID = NOTIFY_SERVICE_ID;
        final NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        notificationManager.cancel(notifyID);
    }


}
