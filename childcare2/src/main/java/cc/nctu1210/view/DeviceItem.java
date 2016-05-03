package cc.nctu1210.view;

import java.io.File;

public class DeviceItem {
    public String child_name;
    public String device_addr;
    public String photoName;
	
    public DeviceItem(String addr) {
        this.device_addr = addr;
        this.child_name = "";
        this.photoName = null;
	}
}
