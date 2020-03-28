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
import android.widget.ImageButton;
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

import io.github.technocrats.capstone.adapters.CreateOrderExpandableListAdapter;
import io.github.technocrats.capstone.models.Product;

public class CreateOrderActivity extends AppCompatActivity
        implements View.OnClickListener, CreateOrderExpandableListAdapter.ThreeLevelListViewListener,
        SetOrderQuantityDialog.SetOrderQuantityDialogListener {

    // declarations - global variables
    private ExpandableListView expandableListView;
    public static TextView productTextView;
    TextView tvProductName, tvProductCost, tvTotal, searchProductTextView, dateDisplayTextView, storeNumberTextView;
    Button btnSubmit;
    ImageButton btnAddProductToOrder;
    RequestQueue queue;
    static Product selectedProduct;
    NumberFormat formatter;
    CreateOrderExpandableListAdapter listAdapter;
    Toolbar toolbar;
    GlobalMethods globalMethods;

    // declare lists to store all categories, subcategories, and products
    String[] listCategories;
    List<String[]> listSubcategories;
    List<LinkedHashMap<String, List<Product>>> listProducts;

    // declare lists to store ordered products
    public static ArrayList<Product> orderedItems;

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

    // declare Shared Preferences
    private SharedPreferences sharedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        setTitle("Create Order");

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        // initialize SharedPreferences
        this.sharedPlace = getSharedPreferences("SharedPlace", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.createOrderToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        tvProductCost = (TextView) findViewById(R.id.tvProductCost);
        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvTotal = (TextView) findViewById(R.id.totalTextView);
        searchProductTextView = (TextView) findViewById(R.id.txtSearchProduct);
        dateDisplayTextView = findViewById(R.id.txtDateDisplay);
        storeNumberTextView = (TextView) findViewById(R.id.txtStoreNumber);
        btnSubmit = (Button) findViewById(R.id.btnGoToOrderSummary);
        btnAddProductToOrder = (ImageButton) findViewById(R.id.btnAddProductToOrder);

        // display date and store number
        globalMethods.DisplayDate(dateDisplayTextView);
        String storeId = sharedPlace.getString("storeID", "");
        storeNumberTextView.setText("Create New Order for Store Number: " + storeId);

        // set onClickListener
        btnSubmit.setOnClickListener(this);
        btnAddProductToOrder.setOnClickListener(this);
        searchProductTextView.setOnClickListener(this);

        // initialize queue
        queue = Volley.newRequestQueue(this);

        // initialize category list with hardcoded data
        listCategories = new String[]{"Food", "N/A", "Paper", "Advertising", "Cleaning",
                "Miscellaneous", "Uniforms", "inventory"};

        // initialize subcategories list
        listSubcategories = new ArrayList<>();

        // initialize list of ordered products
        orderedItems = new ArrayList<>();

        // initialize selected product
        selectedProduct = null;
        formatter = new DecimalFormat("#,###.##");

        // add lists of subcategories to List of all subcategories
        listSubcategories.add(sFood);
        listSubcategories.add(sNA);
        listSubcategories.add(sPaper);
        listSubcategories.add(sAdvertising);
        listSubcategories.add(sCleaning);
        listSubcategories.add(sMiscellaneous);
        listSubcategories.add(sUniforms);
        listSubcategories.add(sInventory);

        productTextView = (TextView) findViewById(R.id.productTextView);
        productTextView.setText("");

        getProducts();
    }

    private void getProducts() {

        String url ="https://huexinventory.ngrok.io/?a=select%20*%20from%20products&b=Capstone";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            // initialize products list
                            listProducts = new ArrayList<>();

                            List<Product> SugarAndShortening = new ArrayList<>();
                            List<Product> Fillings = new ArrayList<>();
                            List<Product> Drinks = new ArrayList<>();
                            List<Product> CansAndHomeBrew = new ArrayList<>();
                            List<Product> SoupSandwiches = new ArrayList<>();
                            List<Product> FoodIngredients = new ArrayList<>();
                            List<Product> Produce = new ArrayList<>();
                            List<Product> NA = new ArrayList<>();
                            List<Product> Bread = new ArrayList<>();
                            List<Product> Emulsions = new ArrayList<>();
                            List<Product> danis = new ArrayList<>();
                            List<Product> MustardSpread = new ArrayList<>();
                            List<Product> Toppings = new ArrayList<>();
                            List<Product> Paper = new ArrayList<>();
                            List<Product> HotDrinkCups = new ArrayList<>();
                            List<Product> IcedBeverageCupsLids = new ArrayList<>();
                            List<Product> Advertising = new ArrayList<>();
                            List<Product> coffeeBowlCleaner = new ArrayList<>();
                            List<Product> StoreSupplies = new ArrayList<>();
                            List<Product> StaffUniform = new ArrayList<>();
                            List<Product> Dairy = new ArrayList<>();

                            LinkedHashMap<String, List<Product>> thirdLevelFood = new LinkedHashMap<>();
                            LinkedHashMap<String, List<Product>> thirdLevelNA = new LinkedHashMap<>();
                            LinkedHashMap<String, List<Product>> thirdLevelPaper = new LinkedHashMap<>();
                            LinkedHashMap<String, List<Product>> thirdLevelAdvertising = new LinkedHashMap<>();
                            LinkedHashMap<String, List<Product>> thirdLevelCleaning = new LinkedHashMap<>();
                            LinkedHashMap<String, List<Product>> thirdLevelMiscellaneous = new LinkedHashMap<>();
                            LinkedHashMap<String, List<Product>> thirdLevelUniforms = new LinkedHashMap<>();
                            LinkedHashMap<String, List<Product>> thirdLevelInventory = new LinkedHashMap<>();

                            JSONArray objArray = new JSONArray(response);
                            int objArrayLength = objArray.length();

                            // check if response is not empty
                            if (objArrayLength > 0)
                            {
                                for (int i = 0; i < objArray.length(); i++)
                                {
                                    JSONObject obj = objArray.getJSONObject(i);

                                    String productId = obj.getString("product_id");
                                    String productName = obj.getString("product");
                                    int subcategory_id = obj.getInt("subcategory_id");
                                    int category_id = obj.getInt("category_id");
                                    String unitCost = obj.getString("unit_cost");
                                    float fUnitCost = Float.parseFloat(unitCost);
                                    float quantity = 0f;;

                                    // check if product is already in orderedItems list
                                    for (int j = 0; j < orderedItems.size(); j++){
                                        // get quantity from list
                                        if (orderedItems.get(j).getProductId().equals(productId)) {
                                            quantity = orderedItems.get(j).getQuantity();
                                            // Log.d("QUANTITY", "product quantity: " + quantity);
                                        }
                                    }

                                    // create new product
                                    Product product = new Product(productId, productName, fUnitCost,
                                            quantity, subcategory_id, category_id);

                                    switch(product.getSubcategory()) {
                                        case 1:
                                            SugarAndShortening.add(product);
                                            break;
                                        case 2:
                                            Fillings.add(product);
                                            break;
                                        case 3:
                                            Drinks.add(product);
                                            break;
                                        case 4:
                                            CansAndHomeBrew.add(product);
                                            break;
                                        case 5:
                                            SoupSandwiches.add(product);
                                            break;
                                        case 6:
                                            FoodIngredients.add(product);
                                            break;
                                        case 7:
                                            Produce.add(product);
                                            break;
                                        case 8:
                                            NA.add(product);
                                            break;
                                        case 9:
                                            Bread.add(product);
                                            break;
                                        case 10:
                                            Emulsions.add(product);
                                            break;
                                        case 11:
                                            danis.add(product);
                                            break;
                                        case 12:
                                            MustardSpread.add(product);
                                            break;
                                        case 13:
                                            Toppings.add(product);
                                            break;
                                        case 14:
                                            Paper.add(product);
                                            break;
                                        case 15:
                                            HotDrinkCups.add(product);
                                            break;
                                        case 16:
                                            IcedBeverageCupsLids.add(product);
                                            break;
                                        case 17:
                                            Advertising.add(product);
                                            break;
                                        case 18:
                                            coffeeBowlCleaner.add(product);
                                            break;
                                        case 19:
                                            StoreSupplies.add(product);
                                            break;
                                        case 20:
                                            StaffUniform.add(product);
                                            break;
                                        case 21:
                                            Dairy.add(product);
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

                                listProducts.add(thirdLevelFood);
                                listProducts.add(thirdLevelNA);
                                listProducts.add(thirdLevelPaper);
                                listProducts.add(thirdLevelAdvertising);
                                listProducts.add(thirdLevelCleaning);
                                listProducts.add(thirdLevelMiscellaneous);
                                listProducts.add(thirdLevelUniforms);
                                listProducts.add(thirdLevelInventory);

                            }

                            // set-up adapter and expandable list view
                            setUpAdapter();


                        } catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void setUpAdapter() {

        // declare and initialize adapter
        listAdapter = new CreateOrderExpandableListAdapter(this, listCategories,
                listSubcategories, listProducts, this);

        // attach adapter to expandable listview
        expandableListView.setAdapter(listAdapter);

        // show one list at a time
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

    }

    public static ArrayList<Product> getOrderedItems(){
        ArrayList<Product> order_items = orderedItems;
        return order_items;
    }

    @Override
    public void onFinalChildClick(int plpos, int slpos, int tlpos) {

    }


    @Override
    public void onFinalItemClick(String plItem, String slItem, final Product tlItem) {
        // set item clicked as selected product
        setProduct(tlItem);
    }

    public static void setProduct(Product product) {
        selectedProduct = product;

        productTextView.setText(product.getProductName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoToOrderSummary:
                goToOrderSummary();
                break;
            case R.id.btnAddProductToOrder:
                // display dialog box
                showSetOrderQuantityDialog(selectedProduct);
                break;
            case R.id.txtSearchProduct:
                startActivity(new Intent(getApplicationContext(), SearchProductActivity.class));
                break;
            default:
                break;
        }
    }

    public void goToOrderSummary() {
        if  (orderedItems.size() > 0 ) {
            Intent intent = new Intent(CreateOrderActivity.this, OrderSummaryActivity.class);
            intent.putParcelableArrayListExtra("orderedItems", orderedItems);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(getApplicationContext(), "Empty order.", Toast.LENGTH_LONG).show();
        }
    }

    private void showSetOrderQuantityDialog(Product selectedProduct) {
        SetOrderQuantityDialog setOrderQuantityDialog = new SetOrderQuantityDialog();

        Bundle args = new Bundle();
        args.putString("productName", selectedProduct.getProductName());
        args.putString("productID", selectedProduct.getProductId());
        args.putFloat("unitCost", selectedProduct.getUnitCost());
        args.putFloat("quantity", selectedProduct.getQuantity());
        args.putInt("category", selectedProduct.getCategory());
        args.putInt("subcategory", selectedProduct.getSubcategory());
        setOrderQuantityDialog.setArguments(args);

        setOrderQuantityDialog.show(getSupportFragmentManager(), "Order Quantity");
    }

    @Override
    public void applyProductOrderQuantity(Product product, float updatedQuantity, float qTotal) {
        // update product's quantity
        product.setQuantity(updatedQuantity);

        // check if product is already part of orderedItems
        int index = 0;
        boolean itemFound = false;

        // check if selected product has been added to order
        for (int i = 0; i < orderedItems.size(); i++){
            if (orderedItems.get(i).getProductId().equals(product.getProductId())) {
                index = i;
                itemFound = true;
            }
        }

        if (itemFound) {
            // update product quantity on list
            orderedItems.set(index, product);
        }
        else {
            orderedItems.add(product);
        }

        // display success message
        Toast.makeText(getApplicationContext(), product.getProductName() +
                " has been added to order.", Toast.LENGTH_LONG).show();

        // re-calculate order total
        displayOrderTotal();

        // display products
        getProducts();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                orderedItems = data.getParcelableArrayListExtra("orderedItems");
                getProducts();
                displayOrderTotal();
            }
        }
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
}
