package brasnore.elctrowake;
import android.annotation.TargetApi;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@TargetApi(21)
class SoundAnalizer {
    public int isSnore(String filePath){
        Log.i("Sound", String.format(Locale.US,
                "Returning snore result for file %s", filePath));
        return ThreadLocalRandom.current().nextInt(0, 100);
    }
}
