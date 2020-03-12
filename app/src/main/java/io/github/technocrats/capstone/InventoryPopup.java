package io.github.technocrats.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

public class InventoryPopup extends AppCompatActivity
        implements View.OnClickListener{

    ImageButton btnSetInventory;
    ImageButton btnCheckInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_inventory);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9), (int)(height*.35));

        btnSetInventory = (ImageButton) findViewById(R.id.btnSetInventory);
        btnCheckInventory = (ImageButton) findViewById(R.id.btnCheckInventory);

        btnSetInventory.setOnClickListener(this);
        btnCheckInventory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSetInventory){
            /*startActivity(new Intent(
                    getApplicationContext(), CreateOrderActivity.class));
            finish();*/
        } else if (view.getId() == R.id.btnCheckInventory){
            startActivity(new Intent(
                    getApplicationContext(), CheckInventoryActivity.class));
            finish();
        }

    }
}
