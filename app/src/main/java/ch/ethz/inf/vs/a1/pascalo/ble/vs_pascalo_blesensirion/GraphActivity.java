package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.NOTIFICATION_DESCRIPTOR_UUID;
import static ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion.SensirionSHT31UUIDS.UUID_TEMPERATURE_CHARACTERISTIC;

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

        if (null != savedInstanceState) {
            Log.d(TAG, "Bundle contains: " + savedInstanceState.toString());
        }

        //initialising graph
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(false);
        graph.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.axis_title));

        LineGraphSeries<DataPoint> tempSeries = new LineGraphSeries();
        tempSeries.setColor(Color.MAGENTA);
        LineGraphSeries<DataPoint> humidSeries = new LineGraphSeries();
        humidSeries.setColor(Color.BLUE);

        graph.addSeries(tempSeries);
        graph.getSecondScale().addSeries(humidSeries);

        graph.getViewport().setMinX(-15.0);
        graph.getViewport().setMaxX(0.0);

        graph.getViewport().setMinY(10.0);
        graph.getViewport().setMaxY(30.0);

        graph.getSecondScale().setMinY(0.0);
        graph.getSecondScale().setMaxY(100.0);


        mDisconnectButton = (Button) findViewById(R.id.disconnect_button);
        mDisconnectButton.setOnClickListener(this);

        Intent intent = getIntent();
        mBluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("BluetoothDevice");
        mGattCallback = new GattCallback(getApplicationContext(), tempSeries, humidSeries);
        Log.d(TAG, "You've got mail! Device: " + mBluetoothDevice.toString() + " arrived in GraphActivity");

        TextView text = (TextView) findViewById(R.id.device_name);
        text.setText(mBluetoothDevice.getName());




    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBluetoothGatt == null) {
            Log.d(TAG, "mBluetoothGatt is null in onStart creating a new one");
            mBluetoothGatt = mBluetoothDevice.connectGatt(getApplicationContext(), false, mGattCallback);
        } else {
            Log.d(TAG, "mBluetoothGatt is non-null in onStart connecting again");
            mBluetoothGatt.connect();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "mBluetoothGatt is being disconnected in onStop");


        if (null != mGattCallback.mTemperatureService) {
            BluetoothGattCharacteristic tempChar =  mGattCallback.mTemperatureService.getCharacteristic(UUID_TEMPERATURE_CHARACTERISTIC);
            BluetoothGattDescriptor descriptor = tempChar.getDescriptor(NOTIFICATION_DESCRIPTOR_UUID);
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

            mBluetoothGatt.writeDescriptor(descriptor);
            mBluetoothGatt.setCharacteristicNotification(tempChar, false);
        }


        mBluetoothGatt.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy()  {
        Log.d(TAG, "mBluetoothGatt is being closed in onDestroy");
        mBluetoothGatt.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // Don't call mBluetoothGatt close or disconnect here, because this will
        // happen in onStop and on Destroy anyway
        finish();
    }
}
