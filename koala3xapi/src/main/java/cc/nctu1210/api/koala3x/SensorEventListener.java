package cc.nctu1210.api.koala3x;

public interface SensorEventListener {
    void onSensorChange(SensorEvent e);
    void onConnectionStatusChange(boolean status);
    void onRSSIChange(String addr, float rssi);
    void onKoalaServiceStatusChanged(boolean status);
}