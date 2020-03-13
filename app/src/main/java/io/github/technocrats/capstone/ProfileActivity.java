package io.github.technocrats.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    GlobalMethods globalMethods;
    Toolbar toolbar;
    TextView tvFirstLast;
    TextView tvUserStore;
    TextView tvChangePasswordLink;
    EditText eOldPassword;
    EditText eNewPassword;
    EditText eConfirmPassword;
    LinearLayout layoutChangePassword;
    Button btnChangePassword;
    private SharedPreferences sharedPlace;

    String newPassword;
    String password;
    String username;
    String firstName;
    String lastName;
    String storeID;
    String sOutput;
    String typedOldPassword;
    String typedNewPassword;
    String typedConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile Settings");

        // initialize SharedPreferences
        this.sharedPlace = getSharedPreferences("SharedPlace", MODE_PRIVATE);

        username = sharedPlace.getString("username", "");
        password = sharedPlace.getString("password", "");
        firstName = sharedPlace.getString("firstName", "");
        lastName = sharedPlace.getString("lastName", "");
        storeID = sharedPlace.getString("storeID", "");

        // initialize GlobalMethods
        globalMethods = new GlobalMethods(this);
        globalMethods.checkIfLoggedIn();

        // initialize and display toolbar
        toolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize and set text views
        tvFirstLast = (TextView) findViewById(R.id.tvUserFirstLast);
        tvFirstLast.setText(firstName + " " + lastName);

        tvUserStore = (TextView) findViewById(R.id.tvUserStore);
        tvUserStore.setText("Store Number: " + storeID);

        tvChangePasswordLink = (TextView) findViewById(R.id.tvChangePasswordLink);
        tvChangePasswordLink.setOnClickListener(this);

        // initialize edit texts
        eOldPassword = (EditText) findViewById(R.id.txtOldPassword);
        eNewPassword = (EditText) findViewById(R.id.txtNewPassword);
        eConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        // initialize buttons
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);

        layoutChangePassword = (LinearLayout) findViewById(R.id.layoutChangePassword);

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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvChangePasswordLink){
            if (layoutChangePassword.getVisibility() == View.VISIBLE){
                layoutChangePassword.setVisibility(View.GONE);
            } else {
                layoutChangePassword.setVisibility(View.VISIBLE);
            }

        } else if(view.getId() == R.id.btnChangePassword){
            if((eOldPassword.getText().length()<1) || (eNewPassword.getText().length()<1)
                    || (eConfirmPassword.getText().length()<1)){
                Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_LONG).show();
            } else{
                changePassword();
                Toast.makeText(getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_LONG).show();
                layoutChangePassword.setVisibility(View.GONE);
            }
        }
    }

    private void changePassword(){
        typedOldPassword = eOldPassword.getText().toString();
        typedNewPassword = eNewPassword.getText().toString();
        typedConfirmPassword = eConfirmPassword.getText().toString();

        try {
            sOutput = SymmetricEncryptionUtils.decryptPassword(password, typedOldPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sOutput.equals(typedOldPassword)){
            if (typedNewPassword.equals(typedConfirmPassword)){
                try {
                    newPassword = SymmetricEncryptionUtils.encryptPassword(typedNewPassword);
                    setDataInServerDB(newPassword, username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "New Password and Confirm Password do not match",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Old Password does not match",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setDataInServerDB(String sPassword, String sUsername){
        String url ="https://f8a6792c.ngrok.io/?a=Update%20users%20Set%20password_hash=%27"+sPassword+"%27%20Where%20user_name%20=%20%27"+sUsername+"%27";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
