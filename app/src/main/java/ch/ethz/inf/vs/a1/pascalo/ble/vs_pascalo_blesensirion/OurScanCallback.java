package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;
import java.util.List;

class OurScanCallback extends ScanCallback {

    private MainActivity mMainActivity;
    private static final String TAG = "OurScanCallback";

    public OurScanCallback(MainActivity main) {
        mMainActivity = main;
    }

    @Override
    public void onBatchScanResults (List<ScanResult> results){
        //Not used, because we use CALLBACK_TYPE_ALL_MATCHES
        Log.d(TAG, "onBatchScanResults was called");
    }

    @Override
    public void onScanFailed (int errorCode) {
        //TODO: Handle scan fails
    }

    @Override
    public void onScanResult (int callbackType, ScanResult result) {

        Log.d(TAG, "onScanResult was called");
        Log.d(TAG, result.getDevice().toString());

        if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {

            // Call into main to add device to listview?
            BluetoothDevice device = result.getDevice();
            if (null != device) {
                mMainActivity.addDeviceToListview(device);
            }
        }
    }
}
