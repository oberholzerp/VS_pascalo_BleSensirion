package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.util.Log;
import android.widget.ArrayAdapter;
import java.util.LinkedList;
import java.util.List;
import android.bluetooth.le.ScanSettings;

import ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS;

public class MainActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int MY_PERMISSIONS_REQUEST = 42;

    // These request finals serve to distinguish requests, they must not both be the same
    private final int REQUEST_ENABLE_BT = 5;
    private final int REQUEST_ENABLE_LS = 7;

    private static final long SCAN_PERIOD = 30000;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothLeScanner mBluetoothLeScanner;
    private List<ScanFilter> scanFilters;

    private LocationProvider mLocationProvider;
    private ArrayAdapter<BluetoothDevice> mDevices;

    private OurScanCallback mScanCallback;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            //something went really wrong, our manifest states that BLE is a requirement
            return;
        }

        mHandler = new Handler();

        mDevices = new ArrayAdapter<BluetoothDevice>(getApplicationContext(),R.layout.activity_main, R.id.devicesListView) /*{

            //In this override we can make the Listview prettier
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {


                View itemView = null;

                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) parent.getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    itemView = inflater.inflate(R.layout.table_row, null);
                } else {
                    itemView = convertView;
                }

                // play with itemView

                return itemView;


            }
        }*/;

        scanFilters = new LinkedList<ScanFilter>();
        scanFilters.add(new ScanFilter.Builder().setDeviceName("Smart Humigadget").build());

        mScanCallback = new OurScanCallback(this);

        // Check for and if neccessary request some permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {


            // This request is async, it returns in onRequestPermissionResult, so watch the control flow
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    MY_PERMISSIONS_REQUEST);

        } else {
            Log.d(TAG, "Already have permissions");

            // unify control flow
            doFurtherSetup();
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED
                || grantResults[1] == PackageManager.PERMISSION_DENIED
                || grantResults[2] == PackageManager.PERMISSION_DENIED
                || grantResults[3] == PackageManager.PERMISSION_DENIED) {

            Log.d(TAG, "Can't do my job without permissions");
            return;

        } else {

            Log.d(TAG, "Thanks for permissions you bastard");

            // unify control flow
            doFurtherSetup();
        }
    }

    // This method is basically just needed to unify control flow, from where we had permissions
    // already and from where we were granted those we didn't have. All further activity continues here.
    private void doFurtherSetup() {

        // call enable bluetooth
        enableBT();

        //call enable location service
        enableLS();

        scanForDevices();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Bluetooth is on");
                }
                else {
                    Log.d(TAG, "Bluetooth is off");
                }
                break;

            case REQUEST_ENABLE_LS:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Location service is on");
                }
                else {
                    Log.d(TAG, "Location service is off");
                }
                break;

        }
    }

    private void scanForDevices() {
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //mScanning = false;
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                Log.d(TAG, "Scan is stopping");
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, SCAN_PERIOD);

        //mScanning = true;
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
        Log.d(TAG, "Scan is starting");
        mBluetoothLeScanner.startScan(scanFilters, new ScanSettings.Builder().build(), mScanCallback);
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
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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
            Intent enableLsIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableLsIntent, REQUEST_ENABLE_LS);
        }
    }

    public void addDeviceToListview (BluetoothDevice device) {
        mDevices.add(device);
    }

}
