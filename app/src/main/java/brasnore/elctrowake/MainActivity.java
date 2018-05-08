package brasnore.elctrowake;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import brasnore.elctrowake.bluetooth.bluetoothInit;
import brasnore.elctrowake.bluetooth.BluetoothUtils;

import static brasnore.elctrowake.bluetooth.BluetoothUtils.REQUEST_DISCOVERY;

@TargetApi(23)
public class MainActivity extends AppCompatActivity {

    static public final int REQUEST_ENABLE_BT = 3;
    private BluetoothUtils BU = new BluetoothUtils();

    public static final String EXTRA_MESSAGE = "LOL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void recordAudio(View view) {
        Intent intent = new Intent(this, VoiceRecordingActivity.class);
        startActivity(intent);
    }

    // Initiate a bluetooth connection, and run a simple check to send data
    public void bluetooth(View view){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Intent intent = new Intent(this, bluetoothInit.class);
		/* Select device for list */
        startActivityForResult(intent, REQUEST_DISCOVERY);

    }

    /* after select, connect to device */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DISCOVERY) {

            final BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            new Thread() {
                public void run() {
                    BU.connect(device);
                }
            }.start();
        }
    }

    public void sendBTSignal(View view){
        // TODO: this will be the part where we tell the bracelet to elctrocute
        // TODO: preform a check on whether we are connected, if not, show a warning
    }
}
