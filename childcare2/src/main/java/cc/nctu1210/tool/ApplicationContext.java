package cc.nctu1210.tool;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.entity.ChildProfile;

/**
 * Created by Yi-Ta_Chuang on 2016/4/17.
 */
public class ApplicationContext extends Application {
    private static final String TAG = ApplicationContext.class.getSimpleName();

    public static HashMap<String, ChildProfile> mMapChildren;
    public static List<ChildProfile> mListChildren;
    public static List<BluetoothDevice> mDeviceList;

    public static KoalaServiceManager mKoalaManager;
    public static boolean mBooleanKoalaServiceCreated;
    public static String mTeacherName;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BLESanner mBLEScanner;
    public static boolean mIsScan;

    private static ApplicationContext mInstance;

    //for image cache
    public static LruCache<String, Bitmap> mMemoryCache;

    public static final String APPLICATION_PREFERENCES = "APPLICATION_PREFERENCES";
    public static final String TEACHER_NAME_PREFERENCE = "TEACHER_NAME_PREFERENCE";
    public static final String NUM_OF_CHILDREN = "NUM_OF_CHILDREN";
    public static final String CHILD_NAME_PREFERENCE = "CHILD_NAME_PREFERENCE";
    public static final String CHILD_DEVICE_PREFERENCE = "CHILD_DEVICE_PREFERENCE";
    public static final String CHILD_PHOTO_PREFERENCE = "CHILD_PHOTO_PREFERENCE";

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String CHILD_PHOTO_FILE_PATH = ROOT_PATH+"/koala/childcare/";
    public static final String CHILD_PHOTO_FILE_URL = "http://140.113.169.174/child_care/photos/";
    public static final String CHILD_PHOTO_FILE_API_URL = "http://140.113.169.174/child_care/";
    public static final String CHILD_PHOTO_FILE_UPLOAD_URL = CHILD_PHOTO_FILE_API_URL + "up_image.php";
    public static final String CHILD_PHOTO_FILE_DELETE_URL = CHILD_PHOTO_FILE_API_URL + "del_image.php";

    public static final String CHILD_NAME = "CHILD_NAME";
    public static final String DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String DEVICE_RANGE = "DEVICE_RANGE";
    public static final String PHOTO_NAME = "PHOTO_NAME";
    public static final String LIST_VIEW_POSITION = "LIST_VIEW_POSITION";
    public static final int REQUEST_CODE_EDIT = 0x01;
    public static final int REQUEST_CODE_SCAN = 0x01 << 2;
    public static final int REQUEST_CODE_ADD = 0x01 << 3;
    public static final int REQUEST_CODE_TAKE_PHOTO = 0x01 << 4;
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 0x01 << 5;
    public static final int REQUEST_CODE_CROP_PHOTO = Crop.REQUEST_CROP;
    public static final int REQUEST_COARSE_LOCATION = 0x01 << 7;
    public static final int REQUEST_EXTERNAL_STORAGE = 0x01 << 8 ;
    public static final int REQUEST_WARNING_NOTIFICATION = 0x01 << 9;
    public static final int REQUEST_NOTIFICATION_SERVICE = 0x01 << 10;
    public static final int RESULT_CODE_REMOVE = 0x01;
    public static final int RESULT_CODE_CROP_ERROR = Crop.RESULT_ERROR;

    public static final int NOTIFY_SERVICE_ID = 0x63;

    public static final String EXTRA_ORIENTATION =  Crop.EXTRA_ORIENTATION;

    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    public static ApplicationContext getInstance(){
        ApplicationContext mApplication = mInstance;
        if(mInstance == null){
            mInstance = new ApplicationContext();
        }
        return mInstance;
    }

    static {
        mMapChildren = new HashMap<String, ChildProfile>();
        mListChildren = new ArrayList<ChildProfile>();
        mDeviceList = new ArrayList<BluetoothDevice>();
        mKoalaManager = null;
        mBooleanKoalaServiceCreated = false;
        mBluetoothAdapter = null;
        mBLEScanner = null;
        mTeacherName = "Teacher Name";
        mIsScan = false;

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
        int num_of_children = mPref.getInt(NUM_OF_CHILDREN, 0);
        if (num_of_children != 0) {
            for (int i=0; i<num_of_children; i++) {
                String mChildName = mPref.getString(CHILD_NAME_PREFERENCE+i, "");
                String mStatus = mPref.getString(CHILD_DEVICE_PREFERENCE+i, "");
                String mFileName = mPref.getString(CHILD_PHOTO_PREFERENCE+i, "");
                ChildProfile mChild = new ChildProfile(mChildName, mStatus);
                mChild.setPhotoName(mFileName);
                Log.i(TAG, "init Child:"+i+" name:"+mChildName+" status:"+mStatus+" photoName:"+mFileName);
                addANewChild(mChild);
            }
        }
    }

    public static void addANewChild(ChildProfile child) {
        if (findChild(child.getDeviceAddress()) == -1) {
            mMapChildren.put(child.getDeviceAddress(), child);
            mListChildren.add(child);
        }
    }

    public static ChildProfile removeAChild(String addr) {
        if (findChild(addr) != -1) {
            ChildProfile child = mMapChildren.get(addr);
            mListChildren.remove(child);
            mMapChildren.remove(child);
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

    public void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        final int num_of_children = mListChildren.size();
        final String teacherName = mTeacherName;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEACHER_NAME_PREFERENCE, teacherName);
        editor.putInt(NUM_OF_CHILDREN, num_of_children);
        for (int i=0; i<num_of_children; i++) {
            Log.i(TAG, "save Child:" + i + " name:" + mListChildren.get(i).getName() + " address:" + mListChildren.get(i).getDeviceAddress() + " status:" + mListChildren.get(i).getStatus()+ " photoName:"+ mListChildren.get(i).getPhotoName());
            editor.putString(CHILD_NAME_PREFERENCE + i, mListChildren.get(i).getName());
            editor.putString(CHILD_DEVICE_PREFERENCE+i, mListChildren.get(i).getDeviceAddress());
            editor.putString(CHILD_PHOTO_PREFERENCE+i, mListChildren.get(i).getPhotoName());
        }
        editor.apply();
        //clear photo cache
        clearPhotoCache();
    }

    public static void clearPhotoCache() {
        final int num_of_children = mListChildren.size();
        for (int i=0; i<num_of_children; i++) {
            File tmpFile = new File(CHILD_PHOTO_FILE_PATH, mListChildren.get(i).getPhotoName());
            Log.i(TAG, "file:"+CHILD_PHOTO_FILE_PATH+mListChildren.get(i).getPhotoName());
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

    public static void uploadPhoto(String fileName, Bitmap bitmap) {
        String base64 = encodeImagetoString(bitmap);
        JSONObject json = new JSONObject();
        try {
            json.put("fileName", fileName);
            json.put("content64", base64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, CHILD_PHOTO_FILE_UPLOAD_URL, json, new Response.Listener<JSONObject>() {
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
        Log.i(TAG, "upload image: URL:" + CHILD_PHOTO_FILE_UPLOAD_URL + " fileName:" + fileName + " content64:" + base64);
        //no cache
        jsObjRequest.setShouldCache(false);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public static void deletePhoto(String fileName) {
        JSONObject json = new JSONObject();
        try {
            json.put("fileName", fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, CHILD_PHOTO_FILE_DELETE_URL, json, new Response.Listener<JSONObject>() {
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
        Log.i(TAG, "delete image: URL:"+ CHILD_PHOTO_FILE_DELETE_URL+" fileName:"+fileName);
        VolleyRequestManager.getInstance(getInstance().getApplicationContext()).addToRequestQueue(jsObjRequest);
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
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
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
}
