package io.github.technocrats.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{

    ImageButton inventory;
    ImageButton orders;
    ImageButton profile;

    Toolbar toolbar;
    FrameLayout layout_main;

    GlobalMethods globalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        inventory = (ImageButton) findViewById(R.id.btnInventory);
        orders = (ImageButton) findViewById(R.id.btnOrders);
        profile = (ImageButton) findViewById(R.id.btnProfile);

        inventory.setOnClickListener(this);
        orders.setOnClickListener(this);
        profile.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        layout_main = (FrameLayout) findViewById( R.id.frame_layout_main);
        layout_main.getForeground().setAlpha(0);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnInventory){
            startActivity(new Intent(MainActivity.this, InventoryPopup.class));
            layout_main.getForeground().setAlpha(180);
        } else if (view.getId() == R.id.btnOrders){
            startActivity(new Intent(MainActivity.this, OrdersPopup.class));
            layout_main.getForeground().setAlpha(180);
        } else if (view.getId() == R.id.btnProfile){
            startActivity(new Intent(
                    getApplicationContext(), ProfileActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        layout_main.getForeground().setAlpha(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnMenuCheckInventory:
                startActivity(new Intent(getApplicationContext(), CheckInventoryActivity.class));
                return true;
            case R.id.btnMenuRecommendations:
                startActivity(new Intent(getApplicationContext(), CalendarRecommendation.class));
                return true;
            case R.id.btnMenuSetInventory:
                startActivity(new Intent(getApplicationContext(), SetInventoryActivity.class));
                return true;
            case R.id.btnMenuNewOrder:
                startActivity(new Intent(getApplicationContext(), CreateOrderActivity.class));
                return true;
            case R.id.btnMenuTrackOrder:
                startActivity(new Intent(getApplicationContext(), TrackOrderActivity.class));
                return true;
            case R.id.btnMenuUsage:

                return true;
            case R.id.btnMenuProfile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return true;
            case R.id.btnLogout:
                globalMethods.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
