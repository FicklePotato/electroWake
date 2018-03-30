package brasnore.elctrowake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {
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
}
