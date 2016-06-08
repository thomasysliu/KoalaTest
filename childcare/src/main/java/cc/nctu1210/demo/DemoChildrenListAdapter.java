package cc.nctu1210.demo;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import cc.nctu1210.childcare.R;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.VolleyRequestManager;
import cc.nctu1210.view.ChildItem;

/**
 * Created by User on 2016/6/2.
 */
public class DemoChildrenListAdapter extends BaseAdapter{
    private final static String TAG = DemoChildrenListAdapter.class.getSimpleName();
    private int CONTROL_CLICK_NEAR = 1;
    private int CONTROL_CLICK_MEDIATE = 2;
    private int CONTROL_CLICK_FAR = 3;
    private int CONTROL_RSSI_NEAR = -50;
    private int CONTROL_RSSI_MEDIATE = -75;
    private int CONTROL_RSSI_FAR = -100;
    /**
     * this is our own collection of data, can be anything we want it to be as long as we get the
     * abstract methods implemented using this data and work on this data (see getter) you should
     * be fine
     */
    private List<ChildItem> mData;
    /**
     * some context can be useful for getting colors and other resources for layout
     */
    private Context mContext;
    private int isMaster=0;
    /**
     * our ctor for this adapter, we'll accept all the things we need here
     *
     * @param mData
     */
    public DemoChildrenListAdapter(final Context context, final List<ChildItem> mData) {
        this.mData = mData;
        this.mContext = context;
    }

    public List<ChildItem> getData() {
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
        final ViewHolder viewHolder = new ViewHolder();
        /*
        if (view == null) {

            // inflate the layout, see how we can use this context reference?
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.demo_list_child_item, parent, false);
            Log.d(TAG, String.format("Get view %d", position));
            // we'll set up the ViewHolder
            viewHolder.photo = (ImageView) view.findViewById(R.id.image_child_user);
            viewHolder.name     = (TextView) view.findViewById(R.id.text_child_user);
            viewHolder.near      = (ImageView) view.findViewById(R.id.image_demo_green);
            viewHolder.mediate      = (ImageView) view.findViewById(R.id.image_demo_yellow);
            viewHolder.far      = (ImageView) view.findViewById(R.id.image_demo_red);
            // store the holder with the view.
            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder instead
            viewHolder = (ViewHolder) view.getTag();
        }*/
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        view = inflater.inflate(R.layout.demo_list_child_item, parent, false);
        Log.d(TAG, String.format("Get view %d", position));
        // we'll set up the ViewHolder
        viewHolder.photo = (ImageView) view.findViewById(R.id.image_child_user);
        viewHolder.name     = (TextView) view.findViewById(R.id.text_child_user);
        viewHolder.near      = (ImageView) view.findViewById(R.id.image_demo_green);
        viewHolder.mediate      = (ImageView) view.findViewById(R.id.image_demo_yellow);
        viewHolder.far      = (ImageView) view.findViewById(R.id.image_demo_red);
        // store the holder with the view.
        view.setTag(viewHolder);

        // object item based on the position
        final ChildItem obj = mData.get(position);

        // assign values if the object is not null
        if (mData != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and other values
            final String photoName = obj.photoName;
            //Bitmap photo = ApplicationContext.getBitmapByFileName(photoName);
            //Log.i(TAG, "position: "+position+" photoName:"+photoName);
            Bitmap photo = ApplicationContext.getBitmapFromMemCache(photoName);

            if (photo == null) {
                Log.i(TAG, "get image  from volley!");
                //viewHolder.photo.setBackground(ApplicationContext.controlBitMap(mContext, R.drawable.default_user));
                String photoURL = ApplicationContext.CHILD_PHOTO_FILE_URL + photoName;
                final ImageView photoImage = viewHolder.photo;
                //ImageLoader mImageLoader = VolleyRequestManager.getInstance(mContext).getImageLoader();
                //mImageLoader.get(photoURL, ImageLoader.getImageListener(viewHolder.photo, R.drawable.default_user, R.drawable.default_user));
                ImageRequest request = new ImageRequest(photoURL,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                //File photoFile = new File(ApplicationContext.CHILD_PHOTO_FILE_PATH, photoName);\
                                Bitmap out = null;
                                //Log.i(TAG, "Before compressed: " + photoName + ":size: " + response.getByteCount() + "bytes");
                                out = ApplicationContext.scaleBitmap(response, 100, 100);
                                //Log.i(TAG, "After compressed: "+photoName+":size: "+out.getByteCount()+"bytes");
                                photoImage.setImageBitmap(out);
                                //ApplicationContext.saveBitmap(photoFile, response);
                                ApplicationContext.addBitmapToMemoryCache(photoName, out);
                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.w(TAG, "image access fail!!!");
                                photoImage.setImageResource(R.drawable.default_user);
                            }
                        });
                VolleyRequestManager.getInstance(mContext).addToRequestQueue(request);
            } else {
                Log.i(TAG, "image set from cache!");
                viewHolder.photo.setImageBitmap(photo);
            }

            viewHolder.name.setText(obj.name);

            switch (obj.control_click)
            {
                case 1:
                    viewHolder.near.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_green, null));
                    viewHolder.mediate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    viewHolder.far.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    break;
                case  2:
                    viewHolder.mediate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_yellow, null));
                    viewHolder.near.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    viewHolder.far.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    break;
                case 3:
                    viewHolder.far.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_red, null));
                    viewHolder.near.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    viewHolder.mediate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    break;
            }

            viewHolder.near.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obj.control_click = CONTROL_CLICK_NEAR;
                    obj.control_rssi = CONTROL_RSSI_NEAR;

                    StringBuilder cids = new StringBuilder("");
                    StringBuilder status = new StringBuilder("");
                    int unixTime = (int) (System.currentTimeMillis() / 1000L);
                    cids.append(obj.cid).append(",");
                    status.append(String.valueOf(CONTROL_RSSI_NEAR)).append(",");
                    ApplicationContext.gateway_upload(ApplicationContext.mGid, cids.toString(), status.toString(), String.valueOf(unixTime));

                    viewHolder.near.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_green, null));
                    viewHolder.mediate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    viewHolder.far.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                }
            });

            viewHolder.mediate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obj.control_click = CONTROL_CLICK_MEDIATE;
                    obj.control_rssi = CONTROL_RSSI_MEDIATE;

                    StringBuilder cids = new StringBuilder("");
                    StringBuilder status = new StringBuilder("");
                    int unixTime = (int) (System.currentTimeMillis() / 1000L);
                    cids.append(obj.cid).append(",");
                    status.append(String.valueOf(CONTROL_RSSI_MEDIATE)).append(",");
                    ApplicationContext.gateway_upload(ApplicationContext.mGid, cids.toString(), status.toString(), String.valueOf(unixTime));

                    viewHolder.mediate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_yellow, null));
                    viewHolder.near.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    viewHolder.far.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                }
            });

            viewHolder.far.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obj.control_click = CONTROL_CLICK_FAR;
                    obj.control_rssi = CONTROL_RSSI_FAR;

                    StringBuilder cids = new StringBuilder("");
                    StringBuilder status = new StringBuilder("");
                    int unixTime = (int) (System.currentTimeMillis() / 1000L);
                    cids.append(obj.cid).append(",");
                    status.append(String.valueOf(CONTROL_RSSI_FAR)).append(",");
                    ApplicationContext.gateway_upload(ApplicationContext.mGid, cids.toString(), status.toString(), String.valueOf(unixTime));

                    viewHolder.far.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_red, null));
                    viewHolder.near.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                    viewHolder.mediate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.status_not_click, null));
                }
            });


        }
        return view;
    }

    private static class ViewHolder {
        public ImageView photo;
        public TextView name;
        public ImageView near;
        public ImageView mediate;
        public ImageView far;
    }
}
