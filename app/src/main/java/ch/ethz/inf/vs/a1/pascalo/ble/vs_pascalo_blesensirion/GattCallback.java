package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_HUMIDITY_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_HUMIDITY_SERVICE;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_TEMPERATURE_SERVICE;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_TEMPERATURE_CHARACTERISTIC;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.NOTIFICATION_DESCRIPTOR_UUID;

public class GattCallback extends BluetoothGattCallback {
    private final String TAG = "GattCallback";
    private Context mContext;
    private LineGraphSeries<DataPoint> tempSeries;
    private LineGraphSeries<DataPoint> humidSeries;
    private long timeZero;
    private BluetoothGattDescriptor mTemperatureDescriptorWriteAttempt;
    private BluetoothGattDescriptor mHumidityDescriptorWriteAttempt;


    public BluetoothGattService mTemperatureService;
    public BluetoothGattService mHumidityService;

    public GattCallback (Context context, LineGraphSeries<DataPoint> tempSeries, LineGraphSeries<DataPoint> humidSeries) {
        super();
        mContext = context;
        this.tempSeries = tempSeries;
        this.humidSeries = humidSeries;
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
            mHumidityService = gatt.getService(UUID_HUMIDITY_SERVICE);

            // Set up notifications
            BluetoothGattCharacteristic tempChar =  mTemperatureService.getCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC);
            gatt.setCharacteristicNotification(tempChar, true);
            BluetoothGattDescriptor tempDesc = tempChar.getDescriptor(NOTIFICATION_DESCRIPTOR_UUID);
            tempDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mTemperatureDescriptorWriteAttempt = tempDesc;

            tempChar = mHumidityService.getCharacteristic(UUID_HUMIDITY_CHARACTERISTIC);
            gatt.setCharacteristicNotification(tempChar, true);
            tempDesc = tempChar.getDescriptor(NOTIFICATION_DESCRIPTOR_UUID);
            tempDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mHumidityDescriptorWriteAttempt = tempDesc;

            // In the callback of this one the other descriptor shall also be written
            gatt.writeDescriptor(mTemperatureDescriptorWriteAttempt);


            /*BluetoothGattCharacteristic oldChara =  mTemperatureService.getCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC);
            BluetoothGattCharacteristic newChara = new BluetoothGattCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC, oldChara.getProperties(), oldChara.getPermissions() |  BluetoothGattCharacteristic.PERMISSION_WRITE );
            mTemperatureService.addCharacteristic(newChara);

            byte[] one_byte = {(byte)1};
            newChara.setValue(one_byte);
            gatt.writeCharacteristic(newChara);*/



        } else {

            Toast.makeText(mContext, "Service discovery failed.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Service discovery failed in the onServicesDiscovered callback, code: " + String.valueOf(status));

        }
    }
/*
    @Override
    public void onCharacteristicWrite( BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        Log.d(TAG, "onCharacteristicWrite reports status of: " + String.valueOf(status));

        byte[] value = characteristic.getValue();

        if (value[0] == (byte)1) {
            Log.d(TAG, "Writing the one-byte was successful :)");
        } else {
            Log.d(TAG, "Writing the one-byte was unsuccessful :(");
        }

        // Set up notifications
        BluetoothGattCharacteristic tempChar =  mTemperatureService.getCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC);
        gatt.setCharacteristicNotification(tempChar, true);
        BluetoothGattDescriptor descriptor = tempChar.getDescriptor(NOTIFICATION_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);

    }
*/
    @Override
    public void onDescriptorWrite (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        if (BluetoothGatt.GATT_FAILURE == status) {
            Log.d(TAG, "onDescriptorWrite reports GATT_FAILURE");
            return;
        }

        if (mTemperatureDescriptorWriteAttempt == descriptor) {
            Log.d(TAG, "onDescriptorWrite reports temperature descriptor as written, writing humidity descriptor...");
            gatt.writeDescriptor(mHumidityDescriptorWriteAttempt);
            return;
        }

        if (mHumidityDescriptorWriteAttempt == descriptor) {
            Log.d(TAG, "onDescriptorWrite reports humidity descriptor as written.");

        }

    }

    @Override
    // Characteristic notification
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "Characteristic has been changed and the notification received.");
        Log.d(TAG, "Value converted: " + convertRawValue(characteristic.getValue()));

        if(timeZero == 0) {
            timeZero = SystemClock.elapsedRealtime()/1000L;
        }
        long passedTime = SystemClock.elapsedRealtime()/1000L - timeZero;
        float dataPoint = convertRawValue(characteristic.getValue());

        if (characteristic.getUuid().equals(UUID_TEMPERATURE_CHARACTERISTIC)) {
            tempSeries.appendData(new DataPoint(passedTime, dataPoint), true, 150);
        } else if (characteristic.getUuid().equals(UUID_HUMIDITY_CHARACTERISTIC)) {
            humidSeries.appendData(new DataPoint(passedTime, dataPoint), true, 150);
        } else {
            Log.d(TAG, "A strange characteristic came back, it's not humidity nor temperature. It's: " + characteristic.getUuid() .toString());
        }

    }


    private float convertRawValue(byte[] raw) {
        ByteBuffer wrapper = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        return wrapper.getFloat();
    }

}
