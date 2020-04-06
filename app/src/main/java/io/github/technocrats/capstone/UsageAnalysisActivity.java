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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.technocrats.capstone.adapters.ExpandableListAdapter;

public class UsageAnalysisActivity extends AppCompatActivity implements
        ExpandableListAdapter.ThreeLevelListViewListener, View.OnClickListener,
        AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

        int[] months = new int[1000];
        int[] years = new int[1000];
        double[] usages2 = new double[1000];

        String selectedCategory, selectedSubcategory;

        int selectedMonth, selectedYear, positionOfSelectedCategory, categoryIdOfSelectedCategory, numberOfSubcategoriesInSelectedCategory;

        String[] categories = {"Food", "N/A", "Paper", "Advertising", "Cleaning", "Miscellaneous", "Uniforms", "inventory"};

        String[][] subcategories = {{"Sugar and Shortening", "Fillings", "Drinks", "Cans and Home Brew", "Soup and Sandwiches", "Food Ingredients", "Produce", "Bread", "Emulsions and Paste", "danis", "mustard spread", "Toppings"},
                                    {"N/A"},
                                    {"Paper - Other Packaging", "Hot Drink Cups", "Iced Beverage Cups/Lids"},
                                    {"Advertising"},
                                    {"coffee bowl cleaner"},
                                    {"Store Supplies"},
                                    {"Staff Uniform"},
                                    {"Dairy"}};

        double[] usages = new double[12];

        RadioGroup radioGroup;
        RadioButton radioBar, radioLine;

        RequestQueue queue;
        BarChart barChart;
        LineChart lineChart;
        TextView dateDisplay;
        Toolbar toolbar;
        Spinner categoriesSpinner, subcategoriesSpinner;
        private DatePickerDialog.OnDateSetListener dateSetListener;
        GlobalMethods globalMethods;

        private static final String TAG = "MainActivity";

        private TextView tvInfo;
        private Button btnViewUsage, btnCategorySubcategory, btnDate;
        private DatePickerDialog.OnDateSetListener pickDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_analysis);

        radioGroup = (RadioGroup) findViewById(R.id.radioButtonsLayout);
        radioBar = (RadioButton) findViewById(R.id.radio_bar);
        radioLine = (RadioButton) findViewById(R.id.radio_line);

        barChart = (BarChart) findViewById(R.id.usageBarChart);
        lineChart = (LineChart) findViewById(R.id.usageLineChart);

        categoriesSpinner = (Spinner) findViewById(R.id.categories_spinner);

        categoriesSpinner.setOnItemSelectedListener(this);

        subcategoriesSpinner = (Spinner) findViewById(R.id.subcategories_spinner);

        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        setTitle("Usage Analysis");

        dateDisplay = findViewById(R.id.txtUsageAnalysisDateDisplay);
        toolbar = findViewById(R.id.usageAnalysisToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        globalMethods.DisplayDate(dateDisplay);

        btnCategorySubcategory = (Button) findViewById(R.id.btnCategorySubcategory);

        btnCategorySubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoriesSpinner.setVisibility(View.VISIBLE);

                if (radioBar.isChecked()) {
                    btnDate.setVisibility(View.VISIBLE);
                }
                else if (radioLine.isChecked()) {
                    subcategoriesSpinner.setVisibility(View.VISIBLE);
                    btnViewUsage.setVisibility(View.VISIBLE);
                }
            }
        });

        tvInfo = (TextView) findViewById(R.id.tvInfo);

        btnDate = (Button) findViewById(R.id.btnDate);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        UsageAnalysisActivity.this,
                        R.style.DateSelector,
                        pickDateListener,
                        year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
                dialog.getDatePicker().setCalendarViewShown(false);
                dialog.show();
            }
        });

        pickDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                selectedYear = year;
                selectedMonth = month;

                selectedCategory = categoriesSpinner.getSelectedItem().toString();

                positionOfSelectedCategory = categoriesSpinner.getSelectedItemPosition();

                numberOfSubcategoriesInSelectedCategory = subcategories[positionOfSelectedCategory].length;

                for(int i = 0; i < numberOfSubcategoriesInSelectedCategory; i ++) {
                    usages[i] = 0;
                }

                getBarChart();
            }
        };

        btnViewUsage = (Button) findViewById(R.id.btnViewUsage);

        btnViewUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCategory = categoriesSpinner.getSelectedItem().toString();
                selectedSubcategory = subcategoriesSpinner.getSelectedItem().toString();

                getLineChart();
            }
        });

        radioGroup.clearCheck();

        radioBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCategoriesSpinner();
                btnCategorySubcategory.setText("Select Category");
                btnCategorySubcategory.setVisibility(View.VISIBLE);
                categoriesSpinner.setVisibility(View.GONE);
                subcategoriesSpinner.setVisibility(View.GONE);
                btnDate.setVisibility(View.GONE);
                barChart.setVisibility(View.GONE);
                lineChart.setVisibility(View.GONE);
                btnViewUsage.setVisibility(View.GONE);
                tvInfo.setVisibility(View.GONE);
            }
        });

        radioLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCategoriesSpinner();
                loadSubcategoriesSpinner();
                btnCategorySubcategory.setText("Select Category And Subcategory");
                btnCategorySubcategory.setVisibility(View.VISIBLE);
                categoriesSpinner.setVisibility(View.GONE);
                subcategoriesSpinner.setVisibility(View.GONE);
                btnDate.setVisibility(View.GONE);
                barChart.setVisibility(View.GONE);
                lineChart.setVisibility(View.GONE);
                btnViewUsage.setVisibility(View.GONE);
                tvInfo.setVisibility(View.GONE);
            }
        });
    }

    private void getBarChart() {
        String url = "https://huexinventory.ngrok.io/?a=SELECT * FROM usages INNER JOIN products ON usages.product_id = products.product_id INNER JOIN categories ON products.category_id = categories.category_id INNER JOIN subcategories ON products.subcategory_id = subcategories.subcategory_id WHERE categories.category = '" + selectedCategory + "' AND usages.year = " + selectedYear + " AND usages.month = " + selectedMonth + "&b=Capstone";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String subcategory = jsonObject.getString("subcategory");
                        double usage = jsonObject.getDouble("usage");

                        for (int j = 0; j < numberOfSubcategoriesInSelectedCategory; j++) {
                            if (subcategory.equals(subcategories[positionOfSelectedCategory][j])) {
                                usages[j] += usage;
                                break;
                            }
                        }
                    }

                    ArrayList<BarEntry> barEntries = new ArrayList<>();

                    for(int i = 0; i < numberOfSubcategoriesInSelectedCategory; i ++)
                    {
                        barEntries.add(new BarEntry(i, (float)usages[i]));
                    }

                    BarDataSet barDataSet = new BarDataSet(barEntries, "");

                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData(barDataSet);

                    ArrayList<String> labels = new ArrayList<>();

                    for(int i = 0; i < numberOfSubcategoriesInSelectedCategory; i ++)
                    {
                        labels.add(subcategories[positionOfSelectedCategory][i]);
                    }

                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    barChart.animateY(1000);
                    barChart.setData(barData);
                    barChart.getDescription().setText("usages of subcategories of " + selectedCategory + " during " + selectedYear + " / " + selectedMonth);
                    barChart.getDescription().setTextSize(25f);
                    barChart.getDescription().setPosition(1300f, 34f);

                    XAxis xAxis = barChart.getXAxis();

                    xAxis.setLabelCount(barEntries.size());
                    xAxis.setLabelRotationAngle(45);
                    xAxis.setTextSize(16f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                    barChart.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getLineChart() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://huexinventory.ngrok.io/?a=SELECT * FROM subcatusages INNER JOIN categories ON subcatusages.category_id = categories.category_id INNER JOIN subcategories ON subcatusages.subcategory_id = subcategories.subcategory_id WHERE categories.category = '" + selectedCategory + "' AND subcategories.subcategory = '" + selectedSubcategory + "'&b=Capstone";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    double x_bar = 0;
                    double y_bar = 0;

                    ArrayList<Entry> dataVals = new ArrayList<Entry>();
                    ArrayList<Entry> dataVals2 = new ArrayList<Entry>();
                    ArrayList<Entry> dataVals3 = new ArrayList<Entry>();
                    ArrayList<Entry> dataVals4 = new ArrayList<Entry>();

                    ArrayList<String> labels = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int month = jsonObject.getInt("month");
                        int year = jsonObject.getInt("year");
                        double usage = jsonObject.getDouble("usage");

                        months[i] = month;
                        years[i] = year;
                        usages2[i] = usage;

                        dataVals.add(new Entry(i, (float) usage));
                        labels.add(year + " / " + month);

                        y_bar += usage;
                        x_bar += (double)i;
                    }

                    y_bar /= (double)jsonArray.length();
                    x_bar /= (double)jsonArray.length();

                    double numerator = 0;
                    double denominator = 0;

                    double standardDeviation = 0;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        double usage = jsonObject.getDouble("usage");

                        numerator += ((double)i - x_bar) * (usage - y_bar);
                        denominator += Math.pow((double)i - x_bar, 2);

                        standardDeviation += Math.pow(usage - y_bar, 2);
                    }

                    double beta_1_hat = numerator / denominator;
                    double beta_0_hat = y_bar - beta_1_hat * x_bar;

                    standardDeviation = Math.sqrt(standardDeviation / (double)jsonArray.length());

                    dataVals2.add(new Entry(0, (float)(beta_0_hat + beta_1_hat * 0)));
                    dataVals2.add(new Entry(jsonArray.length() - 1, (float)(beta_0_hat + beta_1_hat * (jsonArray.length() - 1))));

                    dataVals3.add(new Entry(0, (float)(beta_0_hat + beta_1_hat * 0 + standardDeviation)));
                    dataVals3.add(new Entry(jsonArray.length() - 1, (float)(beta_0_hat + beta_1_hat * (jsonArray.length() - 1) + standardDeviation)));

                    dataVals4.add(new Entry(0, (float)(beta_0_hat + beta_1_hat * 0 - standardDeviation)));
                    dataVals4.add(new Entry(jsonArray.length() - 1, (float)(beta_0_hat + beta_1_hat * (jsonArray.length() - 1) - standardDeviation)));

                    LineDataSet lineDataSet = new LineDataSet(dataVals, "");
                    LineDataSet lineDataSet2 = new LineDataSet(dataVals2, "");
                    LineDataSet lineDataSet3 = new LineDataSet(dataVals3, "");
                    LineDataSet lineDataSet4 = new LineDataSet(dataVals4, "");

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                    dataSets.add(lineDataSet);
                    dataSets.add(lineDataSet2);
                    dataSets.add(lineDataSet3);
                    dataSets.add(lineDataSet4);

                    LineData data = new LineData(dataSets);

                    lineChart.setData(data);

                    data.setValueTextSize(40f);

                    lineDataSet.setColors(Color.GREEN);
                    lineDataSet.setValueTextColor(Color.BLACK);
                    lineDataSet.setValueTextSize(17f);
                    lineDataSet.setLineWidth(3);

                    lineDataSet2.setColors(Color.BLACK);
                    lineDataSet2.setValueTextColor(Color.BLACK);
                    lineDataSet2.setValueTextSize(17f);
                    lineDataSet2.setLineWidth(3);

                    lineDataSet3.setColors(Color.BLACK);
                    lineDataSet3.setValueTextColor(Color.BLACK);
                    lineDataSet3.setValueTextSize(17f);
                    lineDataSet3.setLineWidth(3);
                    lineDataSet3.enableDashedLine(10f, 10f, 0f);

                    lineDataSet4.setColors(Color.BLACK);
                    lineDataSet4.setValueTextColor(Color.BLACK);
                    lineDataSet4.setValueTextSize(17f);
                    lineDataSet4.setLineWidth(3);
                    lineDataSet4.enableDashedLine(10f, 10f, 0f);

                    lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    lineChart.invalidate();
                    lineChart.getDescription().setText("usages of " + selectedCategory + " / " + selectedSubcategory + " between " + years[0] + " / " + months[0] + " and " + years[jsonArray.length() - 1] + " / " + months[jsonArray.length() - 1]);
                    lineChart.getDescription().setTextSize(20f);
                    lineChart.getDescription().setPosition(1550f, 33f);

                    XAxis xAxis = lineChart.getXAxis();

                    xAxis.setLabelCount(dataVals.size());
                    xAxis.setLabelRotationAngle(45);
                    xAxis.setTextSize(16f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                    lineChart.setVisibility(View.VISIBLE);

                    tvInfo.setVisibility(View.VISIBLE);
                    tvInfo.setText("Slope = " + String.format("%.2f", beta_1_hat) + "          " + "Standard Deviation = " + String.format("%.2f", standardDeviation));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void loadCategoriesSpinner(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);

        categoriesSpinner.setAdapter(arrayAdapter);
    }

    public void loadSubcategoriesSpinner(){
        positionOfSelectedCategory = categoriesSpinner.getSelectedItemPosition();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, subcategories[positionOfSelectedCategory]);

        subcategoriesSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadSubcategoriesSpinner();
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
    public void onClick(View view) { }

    @Override
    public void onValueSelected(Entry e, Highlight h) { }

    @Override
    public void onNothingSelected() { }

    @Override
    public void onFinalChildClick(int plpos, int slpos, int tlpos) { }

    @Override
    public void onFinalItemClick(int plItem, String slItem, String tlItem) { }

}
