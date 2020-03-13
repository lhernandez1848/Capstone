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
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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

import io.github.technocrats.capstone.adapters.ExpandableListAdapter;

public class CheckInventoryActivity extends AppCompatActivity implements
        ExpandableListAdapter.ThreeLevelListViewListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    PieChart pieChart;
    LinearLayout inventoryValuesLayout;
    TextView dateDisplay, totalTextView, selectDate;
    Toolbar toolbar;
    RadioGroup radioGroup;
    RadioButton rdValue, rdProportion;
    Spinner categoriesSpinner;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    GlobalMethods globalMethods;

    String[] listDataCategories, categoriesForSpinner;
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

    NumberFormat formatter;

    int currentYear, currentMonth, currentDay, selectedYear, selectedMonth, selectedDay, category_id;
    float totalValue;
    float[] yData;
    String[] xData;
    String radioSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_inventory);

        globalMethods = new GlobalMethods(this);

        globalMethods.checkIfLoggedIn();

        setTitle("Check Inventory");
        dateDisplay = findViewById(R.id.txtCheckInventoryDateDisplay);
        toolbar = findViewById(R.id.checkInventoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        globalMethods.DisplayDate(dateDisplay);

        inventoryValuesLayout = (LinearLayout) findViewById(R.id.inventoryValuesLayout);

        radioGroup = (RadioGroup) findViewById(R.id.radioButtonsLayout);
        rdValue = (RadioButton) findViewById(R.id.radio_value);
        rdProportion = (RadioButton) findViewById(R.id.radio_proportion);

        radioGroup.clearCheck();
        rdValue.setOnClickListener(this);
        rdProportion.setOnClickListener(this);

        // get the listview
        expListView = findViewById(R.id.checkInventoryExpandableListView);

        // initialize category list with hardcoded data
        listDataCategories = new String[]{"Food", "N/A", "Paper", "Advertising", "Cleaning",
                "Miscellaneous", "Uniforms", "inventory"};
        categoriesForSpinner = new String[]{"Select Category", "Food", "N/A", "Paper", "Advertising", "Cleaning",
                "Miscellaneous", "Uniforms", "inventory"};

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

        formatter = new DecimalFormat("#,###.##");

        categoriesSpinner = (Spinner) findViewById(R.id.checkInvCategorySpinner);
        categoriesSpinner.setOnItemSelectedListener(this);

        totalTextView = (TextView) findViewById(R.id.totalCheckInvTextView);

        selectDate = (TextView) findViewById(R.id.txtSortDatePicker);
        selectDate.setOnClickListener(this);

        totalValue = 0;
        category_id = 0;

        pieChart = (PieChart) findViewById(R.id.inventoryPieChart);
        pieChart.getDescription().setText("Subcategory Quantities");
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(20);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(20);
    }

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
        if (category_id != 0) {
            if (selectedDay==0){
                selectDate.setVisibility(View.VISIBLE);
            } else {
                getProductProportions(category_id, selectedDay, selectedMonth, selectedYear);
            }
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
                startActivity(new Intent(
                        getApplicationContext(), CheckInventoryActivity.class));
                return true;
            case R.id.btnMenuRecommendations:

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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.radio_value){
            selectDate.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            categoriesSpinner.setVisibility(View.GONE);
            radioSelected = "value";
        } else if (view.getId() == R.id.radio_proportion){
            inventoryValuesLayout.setVisibility(View.GONE);
            selectDate.setVisibility(View.GONE);
            radioSelected = "proportion";
            categoriesSpinner.setVisibility(View.VISIBLE);
            loadSpinner();
        } else if (view.getId() == R.id.txtSortDatePicker){
            setDate();
        }

    }

    public void setDate(){
        Calendar calendar = Calendar.getInstance();

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
                    categoriesSpinner.setVisibility(View.GONE);
                    pieChart.setVisibility(View.GONE);
                    inventoryValuesLayout.setVisibility(View.VISIBLE);
                    getProductValues(selectedDay, selectedMonth, selectedYear);
                } else if (radioSelected.equals("proportion")){
                    pieChart.setVisibility(View.VISIBLE);
                    getProductProportions(category_id, selectedDay, selectedMonth, selectedYear);
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
        String url ="https://f8a6792c.ngrok.io/?a=select%20day,month,year,quantity,inventories.unit_cost,inventories.product_id,product,products.subcategory_id,subcategory,products.category_id,category%20from%20inventories%20join%20products%20on%20inventories.product_id=products.product_id%20join%20subcategories%20on%20subcategories.subcategory_id=products.subcategory_id%20join%20categories%20on%20categories.category_id=products.category_id%20where%20day="+day+"%20and%20month="+month+"%20and%20year="+year;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
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

                            // lists for storing products per subcategory
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

                            // linked hashmap for storing lists of subcategories lists of products
                            LinkedHashMap<String, List<String>> thirdLevelFood = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelNA = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelPaper = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelAdvertising = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelCleaning = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelMiscellaneous = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelUniforms = new LinkedHashMap<>();
                            LinkedHashMap<String, List<String>> thirdLevelInventory = new LinkedHashMap<>();

                            jsonarrayProducts = new JSONArray(response);

                            String products = "";
                            products += jsonarrayProducts.length() + "\n";

                            for (int i = 0; i < jsonarrayProducts.length(); i++)
                            {
                                JSONObject jsonobject = jsonarrayProducts.getJSONObject(i);

                                String product = jsonobject.getString("product");
                                String sPrice = jsonobject.getString("unit_cost");
                                float fPrice = Float.parseFloat(sPrice);
                                String sQuantity = jsonobject.getString("quantity");
                                float fQuantity = Float.parseFloat(sQuantity);
                                int subcategory_id = jsonobject.getInt("subcategory_id");
                                String subcategory_name = jsonobject.getString("subcategory");
                                String category_name = jsonobject.getString("category");
                                String productValue = product + "            $" + formatter.format(fPrice*fQuantity);

                                totalValue += fPrice*fQuantity;

                                products += product + " ";

                                switch(subcategory_id) {
                                    case 1:
                                        SugarAndShortening.add(productValue);
                                        sugarValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Sugar and Shortening")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(sugarValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 2:
                                        Fillings.add(productValue);
                                        fillingsValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Fillings")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(fillingsValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 3:
                                        Drinks.add(productValue);
                                        drinksValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Drinks")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(drinksValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 4:
                                        CansAndHomeBrew.add(productValue);
                                        cansValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Cans and Home Brew")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(cansValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 5:
                                        SoupSandwiches.add(productValue);
                                        soupValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Soup and Sandwiches")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(soupValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 6:
                                        FoodIngredients.add(productValue);
                                        foodIngValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Food Ingredients")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(foodIngValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 7:
                                        Produce.add(productValue);
                                        produceValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Produce")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(produceValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 8:
                                        NA.add(productValue);
                                        naValue += (fPrice*fQuantity);
                                        na += (fPrice*fQuantity);

                                        for (int y=0; y < sNA.length; y++){
                                            if (sNA[y].contains("N/A")){
                                                sNA[y] = subcategory_name + "            $"+formatter.format(naValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("N/A")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(na);
                                            }
                                        }
                                        break;
                                    case 9:
                                        Bread.add(productValue);
                                        breadValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Bread")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(breadValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 10:
                                        Emulsions.add(productValue);
                                        emulsionsValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Emulsions and Paste")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(emulsionsValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 11:
                                        danis.add(productValue);
                                        danisValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("danis")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(danisValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 12:
                                        MustardSpread.add(productValue);
                                        mustardValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("mustard spread")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(mustardValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 13:
                                        Toppings.add(productValue);
                                        toppingsValue += (fPrice*fQuantity);
                                        food += (fPrice*fQuantity);

                                        for (int y=0; y < sFood.length; y++){
                                            if (sFood[y].contains("Toppings")){
                                                sFood[y] = subcategory_name + "            $"+formatter.format(toppingsValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Food")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(food);
                                            }
                                        }
                                        break;
                                    case 14:
                                        Paper.add(productValue);
                                        paperValue += (fPrice*fQuantity);
                                        paper += (fPrice*fQuantity);

                                        for (int y=0; y < sPaper.length; y++){
                                            if (sPaper[y].contains("Paper - Other Packaging")){
                                                sPaper[y] = subcategory_name + "            $"+formatter.format(paperValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Paper")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(paper);
                                            }
                                        }
                                        break;
                                    case 15:
                                        HotDrinkCups.add(productValue);
                                        hotDrinksValue += (fPrice*fQuantity);
                                        paper += (fPrice*fQuantity);

                                        for (int y=0; y < sPaper.length; y++){
                                            if (sPaper[y].contains("Hot Drink Cups")){
                                                sPaper[y] = subcategory_name + "            $"+formatter.format(hotDrinksValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Paper")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(paper);
                                            }
                                        }
                                        break;
                                    case 16:
                                        IcedBeverageCupsLids.add(productValue);
                                        icedValue += (fPrice*fQuantity);
                                        paper += (fPrice*fQuantity);

                                        for (int y=0; y < sPaper.length; y++){
                                            if (sPaper[y].contains("Iced Beverage Cups/Lids")){
                                                sPaper[y] = subcategory_name + "            $"+formatter.format(icedValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Paper")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(paper);
                                            }
                                        }
                                        break;
                                    case 17:
                                        Advertising.add(productValue);
                                        advertisingValue += (fPrice*fQuantity);
                                        advertising += (fPrice*fQuantity);

                                        for (int y=0; y < sAdvertising.length; y++){
                                            if (sAdvertising[y].contains("Advertising")){
                                                sAdvertising[y] = subcategory_name + "            $"+formatter.format(advertisingValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Advertising")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(advertising);
                                            }
                                        }
                                        break;
                                    case 18:
                                        coffeeBowlCleaner.add(productValue);
                                        coffeeCleanerValue += (fPrice*fQuantity);
                                        cleaning += (fPrice*fQuantity);

                                        for (int y=0; y < sCleaning.length; y++){
                                            if (sCleaning[y].contains("coffee bowl cleaner")){
                                                sCleaning[y] = subcategory_name + "            $"+formatter.format(coffeeCleanerValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Cleaning")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(cleaning);
                                            }
                                        }
                                        break;
                                    case 19:
                                        StoreSupplies.add(productValue);
                                        storeSuppliesValue += (fPrice*fQuantity);
                                        miscellaneous += (fPrice*fQuantity);

                                        for (int y=0; y < sMiscellaneous.length; y++){
                                            if (sMiscellaneous[y].contains("Store Supplies")){
                                                sMiscellaneous[y] = subcategory_name + "            $"+formatter.format(storeSuppliesValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Miscellaneous")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(miscellaneous);
                                            }
                                        }
                                        break;
                                    case 20:
                                        StaffUniform.add(productValue);
                                        uniformValue += (fPrice*fQuantity);
                                        uniforms += (fPrice*fQuantity);

                                        for (int y=0; y < sUniforms.length; y++){
                                            if (sUniforms[y].contains("Staff Uniform")){
                                                sUniforms[y] = subcategory_name + "            $"+formatter.format(uniformValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("Uniforms")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(uniforms);
                                            }
                                        }
                                        break;
                                    case 21:
                                        Dairy.add(productValue);
                                        dairyValue += (fPrice*fQuantity);
                                        inventory += (fPrice*fQuantity);

                                        for (int y=0; y < sInventory.length; y++){
                                            if (sInventory[y].contains("Dairy")){
                                                sInventory[y] = subcategory_name + "            $"+formatter.format(dairyValue);
                                            }
                                        }
                                        for (int x=0; x < listDataCategories.length; x++){
                                            if (listDataCategories[x].contains("inventory")){
                                                listDataCategories[x] = category_name + "            $"+formatter.format(inventory);
                                            }
                                        }
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
    }

    // get products and set up list views for sorting by proportions
    public void getProductProportions(final int cat_id, int day, int month, int year){
        String url ="https://f8a6792c.ngrok.io/?a=select%20quantity,inventories.unit_cost,inventories.product_id,product,products.subcategory_id,subcategory,products.category_id,category%20from%20inventories%20join%20products%20on%20inventories.product_id=products.product_id%20join%20subcategories%20on%20subcategories.subcategory_id=products.subcategory_id%20join%20categories%20on%20categories.category_id=products.category_id%20where%20day="+day+"%20and%20month="+month+"%20and%20year="+year+"and%20products.category_id="+cat_id;
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

                            // declare and initialize variables used for storing category quantity
                            float food = 0;
                            float na = 0;
                            float paper = 0;
                            float advertising = 0;
                            float cleaning = 0;
                            float miscellaneous = 0;
                            float uniforms = 0;
                            float inventory = 0;

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
                                        food += fQuantity;
                                        break;
                                    case 2:
                                        fillingsQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 3:
                                        drinksQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 4:
                                        cansQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 5:
                                        soupQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 6:
                                        foodIngQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 7:
                                        produceQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 8:
                                        naQuantity += fQuantity;
                                        na += fQuantity;
                                        break;
                                    case 9:
                                        breadQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 10:
                                        emulsionsQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 11:
                                        danisQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 12:
                                        mustardQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 13:
                                        toppingsQuantity += fQuantity;
                                        food += fQuantity;
                                        break;
                                    case 14:
                                        paperQuantity += fQuantity;
                                        paper += fQuantity;
                                        break;
                                    case 15:
                                        hotDrinksQuantity += fQuantity;
                                        paper += fQuantity;
                                        break;
                                    case 16:
                                        icedQuantity += fQuantity;
                                        paper += fQuantity;
                                        break;
                                    case 17:
                                        advertisingQuantity += fQuantity;
                                        advertising += fQuantity;
                                        break;
                                    case 18:
                                        coffeeCleanerQuantity += fQuantity;
                                        cleaning += fQuantity;
                                        break;
                                    case 19:
                                        storeSuppliesQuantity += fQuantity;
                                        miscellaneous += fQuantity;
                                        break;
                                    case 20:
                                        uniformQuantity += fQuantity;
                                        uniforms += fQuantity;
                                        break;
                                    case 21:
                                        dairyQuantity += fQuantity;
                                        inventory += fQuantity;
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
                                    xData = sFood;
                                    yData = categoryFoodQuantity;
                                    addDataSet();
                                    break;
                                case 2:
                                    float[] categoryNAQuantity = new float[1];
                                    categoryNAQuantity[0] = naQuantity;
                                    xData = sNA;
                                    yData = categoryNAQuantity;
                                    addDataSet();
                                    break;
                                case 3:
                                    float[] categoryPaperQuantity = new float[3];
                                    categoryPaperQuantity[0] = paperQuantity;
                                    categoryPaperQuantity[1] = hotDrinksQuantity;
                                    categoryPaperQuantity[2] = icedQuantity;
                                    xData = sPaper;
                                    yData = categoryPaperQuantity;
                                    addDataSet();
                                    break;
                                case 4:
                                    float[] categoryAdvertisingQuantity = new float[1];
                                    categoryAdvertisingQuantity[0] = advertisingQuantity;
                                    xData = sAdvertising;
                                    yData = categoryAdvertisingQuantity;
                                    addDataSet();
                                    break;
                                case 5:
                                    float[] categoryCleaningQuantity = new float[1];
                                    categoryCleaningQuantity[0] = coffeeCleanerQuantity;
                                    xData = sCleaning;
                                    yData = categoryCleaningQuantity;
                                    addDataSet();
                                    break;
                                case 6:
                                    float[] categoryMiscellaneousQuantity = new float[1];
                                    categoryMiscellaneousQuantity[0] = storeSuppliesQuantity;
                                    xData = sMiscellaneous;
                                    yData = categoryMiscellaneousQuantity;
                                    addDataSet();
                                    break;
                                case 7:
                                    float[] categoryUniformsQuantity = new float[1];
                                    categoryUniformsQuantity[0] = uniformQuantity;
                                    xData = sUniforms;
                                    yData = categoryUniformsQuantity;
                                    addDataSet();
                                    break;
                                case 8:
                                    float[] categoryInventoryQuantity = new float[1];
                                    categoryInventoryQuantity[0] = dairyQuantity;
                                    xData = sInventory;
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

    // display pie chart
    private void addDataSet() {
        List<PieEntry> entries = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            entries.add(new PieEntry(yData[i] , xData[i]));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(entries, "Category Quantities");
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(20);
        pieDataSet.setValueLinePart1OffsetPercentage(10.f);
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
    public void onFinalChildClick(int plpos, int slpos, int tlpos) {}

    @Override
    public void onFinalItemClick(int plItem, String slItem, String tlItem) {}

    // on pie chart value selected
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int i = (int) h.getX();
        String subcategorySelected = xData[i];

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
