package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class OurScanCallback extends ScanCallback {

    private MainActivity mMainActivity;
    private static final String TAG = "OurScanCallback";
    private Set<BluetoothDevice> mDevices;

    public OurScanCallback(MainActivity main) {
        mMainActivity = main;
        mDevices = new HashSet<BluetoothDevice>();
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
        Log.d(TAG, String.valueOf(callbackType) + " whereas it should be " + String.valueOf(ScanSettings.CALLBACK_TYPE_ALL_MATCHES));

        if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {

            // Call into main to add device to listview?
            BluetoothDevice device = result.getDevice();
            if (null != device) {
                // Only hand the device over to main if it's a new one, implementation using a set
                int t = mDevices.size();
                mDevices.add(device);
                if (mDevices.size() > t) mMainActivity.addDeviceToListview(device);
            }
        }
    }
}
