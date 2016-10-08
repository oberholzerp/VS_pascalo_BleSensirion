package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE_BT = 0;
    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        TextView body = (TextView) findViewById(R.id.body_text);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            body.setText(R.string.ble_enabled);
        }
        else {
            body.setText(R.string.ble_disabled);
            return;
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            TextView body = (TextView) findViewById(R.id.body_text);

            if (resultCode == RESULT_OK) {
                body.setText("Bluetooth is on");
            }
            else {
                body.setText("Bluetooth is off");
            }
        }
    }

    private void scanForDevices() {
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);

        //mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }


}
