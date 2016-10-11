package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphActivity extends AppCompatActivity implements Button.OnClickListener {

    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private GattCallback mGattCallback;
    private final String TAG = "GraphActivity";
    private Button mDisconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mDisconnectButton = (Button) findViewById(R.id.disconnect_button);
        mDisconnectButton.setOnClickListener(this);

        Intent intent = getIntent();
        mBluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("BluetoothDevice");
        mGattCallback = new GattCallback(getApplicationContext());
        Log.d(TAG, "You've got mail! Device: " + mBluetoothDevice.toString() + " arrived in GraphActivity");

        TextView text = (TextView) findViewById(R.id.device_name);
        text.setText(mBluetoothDevice.getName());

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(false);
        graph.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.axis_title));

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBluetoothGatt == null) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(getApplicationContext(), true, mGattCallback);
        } else {
            mBluetoothGatt.connect();
        }
    }


    @Override
    public void onClick(View v) {
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        finish();
    }
}
