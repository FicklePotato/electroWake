package brasnore.elctrowake.bluetooth;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import brasnore.elctrowake.R;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

@TargetApi(21)
public class bluetoothInit extends ListActivity
{
    /* Get Default Adapter */
    private BluetoothAdapter  _bluetooth = BluetoothAdapter.getDefaultAdapter();
    private Handler _handler = new Handler();
    /* Storage the BT devices */
    private List<BluetoothDevice> _devices = new ArrayList<>();
    /* Discovery is Finished */
    private volatile boolean _discoveryFinished = false;
    private static final int REQUEST_COARSE_LOCATION = 999;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        _bluetooth = bluetoothManager.getAdapter();
        setContentView(R.layout.activity_bluetooth_initf);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("perm", "no coarse loc permiss");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }
        //Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //startActivityForResult(enabler, REQUEST_DISCOVERABLE);
    }

    /* Enable BT */
    public void onResume() {
        super.onResume();
        _bluetooth.enable();
        // TODO: check if connected before attempting to discover and connect
        discover();
    }

        private Runnable _discoveryWorker = new Runnable() {
            public void run()
            {
			/* Start search device */
                if (!_bluetooth.startDiscovery()){
                    Log.e("BT", "Failed to start discovery");}
                Log.d("EF-BTBee", ">>Starting Discovery");
                for (;;) {
                    if (_discoveryFinished) {
                        Log.d("EF-BTBee", ">>Finished");
                        break;}
                    try {Thread.sleep(1000);}
                    catch (InterruptedException e){}
                }
            }
        };

        /**
         * Receiver
         * When the discovery finished be called.
         */
        private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d("BT", "found receiver");
			/* get the search results */
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			/* add to list */
                _devices.add(device);
            }
        };

        private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)
            {
			/* unRegister Receiver */
                Log.d("EF-BTBee", ">>unregisterReceiver");
                context.unregisterReceiver(_foundReceiver);
                context.unregisterReceiver(this);
                _discoveryFinished = true;
                showDevices();
            }
        };


        protected void discover()
        {
		/* Register Receiver*/
            IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(_discoveryReceiver, discoveryFilter);
                IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(_foundReceiver, foundFilter);
            Thread dicoverThread = new Thread() {
                @Override
                public void run()
                {
                    _discoveryWorker.run();
                }
            };
            dicoverThread.start();
            for (; _bluetooth.isDiscovering();) {
                _bluetooth.cancelDiscovery();
            }
        }


        /* Show devices list */
        protected void showDevices()
        {
            List<String> list = new ArrayList<>();
            for (int i = 0, size = _devices.size(); i < size; ++i)
            {
                StringBuilder b = new StringBuilder();
                BluetoothDevice d = _devices.get(i);
                b.append(d.getAddress());
                b.append('\n');
                b.append(d.getName());
                String s = b.toString();
                Log.d("BT", s);
                list.add(s);
            }
            Log.d("EF-BTBee", ">>showDevices");
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            _handler.post(new Runnable() {
                public void run()
                {
                    setListAdapter(adapter);
                }
            });
        }
    /* Select device */
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        // TODO: connect and send data!!!!!
        Log.d("EF-BTBee", ">>Click device");
        Intent result = new Intent();
        result.putExtra(BluetoothDevice.EXTRA_DEVICE, _devices.get(position));
        setResult(RESULT_OK, result);
        finish();
    }
}
