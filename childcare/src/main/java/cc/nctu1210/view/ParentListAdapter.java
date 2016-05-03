package cc.nctu1210.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cc.nctu1210.childcare.ParentCreateActivity;
import cc.nctu1210.childcare.R;
import cc.nctu1210.tool.ApplicationContext;

/**
 * Created by User on 2016/4/27.
 */
public class ParentListAdapter extends BaseAdapter {
    private String password;
    private String confirm;
    private final static String TAG = "ParentListAdapter";
    /**
     * this is our own collection of data, can be anything we want it to be as long as we get the
     * abstract methods implemented using this data and work on this data (see getter) you should
     * be fine
     */
    private List<NewParentItem> mData;

    /**
     * some context can be useful for getting colors and other resources for layout
     */
    private Context mContext;

    /**
     * our ctor for this adapter, we'll accept all the things we need here
     *
     * @param mData
     */
    public ParentListAdapter(final Context context, final List<NewParentItem> mData) {
        this.mData = mData;
        this.mContext = context;
    }

    public List<NewParentItem> getData() {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        // this is where we'll be creating our view, anything that needs to update according to
        // your model object will need a view to visualize the state of that propery
        View view = convertView;


        // the viewholder pattern for performance
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder= new ViewHolder();
            // inflate the layout, see how we can use this context reference?
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.list_new_parent_item, parent, false);
            Log.d(TAG, String.format("Get view %d", position));
            // we'll set up the ViewHolder
            viewHolder.parentTitle = (TextView) view.findViewById(R.id.parent_title);
            viewHolder.editAccount  = (EditText) view.findViewById(R.id.edt_account);
            viewHolder.editPassword  = (EditText) view.findViewById(R.id.edt_password);
            viewHolder.editConfirm   = (EditText) view.findViewById(R.id.edt_confirm);
            viewHolder.newChild    = (LinearLayout) view.findViewById(R.id.new_child);
            // store the holder with the view.
            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder instead
            viewHolder = (ViewHolder) view.getTag();
        }

        // object item based on the position
        final NewParentItem obj = mData.get(position);

        // assign values if the object is not null
        if (mData != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and other values
            String title = mContext.getString(R.string.parent) + String.valueOf(position+1);
            viewHolder.parentTitle.setText(title);
            obj.setTitle(title);
            viewHolder.editAccount.setText("");
            viewHolder.editPassword.setText("");
            viewHolder.editConfirm.setText("");
            if (!obj.getAccount().equals(""))
                viewHolder.editAccount.setText(obj.getAccount());
            if (!obj.getPassword().equals(""))
                viewHolder.editPassword.setText(obj.getPassword());
            if (!obj.getConfirm().equals(""))
                viewHolder.editConfirm.setText(obj.getConfirm());
            viewHolder.editAccount.setFocusable(false);
            viewHolder.editPassword.setFocusable(false);
            viewHolder.editConfirm.setFocusable(false);

            if (obj.getAddedChildNum() == 0) {
                viewHolder.newChild.removeAllViews();
                for (int i = 0; i < obj.getmChildList().size(); i++) {
                    View newChildView = LayoutInflater.from(mContext).inflate(R.layout.new_child_in_parent_create_for_show, null);
                    TextView child = (TextView) newChildView.findViewById(R.id.text_child);
                    TextView name = (TextView) newChildView.findViewById(R.id.text_child_name);
                    name.setText(ApplicationContext.mSpinChildName.get(obj.getmChildList().get(i).getSpinnerSelect()));
                    viewHolder.newChild.addView(newChildView);
                }
                obj.setAddedChildNum(obj.getmChildList().size());
            }
            else
            {
                viewHolder.newChild.removeAllViews();
                for (int i = 0; i < obj.getAddedChildNum(); i++) {
                    View newChildView = LayoutInflater.from(mContext).inflate(R.layout.new_child_in_parent_create_for_show, null);
                    TextView child = (TextView) newChildView.findViewById(R.id.text_child);
                    TextView name = (TextView) newChildView.findViewById(R.id.text_child_name);
                    name.setText(ApplicationContext.mSpinChildName.get(obj.getmChildList().get(i).getSpinnerSelect()));
                    viewHolder.newChild.addView(newChildView);
                }
            }

        }
        return view;
    }

    private static class ViewHolder {
        public EditText editAccount;
        public EditText editPassword;
        public EditText editConfirm;
        public TextView parentTitle;
        public LinearLayout newChild;

    }
}

