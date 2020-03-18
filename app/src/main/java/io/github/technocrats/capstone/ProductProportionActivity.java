package io.github.technocrats.capstone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductProportionActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    String selectedSubcategory;
    int selectedDay, selectedMonth, selectedYear;
    JSONArray jsonarrayProducts;

    GlobalMethods globalMethods;

    BarChart barChart;
    TextView tvProductSelected;
    Toolbar toolbar;
    NumberFormat formatter;

    float[] yData;
    String[] xData;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_proportion);

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        setTitle("Product Proportions");
        toolbar = findViewById(R.id.productProportionToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        formatter = new DecimalFormat("#,###.##");

        selectedSubcategory = getIntent().getStringExtra("subcategorySelected");
        selectedDay = getIntent().getIntExtra("selectedDay", 0);
        selectedMonth = getIntent().getIntExtra("selectedMonth", 0);
        selectedYear = getIntent().getIntExtra("selectedYear", 0);

        tvProductSelected = (TextView) findViewById(R.id.barchartSelection);
        barChart = (BarChart) findViewById(R.id.inventoryBarChart);
        barChart.setOnChartValueSelectedListener(this);
        barChart.getDescription().setText("Top 12 " + selectedSubcategory + " Product Quantities");
        barChart.getDescription().setTextSize(20f);

        getProductProportions();
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
                startActivity(new Intent(
                        getApplicationContext(), CheckInventoryActivity.class));
                return true;
            case R.id.btnMenuRecommendations:
                startActivity(new Intent(
                        getApplicationContext(), CalendarRecommendation.class));
                return true;
            case R.id.btnMenuSetInventory:
                startActivity(new Intent(
                        getApplicationContext(), SetInventoryActivity.class));
                return true;
            case R.id.btnMenuNewOrder:
                startActivity(new Intent(
                        getApplicationContext(), CreateOrderActivity.class));
                return true;
            case R.id.btnMenuTrackOrder:
                startActivity(new Intent(
                        getApplicationContext(), TrackOrderActivity.class));
                return true;
            case R.id.btnMenuUsage:

                return true;
            case R.id.btnMenuProfile:

                return true;
            case R.id.btnLogout:
                globalMethods.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // get products and set up list views for sorting by values
    public void getProductProportions(){
        String url = "https://huexinventory.ngrok.io/?a=select%20top%2012%20day,month,year,quantity,inventories.unit_cost,product,subcategory%20from%20inventories%20join%20products%20on%20inventories.product_id=products.product_id%20join%20subcategories%20on%20subcategories.subcategory_id=products.subcategory_id%20where%20day="+selectedDay+"%20and%20month="+selectedMonth+"%20and%20year="+selectedYear+"%20and%20subcategory=%27"+selectedSubcategory+"%27%20and%20quantity%3C%3E0%20order%20by%20quantity%20desc";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            jsonarrayProducts = new JSONArray(response);

                            xData = new String[jsonarrayProducts.length()];
                            yData = new float[jsonarrayProducts.length()];

                            for (int i = 0; i < jsonarrayProducts.length(); i++) {
                                JSONObject jsonobject = jsonarrayProducts.getJSONObject(i);

                                String product = jsonobject.getString("product");
                                String sPrice = jsonobject.getString("unit_cost");
                                float fPrice = Float.parseFloat(sPrice);
                                String sQuantity = jsonobject.getString("quantity");
                                float fQuantity = Float.parseFloat(sQuantity);

                                xData[i] = product;
                                yData[i] = fQuantity;
                            }

                            addDataSet();

                        } catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void addDataSet() {
        List<BarEntry> entries = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            entries.add(new BarEntry(i , yData[i]));
        }

        BarDataSet set = new BarDataSet(entries, "Product Quantity");
        set.setValueTextSize(15f);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int i = (int) h.getX();
        String productSelected = xData[i];
        float quantitySelected = yData[i];

        tvProductSelected.setText(productSelected + "       " + formatter.format(quantitySelected));
    }

    @Override
    public void onNothingSelected() {

    }
}
