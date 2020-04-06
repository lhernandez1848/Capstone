package io.github.technocrats.capstone;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class MainActivityFragment extends Fragment implements View.OnClickListener {

    private ViewGroup container;
    private LayoutInflater inflater;
    GlobalMethods globalMethods;

    Toolbar toolbar;
    FrameLayout layout_main;
    private ImageButton inventory, orders, usage, profile;

    public MainActivityFragment() {
    }

    private View initializeUserInterface() {
        View view;

        if(container!=null){
            container.removeAllViewsInLayout();
        }

        int orientation = Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            view = inflater.inflate(R.layout.fragment_main_activity, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_main_activity_horizontal, container, false);
        }

        globalMethods = new GlobalMethods(getContext());
        globalMethods.checkIfLoggedIn();

        toolbar = (Toolbar) view.findViewById(R.id.homeToolbar);
        inventory = (ImageButton) view.findViewById(R.id.btnInventory);
        orders = (ImageButton) view.findViewById(R.id.btnOrders);
        usage = (ImageButton) view.findViewById(R.id.btnUsage);
        profile = (ImageButton) view.findViewById(R.id.btnProfile);
        layout_main = (FrameLayout) view.findViewById( R.id.frame_layout_main);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        layout_main.getForeground().setAlpha(0);

        inventory.setOnClickListener(this);
        orders.setOnClickListener(this);
        usage.setOnClickListener(this);
        profile.setOnClickListener(this);

        return view;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instantiate our container and inflater handles.
        this.container = container;
        this.inflater = inflater;

        setHasOptionsMenu(true);

        // Display the desired layout and return the view.
        return initializeUserInterface();
    }

    // This is called when the user rotates the device.
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // Create the new layout.
        View view = initializeUserInterface();

        // Display the new layout on the screen.
        container.addView(view);

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnInventory){
            startActivity(new Intent(getContext(), InventoryPopup.class));
            layout_main.getForeground().setAlpha(180);
        } else if (view.getId() == R.id.btnOrders){
            startActivity(new Intent(getContext(), OrdersPopup.class));
            layout_main.getForeground().setAlpha(180);
        } else if (view.getId() == R.id.btnUsage){
            startActivity(new Intent(getContext(), UsageAnalysisActivity.class));
        } else if (view.getId() == R.id.btnProfile){
            startActivity(new Intent(getContext(), ProfileActivity.class));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnMenuCheckInventory:
                startActivity(new Intent(getContext(), CheckInventoryActivity.class));
                return true;
            case R.id.btnMenuRecommendations:
                startActivity(new Intent(getContext(), CalendarRecommendation.class));
                return true;
            case R.id.btnMenuSetInventory:
                startActivity(new Intent(getContext(), SetInventoryActivity.class));
                return true;
            case R.id.btnMenuNewOrder:
                startActivity(new Intent(getContext(), CreateOrderActivity.class));
                return true;
            case R.id.btnMenuTrackOrder:
                startActivity(new Intent(getContext(), TrackOrderActivity.class));
                return true;
            case R.id.btnMenuUsage:

                return true;
            case R.id.btnMenuProfile:
                startActivity(new Intent(getContext(), ProfileActivity.class));
                return true;
            case R.id.btnLogout:
                globalMethods.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        layout_main.getForeground().setAlpha(0);
    }
}
