package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GraphActivity extends AppCompatActivity {

    private BluetoothDevice mBluetoothDevice;
    private GattCallback mGattCallback;
    private final String TAG = "GraphActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        mBluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("BluetoothDevice");
        mGattCallback = new GattCallback();
        Log.d(TAG, "You've got mail! Device: " + mBluetoothDevice.toString() + " arrived in GraphActivity");


    }

    @Override
    protected void onStart() {
        super.onStart();

        mBluetoothDevice.connectGatt(getApplicationContext(), true, mGattCallback);
    }
}
