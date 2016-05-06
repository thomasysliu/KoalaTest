package cc.nctu1210.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import cc.nctu1210.childcare.R;
import cc.nctu1210.tool.ApplicationContext;

/**
 * Created by User on 2016/4/28.
 */
public class ParentCreateChildrenListAdapter extends BaseAdapter {
    private final static String TAG = ParentCreateChildrenListAdapter.class.getSimpleName();
    private ArrayAdapter<String> ChildNameList;
    /**
     * this is our own collection of data, can be anything we want it to be as long as we get the
     * abstract methods implemented using this data and work on this data (see getter) you should
     * be fine
     */
    private List<ParentCreateChildItem> mData;

    /**
     * some context can be useful for getting colors and other resources for layout
     */
    private Context mContext;

    public ParentCreateChildrenListAdapter(final Context context, final List<ParentCreateChildItem> mData) {
        this.mData = mData;
        this.mContext = context;
    }

    public List<ParentCreateChildItem> getData() {
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
            view = inflater.inflate(R.layout.new_child_in_parent_create, parent, false);
            Log.d(TAG, String.format("Get view %d", position));
            // we'll set up the ViewHolder
            viewHolder.child     = (TextView) view.findViewById(R.id.text_child);
            viewHolder.child_name      = (Spinner) view.findViewById(R.id.child_name_spinner);
            // store the holder with the view.
            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder instead
            viewHolder = (ViewHolder) view.getTag();
        }



        // object item based on the position
        final ParentCreateChildItem obj = mData.get(position);

        // assign values if the object is not null
        if (mData != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and other values
            String [] mSpinChildName= new String[ApplicationContext.mListChildren.size()];
            ApplicationContext.mSpinChildName.toArray(mSpinChildName);
            ChildNameList = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,mSpinChildName);
            ChildNameList.setDropDownViewResource(R.layout.spinner_dropdown_item);
            viewHolder.child_name.setAdapter(ChildNameList);
            viewHolder.child_name.setSelection(obj.getSpinnerSelect());
            viewHolder.child_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    obj.setSpinnerSelect(position);
                    obj.setId(ApplicationContext.mListChildren.get(position).getCid());
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
        return view;
    }


    private static class ViewHolder {
        public TextView child;
        public Spinner child_name;

    }
}
