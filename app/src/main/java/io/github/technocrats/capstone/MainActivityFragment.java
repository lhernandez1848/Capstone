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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    public static LinearLayout inventoryOptions, orderOptions;
    private RelativeLayout mainButtons;
    private ImageButton inventory, orders, btnSetInventory, btnCheckInventory, btnNewOrder, btnOrderHistory;

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
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        inventoryOptions = (LinearLayout) view.findViewById(R.id.inventoryOptions);
        orderOptions = (LinearLayout) view.findViewById(R.id.orderOptions);
        mainButtons = (RelativeLayout) view.findViewById(R.id.mainButtons);
        inventory = (ImageButton) view.findViewById(R.id.btnInventory);
        orders = (ImageButton) view.findViewById(R.id.btnOrders);
        btnSetInventory = (ImageButton) view.findViewById(R.id.btnSetInventory);
        btnCheckInventory = (ImageButton) view.findViewById(R.id.btnCheckInventory);
        btnNewOrder = (ImageButton) view.findViewById(R.id.btnNewOrder);
        btnOrderHistory = (ImageButton) view.findViewById(R.id.btnOrderHistory);

        inventory.setOnClickListener(this);
        orders.setOnClickListener(this);
        btnSetInventory.setOnClickListener(this);
        btnCheckInventory.setOnClickListener(this);
        btnNewOrder.setOnClickListener(this);
        btnOrderHistory.setOnClickListener(this);
        mainButtons.setOnClickListener(this);

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
        switch (view.getId()){
            case R.id.btnInventory:
                if(inventoryOptions.getVisibility()==View.VISIBLE){
                    inventoryOptions.setVisibility(View.GONE);
                    orders.setVisibility(View.VISIBLE);
                } else {
                    inventoryOptions.setVisibility(View.VISIBLE);
                    orders.setVisibility(View.GONE);
                }
                break;
            case R.id.btnOrders:
                if(orderOptions.getVisibility()==View.VISIBLE){
                    orderOptions.setVisibility(View.GONE);
                    inventory.setVisibility(View.VISIBLE);
                } else {
                    orderOptions.setVisibility(View.VISIBLE);
                    inventory.setVisibility(View.GONE);
                }
                break;
            case R.id.mainButtons:
                inventoryOptions.setVisibility(View.GONE);
                orderOptions.setVisibility(View.GONE);
                inventory.setVisibility(View.VISIBLE);
                orders.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSetInventory:
                startActivity(new Intent(getContext(), SetInventoryActivity.class));
                break;
            case R.id.btnCheckInventory:
                startActivity(new Intent(getContext(), CheckInventoryActivity.class));
                break;
            case R.id.btnNewOrder:
                startActivity(new Intent(getContext(), CreateOrderActivity.class));
                break;
            case R.id.btnOrderHistory:
                startActivity(new Intent(getContext(), TrackOrderActivity.class));
                break;
                default:
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
}
