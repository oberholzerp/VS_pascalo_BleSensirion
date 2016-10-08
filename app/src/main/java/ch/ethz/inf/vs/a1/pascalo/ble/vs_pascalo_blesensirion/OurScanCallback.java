package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;

import java.util.List;

import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.R.id.add;


class OurScanCallback extends ScanCallback {

    @Override
    public void onBatchScanResults (List<ScanResult> results){
        //Not used, because we use CALLBACK_TYPE_ALL_MATCHES
    }

    @Override
    public void onScanFailed (int errorCode) {
        //TODO: Handle scan fails
    }

    @Override
    public void onScanResult (int callbackType, ScanResult result) {
        if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {

            // Call into main to add device to listview?
            MainActivity.mDevices.add(result.getDevice());

        }


    }
}
