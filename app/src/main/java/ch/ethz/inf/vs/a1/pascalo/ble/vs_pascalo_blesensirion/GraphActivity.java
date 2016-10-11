package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphActivity extends AppCompatActivity {

    private BluetoothDevice mBluetoothDevice;
    private final String TAG = "GraphActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        mBluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("BluetoothDevice");

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
}
