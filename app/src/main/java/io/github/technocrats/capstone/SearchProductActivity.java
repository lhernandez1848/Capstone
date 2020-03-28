package io.github.technocrats.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import io.github.technocrats.capstone.adapters.ProductAdapter;
import io.github.technocrats.capstone.models.Product;

public class SearchProductActivity extends AppCompatActivity implements DialogInterface.OnClickListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    GlobalMethods globalMethods;

    Toolbar toolbar;
    TextView voiceActivated, tvProductSelected, productTextView;
    SwitchCompat switchCompat;
    private EditText etProductName;
    private TextView tvProductNameError;
    private Button btnSearchProduct, btnAddProductFromSearch;
    private String productNameError;
    private String productName;
    private RecyclerView recyclerViewProductSearch;
    private ProductAdapter adapter;
    private ArrayList<Product> productList;
    private TextView tvResultProductSearch;
    Intent intent;
    public static Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        setTitle("Search Product");

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        toolbar = (Toolbar) findViewById(R.id.searchProductToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        voiceActivated = (TextView) findViewById(R.id.txtVoiceActiveSearchProduct);

        switchCompat = (SwitchCompat) findViewById(R.id.voiceSwitchSearchProduct);
        switchCompat.setOnCheckedChangeListener(this);

        // Initializations
        btnSearchProduct = (Button) findViewById(R.id.btnSearchProduct);
        btnAddProductFromSearch = (Button) findViewById(R.id.btnAddProductFromSearch);
        tvProductNameError = (TextView) findViewById(R.id.tvProductNameError);
        etProductName = (EditText) findViewById(R.id.etProductName);
        recyclerViewProductSearch = (RecyclerView) findViewById(R.id.recyclerViewProductSearch);
        tvResultProductSearch = (TextView) findViewById(R.id.tvResultProductSearch);
        tvProductSelected = (TextView) findViewById(R.id.tvProductSelected);
        productTextView = (TextView) findViewById(R.id.productTextView);
        productNameError = "";

        // initialize recyclerview
        recyclerViewProductSearch.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, tvProductSelected);
        recyclerViewProductSearch.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewProductSearch.setAdapter(adapter);

        // Set onClickListener
        btnSearchProduct.setOnClickListener(this);
        btnAddProductFromSearch.setOnClickListener(this);

        // Set error message to null
        tvProductNameError.setText(productNameError);

        intent = new Intent(this, TrackOrderActivity.class);

        selectedProduct = null;
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

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
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked){
            Toast.makeText(getApplicationContext(),
                    "Voice Control is on", Toast.LENGTH_LONG).show();
            voiceActivated.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Voice Control is off", Toast.LENGTH_LONG).show();
            voiceActivated.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        String productSelected = tvProductSelected.getText().toString();
        if (view.getId() == R.id.btnSearchProduct) {
            // save user input to variable
            productName = etProductName.getText().toString();

            // validate user input
            if (validateProductName(productName)){
                // search db for product
                searchProduct(productName);
                btnAddProductFromSearch.setEnabled(true);
            } else{
                // display error message
                tvProductNameError.setText("Invalid product name.");
            }
            // clear EditText
            etProductName.setText("");
        } else if (view.getId() == R.id.btnAddProductFromSearch){
            if (productSelected.length()<1){
                Toast.makeText(getApplicationContext(), "Please select a product",
                        Toast.LENGTH_LONG).show();
            } else {
                CreateOrderActivity.setProduct(selectedProduct);
                finish();
            }
        }
    }

    public static void setProductSelected(Product productSelected){
        selectedProduct = productSelected;

        ArrayList<Product> orderedItems = new ArrayList<>();
        orderedItems = CreateOrderActivity.getOrderedItems();
        for (Product p : orderedItems){
            if(selectedProduct.getProductId().equals(p.getProductId())){
                selectedProduct.setQuantity(p.getQuantity());
            }
        }

    }

    private boolean validateProductName(String productName) {
        // check if product is empty
        return !productName.isEmpty();
    }

    private void searchProduct(final String productName) {
        String url ="https://huexinventory.ngrok.io/?a=select%20*%20from%20products%20where%20product%20like%20%27%25"+productName+"%25%27&b=Capstone";

        RequestQueue queue = Volley.newRequestQueue(this);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray objArray = new JSONArray(response);
                            int objArrayLength = objArray.length();

                            if (objArrayLength > 0)
                            {
                                for (int i = 0; i < objArray.length(); i++)
                                {
                                    JSONObject obj = objArray.getJSONObject(i);

                                    String product_id = obj.getString("product_id");
                                    String product_name = obj.getString("product");
                                    String s_unit_cost = obj.getString("unit_cost");
                                    int subcategory = obj.getInt("subcategory_id");
                                    int category = obj.getInt("category_id");
                                    float f_unit_cost = Float.parseFloat(s_unit_cost);

                                    // create order
                                    Product product = new Product(product_id, product_name, f_unit_cost, 0, subcategory, category);

                                    // add to list
                                    productList.add(product);

                                    // display result to recyclerview
                                    adapter.notifyDataSetChanged();
                                }

                                String result = objArrayLength + " product(s) found.";
                                tvResultProductSearch.setText(result);
                            }

                            else
                            {
                                tvResultProductSearch.setText("No products found.");
                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvProductNameError.setText("Sorry! An error occured.");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
