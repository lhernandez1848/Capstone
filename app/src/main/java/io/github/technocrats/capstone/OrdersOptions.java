package io.github.technocrats.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class OrdersOptions extends AppCompatActivity
            implements View.OnClickListener{

    Toolbar toolbar;
    ImageButton inventory;
    ImageButton orders;

    GlobalMethods globalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_options);

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        inventory = (ImageButton) findViewById(R.id.btnNewOrder);
        orders = (ImageButton) findViewById(R.id.btnOrderHistory);
        toolbar = (Toolbar) findViewById(R.id.order_options_toolbar);

        inventory.setOnClickListener(this);
        orders.setOnClickListener(this);
        setSupportActionBar(toolbar);

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
    public void onClick(View view) {
        if (view.getId() == R.id.btnNewOrder){
            startActivity(new Intent(getApplicationContext(), CreateOrderActivity.class));
        } else if (view.getId() == R.id.btnOrderHistory){
            startActivity(new Intent(getApplicationContext(), TrackOrderActivity.class));
        }
    }

}
