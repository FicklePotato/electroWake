package brasnore.elctrowake;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


@TargetApi(23)
public class MainActivity extends AppCompatActivity {

    static public final int REQUEST_ENABLE_BT = 3;

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

    public void bluetooth(View view){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            Intent intent = new Intent(this, BluetoothActivity.class);
            // TODO: use start activity for result instead, to connect to a device and return the connection or some shit
            // todo: add another activity and button after ive connected, to send some date to the bracelet
            startActivity(intent);
        }
    }
}
