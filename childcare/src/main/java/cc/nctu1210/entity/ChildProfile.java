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
    private String mGatewayId;
    private String mPlace;
    private String rssi;
    private String mFlag; //missing flag; 1 is missing. 0 is not missing.
    private String cid;

    public ChildProfile(String name, String deviceAddress) {
        this.mName = name;
        this.mDeviceAddress = deviceAddress;
        this.mStatus = "miss";
        this.mPhotoFileName = "";
    }

    public ChildProfile(String name, String deviceAddress, String photo) {
        this.mName = name;
        this.mDeviceAddress = deviceAddress;
        this.mStatus = "miss";
        this.mPhotoFileName = photo;
    }

    public ChildProfile(String name, String PhotoURL, String deviceAddress, String gid, String place, String rssi, String status, String flag) {
        this.mName = name;
        this.mPhotoFileName = PhotoURL;
        this.mDeviceAddress = deviceAddress;
        this.mGatewayId = gid;
        this.mPlace = place;
        this.rssi = rssi;
        this.mStatus = status;
        this.mFlag = flag;
    }

    public ChildProfile(String cid) {
        this.cid = cid;
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

    public void setCid(String cid){this.cid = cid;}

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public void setGatewayId(String gatewayId) {
        this.mGatewayId = gatewayId;
    }

    public void setFlag(String flag) {
        this.mFlag =flag;
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

    public String getGatewayId()
    {return this.mGatewayId; }

    public String getPlace()
    {return this.mPlace;}

    public String getRssi()
    {return this.rssi;}

    public String getFlag()
    {return  this.mFlag;}

    public String getCid()
    {return this.cid;}

}
