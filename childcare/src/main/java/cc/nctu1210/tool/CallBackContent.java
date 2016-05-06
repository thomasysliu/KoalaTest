package cc.nctu1210.tool;

import java.util.ArrayList;
import java.util.List;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.view.NewGatewayItem;
import cc.nctu1210.view.NewParentItem;
import cc.nctu1210.view.ParentCreateChildItem;

/**
 * Created by admin on 2016/3/25.
 */
public class CallBackContent {
    int mid;
    String gids;
    String cids;
    String pids;
    String success_msg;
    ChildProfile child;
    NewParentItem parent;
    List<NewGatewayItem> show_gateway = new ArrayList<NewGatewayItem>();
    List<NewParentItem> show_parent = new ArrayList<NewParentItem>();
    List<ChildProfile> show_children = new ArrayList<ChildProfile>();
    String mGid;
    String mPid;
    String mPlace;
    public CallBackContent(){
    }

    public int getMid()
    {return this.mid;}

    public String getGids()
    {return this.gids;}

    public String getCids()
    {return this.cids;}

    public String getPids()
    {return this.pids;}

    public ChildProfile getChild()
    {return this.child;}

    public NewParentItem getParent()
    {return this.parent;}

    public String getmGid()
    {return this.mGid;}

    public String getmPid()
    {return this.mPid;}

    public List<NewGatewayItem> getShow_gateway()
    {return this.show_gateway;}

    public  List<NewParentItem> getShow_parent()
    {return this.show_parent;}

    public String getPlace() {
        return this.mPlace;
    }

    public List<ChildProfile> getShow_children() {
        return this.show_children;
    }

}
