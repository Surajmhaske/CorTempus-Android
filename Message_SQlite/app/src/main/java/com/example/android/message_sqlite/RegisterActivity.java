package com.example.android.message_sqlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.android.message_sqlite.R.id.home;
import static com.example.android.message_sqlite.R.id.username;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //private static final String REGISTER_URL = "http://spatil20.000webhostapp.com/index.php";
    private static final String REGISTER_URL = GlobalVariables.serverBaseURL + "/index.php";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_CONTACT = "user_contact";
    public static final String KEY_EMERGENCY_CONTACT = "emergency_contact";
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextUserContact;
    private EditText editTextEmergencyContact;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = (EditText) findViewById(R.id.username_field);
        editTextPassword = (EditText) findViewById(R.id.password_field);
        editTextEmail = (EditText) findViewById(R.id.email_field);
        editTextUserContact = (EditText) findViewById(R.id.contact_field);
        editTextEmergencyContact = (EditText) findViewById(R.id.emergency_contact_field);

        buttonRegister = (Button) findViewById(R.id.sign_up);
        buttonRegister.setOnClickListener(this);
        Button home2 = (Button) findViewById(home);
        home2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(RegisterActivity.this, MainActivity.class);

                startActivity(home);
            }
        });


    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerUser();
        }
    }


    private void registerUser() {

        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String user_contact = editTextUserContact.getText().toString().trim();
        final String emergency_contact = editTextEmergencyContact.getText().toString().trim();

        System.out.println("Resulted Value in user_contact : " + user_contact);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                        System.out.println("Resulted Value in response : " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Resulted Value in error : " + error);
                        Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        // Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);

                params.put(KEY_EMAIL, email);
                params.put(KEY_USER_CONTACT, user_contact);
                System.out.println("Resulted Value in KEY_USER_CONTACT : " + KEY_USER_CONTACT);
                System.out.println("Resulted Value in user_contact1 : " + user_contact);
                params.put(KEY_EMERGENCY_CONTACT, emergency_contact);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(stringRequest);


    }


}

//        });
//
//
//
//
//    }
//
//  }
