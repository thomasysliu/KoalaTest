package cc.nctu1210.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.File;
import java.util.List;

import cc.nctu1210.childcare.R;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.VolleyRequestManager;


/**
 * A custom adapter for our listview
 * <p/>
 * If you check http://developer.android.com/reference/android/widget/Adapter.html you'll notice
 * there are several types. BaseAdapter is a good generic adapter that should suit all your needs.
 * Just implement all what's abstract and add your collection of data
 * <p/>
 * Created by hanscappelle on 7/10/14.
 * https://github.com/hanscappelle/so-2250770
 */
public class DeviceListAdapter extends BaseAdapter {
	private final static String TAG = DeviceListAdapter.class.getSimpleName();
    /**
     * this is our own collection of data, can be anything we want it to be as long as we get the
     * abstract methods implemented using this data and work on this data (see getter) you should
     * be fine
     */
    private List<DeviceItem> mData;

    /**
     * some context can be useful for getting colors and other resources for layout
     */
    private Context mContext;

    /**
     * our ctor for this adapter, we'll accept all the things we need here
     *
     * @param mData
     */
    public DeviceListAdapter(final Context context, final List<DeviceItem> mData) {
        this.mData = mData;
        this.mContext = context;
    }

    public List<DeviceItem> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mData != null ? mData.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        // just returning position as id here, could be the id of your model object instead
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // this is where we'll be creating our view, anything that needs to update according to
        // your model object will need a view to visualize the state of that propery
    	View view = convertView;
    	
    	
        // the viewholder pattern for performance
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {

            // inflate the layout, see how we can use this context reference?
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.list_device_item, parent, false);
            Log.d(TAG, String.format("Get view %d", position));
            // we'll set up the ViewHolder
            viewHolder.photo    =  (ImageView) view.findViewById(R.id.image_device_user);
            viewHolder.name     = (TextView) view.findViewById(R.id.text_device_child);
            viewHolder.mac      = (TextView) view.findViewById(R.id.text_device_mac);
            // store the holder with the view.
            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder instead
            viewHolder = (ViewHolder) view.getTag();
        }

        // object item based on the position
        DeviceItem obj = mData.get(position);

        // assign values if the object is not null
        if (mData != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and other values
            final String photoName = obj.photoName;
            //Bitmap photo = ApplicationContext.getBitmapByFileName(photoName);
            Bitmap photo = ApplicationContext.getBitmapFromMemCache(photoName);

            if (photo == null) {
                //viewHolder.photo.setBackground(ApplicationContext.controlBitMap(mContext, R.drawable.default_user));
                String photoURL = ApplicationContext.CHILD_PHOTO_FILE_URL + photoName;
                final ImageView photoImage = viewHolder.photo;
                //ImageLoader mImageLoader = VolleyRequestManager.getInstance(mContext).getImageLoader();
                //mImageLoader.get(photoURL, ImageLoader.getImageListener(viewHolder.photo, R.drawable.default_user, R.drawable.default_user));
                ImageRequest request = new ImageRequest(photoURL,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                //File photoFile = new File(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);
                                photoImage.setImageBitmap(response);
                                //ApplicationContext.saveBitmap(photoFile, response);
                                ApplicationContext.addBitmapToMemoryCache(photoName, response);
                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                photoImage.setImageResource(R.drawable.default_user);
                            }
                        });
                VolleyRequestManager.getInstance(mContext).addToRequestQueue(request);

            } else {
                viewHolder.photo.setImageBitmap(photo);
            }

            viewHolder.name.setText(obj.child_name);
            viewHolder.mac.setText(obj.device_addr);
        }
        return view;
    }

    private static class ViewHolder {
        public ImageView photo;
        public TextView name;
        public TextView mac;
    }
}