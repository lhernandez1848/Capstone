package io.github.technocrats.capstone;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity{

    FrameLayout frameLayout;

    public void loadFragment(Fragment fragment){
        frameLayout.removeAllViews();

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameLayout, fragment)
                .commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        loadFragment(new MainActivityFragment());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
