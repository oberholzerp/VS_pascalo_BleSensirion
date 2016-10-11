package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_HUMIDITY_SERVICE;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_TEMPERATURE_SERVICE;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_TEMPERATURE_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_HUMIDITY_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.NOTIFICATION_DESCRIPTOR_UUID;

public class GattCallback extends BluetoothGattCallback {
    private final String TAG = "GattCallback";
    private Context mContext;
    private BluetoothGattService mTemperatureService;
    private BluetoothGattService mHumidityService;

    public GattCallback (Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onConnectionStateChange (BluetoothGatt gatt,
                                  int status,
                                  int newState) {

        Log.d(TAG, "Connection state changed, we are now in state: " + String.valueOf(newState));

        if (newState == BluetoothProfile.STATE_CONNECTED) {

            if (!gatt.discoverServices()) {

                Toast.makeText(mContext, "Service discovery failed.", Toast.LENGTH_LONG).show();

            }

        }

    }

    @Override
    public void onServicesDiscovered (BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {

            mTemperatureService = gatt.getService(UUID_TEMPERATURE_SERVICE);
            //mHumidityService = gatt.getService(UUID_HUMIDITY_SERVICE);

            Log.d(TAG, "Whe have the following number of characteristics in the temperature service: " + String.valueOf(mTemperatureService.getCharacteristics().size()));

            BluetoothGattCharacteristic oldChara =  mTemperatureService.getCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC);
            BluetoothGattCharacteristic newChara = new BluetoothGattCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC, oldChara.getProperties(), oldChara.getPermissions() |  BluetoothGattCharacteristic.PERMISSION_WRITE );
            mTemperatureService.addCharacteristic(newChara);

            byte[] one_byte = {1};
            newChara.setValue(one_byte);
            gatt.writeCharacteristic(newChara);



        } else {

            Toast.makeText(mContext, "Service discovery failed.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Service discovery failed in the onServicesDiscovered callback, code: " + String.valueOf(status));

        }
    }

    @Override
    public void onCharacterristicWrite( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        Log.d(TAG, "onCharacterristicWrite reports status of: " + String.valueOf(status));

        //byte[] value = newChara.getValue();
        //gatt.readCharacteristic()

        Log.d(TAG, "First raw value to be read has a length in Byte of: " + String.valueOf(value.length));
        Log.d(TAG, "First raw value to be read has is: " + Arrays.toString(value));
        Log.d(TAG, "First value to be read is: " + String.valueOf(convertRawValue(value)));

    }

    private float convertRawValue(byte[] raw) {
        ByteBuffer wrapper = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getFloat();
    }

}
