package io.github.technocrats.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.github.technocrats.capstone.adapters.OrderSummaryAdapter;
import io.github.technocrats.capstone.models.OrderSummary;

public class OrderSummaryActivity extends AppCompatActivity implements View.OnClickListener {
    TextView totalTextView, dateDisplay, storeNumberDisplay;

    ListView ProductQuantityListView;
    GlobalMethods globalMethods;
    Calendar calendar;

    Button btnSubmitOrder;

    Toolbar toolbar;

    private SharedPreferences sharedPlace;

    String storeID, new_order_id, total;
    int currentYear, currentMonth, currentDay;
    NumberFormat formatter;
    OrderSummaryAdapter orderSummaryAdapter;
    ArrayList<OrderSummary> orderItemsArray;
    OrderSummary item;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        this.sharedPlace = getSharedPreferences("SharedPlace", MODE_PRIVATE);
        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        toolbar = (Toolbar) findViewById(R.id.ordersSummaryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Order Summary");

        btnSubmitOrder = (Button) findViewById(R.id.btnSubmitOrder);
        btnSubmitOrder.setOnClickListener(this);

        dateDisplay = (TextView) findViewById(R.id.txtOrderSummaryDateDisplay);
        storeNumberDisplay = (TextView) findViewById(R.id.txtOrderSummaryStoreNumber);

        globalMethods.DisplayDate(dateDisplay);
        formatter = new DecimalFormat("#,###.##");

        storeID = sharedPlace.getString("storeID", "");
        storeNumberDisplay.setText("Store Number: " + storeID);

        ProductQuantityListView = (ListView) findViewById(R.id.ProductQuantityListView);

        String order = getIntent().getStringExtra("order");

        ArrayList<String> list = new ArrayList<>();

        String[] temp = order.split("\n");

        for(int i = 0; i < temp.length; i ++)
        {
            list.add(temp[i]);
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        ProductQuantityListView.setAdapter(adapter);

        total = getIntent().getStringExtra("total");

        totalTextView = (TextView) findViewById(R.id.orderSummaryTotal);
        totalTextView.setText(total);

        // data used for inserting new order and ordered_items, as well as generating a new order_id
        calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentMonth = currentMonth+1;
        currentYear = calendar.get(Calendar.YEAR);

        String timeHours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String timeMinutes = String.valueOf(calendar.get(Calendar.MINUTE));
        String timeSecond = String.valueOf(calendar.get(Calendar.SECOND));

        new_order_id = currentYear + "" + currentMonth + "" + currentDay + "" + timeHours + "" + timeMinutes + "" + timeSecond;
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
        if (view.getId() == R.id.btnSubmitOrder){
            // TODO: insert new order into orders
            insertOrder();

            String[] tempSplit;
            float p_quantity, p_price;
            String p_id;

            /*for(String x : orderItemsFinal){
                tempSplit = x.split("@");
                p_price = Float.parseFloat(tempSplit[3]);
                p_id = tempSplit[0];
                p_quantity = Float.parseFloat(tempSplit[2]);

                // TODO: insert each item into ordered_products
                insertOrderItems(p_quantity, p_price, p_id);
            }*/

            Toast.makeText(getApplicationContext(), "Order number: " + new_order_id
                    + " added successfully", Toast.LENGTH_LONG).show();

            // orderItemsFinal.clear();

            Intent setIntent = new Intent(getApplicationContext(), MainActivity.class);
            setIntent.putExtra("FROM_ACTIVITY", "ORDER_SUBMITTED");
            startActivity(setIntent);
            finish();
        }
    }

    //insert a new order into the server
    private void insertOrder() {
        String url = "https://huexinventory.ngrok.io/?a=insert%20into%20orders(order_id,day,month,year,total_cost,status_id,store_id)%20values(%27"+new_order_id+"%27,"+currentDay+","+currentMonth+","+currentYear+","+total+",2,"+storeID+")&b=Capstone";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void insertOrderItems(float quantity, float price, String p_id){
        String url ="https://huexinventory.ngrok.io/?a=insert%20into%20ordered_products(order_id,day,month,year,quantity,unit_cost,product_id)%20values(%27"+new_order_id+"%27,"+currentDay+","+currentMonth+","+currentYear+","+quantity+","+price+",%27"+p_id+"%27)&b=Capstone";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
