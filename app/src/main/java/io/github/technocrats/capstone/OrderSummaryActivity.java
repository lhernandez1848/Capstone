package io.github.technocrats.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.technocrats.capstone.adapters.OrderSummaryAdapter;
import io.github.technocrats.capstone.models.OrderSummary;

public class OrderSummaryActivity extends AppCompatActivity implements View.OnClickListener {
    List<String> itemsOrdered;
    public static List<String> orderItemsFinal;
    OrderSummaryAdapter orderSummaryAdapter;
    ArrayList<OrderSummary> orderItemsArray;
    OrderSummary item;

    TextView totalTextView, dateDisplay, storeNumberDisplay;
    Button btnSubmitOrder, btnEditOrder;
    Toolbar toolbar;

    RecyclerView recyclerView;
    GlobalMethods globalMethods;
    Calendar calendar;

    String storeID, product_name, product_id, sProductPrice, new_order_id;
    float product_quantity, product_price, total, line_total;
    int currentYear, currentMonth, currentDay;

    private SharedPreferences sharedPlace;
    NumberFormat formatter;

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

        orderItemsArray = new ArrayList<>();
        orderItemsFinal = new ArrayList<>();
        orderSummaryAdapter = new OrderSummaryAdapter(this, orderItemsArray);

        btnSubmitOrder = (Button) findViewById(R.id.btnSubmitOrder);
        btnEditOrder = (Button) findViewById(R.id.btnEditOrder);
        btnSubmitOrder.setOnClickListener(this);
        btnEditOrder.setOnClickListener(this);

        dateDisplay = (TextView) findViewById(R.id.txtOrderSummaryDateDisplay);
        storeNumberDisplay = (TextView) findViewById(R.id.txtOrderSummaryStoreNumber);

        globalMethods.DisplayDate(dateDisplay);
        formatter = new DecimalFormat("#,###.##");

        storeID = sharedPlace.getString("storeID", "");

        storeNumberDisplay.setText("Store Number: " + storeID);

        calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentMonth = currentMonth+1;
        currentYear = calendar.get(Calendar.YEAR);

        totalTextView = (TextView) findViewById(R.id.orderSummaryTotal);

        itemsOrdered = CreateOrderActivity.getOrderedItems();
        getCategories(itemsOrdered);

        recyclerView = (RecyclerView) findViewById(R.id.orderedProductListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(orderSummaryAdapter);

        String totalDisplay = formatter.format(total);
        totalDisplay = "Order Total:  $" + totalDisplay;
        totalTextView.setText(totalDisplay);

        String timeHours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String timeMinutes = String.valueOf(calendar.get(Calendar.MINUTE));
        String timeSecond = String.valueOf(calendar.get(Calendar.SECOND));

        new_order_id = currentYear + "" + currentMonth + "" + currentDay + "" + timeHours + "" + timeMinutes + "" + timeSecond;
    }

    public void getCategories(List<String> orders){
        String[] tempSplit;
        String newOrderLine;

        for(int y = 0; y < orders.size(); y++){
            tempSplit = orders.get(y).split("@");

            product_id = tempSplit[0];
            product_name = tempSplit[1];
            product_quantity = Float.parseFloat(tempSplit[2]);
            product_price = Float.parseFloat(tempSplit[3]);
            line_total = Float.parseFloat(tempSplit[4]);

            newOrderLine = product_id + "@" + product_name + "@" + product_quantity + "@" + product_price + "@" + line_total;
            orderItemsFinal.add(newOrderLine);

            sProductPrice = "$" + product_price;
            total += (product_quantity * product_price);

            // create order
            item = new OrderSummary(product_quantity, product_name, sProductPrice);

            // add to list
            orderItemsArray.add(item);

            // display result to recyclerview
            orderSummaryAdapter.notifyDataSetChanged();
        }

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

                return true;
            case R.id.btnMenuSetInventory:

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


    @Override
    public void onClick(View view) {
         if (view.getId() == R.id.btnSubmitOrder){
             insertOrder();

             String[] tempSplit;
             float p_quantity, p_price;
             String p_id;
             for(String x : orderItemsFinal){
                 tempSplit = x.split("@");
                 p_price = Float.parseFloat(tempSplit[3]);
                 p_id = tempSplit[0];
                 p_quantity = Float.parseFloat(tempSplit[2]);

                 insertOrderItems(p_quantity, p_price, p_id);
             }

             Toast.makeText(getApplicationContext(), "Order number: " + new_order_id
                     + " added successfully", Toast.LENGTH_LONG).show();

             Intent setIntent = new Intent(getApplicationContext(), MainActivity.class);
             setIntent.putExtra("FROM_ACTIVITY", "ORDER_SUBMITTED");
             startActivity(setIntent);
             finish();

        } else if(view.getId() == R.id.btnEditOrder){
             Intent setIntent = new Intent(getApplicationContext(), CreateOrderActivity.class);
             setIntent.putExtra("FROM_ACTIVITY", "ORDER_SUMMARY");
             startActivity(setIntent);
             finish();
         }
    }

    //insert a new order into the server
    private void insertOrder() {
        String url = "https://f8a6792c.ngrok.io/?a=insert%20into%20orders(order_id,day,month,year,total_cost,status_id,store_id)%20values(%27"+new_order_id+"%27,"+currentDay+","+currentMonth+","+currentYear+","+total+",2,"+storeID+")";

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
        String url ="https://f8a6792c.ngrok.io/?a=insert%20into%20ordered_products(order_id,day,month,year,quantity,unit_cost,product_id)%20values(%27"+new_order_id+"%27,"+currentDay+","+currentMonth+","+currentYear+","+quantity+","+price+",%27"+p_id+"%27)";
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

    public static List<String> getOrderSummaryItems(){
        return orderItemsFinal;
    }
}