package cc.nctu1210.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.nctu1210.childcare.R;

/**
 * Created by Yi-Ta_Chuang on 2016/4/18.
 */
public class TabItemView extends LinearLayout {
    private Context context;

    public TabItemView(Context mcontext,int i, int j) {
        super(mcontext);
        this.context = mcontext;
        initView(i,j);
    }

    private void initView(int i, int j) {
        View.inflate(this.context, R.layout.tab_view_indicator, this);
        setView(i,j);

    }

    private void setView(int i, int j) {
        TextView textView = (TextView)findViewById(R.id.label);
        if (i > 0) {
            Drawable drawable = this.context.getResources().getDrawable(i, null);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setBackground(drawable);
        }
        textView.setText(j);

    }


}
