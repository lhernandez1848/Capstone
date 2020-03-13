package io.github.technocrats.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import io.github.technocrats.capstone.adapters.OrderAdapter;
import io.github.technocrats.capstone.models.Order;

public class TrackOrderActivity extends AppCompatActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Toolbar toolbar;
    TextView voiceActivated;
    SwitchCompat switchCompat;
    private TextView tvOrderNumberError;
    private Button btnTrackOrder;
    private EditText etOrderNumber;
    private String orderNumberError;
    private String orderNumber;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private ArrayList<Order> orderList;
    private TextView tvResult;

    GlobalMethods globalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        /* start of toolbar (voice) initializations */
        toolbar = (Toolbar) findViewById(R.id.ordersToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        voiceActivated = (TextView) findViewById(R.id.txtVoiceActive);

        switchCompat = (SwitchCompat) findViewById(R.id.voiceSwitch);
        switchCompat.setOnCheckedChangeListener(this);
        /* end of toolbar (voice) initializations */

        // Initializations
        btnTrackOrder = (Button) findViewById(R.id.btnTrackOrder);
        tvOrderNumberError = (TextView) findViewById(R.id.tvOrderNumberError);
        etOrderNumber = (EditText) findViewById(R.id.etOrderNumber);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvResult = (TextView) findViewById(R.id.tvResult);
        orderNumberError = "";

        // initialize recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        // Set onClickListener
        btnTrackOrder.setOnClickListener(this);

        // Set error message to null
        tvOrderNumberError.setText(orderNumberError);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnTrackOrder) {

            // save user input to variable
            orderNumber = etOrderNumber.getText().toString();

            // validate user input
            if (validateOrderNumber(orderNumber))
            {
                // search db for order
                searchOrder(orderNumber);
            }

            else
            {
                // display error message
                tvOrderNumberError.setText("Invalid order number.");
            }

            // clear EditText
            etOrderNumber.setText("");
        }

    }

    private boolean validateOrderNumber(String orderNumber) {

        // Check if order number only contains letters
        if (orderNumber.matches("[0-9]+"))
        {
            return true;
        }

        return false;
    }

    private void searchOrder(String orderNumber) {

        String url ="https://f8a6792c.ngrok.io/?a=select%20*%20from%20orders%20where%20order_id=" + orderNumber;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // test - delete later
                        // tvResult.setText(response);

                        try
                        {
                            JSONArray objArray = new JSONArray(response);
                            int objArrayLength = objArray.length();

                            if (objArrayLength > 0)
                            {
                                for (int i = 0; i < objArray.length(); i++)
                                {
                                    JSONObject obj = objArray.getJSONObject(i);

                                    String orderId = obj.getString("order_id");
                                    int day = obj.getInt("day");
                                    int month = obj.getInt("month");
                                    int year = obj.getInt("year");
                                    int statusId = obj.getInt("status_id");
                                    int storeId = obj.getInt("store_id");
                                    String totalCost = obj.getString("total_cost");
                                    float fTotalCost = Float.parseFloat(totalCost);

                                    // test - delete later
                                    Log.d("JSON", "order_id: " + orderId);
                                    Log.d("JSON", "day: " + day);
                                    Log.d("JSON", "month: " + month);
                                    Log.d("JSON", "year: " + year);
                                    Log.d("JSON", "statusId: " + statusId);
                                    Log.d("JSON", "storeId: " + storeId);
                                    Log.d("JSON", "total_cost: " + totalCost);

                                    // create order
                                    Order o = new Order(orderId, day, month, year, fTotalCost, statusId, storeId);

                                    // add to list
                                    orderList.add(o);

                                    // display result to recyclerview
                                    adapter.notifyDataSetChanged();
                                }

                                String result = objArrayLength + " order(s) found.";
                                tvResult.setText(result);
                            }

                            else
                            {
                                Log.d("JSON", "No orders found.");
                                tvResult.setText("No orders found.");
                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.d("searchOrder JSON", "Error in searchOrder() method.");
                    tvResult.setText("Sorry! An error occured.");
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
                startActivity(new Intent(
                        getApplicationContext(), CheckInventoryActivity.class));
                return true;
            case R.id.btnMenuRecommendations:
                startActivity(new Intent(
                        getApplicationContext(), CalendarRecommendation.class));
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

}
