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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.technocrats.capstone.adapters.ExpandableListAdapter;

public class CreateOrderActivity extends AppCompatActivity implements SetOrderQuantityDialog.SetOrderQuantityDialogListener,
        ExpandableListAdapter.ThreeLevelListViewListener, View.OnClickListener {

    String storeID;

    Button btnSubmit, btnCancel, btnAddProductToOrder, btnRemoveProductFromOrder;
    TextView storeNumber;
    TextView productTextView;
    TextView totalTextView;
    TextView dateDisplay;

    Toolbar toolbar;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    NumberFormat formatter;

    // declare lists to store all categories, subcategories, and products
    String[] listDataCategories;
    List<String[]> listDataSubcategories;
    List<LinkedHashMap<String, List<String>>> listDataProducts;

    // declare lists to store ordered products
    public static List<String> orderedItems;

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
    SharedPreferences.Editor sharedEditor;

    GlobalMethods globalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        globalMethods = new GlobalMethods(this);

        this.sharedPlace = getSharedPreferences("SharedPlace", MODE_PRIVATE);
        sharedEditor = this.sharedPlace.edit();
        globalMethods.checkIfLoggedIn();

        setTitle("Create Order");
        dateDisplay = findViewById(R.id.txtDateDisplay);
        toolbar = findViewById(R.id.createOrderToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        globalMethods.DisplayDate(dateDisplay);

        formatter = new DecimalFormat("#,###.##");

        // get the listview
        expListView = findViewById(R.id.expandableListView);

        // initialize category list with hardcoded data
        listDataCategories = new String[]{"Food", "N/A", "Paper", "Advertising", "Cleaning",
                "Miscellaneous", "Uniforms", "inventory"};

        // initialize subcategories list
        listDataSubcategories = new ArrayList<>();

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
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        productTextView = (TextView) findViewById(R.id.productTextView);

        btnSubmit = (Button) findViewById(R.id.btnGoToOrderSummary);
        btnCancel = (Button) findViewById(R.id.btnCancelOrder);
        btnRemoveProductFromOrder = (Button) findViewById(R.id.btnRemoveProductFromOrder);
        btnAddProductToOrder = (Button) findViewById(R.id.btnAddProductToOrder);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnRemoveProductFromOrder.setOnClickListener(this);
        btnAddProductToOrder.setOnClickListener(this);

        orderedItems = new ArrayList<>();

        Intent mIntent = getIntent();
        String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");
        if (previousActivity != null && previousActivity.equals("ORDER_SUMMARY")) {
            orderedItems = OrderSummaryActivity.getOrderSummaryItems();
            setupItems();
        } else {
            totalTextView.setText("Order Total: $0.00");
        }

        storeID = sharedPlace.getString("storeID", "");
        String displayStore = "Create New Order for Store Number: " + storeID;
        storeNumber.setText(displayStore);

        getProducts();
    }

    public void setupItems(){
        float temp_total = 0f;
        for(int x = 0; x<orderedItems.size(); x++){
            String[] temp = orderedItems.get(x).split("@");
            String tempTotal = temp[4];
            float fTotal = Float.parseFloat(tempTotal);

            temp_total += fTotal;
        }
        totalTextView.setText("Order Total: $" + formatter.format(temp_total));
    }

    public void getProducts(){
        String url ="https://f8a6792c.ngrok.io/?a=select%20*%20from%20products";
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

                            jsonarrayProducts = new JSONArray(response);

                            for (int i = 0; i < jsonarrayProducts.length(); i++)
                            {
                                JSONObject jsonobject = jsonarrayProducts.getJSONObject(i);

                                String product = jsonobject.getString("product");
                                String product_id = jsonobject.getString("product_id");
                                String price = jsonobject.getString("unit_cost");
                                int subcategory_id = jsonobject.getInt("subcategory_id");
                                float quantity = 0f;

                                for(int x = 0; x<orderedItems.size(); x++){
                                    String[] temp = orderedItems.get(x).split("@");
                                    String tempQuantity = temp[2];
                                    if (product_id.equals(temp[0])){
                                        quantity = Float.parseFloat(tempQuantity);
                                    }
                                }
                                String listString = product_id + " - " + product + " : " + quantity + "  *  $" + price;

                                switch(subcategory_id) {
                                    case 1:
                                        SugarAndShortening.add(listString);
                                        break;
                                    case 2:
                                        Fillings.add(listString);
                                        break;
                                    case 3:
                                        Drinks.add(listString);
                                        break;
                                    case 4:
                                        CansAndHomeBrew.add(listString);
                                        break;
                                    case 5:
                                        SoupSandwiches.add(listString);
                                        break;
                                    case 6:
                                        FoodIngredients.add(listString);
                                        break;
                                    case 7:
                                        Produce.add(listString);
                                        break;
                                    case 8:
                                        NA.add(listString);
                                        break;
                                    case 9:
                                        Bread.add(listString);
                                        break;
                                    case 10:
                                        Emulsions.add(listString);
                                        break;
                                    case 11:
                                        danis.add(listString);
                                        break;
                                    case 12:
                                        MustardSpread.add(listString);
                                        break;
                                    case 13:
                                        Toppings.add(listString);
                                        break;
                                    case 14:
                                        Paper.add(listString);
                                        break;
                                    case 15:
                                        HotDrinkCups.add(listString);
                                        break;
                                    case 16:
                                        IcedBeverageCupsLids.add(listString);
                                        break;
                                    case 17:
                                        Advertising.add(listString);
                                        break;
                                    case 18:
                                        coffeeBowlCleaner.add(listString);
                                        break;
                                    case 19:
                                        StoreSupplies.add(listString);
                                        break;
                                    case 20:
                                        StaffUniform.add(listString);
                                        break;
                                    case 21:
                                        Dairy.add(listString);
                                        break;
                                    default:
                                }
                            }

                            // initialize products list
                            listDataProducts = new ArrayList<>();

                            LinkedHashMap<String, List<String>> thirdLevelFood = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelNA = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelPaper = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelAdvertising = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelCleaning = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelMiscellaneous = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelUniforms = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelInventory = new LinkedHashMap<>();

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
    public void onFinalChildClick(int plpos, int slpos, int tlpos){
        //Toast.makeText(this, plpos + ", " + slpos + ", " + tlpos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinalItemClick(int plItem, String slItem, String tlItem) {
        String itemName = "";
        String itemPrice = "";
        String itemQuantity = "";
        String[] tempArray = tlItem.split(" {2}\\* {2}\\$");
        String tempFirstHalf = tempArray[0];
        String[] tempArray2 = tempFirstHalf.split(" : ");
        itemName = tempArray2[0];
        itemName = globalMethods.trimEnd(itemName);
        itemQuantity = tempArray2[1];
        itemQuantity = globalMethods.trimEnd(itemQuantity);
        itemPrice = tempArray[1];

        String productSelected = itemName + " : " + itemQuantity + "  *  $" + itemPrice;
        productTextView.setText(productSelected);
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

    // show dialog activity
    public void showDialog(String productName, String productPrice, String product_id, String quantity){
        SetOrderQuantityDialog setOrderQuantityDialog = new SetOrderQuantityDialog();

        Bundle args = new Bundle();
        args.putString("productName", productName);
        args.putString("productID", product_id);
        args.putString("productPrice", productPrice);
        args.putString("productQuantity", quantity);
        setOrderQuantityDialog.setArguments(args);

        setOrderQuantityDialog.show(getSupportFragmentManager(), "Order Quantity");
    }

    @Override
    public void applyProductOrderQuantity(String product_id, String product, float quantity, float price, float qTotal) {

        if(orderedItems.size()==0){
            orderedItems.add(product_id + "@" + product + "@" + quantity + "@" + price + "@" + qTotal);
        } else {
            String[] tempSplit;
            String newOrderLine;
            for(int y = 0; y < orderedItems.size(); y++) {
                tempSplit = orderedItems.get(y).split("@");
                String p_id = tempSplit[0];

                if(product_id.equals(p_id)){
                    newOrderLine = product_id + "@" + product + "@" + quantity + "@" + price + "@" + qTotal;
                    orderedItems.set(y, newOrderLine);
                } else {
                    orderedItems.add(product_id + "@" + product + "@" + quantity + "@" + price + "@" + qTotal);
                }
            }
        }

        float total = 0;
        String[] tempSplit2;
        for(int i = 0; i < orderedItems.size(); i++) {
            tempSplit2 = orderedItems.get(i).split("@");
            String sTotal = tempSplit2[4];
            float fTotal = Float.parseFloat(sTotal);

            total += fTotal;
        }

        String temp = "Order Total: $" + formatter.format(total);
        totalTextView.setText(temp);

        String productSelected = product_id + " - " + product + " : " + quantity + "  *  $" + price;
        productTextView.setText(productSelected);

        getProducts();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnGoToOrderSummary){
            if (orderedItems.size()>0) {
                startActivity(new Intent(
                        getApplicationContext(), OrderSummaryActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "ERROR: Empty Order", Toast.LENGTH_LONG).show();
            }
        } else if(view.getId() == R.id.btnCancelOrder){
            finish();
        } else if(view.getId() == R.id.btnRemoveProductFromOrder){
            String productTV = productTextView.getText().toString();

            if(!productTV.isEmpty()){
                String[] tempID = productTV.split(" - ");
                String id = tempID[0];

                for (int i = 0; i < orderedItems.size(); i++){
                    String[] temp = orderedItems.get(i).split("@");
                    String order_p_id = temp[0];
                    String order_p_name = temp[1];
                    float order_line_total = Float.parseFloat(temp[4]);

                    if(id.equals(order_p_id)){
                        orderedItems.remove(i);
                        Toast.makeText(getApplicationContext(),
                                order_p_name + " removed from this order", Toast.LENGTH_LONG).show();
                        productTextView.setText("");
                        setupItems();
                        getProducts();
                        break;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Nothing to remove", Toast.LENGTH_LONG).show();
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Please select a product to remove it from the order", Toast.LENGTH_LONG).show();
            }
        } else if(view.getId() == R.id.btnAddProductToOrder){
            String productTV = productTextView.getText().toString();

            if(!productTV.isEmpty()){
                String[] tempID = productTV.split(" - ");
                String id = tempID[0];
                String nameQuantityPrice = tempID[1];
                String[] tempNameQuantityPrice = nameQuantityPrice.split(" {2}\\* {2}\\$");
                String nameQuantity = tempNameQuantityPrice[0];
                String price = tempNameQuantityPrice[1];
                String[] tempQuantity = nameQuantity.split(" : ");
                String name = tempQuantity[0];
                String quantity = tempQuantity[1];

                showDialog(name, price, id, quantity);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please select a product to edit its quantity", Toast.LENGTH_LONG).show();
            }

        }
    }

    public static List<String> getOrderedItems(){
        return orderedItems;
    }
}
