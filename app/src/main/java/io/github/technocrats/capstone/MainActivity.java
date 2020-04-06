package io.github.technocrats.capstone;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity{

    private CapstoneApp app;
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

        // initialize app to start notification service
        app = (CapstoneApp) getApplication();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new MainActivityFragment())
                .commit();
    }


    @Override
    public void onBackPressed() {
        // Clear any existing layouts before popping the stack.
        if (frameLayout != null) {
            frameLayout.removeAllViews();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1 ) {
            fragmentManager.popBackStack();
            return;
        }

        // Exit the app if there are no more fragments.
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
