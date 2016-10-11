package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.util.Log;

public class GattCallback extends BluetoothGattCallback {
    private final String TAG = "GattCallback";

    @Override
    public void onConnectionStateChange (BluetoothGatt gatt,
                                  int status,
                                  int newState) {

        Log.d(TAG, "Connection state changed, we are now in state: " + String.valueOf(newState));

    }

}
