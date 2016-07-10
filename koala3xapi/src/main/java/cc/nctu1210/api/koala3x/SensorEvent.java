package cc.nctu1210.api.koala3x;

import android.bluetooth.BluetoothDevice;

public class SensorEvent{
    public static final int TYPE_STATUS = 0;
    public static final int TYPE_PEDOMETER = 1 << 0;
    public static final int TYPE_SLEEP_MONITOR = 1 << 1;

    public final int type;
    public final BluetoothDevice device;
    /**
     *  values, sleepValues and modeValue exist depended on the type of the event and should not be modified;
     */
    public float values[];
    /**
     *  values, sleepValues and modeValue exist depended on the type of the event and should not be modified;
     */
    public int sleepValues[];
    /**
     *  values, sleepValues and modeValue exist depended on the type of the event and should not be modified;
     */
    public int modeValue;

    protected SensorEvent(final int type, BluetoothDevice device, int valueSize) {
        this.type = type;
        this.device = device;
        switch (type) {
            case TYPE_STATUS:
                break;
            case TYPE_PEDOMETER:
                this.values = new float[valueSize];
                break;
            case TYPE_SLEEP_MONITOR:
                this.sleepValues = new int[valueSize];
                break;
        }
    }

    protected void setValues(float values[]) {
        for (int i=0, size=values.length; i<size; i++) {
            this.values[i] = values[i];
        }
    }

    protected void setValues(int values[]) {
        for (int i=0, size=values.length; i<size; i++) {
            this.sleepValues[i] = values[i];
        }
    }

    protected void setValue(int value) {
        this.modeValue = value;
    }
}