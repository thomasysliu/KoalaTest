package cc.nctu1210.api.koala6x;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */

public class Koala6xGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();

    public static String KOALA_MOTION_SERVICE_UUID                      = "eb371600-347c-fe94-1600-8295a1e42b09";
    public static String KOALA_MOTION_MEASUREMENT_CHARACTERISTIC_UUID   = "eb371601-347c-fe94-1600-8295a1e42b09";
    public static String KOALA_MOTION_RATE_CHANGE_CHARACTERISTIC_UUID  = "eb371603-347c-fe94-1600-8295a1e42b09";
    public static String KOALA_MOTION_ACC_FSR_CHANGE_CHARACTERISTIC_UUID  = "eb371604-347c-fe94-1600-8295a1e42b09";
    public static String KOALA_MOTION_GYRO_FSR_CHANGE_CHARACTERISTIC_UUID  = "eb371605-347c-fe94-1600-8295a1e42b09";

    static {
        // Koala Raw Data (Motion) Services.
        attributes.put(KOALA_MOTION_SERVICE_UUID,
                "Motion Sensor Raw Data Service");
        // Koala Raw Data (Motion)  Characteristics.
        attributes.put(KOALA_MOTION_MEASUREMENT_CHARACTERISTIC_UUID, "Motion Data Notification");
        attributes.put(KOALA_MOTION_RATE_CHANGE_CHARACTERISTIC_UUID, "Motion Rate Configuration");
        attributes.put(KOALA_MOTION_ACC_FSR_CHANGE_CHARACTERISTIC_UUID, "Motion ACC FSR Configuration");
        attributes.put(KOALA_MOTION_GYRO_FSR_CHANGE_CHARACTERISTIC_UUID, "Motion GYRO FSR Configuration");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    // For 9x

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_MOTION_SERVICE_UUID = "00001600-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_MOTION_MEASUREMENT_CHARACTERISTIC_UUID = "00001601-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_MOTION_DMP_MEASUREMENT_CHARACTERISTIC_UUID = "00001602-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_BATTERY_SERVICE_UUID = "0000180f-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_BATTERY_UUID = "00002a19-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_TX_POWER_SERVICE_UUID = "00001804-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_TX_POWER_UUID = "00002a07-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_STORAGE_SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_STORAGE_CMD_CHARACTERISTIC_UUID = "0000fff6-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_STORAGE_READ_CHARACTERISTIC_UUID = "0000fff7-0000-1000-8000-00805f9b34fb";
    public static String NAXSEN_STORAGE_READ_CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb";

}