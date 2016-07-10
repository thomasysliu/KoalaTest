package cc.nctu1210.view;

public class ModelObject {
	private String deviceName;
	private String macAddress;
	private String rssi;
    private String status;
	private String step;
	private String cal;
	private String distance;
    private String time;
	private String sleepState;
	private String sleepTime;
	
	public ModelObject(String name, String addr, String rssi) {
		this.deviceName = name;
		this.macAddress = addr;
		this.rssi = rssi;
        this.status = "";
		this.step ="";
		this.cal="";
		this.distance="";
        this.time = "";
        this.sleepState="";
		this.sleepTime="";

	}
	
	public void setName(String name) {
		this.deviceName = name;
	}
	
	public void setAddress(String addr) {
		this.macAddress = addr;
	}
	
	public void setRssi(float rssi) {
		this.rssi = String.valueOf(rssi);
	}

    public void setStep(String step) {
        this.step = step;
    }
	
	public void setCal(String cal) {
		this.cal = cal;
	}
	
	public void setDistance(String distance) {
		this.distance = distance;
	}

    public void setTime(String time) {
        this.time =time;
    }

	public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
	}

    public void setSleepState(String sleepState) {
        this.sleepState = sleepState;
    }

    public void setStatus(String status) {
        this.status = status;
    }
	
	public void setPedometerData(float step, float cal, float distance, float time) {
		this.step = String.valueOf(step);
		this.cal = String.valueOf(cal);
		this.distance = String.valueOf(distance);
		this.time = String.valueOf(time);
	}

    public void setSleepData(int sleepState, int sleepTime) {
        this.sleepState = String.valueOf(sleepState);
        this.sleepTime = String.valueOf(sleepTime);
    }

	public String getName() {
		return this.deviceName;
	}
	
	public String getAddress() {
		return this.macAddress;
	}
	
	public String getRssi() {
		return this.rssi;
	}
	
	public String getStep() {
		return this.step;
	}
	
	public String getCal() {
		return this.cal;
	}
	
	public String getDistance() {
		return this.distance;
	}
	
	public String getTime() {
		return this.time;
	}

    public String getSleepTime() {
        return this.sleepTime;
    }

    public String getSleepState() {
        return this.sleepState;
    }

    public String getStatus() {
        return this.status;
    }
	
}
