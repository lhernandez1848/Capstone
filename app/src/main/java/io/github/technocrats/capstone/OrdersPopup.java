package io.github.technocrats.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class OrdersPopup extends AppCompatActivity
        implements View.OnClickListener{

    ImageButton btnSetInventory;
    ImageButton btnCheckInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_orders);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9), (int)(height*.35));

        btnSetInventory = (ImageButton) findViewById(R.id.btnNewOrder);
        btnCheckInventory = (ImageButton) findViewById(R.id.btnOrderHistory);

        btnSetInventory.setOnClickListener(this);
        btnCheckInventory.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnNewOrder){
            startActivity(new Intent(
                    getApplicationContext(), CreateOrderActivity.class));
            finish();
        } else if (view.getId() == R.id.btnOrderHistory){
            startActivity(new Intent(
                    getApplicationContext(), TrackOrderActivity.class));
            finish();
        }
    }

}
