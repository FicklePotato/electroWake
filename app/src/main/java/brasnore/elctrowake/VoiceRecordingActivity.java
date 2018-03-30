package brasnore.elctrowake;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;


/*
    TODO: record sound with a button press, save it as a local sound file
    TODO: change this to two buttons, record and stop record
    TODO: while recording, display a timer
    TODO: translate the sound file into an array of integers - use only the amount of data needed for voice recognition
    FIXME: I THE RECORDING DOES NOT SAVE TO THE FUCKING FILE
     */

public class VoiceRecordingActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecord";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private MediaRecorder mRecorder = null;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    public void onBackPressed() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Log.i(LOG_TAG, "Destroyed recorder");

        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // TODO: Understand paths
        String mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        Log.e(LOG_TAG, "Logging to " + mFileName);
        setContentView(R.layout.activity_voice_recording);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        mRecorder = new MediaRecorder();
        // TODO: check if we want unprocessed audio
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // TODO: i think there might be a problem if the output format and audioEncoder are not the sam format, the file isnt created
        // TODO: what audio file do we want?
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // TODO: what audio encoder do we want?
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName);
        try{
            mRecorder.prepare();
        }
        catch (IOException e){
            Log.e(LOG_TAG, "prepare() failed");
            finish();
        }
        mRecorder.start();
    }
}
