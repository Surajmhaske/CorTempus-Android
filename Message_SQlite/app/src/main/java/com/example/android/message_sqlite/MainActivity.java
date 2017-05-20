package com.example.android.message_sqlite;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity {

   // public static final String LOGIN_URL = "http://spatil20.000webhostapp.com/index.php";
    public static final String LOGIN_URL = GlobalVariables.serverBaseURL + "/index.php";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private String username;
    private String password;
    private SharedPreferences sharedPreferences;
    private static final String MyPrefs = "TestPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final String success="success:1";
        Button registerButton = (Button) findViewById(R.id.register_button);
        Button buttonLogin = (Button) findViewById(R.id.login);
        editTextUsername = (EditText) findViewById(R.id.username_field);
        editTextPassword = (EditText) findViewById(R.id.password_field);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
              //  prefs = getSharedPreferences(prefName, MODE_PRIVATE);
                sharedPreferences=getSharedPreferences(MyPrefs,MODE_PRIVATE);
               // prefs = getSharedPreferences("contact", MODE_WORLD_READABLE);
              //  SharedPreferences.Editor editor = prefs.edit();

                //---save the values in the EditText view to preferences---
               // editor.putInt("id", Integer.parseInt(e_id.getText().toString()));
                sharedPreferences.edit().putString("Susername", editTextUsername.getText().toString()).apply();

                //---saves the values---
               // editor.commit();

                Toast.makeText(getBaseContext(), "Saved",
                        Toast.LENGTH_SHORT).show();
                username = editTextUsername.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                    System.out.println("Resulted Value in : " + response);
                                    int jsonResult = returnParsedJsonObject(response);
                                    System.out.println("jsonResult: " + jsonResult);
                                    if(jsonResult == 1){
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.putExtra("KEY_USERNAME", username);
                                        intent.putExtra("MESSAGE", "You have been successfully login");
                                        startActivity(intent);
                                    }
                                    else {
                                       // Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                        Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();

                                    }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put(KEY_USERNAME, username);
                        map.put(KEY_PASSWORD, password);
                        return map;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);

            }
       });

    }
    private int returnParsedJsonObject(String result){

        JSONObject resultObject = null;
        int returnedResult = 0;
        try {
            resultObject = new JSONObject(result);
            returnedResult = resultObject.getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnedResult;
    }
}



