package brasnore.elctrowake;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
    TODO: record sound with a button press, save it as a local sound file
    TODO: change this to two buttons, record and stop record
    TODO: while recording, display a timer
    TODO: translate the sound file into an array of integers - use only the amount of data needed for voice recognition
     */

public class VoiceRecordingActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecord";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String AUDIO_ENDING = ".3gp";
    private static final String REGEX_NUMBER = "_\\d_";

    private MediaRecorder mRecorder = null;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private Pattern numReg = Pattern.compile(REGEX_NUMBER);

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

    private String getAudioPath(){
        String outPath = "%s/Audio_%d_%s%s";
        Date currentTime = Calendar.getInstance().getTime();
        String outDir = getExternalCacheDir().getAbsolutePath();
        outPath = String.format(outPath, outDir, 0, currentTime.toString(), AUDIO_ENDING);
        if (!new File(outPath).exists()){
            return outPath;
        }
        else {
            return getAudioPath(outPath);
        }
    }

    private String getAudioPath(String prevPath){
        Matcher m = numReg.matcher(prevPath);
        int prevNum = 1;
        while (m.find()){
            prevNum = Integer.parseInt(m.group(1).replaceAll("_", ""));
        }
        String newPath = prevPath.replaceFirst(REGEX_NUMBER, String.format("_%d_", Integer.toString(prevNum + 1)));
        if (!new File(newPath).exists()){
            return newPath;
        }
        else {
            return getAudioPath(newPath);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // FYI: there is a bug in some android systems where the file does not get saved until you unplug the device.
        super.onCreate(savedInstanceState);
        // TODO: Understand paths
        String mFileName = getAudioPath();
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
