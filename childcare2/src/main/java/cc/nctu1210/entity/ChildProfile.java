package cc.nctu1210.entity;

import java.io.File;

/**
 * Created by Yi-Ta_Chuang on 2016/4/17.
 */
public class ChildProfile {
    private int mPosition;
    private String mName;
    private String mDeviceAddress;
    private String mStatus;
    private String mPhotoFileName;

    public ChildProfile(String name, String deviceAddress) {
        this.mName = name;
        this.mDeviceAddress = deviceAddress;
        this.mStatus = "miss";
        this.mPhotoFileName = "";
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.mDeviceAddress = deviceAddress;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public void setPhotoName(String fileName) {
        this.mPhotoFileName = fileName;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public String getName() {
        return this.mName;
    }

    public String getDeviceAddress() {
        return this.mDeviceAddress;
    }

    public String getPhotoName() {
        return this.mPhotoFileName;
    }

    public String getStatus() {
        return this.mStatus;
    }

}
