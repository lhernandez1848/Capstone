package io.github.technocrats.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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

import java.util.ArrayList;

import io.github.technocrats.capstone.adapters.OrderProductAdapter;
import io.github.technocrats.capstone.models.OrderProduct;

public class OrderDetailsActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener{

    Toolbar toolbar;
    TextView voiceActivated;
    SwitchCompat switchCompat;
    private TextView tvOrderNumber, tvDate, tvTotal, tvStatus;
    private String orderNumber, orderDate, orderTotal, orderStatus;
    private RecyclerView recyclerView;
    private OrderProductAdapter adapter;
    private ArrayList<OrderProduct> orderItems;
    RequestQueue queue;

    GlobalMethods globalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        setTitle("Order Details");

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        Intent intent = getIntent();
        orderNumber = intent.getStringExtra("orderNumber");
        orderDate = intent.getStringExtra("date");
        orderStatus = intent.getStringExtra("status");
        orderTotal = intent.getStringExtra("total");

        /* start of toolbar (voice) initializations */
        toolbar = (Toolbar) findViewById(R.id.ordersToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        voiceActivated = (TextView) findViewById(R.id.txtVoiceActive);

        switchCompat = (SwitchCompat) findViewById(R.id.voiceSwitch);
        switchCompat.setOnCheckedChangeListener(this);
        /* end of toolbar (voice) initializations */

        // get references to widgets
        tvOrderNumber = (TextView) findViewById(R.id.orderNumberTextView);
        tvDate = (TextView) findViewById(R.id.dateTextView);
        tvTotal = (TextView) findViewById(R.id.totalTextView);
        tvStatus = (TextView) findViewById(R.id.statusTextView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItems = new ArrayList<>();
        adapter = new OrderProductAdapter(this, orderItems);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        // initialize queue
        queue = Volley.newRequestQueue(this);

        displayOrderInformation();
        displayOrderItems();
    }

    private void displayOrderInformation() {

        // display order details
        tvOrderNumber.setText(orderNumber);
        tvDate.setText(orderDate);
        tvStatus.setText(orderStatus);
        tvTotal.setText(orderTotal);
    }

    private void displayOrderItems() {
        String url = "https://huexinventory.ngrok.io/?a=select%20op.quantity,op.unit_cost,p.product%20from%20ordered_products%20op%20join%20products%20p%20on%20op.product_id=p.product_id%20where%20order_id=%27"+orderNumber+"%27&b=Capstone";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray objArray = new JSONArray(response);
                            int objArrayLength = objArray.length();

                            Log.d("JSON", "arrayLength: " + objArrayLength);

                            if (objArrayLength > 0)
                            {
                                for (int i = 0; i < objArray.length(); i++)
                                {
                                    JSONObject obj = objArray.getJSONObject(i);

                                    //String productName = obj.getString("product");
                                    String productName = obj.getString("product");
                                    float quantity = obj.getInt("quantity");
                                    String cost = obj.getString("unit_cost");
                                    float fUnitCost = Float.parseFloat(cost);

                                    // create order
                                    OrderProduct item = new OrderProduct(orderNumber, productName, fUnitCost, quantity);

                                    // add to list
                                    orderItems.add(item);

                                    // display result to recyclerview
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.d("JSON", "No orders found.");
                                // tvResult.setText("No orders found.");
                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("displayOrderItems JSON", "Error in displayOrderItems() method.");
                // tvResult.setText("Sorry! An error occured.");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            Toast.makeText(getApplicationContext(),
                    "Voice Control is on", Toast.LENGTH_LONG).show();
            voiceActivated.setVisibility(View.VISIBLE);

        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Voice Control is off", Toast.LENGTH_LONG).show();
            voiceActivated.setVisibility(View.GONE);
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

}
