package io.github.technocrats.capstone;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import io.github.technocrats.capstone.adapters.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalendarRecommendation extends AppCompatActivity implements ExpandableListAdapter.ThreeLevelListViewListener {
    TextView dateTextView;
    ListView ListView;
    ArrayList<String> list;
    CalendarPickerView calendar;
    Toolbar toolbar;
    int day, month, year;
    String selectedDate;
    JSONArray jsonarray;

    private SharedPreferences sharedPlace;
    SharedPreferences.Editor sharedEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_recommendation);

        this.sharedPlace = getSharedPreferences("SharedPlace", MODE_PRIVATE);
        sharedEditor = this.sharedPlace.edit();
        checkIfLoggedIn();

        toolbar = findViewById(R.id.recommendationToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Recommendation");

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        ListView = (ListView) findViewById(R.id.ListView);

        Calendar calendar1 = Calendar.getInstance();

        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(Calendar.MONTH, 0);
        calendar1.set(Calendar.YEAR, 2018);

        Calendar calendar2 = Calendar.getInstance();

        calendar2.add(Calendar.YEAR, 1);           // one year from today

        calendar = (CalendarPickerView) findViewById(R.id.calendar);

        calendar.init(calendar1.getTime(), calendar2.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE).withSelectedDate(new Date());

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);

                Calendar calSelected = Calendar.getInstance();
                calSelected.setTime(date);

                day = calSelected.get(Calendar.DAY_OF_MONTH);
                month = calSelected.get(Calendar.MONTH) + 1;
                year = calSelected.get(Calendar.YEAR);

                getProducts();
            }

            @Override
            public void onDateUnselected(Date date) { }
        });
    }

    public void getProducts()
    {
        String url = "https://f8a6792c.ngrok.io/?a=SELECT inventories.day, inventories.month, inventories.year, inventories.quantity, inventories.par, products.product, products.category_id, products.subcategory_id FROM inventories INNER JOIN products ON inventories.product_id = products.product_id WHERE inventories.day = " + day + " AND inventories.month = " + month + " AND inventories.year = " + year;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonarray = new JSONArray(response);

                            int numberOfRecommendedProducts = 0;
                            list = new ArrayList<>();

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String product = jsonobject.getString("product");
                                float quantity = Float.parseFloat(jsonobject.getString("quantity"));
                                float par = Float.parseFloat(jsonobject.getString("par"));

                                if (quantity < 2 * par) {
                                    numberOfRecommendedProducts ++;
                                    list.add(numberOfRecommendedProducts + "    " + product +
                                            "                Quantity: " + quantity
                                            + "               Par: " + par);
                                    //list.add(product);
                                }
                            }

                            if (numberOfRecommendedProducts > 0) {
                                dateTextView.setText("the " + numberOfRecommendedProducts + " products recommended for purchase on " + selectedDate + " are");
                            }
                            else {
                                dateTextView.setText("there are no recommended products for purchase on " + selectedDate);
                            }

                            dolist();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void dolist() {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        ListView.setAdapter(adapter);
    }

    private void checkIfLoggedIn() {
        if (this.sharedPlace.getString("username", "").equals("")){
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(
                    getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnMenuCheckInventory:
                return true;
            case R.id.btnMenuRecommendations:
                return true;
            case R.id.btnMenuSetInventory:
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
                return true;
            case R.id.btnLogout:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFinalChildClick(int plpos, int slpos, int tlpos) {
    }

    @Override
    public void onFinalItemClick(int plItem, String slItem, String tlItem) {
    }
}