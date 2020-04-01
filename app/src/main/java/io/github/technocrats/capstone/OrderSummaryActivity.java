package io.github.technocrats.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.github.technocrats.capstone.adapters.OrderSummaryAdapter;
import io.github.technocrats.capstone.models.Product;

public class OrderSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Product> orderedItems;
    TextView tvStoreNumber, tvTotal, tvDateDisplay;
    Button btnSubmitOrder;
    private RecyclerView recyclerView;
    private OrderSummaryAdapter adapter;
    NumberFormat formatter;
    GlobalMethods globalMethods;
    Toolbar toolbar;
    Calendar calendar;

    String storeID, new_order_id;
    int currentYear, currentMonth, currentDay;
    float total;

    // declare Shared Preferences
    private SharedPreferences sharedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        tvStoreNumber = (TextView) findViewById(R.id.txtOrderSummaryStoreNumber);
        tvTotal = (TextView) findViewById(R.id.orderSummaryTotalTextView);
        tvDateDisplay = (TextView) findViewById(R.id.txtOrderSummaryDateDisplay);
        recyclerView = (RecyclerView) findViewById(R.id.orderedProductListView);
        btnSubmitOrder = (Button) findViewById(R.id.btnSubmitOrder);
        btnSubmitOrder.setOnClickListener(this);

        Intent intent = getIntent();
        orderedItems = intent.getParcelableArrayListExtra("orderedItems");

        formatter = new DecimalFormat("#,###.##");

        // display date and store number
        globalMethods.DisplayDate(tvDateDisplay);
        String storeId = sharedPlace.getString("storeID", "");
        tvStoreNumber.setText(storeId);

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

        displayOrderedItems();
        displayOrderTotal();
    }

    public void displayOrderedItems() {
        adapter = new OrderSummaryAdapter(this, orderedItems);
        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new OrderSummaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Toast.makeText(getApplicationContext(), "onItemClick() method", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(View view, int position) {
                //Toast.makeText(getApplicationContext(), "Delete button clicked.", Toast.LENGTH_SHORT).show();
                showDeleteDialog(position);
            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    public void displayOrderTotal(){
        float orderTotal = 0f;
        int orderedItemsLength = orderedItems.size();

        for (int i=0; i < orderedItemsLength; i++) {
            float itemTotal = orderedItems.get(i).getQuantity() * orderedItems.get(i).getUnitCost();
            orderTotal += itemTotal;
        }

        String temp = "Order Total (" + Integer.toString(orderedItemsLength) + " items): $" + formatter.format(orderTotal);
        tvTotal.setText(temp);
    }

    private void showDeleteDialog(int position) {
        final int toDelete = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                R.style.Theme_AppCompat_Light_Dialog);

        builder.setMessage("Do you want to remove this item from this order?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeProduct(toDelete);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void removeProduct(int position) {
        // delete product from order list
        orderedItems.remove(position);

        // update list
        adapter.notifyItemRemoved(position);

        // update order total
        displayOrderTotal();

        // display success message
        // Toast.makeText(getApplicationContext(), "Item has been deleted.", Toast.LENGTH_LONG).show();
        Snackbar.make(findViewById(R.id.mainLayoutOrderSummary), "Item has been deleted.",
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putParcelableArrayListExtra("orderedItems", orderedItems);
        setResult(RESULT_OK, intent);
        finish();
    }

    //insert a new order table row into the server
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

    //insert new ordered_products table rows into the server
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
        if(view.getId() == R.id.btnSubmitOrder){
            insertOrder();

            for(Product p : orderedItems){
                insertOrderItems(p.getQuantity(), p.getUnitCost(), p.getProductId());
            }

            Toast.makeText(getApplicationContext(), "Order Number " + new_order_id
                    + " has been added", Toast.LENGTH_LONG).show();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
