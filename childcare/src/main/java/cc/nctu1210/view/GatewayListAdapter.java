package cc.nctu1210.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;
import cc.nctu1210.childcare.R;
/**
 * Created by User on 2016/4/26.
 */
public class GatewayListAdapter extends BaseAdapter{
    private String password;
    private String confirm;
    private final static String TAG = "GatewayListAdapter";
    /**
     * this is our own collection of data, can be anything we want it to be as long as we get the
     * abstract methods implemented using this data and work on this data (see getter) you should
     * be fine
     */
    private List<NewGatewayItem> mData;

    /**
     * some context can be useful for getting colors and other resources for layout
     */
    private Context mContext;

    /**
     * our ctor for this adapter, we'll accept all the things we need here
     *
     * @param mData
     */
    public GatewayListAdapter(final Context context, final List<NewGatewayItem> mData) {
        this.mData = mData;
        this.mContext = context;
    }

    public List<NewGatewayItem> getData() {
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
            view = inflater.inflate(R.layout.list_new_gateway_item, parent, false);
            Log.d(TAG, String.format("Get view %d", position));
            // we'll set up the ViewHolder
            viewHolder.gatewayTitle = (TextView) view.findViewById(R.id.gateway_title);
            viewHolder.editAccount  = (EditText) view.findViewById(R.id.edt_account);
            viewHolder.editPassword  = (EditText) view.findViewById(R.id.edt_password);
            viewHolder.editConfirm   = (EditText) view.findViewById(R.id.edt_confirm);
            viewHolder.editPlace    = (EditText) view.findViewById(R.id.edt_place);
            viewHolder.editNear    = (EditText) view.findViewById(R.id.edt_near);
            viewHolder.editFar    = (EditText) view.findViewById(R.id.edt_far);

            // store the holder with the view.
            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder instead
            viewHolder = (ViewHolder) view.getTag();
        }

        // object item based on the position
        final NewGatewayItem obj = mData.get(position);

        // assign values if the object is not null
        if (mData != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and other values
            String title = mContext.getString(R.string.gateway) + String.valueOf(position+1);
            viewHolder.gatewayTitle.setText(title);
            obj.setTitle(title);
            viewHolder.editAccount.setText("");
            viewHolder.editPassword.setText("");
            viewHolder.editConfirm.setText("");
            viewHolder.editPlace.setText("");
            viewHolder.editNear.setText("");
            viewHolder.editFar.setText("");

            if(!obj.getAccount().equals(""))
                viewHolder.editAccount.setText(obj.getAccount());
            if(!obj.getPassword().equals(""))
                viewHolder.editPassword.setText(obj.getPassword());
            if(!obj.getConfirm().equals(""))
                viewHolder.editConfirm.setText(obj.getConfirm());
            if(!obj.getPlace().equals(""))
                viewHolder.editPlace.setText(obj.getPlace());
            if(!obj.getNear().equals(""))
                viewHolder.editNear.setText(obj.getNear());
            if(!obj.getFar().equals(""))
                viewHolder.editFar.setText(obj.getFar());

            viewHolder.editAccount.setFocusable(false);
            viewHolder.editPassword.setFocusable(false);
            viewHolder.editConfirm.setFocusable(false);
            viewHolder.editPlace.setFocusable(false);
            viewHolder.editNear.setFocusable(false);
            viewHolder.editFar.setFocusable(false);
/*
            viewHolder.editAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if (!hasFocus) {
                        final EditText etxt = (EditText) v;
                        viewHolder.editAccount.setText(etxt.getText().toString());
                        obj.setAccount(etxt.getText().toString());
                    }
                }
            });
            viewHolder.editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if (!hasFocus) {
                        final EditText etxt = (EditText) v;
                        password = etxt.getText().toString();
                        viewHolder.editPassword.setText(password);
                        obj.setPassword(password);
                        if (!obj.getPassword().equals("") && !obj.getConfirm().equals("") && obj.getPassword().equals(obj.getConfirm()))
                            obj.setCheck(1);
                        else
                            obj.setCheck(0);
                    }
                }
            });

            viewHolder.editConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if (!hasFocus) {
                        final EditText etxt = (EditText) v;
                        confirm = etxt.getText().toString();
                        viewHolder.editConfirm.setText(confirm);
                        obj.setConfirm(confirm);
                        if (!obj.getPassword().equals("") && !obj.getConfirm().equals("") && obj.getPassword().equals(obj.getConfirm()))
                            obj.setCheck(1);
                        else
                            obj.setCheck(0);
                    }
                }
            });

            viewHolder.editPlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    if (!hasFocus) {
                        final EditText etxt = (EditText) v;
                        viewHolder.editPlace.setText(etxt.getText().toString());
                        obj.setPlace(etxt.getText().toString());
                    }
                }
            });
*/
        }
        return view;
    }

    private static class ViewHolder {
        public EditText editAccount;
        public EditText editPassword;
        public EditText editConfirm;
        public EditText editPlace;
        public EditText editNear;
        public EditText editFar;
        public TextView gatewayTitle;
    }
}
