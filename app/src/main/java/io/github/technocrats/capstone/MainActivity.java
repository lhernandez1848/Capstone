package io.github.technocrats.capstone;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

        LinearLayout inventoryOptions, orderOptions;
        inventoryOptions = MainActivityFragment.inventoryOptions;
        orderOptions = MainActivityFragment.orderOptions;

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1 ) {
            fragmentManager.popBackStack();
            return;
        } else if(inventoryOptions.getVisibility()==View.VISIBLE ||
                orderOptions.getVisibility() == View.VISIBLE){
            loadFragment(new MainActivityFragment());
        }

        // Exit the app if there are no more fragments.
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
