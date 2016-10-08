package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import android.bluetooth.le.ScanFilter.Builder;
import android.bluetooth.le.ScanSettings;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS;

public class MainActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int MY_PERMISSIONS_REQUEST = 42;


    private final int REQUEST_ENABLE_BT = 0;
    private final int REQUEST_ENABLE_LS = 0;
    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothLeScanner mBluetoothLeScanner;
    private List<ScanFilter> scanFilters;

    private LocationProvider mLocationProvider;

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


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    MY_PERMISSIONS_REQUEST);
        } else {

            Toast.makeText(getApplicationContext(), "Already have permissions", Toast.LENGTH_LONG).show();

            // call enable bluetooth
            enableBT();

        }

        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder()
                .setDeviceName("Smart Humigadget");
        scanFilters.add(scanFilterBuilder.build());

    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED
                || grantResults[1] == PackageManager.PERMISSION_DENIED
                || grantResults[2] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Give me permissions you bastard", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Thanks for permissions you bastard", Toast.LENGTH_LONG).show();

            // call enable bluetooth
            enableBT();
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
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                //mBluetoothLeScanner.stopScan(callback);
            }
        }, SCAN_PERIOD);

        //mScanning = true;
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
        //mBluetoothLeScanner.startScan(scanFilters, ScanSettings.CALLBACK_TYPE_ALL_MATCHES, callback);
    }

    private void enableBT() {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void enableLS() {
        //initializes Bluetooth adapter
        final LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationProvider = locationManager.getProvider("");

        //ensures location service is available on the device and it is enabled
        //if not, displays a dialog requesting user permission to enable location service
        if (mLocationProvider == null) {
            Intent enableLsIntent = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(enableLsIntent, REQUEST_ENABLE_LS);
        }
    }


}
