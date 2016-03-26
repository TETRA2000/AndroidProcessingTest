package jp.tetra2000.androidprocessingtest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new Sketch();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
