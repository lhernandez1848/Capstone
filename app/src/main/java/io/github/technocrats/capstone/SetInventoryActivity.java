package io.github.technocrats.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.technocrats.capstone.adapters.InventoryExpandableListAdapter;
import io.github.technocrats.capstone.models.Product;

public class SetInventoryActivity extends AppCompatActivity
        implements InventoryExpandableListAdapter.ThreeLevelListViewListener, SetInventoryDialog.SetInventoryDialogListener {

    // declarations - global variables
    private ExpandableListView expandableListView;
    Toolbar toolbar;
    RequestQueue queue;
    GlobalMethods globalMethods;

    // declare lists to store all categories, subcategories, and products
    String[] listCategories;
    List<String[]> listSubcategories;
    List<String> listProductIds;
    List<Product> listProductsTest;
    List<LinkedHashMap<String, List<Product>>> listProducts;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_inventory);
        setTitle("Set Inventory");

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        toolbar = findViewById(R.id.setInventoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // declarations - widgets
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        // initialize queue
        queue = Volley.newRequestQueue(this);

        // initialize category list with hardcoded data
        listCategories = new String[]{"Food", "N/A", "Paper", "Advertising", "Cleaning",
                "Miscellaneous", "Uniforms", "inventory"};

        // initialize subcategories list
        listSubcategories = new ArrayList<>();

        // initialize list of product ids
        listProductIds = new ArrayList<>();
        listProductsTest = new ArrayList<>();

        // initialize products list
        listProducts = new ArrayList<>();

        // add lists of subcategories to List of all subcategories
        listSubcategories.add(sFood);
        listSubcategories.add(sNA);
        listSubcategories.add(sPaper);
        listSubcategories.add(sAdvertising);
        listSubcategories.add(sCleaning);
        listSubcategories.add(sMiscellaneous);
        listSubcategories.add(sUniforms);
        listSubcategories.add(sInventory);


        getProducts();
    }

    public void getProducts(){
        String url ="https://huexinventory.ngrok.io/?a=select%20*%20from%20products&b=Capstone";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
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

                                    // create new product
                                    Product product = new Product(productId, productName, fUnitCost, 0, subcategory_id, category_id);

                                    switch(subcategory_id) {
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
        InventoryExpandableListAdapter listAdapter = new InventoryExpandableListAdapter(this, listCategories,
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

    @Override
    public void onFinalChildClick(int plpos, int slpos, int tlpos) {

    }

    @Override
    public void onFinalItemClick(String plItem, String slItem, final Product tlItem) {
        //
        // generate query to get latest quantity, date, and par from inventories
        //
        String productId = tlItem.getProductId();
        String server ="https://huexinventory.ngrok.io/?a=";
        String query = "SELECT top(1) * FROM inventories WHERE product_id " +
                "LIKE '" + productId + "' ORDER BY year DESC, month DESC, day DESC&b=Capstone";
        String url = server + query;

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray objArray = new JSONArray(response);
                            int objArrayLength = objArray.length();

                            // check if response is not empty
                            if (objArrayLength > 0) {
                                for (int i = 0; i < objArray.length(); i++) {
                                    JSONObject obj = objArray.getJSONObject(i);

                                    int day = obj.getInt("day");
                                    int month = obj.getInt("month");
                                    int year = obj.getInt("year");

                                    String date = formatDate(day, month, year);
                                    String quantity = obj.getString("quantity");
                                    float fQuantity = Float.parseFloat(quantity);
                                    String par = obj.getString("par");
                                    float fPar = Float.parseFloat(par);

                                    // show dialog on click
                                    showSetInventoryCountDialog(tlItem, date, fQuantity, fPar);

                                }
                            }
                        } catch (JSONException e){e.printStackTrace();}

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onFinalItemClick", "Error in getInventoryDetails method.");
                Toast.makeText(getApplicationContext(),
                        "An error occured. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private String formatDate(int day, int month, int year) {

        String sMonth = "";
        String formattedDate = "";

        // format month
        switch (month) {
            case 1:
                sMonth = "Jan";
                break;
            case 2:
                sMonth = "Feb";
                break;
            case 3:
                sMonth = "Mar";
                break;
            case 4:
                sMonth = "Apr";
                break;
            case 5:
                sMonth = "May";
                break;
            case 6:
                sMonth = "Jun";
                break;
            case 7:
                sMonth = "Jul";
                break;
            case 8:
                sMonth = "Aug";
                break;
            case 9:
                sMonth = "Sept";
                break;
            case 10:
                sMonth = "Oct";
                break;
            case 11:
                sMonth = "Nov";
                break;
            case 12:
                sMonth = "Dec";
                break;
            default:
                sMonth = "";
                break;
        }

        if (sMonth == "")
        {
            formattedDate = "";
        }
        else
        {
            formattedDate = day + "-" + sMonth + "-" + year;
        }

        return formattedDate;
    }

    private void showSetInventoryCountDialog(Product product, String date, float quantity, float par) {

        // declaration - set inventory dialog and arguments
        SetInventoryDialog inventoryDialog = new SetInventoryDialog();
        Bundle args = new Bundle();

        // set arguments
        args.putString("productName", product.getProductName());
        args.putString("productId", product.getProductId());
        args.putFloat("unitCost", product.getUnitCost());
        args.putString("date", date);
        args.putFloat("quantity", quantity);
        args.putFloat("par", par);
        args.putInt("subcategory_id", product.getSubcategory());
        args.putInt("category_id", product.getCategory());
        inventoryDialog.setArguments(args);

        // display set inventory dialog
        inventoryDialog.show(getSupportFragmentManager(), "Set Inventory Count");
    }

    @Override
    public void getInventoryCount(String count, Product product, float par) {

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        month = month + 1;


        // declarations and initializations of product's attributes
        String productId = product.getProductId();
        float unitCost = product.getUnitCost();

        // generate query
        String server ="https://huexinventory.ngrok.io/?a=";
        String query = "INSERT INTO inventories (product_id, par, unit_cost, " +
                "quantity, day, month, year, purchase) VALUES " +
                "(N'" + productId + "', " + par + ", " + unitCost + ", "
                + count + ", " + day + ", " + month + ", " + year + ",0.00)&b=Capstone";
        String url = server + query;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(),
                                "Inventory updated.", Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getInventoryCount", "Error in INSERT method.");
                Toast.makeText(getApplicationContext(),
                        "An error occured. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

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
}