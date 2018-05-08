package brasnore.elctrowake.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.content.DialogInterface.OnDismissListener;

public class BluetoothUtils {

    private BluetoothSocket socket = null;
    public static final int REQUEST_DISCOVERY = 0x1;

    public void connect(BluetoothDevice device) {
        //BluetoothSocket socket = null;
        try {
            //Create a Socket connection: need the server's UUID number of registered
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c656"));
            device.getUuids();
            socket.connect();
            Log.d("EF-BTBee", ">>Client connectted");

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(new byte[] { (byte) 0xa0, 0, 7, 16, 0, 4, 0 });


        } catch (IOException e) {
            Log.e("EF-BTBee", "", e);
        } finally {
            if (socket != null) {
                try {
                    Log.d("EF-BTBee", ">>Client Close");
                    socket.close();
                } catch (IOException e) {
                    Log.e("EF-BTBee", "", e);
                }
            }
        }
    }
    public static void indeterminate(Context context, Handler handler, String message, final Runnable runnable, OnDismissListener dismissListener)
    {
        try
        {

            indeterminateInternal(context, handler, message, runnable, dismissListener, true);
        }
        catch (Exception e)
        {

            ; // nop.
        }
    }


    public static void indeterminate(Context context, Handler handler, String message, final Runnable runnable, OnDismissListener dismissListener,
                                     boolean cancelable)
    {

        try
        {

            indeterminateInternal(context, handler, message, runnable, dismissListener, cancelable);
        }
        catch (Exception e)
        {

            ; // nop.
        }
    }

    private static ProgressDialog createProgressDialog(Context context, String message)
    {

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(false);
        dialog.setMessage(message);

        return dialog;
    }


    private static void indeterminateInternal(Context context, final Handler handler, String message, final Runnable runnable,
                                              OnDismissListener dismissListener, boolean cancelable)
    {

        final ProgressDialog dialog = createProgressDialog(context, message);
        dialog.setCancelable(cancelable);

        if (dismissListener != null)
        {

            dialog.setOnDismissListener(dismissListener);
        }

        dialog.show();

        new Thread() {

            @Override
            public void run()
            {
                runnable.run();

                handler.post(new Runnable() {

                    public void run()
                    {

                        try
                        {

                            dialog.dismiss();
                        }
                        catch (Exception e)
                        {

                            ; // nop.
                        }

                    }
                });
            };
        }.start();
    }
}

