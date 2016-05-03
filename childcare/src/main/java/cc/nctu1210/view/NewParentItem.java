package cc.nctu1210.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016/4/27.
 */
public class NewParentItem {
    private String pid = "";
    private String title;
    private String account;
    private String password;
    private String confirm;
    private List<ParentCreateChildItem> mChildList = new ArrayList<ParentCreateChildItem>();
    private int check = 0;
    private int addedChildNum = 0;

    public NewParentItem(String account, String password, String confirm) {
        this.account = account;
        this.password = password;
        this.confirm = confirm;
    }

    public NewParentItem(String pid) {
       this.pid = pid;
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
    public void setCheck(int check) {
        this.check = check;
    }
    public void setAddedChildNum(int addedChildNum) {
        this.addedChildNum = addedChildNum;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }

    public void addChild(ParentCreateChildItem child) {
        this.mChildList.add(child);
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
    public List<ParentCreateChildItem> getmChildList()
    {
        return this.mChildList;
    }
    public int getCheck(){return this.check;}
    public int getAddedChildNum(){return this.addedChildNum;}
    public String getPid(){return this.pid;}
    public void clearmChildList()
    {
        mChildList.clear();
    }
}

