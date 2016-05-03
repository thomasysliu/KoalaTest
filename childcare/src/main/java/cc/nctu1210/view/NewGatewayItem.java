package cc.nctu1210.view;

/**
 * Created by User on 2016/4/26.
 */
public class NewGatewayItem {
    private String gid = "";
    private String title;
    private String account;
    private String password;
    private String confirm;
    private String place;
    private String near;
    private String far;
    private int check = 0;


    public NewGatewayItem(String account, String password, String confirm ,String place, String near, String far) {
        this.account = account;
        this.password = password;
        this.confirm = confirm;
        this.place = place;
        this.near = near;
        this.far = far;
    }

    public NewGatewayItem(String id, String place, String  near ,String far) {
        this.gid = id;
        this.place = place;
        this.near = near;
        this.far = far;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public void setNear(String near) {
        this.near = near;
    }
    public void setFar(String far) {
        this.far = far;
    }
    public void setCheck(int check) {
        this.check = check;
    }
    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getTitle()
    {
        return this.title;
    }
    public String getAccount()
    {
        return this.account;
    }
    public String getPassword()
    {
        return this.password;
    }
    public String getConfirm()
    {
        return this.confirm;
    }
    public String getPlace()
    {
        return this.place;
    }
    public String getNear()
    {
        return this.near;
    }
    public String getFar()
    {
        return this.far;
    }
    public int getCheck(){return this.check;}
    public String getGid(){return this.gid;}
}
