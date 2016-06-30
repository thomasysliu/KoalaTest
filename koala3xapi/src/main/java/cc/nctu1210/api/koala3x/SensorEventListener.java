package cc.nctu1210.api.koala3x;

/**
 *  SensorEventListener
 *
 */
public interface SensorEventListener {
    /**
     *  onSensorChange(SensorEvent e) : a callback function triggered when a SensorEvent occurs
     * @param e The sensor event.
     */
    void onSensorChange(SensorEvent e);

    /**
     * onConnectionStatusChange(boolean status): a callback function triggered when the connection status to Koala is changed
     * @param status Conntection status.
     */
    void onConnectionStatusChange(boolean status);

    /**
     *  onPedometerServiceChange(int servicType): a callback function triggered when the tracking service changes
     * @param serviceType  1: step counter service; 2: sleep monitoring service
     */
    void onPedometerServiceChange(int serviceType);

    /**
     *  onRSSIChange(String addr, float rssi): a callback function triggered when the RSSI values changes
     * @param addr The device MAC address
     * @param rssi The RSSI values in dB.
     */
    void onRSSIChange(String addr, float rssi);
    void onKoalaServiceStatusChanged(boolean status);
}