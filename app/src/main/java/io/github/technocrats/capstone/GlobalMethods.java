package io.github.technocrats.capstone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class GlobalMethods {

    private SharedPreferences sharedPlace;
    private SharedPreferences.Editor sharedEditor;
    Context _context;

    public GlobalMethods(Context context){
        this._context = context;
        sharedPlace = _context.getSharedPreferences("SharedPlace", MODE_PRIVATE);
    }

    void checkIfLoggedIn(){
        String username = sharedPlace.getString("username", "");
        if (Objects.equals(username, "")){
            logoutUser();
        }
    }

    void logoutUser(){
        sharedEditor = sharedPlace.edit();
        sharedEditor.clear().apply();

        Intent intent = new Intent(_context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    void DisplayDate(TextView tv){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        String date = sdf.format(new Date());

        tv.setText(date);
    }

    public String trimEnd(String value) {
        return value.replaceFirst("\\s+$", "");
    }

}
