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
    ImageButton btnRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_inventory);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9), (int)(height*.25));

        btnSetInventory = (ImageButton) findViewById(R.id.btnSetInventory);
        btnCheckInventory = (ImageButton) findViewById(R.id.btnCheckInventory);
        btnRecommendation = (ImageButton) findViewById(R.id.btnRecommendation);

        btnSetInventory.setOnClickListener(this);
        btnCheckInventory.setOnClickListener(this);
        btnRecommendation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRecommendation) {

            Intent i = new Intent(InventoryPopup.this, CalendarRecommendation.class);
            startActivity(i);
        }
    }
}
