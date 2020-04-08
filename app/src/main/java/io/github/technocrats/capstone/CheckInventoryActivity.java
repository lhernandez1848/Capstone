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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.technocrats.capstone.adapters.CheckInventoryValueExpandableListAdapter;
import io.github.technocrats.capstone.adapters.ExpandableListAdapter;
import io.github.technocrats.capstone.models.Category;
import io.github.technocrats.capstone.models.Product;
import io.github.technocrats.capstone.models.Subcategory;

public class CheckInventoryActivity extends AppCompatActivity implements
        ExpandableListAdapter.ThreeLevelListViewListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener, OnChartValueSelectedListener,
        CheckInventoryValueExpandableListAdapter.ThreeLevelListViewListener {

    private CheckInventoryValueExpandableListAdapter listAdapter;
    private ExpandableListView expListView;

    // declaration of widgets
    PieChart pieChart;
    LinearLayout inventoryValuesLayout, inventoryProportionsLayout, categorySpinnerLayout;
    TextView totalTextView, selectDate, tvCheckInvError;
    Toolbar toolbar;
    RadioGroup radioGroup;
    RadioButton rdValue, rdProportion;
    Spinner categoriesSpinner;
    ProgressBar progressBar;
    Button checkInventoryButton;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    // declaration of arrays and lists
    String[] categoriesForSpinner;
    List<String[]> listDataSubcategoriesProportion;
    List<Category> listDataCategories;
    List<List<Subcategory>> listDataSubcategories;
    List<LinkedHashMap<Subcategory, List<Product>>> listDataProducts;

    // declare and initialize lists of subcategories per categories for Proportions
    String[] yFood = new String[]{"Sugar and Shortening", "Fillings", "Drinks",
            "Cans and Home Brew", "Soup and Sandwiches", "Food Ingredients", "Produce",
            "Bread", "Emulsions and Paste", "danis", "mustard spread", "Toppings"};
    String[] yNA = new String[]{"N/A"};
    String[] yPaper = new String[]{"Paper - Other Packaging", "Hot Drink Cups",
            "Iced Beverage Cups/Lids"};
    String[] yAdvertising = new String[]{"Advertising"};
    String[] yCleaning = new String[]{"coffee bowl cleaner"};
    String[] yMiscellaneous = new String[]{"Store Supplies"};
    String[] yUniforms = new String[]{"Staff Uniform"};
    String[] yInventory = new String[]{"Dairy"};

    JSONArray jsonarrayProducts;

    // declaration of global variables
    GlobalMethods globalMethods;
    int currentYear, currentMonth, currentDay, selectedYear, selectedMonth, selectedDay, category_id;
    float totalValue;
    float[] yData;
    String[] xData;
    String radioSelected = "";
    NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_inventory);

        globalMethods = new GlobalMethods(this);

        globalMethods.checkIfLoggedIn();

        setTitle("Check Inventory");
        toolbar = findViewById(R.id.checkInventoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // initialization of widgets
        inventoryValuesLayout = (LinearLayout) findViewById(R.id.inventoryValuesLayout);
        inventoryProportionsLayout = (LinearLayout) findViewById(R.id.inventoryProportionsLayout);
        categorySpinnerLayout = (LinearLayout) findViewById(R.id.categorySpinnerLayout);
        radioGroup = (RadioGroup) findViewById(R.id.radioButtonsLayout);
        rdValue = (RadioButton) findViewById(R.id.radio_value);
        rdProportion = (RadioButton) findViewById(R.id.radio_proportion);
        expListView = findViewById(R.id.checkInventoryExpandableListView);
        totalTextView = (TextView) findViewById(R.id.totalCheckInvTextView);
        tvCheckInvError = (TextView) findViewById(R.id.tvCheckInvError);
        selectDate = (TextView) findViewById(R.id.txtSortDatePicker);
        categoriesSpinner = (Spinner) findViewById(R.id.checkInvCategorySpinner);
        pieChart = (PieChart) findViewById(R.id.inventoryPieChart);
        progressBar = (ProgressBar) findViewById((R.id.simpleProgressBar));
        checkInventoryButton = (Button) findViewById((R.id.checkInventoryButton));

        // set listeners
        radioGroup.clearCheck();
        rdValue.setOnClickListener(this);
        rdProportion.setOnClickListener(this);
        categoriesSpinner.setOnItemSelectedListener(this);
        selectDate.setOnClickListener(this);
        checkInventoryButton.setOnClickListener(this);

        // initialize category list
        listDataCategories = new ArrayList<>();
        categoriesForSpinner = new String[]{"Select Category", "Food", "N/A", "Paper", "Advertising",
                "Cleaning", "Miscellaneous", "Uniforms", "inventory"};

        // initialize subcategories list
        listDataSubcategories = new ArrayList<>();
        listDataSubcategoriesProportion = new ArrayList<>();

        // initialize products list
        listDataProducts = new ArrayList<>();

        // add lists of subcategories to List of all subcategories for proportions
        listDataSubcategoriesProportion.add(yFood);
        listDataSubcategoriesProportion.add(yNA);
        listDataSubcategoriesProportion.add(yPaper);
        listDataSubcategoriesProportion.add(yAdvertising);
        listDataSubcategoriesProportion.add(yCleaning);
        listDataSubcategoriesProportion.add(yMiscellaneous);
        listDataSubcategoriesProportion.add(yUniforms);
        listDataSubcategoriesProportion.add(yInventory);

        formatter = new DecimalFormat("#,###.##");

        totalValue = 0;
        category_id = 0;

        // set pie chart properties
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(20);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(20);
        pieChart.getDescription().setEnabled(false);
    }

    // load the spinner
    public void loadSpinner(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categoriesForSpinner);
        categoriesSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //save selected items to variables
        if (adapterView.getId() == R.id.checkInvCategorySpinner){
            category_id = (int) adapterView.getItemIdAtPosition(i);
        }
        if (category_id > 0 && selectedDay > 0) {
            checkInventoryButton.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

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
    public void onClick(View view) {
        if (view.getId() == R.id.radio_value){
            inventoryProportionsLayout.setVisibility(View.GONE);
            categorySpinnerLayout.setVisibility(View.GONE);
            if(selectedDay > 0){
                checkInventoryButton.setEnabled(true);
                radioSelected = "value";
            } else {
                checkInventoryButton.setEnabled(false);
            }
        } else if (view.getId() == R.id.radio_proportion){
            checkInventoryButton.setEnabled(false);
            inventoryValuesLayout.setVisibility(View.GONE);
            categorySpinnerLayout.setVisibility(View.VISIBLE);
            radioSelected = "proportion";
            loadSpinner();
        } else if (view.getId() == R.id.txtSortDatePicker){
            setDate();
        } else if (view.getId() == R.id.checkInventoryButton){
            if(radioSelected.equals("value")){
                tvCheckInvError.setVisibility(View.GONE);
                inventoryValuesLayout.setVisibility(View.VISIBLE);
                getProductValues(selectedDay, selectedMonth, selectedYear);
            } else if (radioSelected.equals("proportion")) {
                tvCheckInvError.setVisibility(View.GONE);
                inventoryProportionsLayout.setVisibility(View.VISIBLE);
                getProductProportions(category_id, selectedDay, selectedMonth, selectedYear);
            } else {
                tvCheckInvError.setVisibility(View.VISIBLE);
                tvCheckInvError.setText("Please select a \"Sort By value\"");
            }
        }

    }

    public void setDate(){
        final Calendar calendar = Calendar.getInstance();

        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                selectedDay = day;
                selectedMonth = month + 1;
                selectedYear = year;
                String dateSet = selectedDay + "/" + selectedMonth + "/" + selectedYear;

                selectDate.setText(dateSet);

                if(radioSelected.equals("value")){
                    checkInventoryButton.setEnabled(true);
                } else if (radioSelected.equals("proportion") && (category_id > 0)) {
                    checkInventoryButton.setEnabled(true);
                }
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.DateSelector, dateSetListener,
                currentYear, currentMonth, currentDay);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    // get products and set up list views for sorting by values
    public void getProductValues(int day, int month, int year){
        progressBar.setVisibility(View.VISIBLE);
        listDataCategories.clear();
        listDataSubcategories.clear();
        listDataProducts.clear();

        String url ="https://huexinventory.ngrok.io/?a=SELECT%20products.product_id,products.product,products.category_id,categories.category,products.subcategory_id,subcategories.subcategory,products.unit_cost,inventories.quantity%20FROM%20products%20LEFT%20JOIN%20inventories%20ON%20products.product_id=inventories.product_id%20and%20year="+year+"%20and%20month="+month+"%20and%20day="+day+"%20JOIN%20categories%20ON%20products.category_id=categories.category_id%20JOIN%20subcategories%20ON%20products.subcategory_id=subcategories.subcategory_id&b=Capstone";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // declare and initialize variables used for storing subcategory values
                            float sugarValue = 0;
                            float fillingsValue = 0;
                            float drinksValue = 0;
                            float cansValue = 0;
                            float soupValue = 0;
                            float foodIngValue = 0;
                            float produceValue = 0;
                            float naValue = 0;
                            float breadValue = 0;
                            float emulsionsValue = 0;
                            float danisValue = 0;
                            float mustardValue = 0;
                            float toppingsValue = 0;
                            float paperValue = 0;
                            float hotDrinksValue = 0;
                            float icedValue = 0;
                            float advertisingValue = 0;
                            float coffeeCleanerValue = 0;
                            float storeSuppliesValue = 0;
                            float uniformValue = 0;
                            float dairyValue = 0;

                            // declare and initialize variables used for storing category values
                            float food = 0;
                            float na = 0;
                            float paper = 0;
                            float advertising = 0;
                            float cleaning = 0;
                            float miscellaneous = 0;
                            float uniforms = 0;
                            float inventory = 0;

                            // declare and initialize lists of subcategories per categories for Values
                            List<Subcategory> sFood = new ArrayList<>();
                            List<Subcategory> sNA = new ArrayList<>();
                            List<Subcategory> sPaper = new ArrayList<>();
                            List<Subcategory> sAdvertising = new ArrayList<>();
                            List<Subcategory> sCleaning = new ArrayList<>();
                            List<Subcategory> sMiscellaneous = new ArrayList<>();
                            List<Subcategory> sUniforms = new ArrayList<>();
                            List<Subcategory> sInventory = new ArrayList<>();

                            // lists for storing products per subcategory
                            List<Product> SugarAndShortening = new ArrayList<Product>();
                            List<Product> Fillings = new ArrayList<Product>();
                            List<Product> Drinks = new ArrayList<Product>();
                            List<Product> CansAndHomeBrew = new ArrayList<Product>();
                            List<Product> SoupSandwiches = new ArrayList<Product>();
                            List<Product> FoodIngredients = new ArrayList<Product>();
                            List<Product> Produce = new ArrayList<Product>();
                            List<Product> NA = new ArrayList<Product>();
                            List<Product> Bread = new ArrayList<Product>();
                            List<Product> Emulsions = new ArrayList<Product>();
                            List<Product> danis = new ArrayList<Product>();
                            List<Product> MustardSpread = new ArrayList<Product>();
                            List<Product> Toppings = new ArrayList<Product>();
                            List<Product> Paper = new ArrayList<Product>();
                            List<Product> HotDrinkCups = new ArrayList<Product>();
                            List<Product> IcedBeverageCupsLids = new ArrayList<Product>();
                            List<Product> Advertising = new ArrayList<Product>();
                            List<Product> CoffeeBowlCleaner = new ArrayList<Product>();
                            List<Product> StoreSupplies = new ArrayList<Product>();
                            List<Product> StaffUniform = new ArrayList<Product>();
                            List<Product> Dairy = new ArrayList<Product>();

                            // linked hashmap for storing lists of subcategories lists of products
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelFood = new LinkedHashMap<>();
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelNA = new LinkedHashMap<>();
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelPaper = new LinkedHashMap<>();
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelAdvertising = new LinkedHashMap<>();
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelCleaning = new LinkedHashMap<>();
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelMiscellaneous = new LinkedHashMap<>();
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelUniforms = new LinkedHashMap<>();
                            LinkedHashMap<Subcategory, List<Product>> thirdLevelInventory = new LinkedHashMap<>();

                            jsonarrayProducts = new JSONArray(response);

                            for (int i = 0; i < jsonarrayProducts.length(); i++)
                            {
                                JSONObject jsonobject = jsonarrayProducts.getJSONObject(i);

                                String productId = jsonobject.getString("product_id");
                                String productName = jsonobject.getString("product");
                                String sPrice = jsonobject.getString("unit_cost");
                                float fPrice = Float.parseFloat(sPrice);
                                String sQuantity = jsonobject.getString("quantity");
                                float fQuantity = 0;
                                if(!sQuantity.equals("null")){
                                    fQuantity = Float.parseFloat(sQuantity);
                                }
                                int subcategory_id = jsonobject.getInt("subcategory_id");
                                String subcategory_name = jsonobject.getString("subcategory");
                                String category_name = jsonobject.getString("category");
                                int category_id = jsonobject.getInt("category_id");

                                totalValue += fPrice*fQuantity;

                                Product tProduct = new Product(productId, productName, fPrice,
                                        fQuantity, subcategory_id, category_id);

                                switch (category_id){
                                    case 1:
                                        food = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, food));
                                        break;
                                    case 2:
                                        na = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, na));
                                        break;
                                    case 3:
                                        paper = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, paper));
                                        break;
                                    case 4:
                                        advertising = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, advertising));
                                        break;
                                    case 5:
                                        cleaning = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, cleaning));
                                        break;
                                    case 6:
                                        miscellaneous = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, miscellaneous));
                                        break;
                                    case 7:
                                        uniforms = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, uniforms));
                                        break;
                                    case 8:
                                        inventory = (fPrice*fQuantity);
                                        listDataCategories.add(new Category(category_id, category_name, inventory));
                                        break;
                                        default:
                                }

                                switch(subcategory_id) {
                                    case 1:
                                        SugarAndShortening.add(tProduct);
                                        sugarValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, sugarValue));
                                        break;
                                    case 2:
                                        Fillings.add(tProduct);
                                        fillingsValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, fillingsValue));
                                        break;
                                    case 3:
                                        Drinks.add(tProduct);
                                        drinksValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, drinksValue));
                                        break;
                                    case 4:
                                        CansAndHomeBrew.add(tProduct);
                                        cansValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, cansValue));
                                        break;
                                    case 5:
                                        SoupSandwiches.add(tProduct);
                                        soupValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, soupValue));
                                        break;
                                    case 6:
                                        FoodIngredients.add(tProduct);
                                        foodIngValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, foodIngValue));
                                        break;
                                    case 7:
                                        Produce.add(tProduct);
                                        produceValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, produceValue));
                                        break;
                                    case 8:
                                        NA.add(tProduct);
                                        naValue = (fPrice*fQuantity);
                                        sNA.add(new Subcategory(subcategory_id, subcategory_name, naValue));
                                        break;
                                    case 9:
                                        Bread.add(tProduct);
                                        breadValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, breadValue));
                                        break;
                                    case 10:
                                        Emulsions.add(tProduct);
                                        emulsionsValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, emulsionsValue));
                                        break;
                                    case 11:
                                        danis.add(tProduct);
                                        danisValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, danisValue));
                                        break;
                                    case 12:
                                        MustardSpread.add(tProduct);
                                        mustardValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, mustardValue));
                                        break;
                                    case 13:
                                        Toppings.add(tProduct);
                                        toppingsValue = (fPrice*fQuantity);
                                        sFood.add(new Subcategory(subcategory_id, subcategory_name, toppingsValue));
                                        break;
                                    case 14:
                                        Paper.add(tProduct);
                                        paperValue = (fPrice*fQuantity);
                                        sPaper.add(new Subcategory(subcategory_id, subcategory_name, paperValue));
                                        break;
                                    case 15:
                                        HotDrinkCups.add(tProduct);
                                        hotDrinksValue = (fPrice*fQuantity);
                                        sPaper.add(new Subcategory(subcategory_id, subcategory_name, hotDrinksValue));
                                        break;
                                    case 16:
                                        IcedBeverageCupsLids.add(tProduct);
                                        icedValue = (fPrice*fQuantity);
                                        sPaper.add(new Subcategory(subcategory_id, subcategory_name, icedValue));
                                        break;
                                    case 17:
                                        Advertising.add(tProduct);
                                        advertisingValue = (fPrice*fQuantity);
                                        sAdvertising.add(new Subcategory(subcategory_id, subcategory_name, advertisingValue));
                                        break;
                                    case 18:
                                        CoffeeBowlCleaner.add(tProduct);
                                        coffeeCleanerValue = (fPrice*fQuantity);
                                        sCleaning.add(new Subcategory(subcategory_id, subcategory_name, coffeeCleanerValue));
                                        break;
                                    case 19:
                                        StoreSupplies.add(tProduct);
                                        storeSuppliesValue = (fPrice*fQuantity);
                                        sMiscellaneous.add(new Subcategory(subcategory_id, subcategory_name, storeSuppliesValue));
                                        break;
                                    case 20:
                                        StaffUniform.add(tProduct);
                                        uniformValue = (fPrice*fQuantity);
                                        sUniforms.add(new Subcategory(subcategory_id, subcategory_name, uniformValue));
                                        break;
                                    case 21:
                                        Dairy.add(tProduct);
                                        dairyValue = (fPrice*fQuantity);
                                        sInventory.add(new Subcategory(subcategory_id, subcategory_name, dairyValue));
                                        break;
                                    default:
                                }
                            }


                            // remove duplicated names and add values
                            // categories
                            checkCategoryDuplicates(listDataCategories);

                            // subcategories
                            checkSubcategoryDuplicates(sFood);
                            checkSubcategoryDuplicates(sNA);
                            checkSubcategoryDuplicates(sPaper);
                            checkSubcategoryDuplicates(sAdvertising);
                            checkSubcategoryDuplicates(sCleaning);
                            checkSubcategoryDuplicates(sMiscellaneous);
                            checkSubcategoryDuplicates(sUniforms);
                            checkSubcategoryDuplicates(sInventory);

                            // add subcategories to the list of subcategories
                            listDataSubcategories.add(sFood);
                            listDataSubcategories.add(sNA);
                            listDataSubcategories.add(sPaper);
                            listDataSubcategories.add(sAdvertising);
                            listDataSubcategories.add(sCleaning);
                            listDataSubcategories.add(sMiscellaneous);
                            listDataSubcategories.add(sUniforms);
                            listDataSubcategories.add(sInventory);

                            // add subcategories and products to its corresponding linkedhashmap
                            thirdLevelFood.put(sFood.get(0), SugarAndShortening);
                            thirdLevelFood.put(sFood.get(1), Fillings);
                            thirdLevelFood.put(sFood.get(2), Drinks);
                            thirdLevelFood.put(sFood.get(3), CansAndHomeBrew);
                            thirdLevelFood.put(sFood.get(4), SoupSandwiches);
                            thirdLevelFood.put(sFood.get(5), FoodIngredients);
                            thirdLevelFood.put(sFood.get(6), Produce);
                            thirdLevelFood.put(sFood.get(7), Bread);
                            thirdLevelFood.put(sFood.get(8), Emulsions);
                            thirdLevelFood.put(sFood.get(9), danis);
                            thirdLevelFood.put(sFood.get(10), MustardSpread);
                            thirdLevelFood.put(sFood.get(11), Toppings);
                            thirdLevelNA.put(sNA.get(0), NA);
                            thirdLevelPaper.put(sPaper.get(0), Paper);
                            thirdLevelPaper.put(sPaper.get(1), HotDrinkCups);
                            thirdLevelPaper.put(sPaper.get(2), IcedBeverageCupsLids);
                            thirdLevelAdvertising.put(sAdvertising.get(0), Advertising);
                            thirdLevelCleaning.put(sCleaning.get(0), CoffeeBowlCleaner);
                            thirdLevelMiscellaneous.put(sMiscellaneous.get(0), StoreSupplies);
                            thirdLevelUniforms.put(sUniforms.get(0), StaffUniform);
                            thirdLevelInventory.put(sInventory.get(0), Dairy);

                            listDataProducts.add(thirdLevelFood);
                            listDataProducts.add(thirdLevelNA);
                            listDataProducts.add(thirdLevelPaper);
                            listDataProducts.add(thirdLevelAdvertising);
                            listDataProducts.add(thirdLevelCleaning);
                            listDataProducts.add(thirdLevelMiscellaneous);
                            listDataProducts.add(thirdLevelUniforms);
                            listDataProducts.add(thirdLevelInventory);

                         doList();
                         totalTextView.setText("Total Inventory Value:  $" + formatter.format(totalValue));

                        } catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        progressBar.setVisibility(View.INVISIBLE);
    }

    // checks list of categories for duplicates and calculates value accordingly
    public void checkCategoryDuplicates(List<Category> category) {
        if (category.size() > 0) {
            List<Category> deleteCandidates = new ArrayList<>();

            // Pass 1 - collect delete candidates
            for (int i = 0; i < category.size() - 1; i++) {
                for (int k = i + 1; k < category.size(); k++) {
                    if (category.get(i).getCategory_id() == category.get(k).getCategory_id()) {
                        category.get(i).setValue(category.get(i).getValue() + category.get(k).getValue());
                        deleteCandidates.add(category.get(k));
                    }
                }
            }

            // Pass 2 - delete
            for (Category deleteCandidate : deleteCandidates) {
                category.remove(deleteCandidate);
            }
        }
    }

    // checks list of subcategories for duplicates and calculates value accordingly
    public static void checkSubcategoryDuplicates(List<Subcategory> subcategory) {
        if (subcategory.size() > 0) {
            List<Subcategory> deleteCandidates = new ArrayList<>();

            // Pass 1 - collect delete candidates
            for (int i = 0; i < subcategory.size() - 1; i++) {
                for (int k = i + 1; k < subcategory.size(); k++) {
                    if (subcategory.get(i).getSubcategory_id() == subcategory.get(k).getSubcategory_id()) {
                        subcategory.get(i).setValue(subcategory.get(i).getValue() + subcategory.get(k).getValue());
                        deleteCandidates.add(subcategory.get(k));
                    }
                }
            }

            // Pass 2 - delete
            for (Subcategory deleteCandidate : deleteCandidates) {
                subcategory.remove(deleteCandidate);
            }
        }
    }

    // get products and set up list views for sorting by proportions
    public void getProductProportions(final int cat_id, int day, int month, int year){
        String url ="https://huexinventory.ngrok.io/?a=select%20quantity,inventories.unit_cost,inventories.product_id,product,products.subcategory_id,subcategory,products.category_id,category%20from%20inventories%20join%20products%20on%20inventories.product_id=products.product_id%20join%20subcategories%20on%20subcategories.subcategory_id=products.subcategory_id%20join%20categories%20on%20categories.category_id=products.category_id%20where%20day="+day+"%20and%20month="+month+"%20and%20year="+year+"and%20products.category_id="+cat_id+"&b=Capstone";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            // declare and initialize variables used for storing subcategory Quantity
                            float sugarQuantity = 0;
                            float fillingsQuantity = 0;
                            float drinksQuantity = 0;
                            float cansQuantity = 0;
                            float soupQuantity = 0;
                            float foodIngQuantity = 0;
                            float produceQuantity = 0;
                            float naQuantity = 0;
                            float breadQuantity = 0;
                            float emulsionsQuantity = 0;
                            float danisQuantity = 0;
                            float mustardQuantity = 0;
                            float toppingsQuantity = 0;
                            float paperQuantity = 0;
                            float hotDrinksQuantity = 0;
                            float icedQuantity = 0;
                            float advertisingQuantity = 0;
                            float coffeeCleanerQuantity = 0;
                            float storeSuppliesQuantity = 0;
                            float uniformQuantity = 0;
                            float dairyQuantity = 0;

                            String category_name = "";

                            jsonarrayProducts = new JSONArray(response);

                            for (int i = 0; i < jsonarrayProducts.length(); i++)
                            {
                                JSONObject jsonobject = jsonarrayProducts.getJSONObject(i);

                                String sPrice = jsonobject.getString("unit_cost");
                                float fPrice = Float.parseFloat(sPrice);
                                String sQuantity = jsonobject.getString("quantity");
                                float fQuantity = Float.parseFloat(sQuantity);
                                int subcategory_id = jsonobject.getInt("subcategory_id");
                                category_name = jsonobject.getString("category");

                                totalValue += fPrice*fQuantity;

                                switch(subcategory_id) {
                                    case 1:
                                        sugarQuantity += fQuantity;
                                        break;
                                    case 2:
                                        fillingsQuantity += fQuantity;
                                        break;
                                    case 3:
                                        drinksQuantity += fQuantity;
                                        break;
                                    case 4:
                                        cansQuantity += fQuantity;
                                        break;
                                    case 5:
                                        soupQuantity += fQuantity;
                                        break;
                                    case 6:
                                        foodIngQuantity += fQuantity;
                                        break;
                                    case 7:
                                        produceQuantity += fQuantity;
                                        break;
                                    case 8:
                                        naQuantity += fQuantity;
                                        break;
                                    case 9:
                                        breadQuantity += fQuantity;
                                        break;
                                    case 10:
                                        emulsionsQuantity += fQuantity;
                                        break;
                                    case 11:
                                        danisQuantity += fQuantity;
                                        break;
                                    case 12:
                                        mustardQuantity += fQuantity;
                                        break;
                                    case 13:
                                        toppingsQuantity += fQuantity;
                                        break;
                                    case 14:
                                        paperQuantity += fQuantity;
                                        break;
                                    case 15:
                                        hotDrinksQuantity += fQuantity;
                                        break;
                                    case 16:
                                        icedQuantity += fQuantity;
                                        break;
                                    case 17:
                                        advertisingQuantity += fQuantity;
                                        break;
                                    case 18:
                                        coffeeCleanerQuantity += fQuantity;
                                        break;
                                    case 19:
                                        storeSuppliesQuantity += fQuantity;
                                        break;
                                    case 20:
                                        uniformQuantity += fQuantity;
                                        break;
                                    case 21:
                                        dairyQuantity += fQuantity;
                                        break;
                                    default:
                                }

                                pieChart.setCenterText(category_name);
                            }

                            switch(cat_id) {
                                case 1:
                                    float[] categoryFoodQuantity = new float[12];
                                    categoryFoodQuantity[0] = sugarQuantity;
                                    categoryFoodQuantity[1] = fillingsQuantity;
                                    categoryFoodQuantity[2] = drinksQuantity;
                                    categoryFoodQuantity[3] = cansQuantity;
                                    categoryFoodQuantity[4] = soupQuantity;
                                    categoryFoodQuantity[5] = foodIngQuantity;
                                    categoryFoodQuantity[6] = produceQuantity;
                                    categoryFoodQuantity[7] = breadQuantity;
                                    categoryFoodQuantity[8] = emulsionsQuantity;
                                    categoryFoodQuantity[9] = danisQuantity;
                                    categoryFoodQuantity[10] = mustardQuantity;
                                    categoryFoodQuantity[11] = toppingsQuantity;
                                    xData = yFood;
                                    yData = categoryFoodQuantity;
                                    addDataSet();
                                    break;
                                case 2:
                                    float[] categoryNAQuantity = new float[1];
                                    categoryNAQuantity[0] = naQuantity;
                                    xData = yNA;
                                    yData = categoryNAQuantity;
                                    addDataSet();
                                    break;
                                case 3:
                                    float[] categoryPaperQuantity = new float[3];
                                    categoryPaperQuantity[0] = paperQuantity;
                                    categoryPaperQuantity[1] = hotDrinksQuantity;
                                    categoryPaperQuantity[2] = icedQuantity;
                                    xData = yPaper;
                                    yData = categoryPaperQuantity;
                                    addDataSet();
                                    break;
                                case 4:
                                    float[] categoryAdvertisingQuantity = new float[1];
                                    categoryAdvertisingQuantity[0] = advertisingQuantity;
                                    xData = yAdvertising;
                                    yData = categoryAdvertisingQuantity;
                                    addDataSet();
                                    break;
                                case 5:
                                    float[] categoryCleaningQuantity = new float[1];
                                    categoryCleaningQuantity[0] = coffeeCleanerQuantity;
                                    xData = yCleaning;
                                    yData = categoryCleaningQuantity;
                                    addDataSet();
                                    break;
                                case 6:
                                    float[] categoryMiscellaneousQuantity = new float[1];
                                    categoryMiscellaneousQuantity[0] = storeSuppliesQuantity;
                                    xData = yMiscellaneous;
                                    yData = categoryMiscellaneousQuantity;
                                    addDataSet();
                                    break;
                                case 7:
                                    float[] categoryUniformsQuantity = new float[1];
                                    categoryUniformsQuantity[0] = uniformQuantity;
                                    xData = yUniforms;
                                    yData = categoryUniformsQuantity;
                                    addDataSet();
                                    break;
                                case 8:
                                    float[] categoryInventoryQuantity = new float[1];
                                    categoryInventoryQuantity[0] = dairyQuantity;
                                    xData = yInventory;
                                    yData = categoryInventoryQuantity;
                                    addDataSet();
                                    break;
                                default:
                            }

                            totalTextView.setText("Total Inventory Value:  $" + formatter.format(totalValue));

                        } catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Function to remove the float element
    public static float[] removeFloatElement(float[] array, int index) {
        if (array == null
                || index < 0
                || index >= array.length) {

            return array;
        }

        float[] finalArray = new float[array.length - 1];

        for (int i = 0, k = 0; i < array.length; i++) {

            if (i == index) {
                continue;
            }

            finalArray[k++] = array[i];
        }

        // return the result array
        return finalArray;
    }

    // Function to remove the String element
    private String[] removeStringElement(String[] array, int index) {
        if (array == null
                || index < 0
                || index >= array.length) {

            return array;
        }

        String[] finalArray = new String[array.length - 1];

        for (int i = 0, k = 0; i < array.length; i++) {
            if (i == index) {
                continue;
            }
            finalArray[k++] = array[i];
        }

        // return the result array
        return finalArray;

    }

    // display pie chart
    private void addDataSet() {
        List<PieEntry> entries = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            if (yData[i] < 1){
                yData = removeFloatElement(yData, i);
                xData = removeStringElement(xData, i);
            }
            entries.add(new PieEntry(yData[i] , xData[i]));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(20);
        pieDataSet.setValueLinePart1OffsetPercentage(1.f);
        pieDataSet.setValueLinePart1Length(0.43f);
        pieDataSet.setValueLinePart2Length(.1f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieChart.setEntryLabelColor(Color.BLACK);

        //add colors to dataset
        List<Integer> colours = new ArrayList<>();
        // set colours
        colours.add(Color.BLUE);
        colours.add(Color.GRAY);
        colours.add(Color.GREEN);
        colours.add(Color.YELLOW);
        colours.add(Color.argb(99, 93, 225, 232));
        colours.add(Color.MAGENTA);
        colours.add(Color.LTGRAY);
        colours.add(Color.CYAN);
        colours.add(Color.RED);
        colours.add(Color.argb(99, 230, 119, 156));
        colours.add(Color.argb(100, 43, 130, 50));
        colours.add(Color.argb(100, 219, 187, 26));
        pieDataSet.setColors(colours);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    // display expandable list view
    public void doList(){
        listAdapter = new CheckInventoryValueExpandableListAdapter(this, listDataCategories,
                listDataSubcategories, listDataProducts, this);

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
    public void onFinalChildClick(int plpos, int slpos, int tlpos) {}

    @Override
    public void onFinalItemClick(String plItem, String slItem, Product tlItem) {

    }

    @Override
    public void onFinalItemClick(int plItem, String slItem, String tlItem) {}

    // on pie chart value selected
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int i = (int) h.getX();
        String subcategorySelected = xData[i];
        String dataSize = String.valueOf(xData.length);

        Intent intent = new Intent(getBaseContext(), ProductProportionActivity.class);
        intent.putExtra("subcategorySelected", subcategorySelected);
        intent.putExtra("selectedDay", selectedDay);
        intent.putExtra("selectedMonth", selectedMonth);
        intent.putExtra("selectedYear", selectedYear);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected() {}
}
