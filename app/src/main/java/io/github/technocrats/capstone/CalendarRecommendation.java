package io.github.technocrats.capstone;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.technocrats.capstone.adapters.ExpandableListAdapter;
import io.github.technocrats.capstone.adapters.RecommendedProductsAdapter;
import io.github.technocrats.capstone.models.RecommendedProduct;

public class CalendarRecommendation extends AppCompatActivity
        implements ExpandableListAdapter.ThreeLevelListViewListener {
    TextView dateTextView;
    ListView ListView;
    ArrayList<RecommendedProduct> recommendedProducts;
    Toolbar toolbar;
    int selectedDay, selectedMonth, selectedYear;
    JSONArray jsonarray;
    GlobalMethods globalMethods;
    private Button btnDate;
    private DatePickerDialog.OnDateSetListener pickDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_recommendation);
        setTitle("Recommendation");

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        toolbar = findViewById(R.id.recommendationToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnDate = (Button) findViewById(R.id.btnRecommendationDate);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        ListView = (ListView) findViewById(R.id.ListView);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CalendarRecommendation.this,
                        R.style.DateSelector,
                        pickDateListener,
                        year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        pickDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                selectedDay = day;
                selectedMonth = month;
                selectedYear = year;

                getProducts();
            }
        };

        Intent intent = getIntent();
        int day = intent.getIntExtra("day", 0);
        int month = intent.getIntExtra("month", 0);
        int year = intent.getIntExtra("year", 0);

        if (day != 0 && month !=0 && year !=0) {
            selectedDay = day;
            selectedMonth = month;
            selectedYear = year;

            getProducts();
        }
    }

    public void getProducts()
    {
        String url = "https://huexinventory.ngrok.io/?a=SELECT inventories.day,inventories.month,inventories.year,inventories.quantity,inventories.par,products.product,products.category_id,products.subcategory_id FROM inventories INNER JOIN products ON inventories.product_id=products.product_id WHERE inventories.day=" + selectedDay + " AND inventories.month=" + selectedMonth + " AND inventories.year=" + selectedYear + "&b=Capstone";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int numberOfRecommendedProducts = 0;

                    recommendedProducts = new ArrayList<>();

                    jsonarray = new JSONArray(response);

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        String product = jsonobject.getString("product");
                        double quantity = jsonobject.getDouble("quantity");
                        double par = jsonobject.getDouble("par");

                        if (quantity < 2 * par) {
                            numberOfRecommendedProducts ++;
                            recommendedProducts.add(new RecommendedProduct(product, quantity, par));
                        }
                    }

                    if (numberOfRecommendedProducts > 0) {
                        dateTextView.setText("the " + numberOfRecommendedProducts + " products recommended for purchase on " + selectedDay + " / " + selectedMonth + " / " + selectedYear + " are");
                    }
                    else {
                        dateTextView.setText("there are no recommended products for purchase on " + selectedDay + " / " + selectedMonth + " / " + selectedYear);
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
        RecommendedProductsAdapter recommendedProductsAdapter = new RecommendedProductsAdapter(this, R.layout.recommendation_view_layout, recommendedProducts);
        ListView.setAdapter(recommendedProductsAdapter);
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
                startActivity(new Intent(getApplicationContext(), UsageAnalysisActivity.class));
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
    public void onFinalChildClick(int plpos, int slpos, int tlpos) {
    }

    @Override
    public void onFinalItemClick(int plItem, String slItem, String tlItem) {
    }
}