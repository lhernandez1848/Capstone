package io.github.technocrats.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.technocrats.capstone.adapters.ExpandableListAdapter;

public class CreateOrderActivity extends AppCompatActivity implements SetOrderQuantityDialog.SetOrderQuantityDialogListener,
        ExpandableListAdapter.ThreeLevelListViewListener, View.OnClickListener {

    TextView storeNumber, totalTextView, dateDisplay, productTextView;
    String storeID;
    Button btnSubmit, btnAddProductToOrder, btnRemoveProductFromOrder;
    Toolbar toolbar;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    NumberFormat formatter;
    GlobalMethods globalMethods;

    // declare lists to store all categories, subcategories, and products
    String[] listDataCategories;
    List<String[]> listDataSubcategories;
    List<LinkedHashMap<String, List<String>>> listDataProducts;

    // declare and initialize lists of subcategories per categories
    String[] sFood = new String[]{"Sugar and Shortening", "Fillings", "Drinks",
            "Cans and Home Brew", "Soup and Sandwiches", "Food Ingredients", "Produce",
            "Bread", "Emulsions and Paste", "danis", "mustard spread", "Toppings"};
    String[] sNA = new String[]{"N/A"};
    String[] sPaper = new String[]{"Paper - Other Packaging", "Hot Drink Cups",
            "Iced Beverage Cups/Lids"};
    String[] sAdvertising = new String[]{"Advertising"};
    String[] sCleaning = new String[]{"coffee bowl cleaner"};
    String[] sMiscellaneous = new String[]{"Store Supplies"};
    String[] sUniforms = new String[]{"Staff Uniform"};
    String[] sInventory = new String[]{"Dairy"};

    JSONArray jsonarrayProducts;

    private SharedPreferences sharedPlace;

    public static String[] products = new String[690];
    public static int[] quantities = new int[690];
    int index;
    public static String product;
    public static float price;
    public static int position;
    public static float total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        this.sharedPlace = getSharedPreferences("SharedPlace", MODE_PRIVATE);
        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        btnAddProductToOrder = (Button) findViewById(R.id.btnAddProductToOrder);
        btnRemoveProductFromOrder = (Button) findViewById(R.id.btnRemoveProductFromOrder);
        btnSubmit = (Button) findViewById(R.id.btnGoToOrderSummary);
        btnSubmit.setEnabled(false);
        btnAddProductToOrder.setOnClickListener(this);
        btnRemoveProductFromOrder.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        setTitle("Create Order");

        dateDisplay = findViewById(R.id.txtDateDisplay);
        toolbar = findViewById(R.id.createOrderToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        DisplayDate();

        for(int i = 0; i < 690; i ++)
        {
            quantities[i] = 0;
        }

        index = 0;
        total = 0;

        formatter = new DecimalFormat("#,###.##");

        // get the listview
        expListView = findViewById(R.id.expandableListView);

        // initialize category list with hardcoded data
        listDataCategories = new String[]{"Food", "N/A", "Paper", "Advertising", "Cleaning", "Miscellaneous", "Uniforms", "inventory"};

        // initialize subcategories list
        listDataSubcategories = new ArrayList<>();

        // initialize products list
        listDataProducts = new ArrayList<>();

        // add lists of subcategories to List of all subcategories
        listDataSubcategories.add(sFood);
        listDataSubcategories.add(sNA);
        listDataSubcategories.add(sPaper);
        listDataSubcategories.add(sAdvertising);
        listDataSubcategories.add(sCleaning);
        listDataSubcategories.add(sMiscellaneous);
        listDataSubcategories.add(sUniforms);
        listDataSubcategories.add(sInventory);

        storeNumber = (TextView) findViewById(R.id.txtStoreNumber);
        storeID = sharedPlace.getString("storeID", "");
        String displayStore = "Create New Order for Store Number: " + storeID;
        storeNumber.setText(displayStore);

        getProducts();

        productTextView = (TextView) findViewById(R.id.productTextView);
        productTextView.setText("");
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        updateTotalTextView();

        btnSubmit.setEnabled(true);
    }

    public void getProducts(){
        String url ="https://huexinventory.ngrok.io/?a=select%20*%20from%20products" + "&b=Capstone";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            List<String> SugarAndShortening = new ArrayList<String>();
                            List<String> Fillings = new ArrayList<String>();
                            List<String> Drinks = new ArrayList<String>();
                            List<String> CansAndHomeBrew = new ArrayList<String>();
                            List<String> SoupSandwiches = new ArrayList<String>();
                            List<String> FoodIngredients = new ArrayList<String>();
                            List<String> Produce = new ArrayList<String>();
                            List<String> NA = new ArrayList<String>();
                            List<String> Bread = new ArrayList<String>();
                            List<String> Emulsions = new ArrayList<String>();
                            List<String> danis = new ArrayList<String>();
                            List<String> MustardSpread = new ArrayList<String>();
                            List<String> Toppings = new ArrayList<String>();
                            List<String> Paper = new ArrayList<String>();
                            List<String> HotDrinkCups = new ArrayList<String>();
                            List<String> IcedBeverageCupsLids = new ArrayList<String>();
                            List<String> Advertising = new ArrayList<String>();
                            List<String> coffeeBowlCleaner = new ArrayList<String>();
                            List<String> StoreSupplies = new ArrayList<String>();
                            List<String> StaffUniform = new ArrayList<String>();
                            List<String> Dairy = new ArrayList<String>();

                            LinkedHashMap<String, List<String>> thirdLevelFood = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelNA = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelPaper = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelAdvertising = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelCleaning = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelMiscellaneous = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelUniforms = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelInventory = new LinkedHashMap<>();

                            jsonarrayProducts = new JSONArray(response);

                            for (int i = 0; i < jsonarrayProducts.length(); i++) {
                                JSONObject jsonobject = jsonarrayProducts.getJSONObject(i);

                                String product = jsonobject.getString("product");
                                String price = jsonobject.getString("unit_cost");
                                String productAndPrice = product + "    $" + price;
                                int subcategory_id = jsonobject.getInt("subcategory_id");

                                switch(subcategory_id) {
                                    case 1:
                                        SugarAndShortening.add(productAndPrice);
                                        break;
                                    case 2:
                                        Fillings.add(productAndPrice);
                                        break;
                                    case 3:
                                        Drinks.add(productAndPrice);
                                        break;
                                    case 4:
                                        CansAndHomeBrew.add(productAndPrice);
                                        break;
                                    case 5:
                                        SoupSandwiches.add(productAndPrice);
                                        break;
                                    case 6:
                                        FoodIngredients.add(productAndPrice);
                                        break;
                                    case 7:
                                        Produce.add(productAndPrice);
                                        break;
                                    case 8:
                                        NA.add(productAndPrice);
                                        break;
                                    case 9:
                                        Bread.add(productAndPrice);
                                        break;
                                    case 10:
                                        Emulsions.add(productAndPrice);
                                        break;
                                    case 11:
                                        danis.add(productAndPrice);
                                        break;
                                    case 12:
                                        MustardSpread.add(productAndPrice);
                                        break;
                                    case 13:
                                        Toppings.add(productAndPrice);
                                        break;
                                    case 14:
                                        Paper.add(productAndPrice);
                                        break;
                                    case 15:
                                        HotDrinkCups.add(productAndPrice);
                                        break;
                                    case 16:
                                        IcedBeverageCupsLids.add(productAndPrice);
                                        break;
                                    case 17:
                                        Advertising.add(productAndPrice);
                                        break;
                                    case 18:
                                        coffeeBowlCleaner.add(productAndPrice);
                                        break;
                                    case 19:
                                        StoreSupplies.add(productAndPrice);
                                        break;
                                    case 20:
                                        StaffUniform.add(productAndPrice);
                                        break;
                                    case 21:
                                        Dairy.add(productAndPrice);
                                        break;
                                    default:
                                }
                            }

                            thirdLevelFood.put(sFood[0], SugarAndShortening);
                            thirdLevelFood.put(sFood[1], Fillings);
                            thirdLevelFood.put(sFood[2], Drinks);
                            thirdLevelFood.put(sFood[3], CansAndHomeBrew);
                            thirdLevelFood.put(sFood[4], SoupSandwiches);
                            thirdLevelFood.put(sFood[5], FoodIngredients);
                            thirdLevelFood.put(sFood[6], Produce);
                            thirdLevelFood.put(sFood[7], Bread);
                            thirdLevelFood.put(sFood[8], Emulsions);
                            thirdLevelFood.put(sFood[9], danis);
                            thirdLevelFood.put(sFood[10], MustardSpread);
                            thirdLevelFood.put(sFood[11], Toppings);
                            thirdLevelNA.put(sNA[0], NA);
                            thirdLevelPaper.put(sPaper[0], Paper);
                            thirdLevelPaper.put(sPaper[1], HotDrinkCups);
                            thirdLevelPaper.put(sPaper[2], IcedBeverageCupsLids);
                            thirdLevelAdvertising.put(sAdvertising[0], Advertising);
                            thirdLevelCleaning.put(sCleaning[0], coffeeBowlCleaner);
                            thirdLevelMiscellaneous.put(sMiscellaneous[0], StoreSupplies);
                            thirdLevelUniforms.put(sUniforms[0], StaffUniform);
                            thirdLevelInventory.put(sInventory[0], Dairy);

                            listDataProducts.add(thirdLevelFood);
                            listDataProducts.add(thirdLevelNA);
                            listDataProducts.add(thirdLevelPaper);
                            listDataProducts.add(thirdLevelAdvertising);
                            listDataProducts.add(thirdLevelCleaning);
                            listDataProducts.add(thirdLevelMiscellaneous);
                            listDataProducts.add(thirdLevelUniforms);
                            listDataProducts.add(thirdLevelInventory);

                            dolist();
                        } catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void dolist(){
        listAdapter = new ExpandableListAdapter(this, listDataCategories, listDataSubcategories, listDataProducts, this);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                System.out.println(groupPosition);
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " Expanded",Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " Collapsed",Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    @Override
    public void onFinalChildClick(int plpos, int slpos, int tlpos) {
    }

    @Override
    public void onFinalItemClick(int plItem, String slItem, String tlItem) {
        String[] temp = tlItem.split("\\s\\s\\s\\s\\$");

        product = temp[0];
        price = Float.parseFloat(temp[1]);

        boolean productInProducts = false;

        for(int i = 0; i < index; i ++)
        {
            if (product.equals(products[i]))
            {
                productInProducts = true;
                position = i;
                break;
            }
        }

        if(!productInProducts)
        {
            products[index] = product;
            quantities[index] = 0;
            position = index;
            index ++;
        }

        String productSelected = product + " : " + quantities[position] + "  *  $" + price;
        productTextView.setText(productSelected);
    }

    private void DisplayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        String date = sdf.format(new Date());
        dateDisplay.setText(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    public void showDialog() {
        SetOrderQuantityDialog setOrderQuantityDialog = new SetOrderQuantityDialog();
        setOrderQuantityDialog.show(getSupportFragmentManager(), "Order Quantity");
    }

    @Override
    public void updateTotalTextView() {
        totalTextView.setText("Total: $" + formatter.format(total));
    }

    @Override
    public void updateProductTextView(int setQuantity) {
        String productSelected = product + " : " + setQuantity + "  *  $" + price;
        productTextView.setText(productSelected);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnGoToOrderSummary) {
            String order = "";

            for(int i = 0; i < index; i ++) {
                if(quantities[i] > 0) {
                    order += products[i] + "    " + quantities[i] + "\n";
                }
            }

            if(!order.equals("")) {
                Intent i = new Intent(CreateOrderActivity.this, OrderSummaryActivity.class);
                i.putExtra("order", order);
                i.putExtra("total", "Total: $" + String.format("%.2f", total).replaceAll( "^-(?=0(\\.0*)?$)", ""));
                startActivity(i);
            }
        } else if (view.getId() == R.id.btnAddProductToOrder){
            showDialog();
        } else if (view.getId() == R.id.btnRemoveProductFromOrder){
            String productTV = productTextView.getText().toString();

            if(!productTV.isEmpty()){
                float test = (quantities[position] * price);
                total = total - test;
                quantities[position] = 0;
                productTextView.setText("");
                totalTextView.setText("Total: $" + formatter.format(total));
                Toast.makeText(getApplicationContext(),
                        product + " removed from this order", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Please select a product to remove it from the order", Toast.LENGTH_LONG).show();
            }
        }
    }
}
