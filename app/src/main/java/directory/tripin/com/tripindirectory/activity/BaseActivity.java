package directory.tripin.com.tripindirectory.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 14-03-2018
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected abstract void init();
    protected abstract void viewSetup();
}
