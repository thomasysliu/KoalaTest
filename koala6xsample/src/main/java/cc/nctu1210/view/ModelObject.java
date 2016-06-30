package cc.nctu1210.view;

public class ModelObject {
	private String deviceName;
	private String macAddress;
	private String rssi;
	private String sampling;
	private String gX;
	private String gY;
	private String gZ;

	private String aX;
	private String aY;
	private String aZ;

	private String mX;
	private String mY;
	private String mZ;
	
	private String step;
	private String cal;
	private String distance;
	private String time;
	
	public ModelObject(String name, String addr, String rssi) {
		this.deviceName = name;
		this.macAddress = addr;
		this.rssi = rssi;
		this.sampling = "";
		this.gX = "";
		this.gY = "";
		this.gZ = "";
		this.aX = "";
		this.aY = "";
		this.aZ = "";
		this.mX = "";
		this.mY = "";
		this.mZ = "";
		this.step ="";
		this.cal="";
		this.distance="";
		this.time="";
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
	
	public void setSampling(float sampling) {
		this.sampling = String.valueOf(sampling);
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
		this.time = time;
	}
	
	
	public String getSampling() {
		return this.sampling;
	}
	
	
	
	public void setAccelerometerData(double gX, double gY, double gZ) {
		this.gX = String.valueOf(gX);
		this.gY = String.valueOf(gY);
		this.gZ = String.valueOf(gZ);
	}

	public void setGyroscopeData(double aX, double aY, double aZ) {
		this.aX = String.valueOf(aX);
		this.aY = String.valueOf(aY);
		this.aZ = String.valueOf(aZ);
	}

	public void setmagnetometerData(double mX, double mY, double mZ) {
		this.mX = String.valueOf(mX);
		this.mY = String.valueOf(mY);
		this.mZ = String.valueOf(mZ);
	}
	
	public void setPedometerData(float step, float cal, float distance, float time) {
		this.step = String.valueOf(step);
		this.cal = String.valueOf(cal);
		this.distance = String.valueOf(distance);
		this.time = String.valueOf(time);
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
	
	public String getGx() {
		return this.gX;
	}
	
	public String getGy() {
		return this.gY;
	}
	
	public String getGz() {
		return this.gZ;
	}

	public String getAx() {
		return this.aX;
	}

	public String getAy() {
		return this.aY;
	}

	public String getAz() {
		return this.aZ;
	}

	public String getMx() {
		return this.mX;
	}

	public String getMy() {
		return this.mY;
	}

	public String getMz() {
		return this.mZ;
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
	
}
