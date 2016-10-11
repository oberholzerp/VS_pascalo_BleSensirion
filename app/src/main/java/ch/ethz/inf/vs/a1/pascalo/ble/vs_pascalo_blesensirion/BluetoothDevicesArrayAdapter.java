package ch.ethz.inf.vs.a1.pascalo.ble.vs_pascalo_blesensirion;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;

public class BluetoothDevicesArrayAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater mInflater;

    private final Context mContext;

    private final int mResource;

    public BluetoothDevicesArrayAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final TextView text;

        //no idea what this is about, took it from the superclass
        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        //that if statement in the superclass is always true with this constructor
        text = (TextView) view;

        //do a getName instead of a toString here, that's the only real difference
        BluetoothDevice item = (BluetoothDevice) getItem(position);

        // changing text colour because it was white on a SGS7
        text.setTextColor(Color.BLACK);

        text.setText(item.getName() + "\n" + item.getAddress());

        return view;
    }
}

