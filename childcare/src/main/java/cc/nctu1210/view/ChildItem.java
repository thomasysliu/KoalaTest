package cc.nctu1210.view;

import java.io.File;

/**
 * Created by Yi-Ta_Chuang on 2016/4/17.
 */
public class ChildItem {
    public String name;
    public String status;
    public String photoName;
    public String place;
    public String flag;
    public String cid;
    public String rssi;

    //for demo
    public int control_click;      //near = 1, mediate  = 2, far = 3
    public int control_rssi;
    //

    public ChildItem(String name, String status) {
        this.name = name;
        this.status = status;
        this.photoName = "";
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    public String getPlace()
    {
        return this.place;
    }

    public String getName()
    {return this.name;}

}
