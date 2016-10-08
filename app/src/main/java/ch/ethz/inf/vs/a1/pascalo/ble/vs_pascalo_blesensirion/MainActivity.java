package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView body = (TextView) findViewById(R.id.body_text);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            body.setText(R.string.ble_enabled);
        }
        else {
            body.setText(R.string.ble_disabled);
        }

    }
}
