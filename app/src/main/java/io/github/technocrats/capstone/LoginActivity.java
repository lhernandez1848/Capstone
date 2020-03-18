package io.github.technocrats.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText txtUsername;
    EditText txtPassword;

    Button btnLogin;
    Button btnSkipLogin;

    JSONArray jsonarray;

    String typedUsername;
    String typedPassword;
    String sOutput;

    public static String storedUsername;
    public static String storedPassword;
    public static String storedFirstName;
    public static String storedLastName;
    public static String storedStoreNumber;

    private SharedPreferences sharedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnSkipLogin = findViewById(R.id.btnSkipLogin);
        btnSkipLogin.setOnClickListener(this);

        this.sharedPlace = getSharedPreferences("SharedPlace", MODE_PRIVATE);

    }

    @Override
    public void onBackPressed(){
        finish();
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin){
            typedUsername = txtUsername.getText().toString();
            typedPassword = txtPassword.getText().toString();

            try {
                sOutput = SymmetricEncryptionUtils.encryptPassword(typedPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (typedUsername.isEmpty()){
                Toast.makeText(this, "Username cannot be empty",
                        Toast.LENGTH_LONG).show();
            } else if (typedPassword.isEmpty()){
                Toast.makeText(this, "Password cannot be empty",
                        Toast.LENGTH_LONG).show();
            } else {
                getDataFromServerDB(typedUsername);
            }

        } else if (view.getId() == R.id.btnSkipLogin){
            SharedPreferences.Editor sharedEditor = this.sharedPlace.edit();
            sharedEditor.putString("username", "admin");
            sharedEditor.putString("password", "admin");
            sharedEditor.putString("firstName", "Admin");
            sharedEditor.putString("lastName", "Administrator");
            sharedEditor.putString("storeID", "1");
            sharedEditor.apply();
            startActivity(new Intent(
                    getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void loginLogic(){
        try {
            sOutput = SymmetricEncryptionUtils.decryptPassword(storedPassword, typedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (typedPassword.equals(sOutput)) {
            SharedPreferences.Editor sharedEditor = this.sharedPlace.edit();
            sharedEditor.putString("username", storedUsername);
            sharedEditor.putString("password", storedPassword);
            sharedEditor.putString("firstName", storedFirstName);
            sharedEditor.putString("lastName", storedLastName);
            sharedEditor.putString("storeID", storedStoreNumber);
            sharedEditor.apply();

            startActivity(new Intent(
                    getApplicationContext(), MainActivity.class));

            finish();
        } else {
            Toast.makeText(this, "Incorrect username or password",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getDataFromServerDB(String usernameInput){
        String url ="https://huexinventory.ngrok.io/?a=select%20*%20from%20users%20where%20user_name=%27"+usernameInput+"%27";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            jsonarray = new JSONArray(response);

                            for (int i = 0; i < jsonarray.length(); i++)
                            {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String username = jsonobject.getString("user_name");
                                String password = jsonobject.getString("password_hash");
                                String firstName = jsonobject.getString("first_name");
                                String lastName = jsonobject.getString("last_name");
                                int storeId = jsonobject.getInt("store_id");

                                storedUsername = username;
                                storedPassword = password.replace(" ", "+");
                                storedFirstName = firstName;
                                storedLastName = lastName;
                                storedStoreNumber = String.valueOf(storeId);

                                loginLogic();
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
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
