package com.example.android.message_sqlite;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
//import android.os.Handler;


//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.util.Set;
//import java.util.UUID;


import static com.example.android.message_sqlite.MainActivity.KEY_USERNAME;
import static com.example.android.message_sqlite.R.id.lblLocation;
import static com.example.android.message_sqlite.R.id.textView;
import static com.example.android.message_sqlite.R.id.username;

public class LoginActivity extends AppCompatActivity implements SensorEventListener {
    TextView tv_steps;
    SensorManager sensorManager;
    boolean running =false;
   // public static final String LOGIN_URL = "http://snehalspatil1991.000webhostapp.com/contacts.php";
  // public static final String LOGIN_URL =  "http://spatil20.000webhostapp.com/getClosestUser.php";

    public static final String LOGIN_URL =  GlobalVariables.serverBaseURL + "/getClosestUser.php?username=";
    //public static final String LOGIN_URL =  "http://spatil20.000webhostapp.com/getClosestUser.php?username=";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_CONTACT = "user_contact";
    public static final String KEY_EMERGENCY_CONTACT = "emergency_contact";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String JSON_ARRAY = "result";
    private TextView textViewContact;
    private SharedPreferences sharedPreferences;
    private static final String MyPrefs = "TestPreferences";
    private Location mLastLocation;
    // GPSTracker class
    GPS_cordinates gps;
CreateURL  interrupt;

    //devraj

    final static int Request_BT_Enable = 1;
    final static int Xaxis = 0;
    final static int Yaxis = 1;
    final static int Zaxis = 2;
    final static String SOS = "SOS";
    double xAccel,yAccel,zAccel;
    float pulserate,oldPulse,newPulse;
    long start,end;
    boolean fall = false;
    boolean first = true;
    String xValue,yValue,zValue;
    //BluetoothDevice myDevice = null;
    BluetoothAdapter myAdapter;
    //Set<BluetoothDevice> pairedDevices;
//    MyMainReceiver myMainReceiver;
//    HrReceiver pulseReceiver; //suraj
    Intent readIntent = null;
    Intent BtIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv_steps=(TextView) findViewById(R.id.tv_step);
        sensorManager =(SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        String loggedUser = intentBundle.getString("KEY_USERNAME");
        loggedUser = capitalizeFirstCharacter(loggedUser);

        System.out.println("starting gps fetching");

        startService(new Intent(this, GPS_cordinates.class));
        Log.d("intent", "intent is being excecuted!");
        String message = intentBundle.getString("MESSAGE");

        TextView loginUsername = (TextView) findViewById(R.id.login_user);
        TextView successMessage = (TextView) findViewById(R.id.message);
        textViewContact = (TextView) findViewById(R.id.textView6);
        loginUsername.setText(loggedUser);
        successMessage.setText(message);
       // System.out.println("will execute startService service now");
       // startService(new Intent(this, SOS_service.class));
       // Log.d("intent", " sos_service is being excecuted!");
       // String message2 = intentBundle.getString("sos_service started");
       // getResponse();
        interrupt=new CreateURL();
        IntentFilter intentfilter=new IntentFilter();
        intentfilter.addAction("emergency");
        registerReceiver(interrupt,intentfilter);



//        //devraj
//        myAdapter = BluetoothAdapter.getDefaultAdapter();
//
//
//
//        if (myAdapter == null){
//            Toast.makeText(this, "No Bluetooth detected", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            if (!myAdapter.isEnabled()) {
//                turnOnBT();
//            }
//            else if (myAdapter.isEnabled()) {
//                readIntent = new Intent (LoginActivity.this,readService.class);
//                startService(readIntent);
//                BtIntent = new Intent (LoginActivity.this,BluetoothService.class);
//                startService(BtIntent);
//            }
//        }
        Button emergency_message = (Button) findViewById(R.id.buttonSend);
        emergency_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        getResponse();
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running){
            tv_steps.setText(String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // SNEHAL

    private class CreateURL extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        String create =intent.getAction();
        if(create.equals("emergency")){
           // boolean emergency=intent.getBooleanExtra("HeartAttack");
            String emergency=intent.getStringExtra("HeartAttack");
            if(emergency.equals("SOS")){
                getResponse();
            }
        }
    }
}

    private String capitalizeFirstCharacter(String textInput) {
        String input = textInput.toLowerCase();
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }

    public void getResponse() {

        sharedPreferences = getSharedPreferences(MyPrefs, MODE_PRIVATE);
        final String user_result = sharedPreferences.getString("Susername", "default string");
        System.out.println("Resulted Value in shared pref on button click : " + user_result);
        final String username = user_result.trim();
        System.out.println("Resulted Value in shared pref : " + username);

        String url = LOGIN_URL + username;
        System.out.println("Resulted Value in urlf in message service : " + url);
        //StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Resulted Value in : " + response);
                //  String jsonResult = showJSON(response);
                System.out.println("jsonResult: " + response);
               showJSON(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_USERNAME, username);
//                        map.put(KEY_USER_CONTACT, username);
//                        map.put(KEY_EMERGENCY_CONTACT, username);
//                        map.put(KEY_LATITUDE, latitude);
//                        map.put(KEY_PASSWORD, password);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);


    }


    public void showJSON(String response){
        User currentUser;
        String emergency_contact="";
        String address="";
        String nearbyuser= "";
        String user_contact="";
        String latitude = "";
        String longitude ="";

        try {

            JSONObject jsonObject = new JSONObject(response);

            System.out.println("jason object : "  );

//            {"result":{"emergency_contact":"+16619036222","latitude":"39.046281","longitude":"-77.474723","closest_people":[{"username":"Sarosh","latitude":"39.046281","longitude":"-77.474723","user_contact":"+17035418471"},{"username":"Suraj","latitude":"39.046281","longitude":"-77.474723","user_contact":"+15715980793"}]}}
            if(jsonObject.has("result")){
                JSONObject resultObj = jsonObject.getJSONObject("result");
                currentUser = new User();
                sharedPreferences = getSharedPreferences(MyPrefs, MODE_PRIVATE);
                final String user_result = sharedPreferences.getString("Susername", "default string");
                final String username = user_result.trim();
                currentUser.username = username;
                currentUser.emergencyContact = resultObj.getString("emergency_contact");
                currentUser.latitude = resultObj.getString("latitude");
                currentUser.longitude = resultObj.getString("longitude");

                List<User> closestUsers = new ArrayList<>();
                JSONArray closest_people = resultObj.getJSONArray("closest_people");
                for(int i =0; i < closest_people.length(); i++){
                    JSONObject person = closest_people.getJSONObject(i);
                    User user = new User();
                    user.username = person.getString("username");
                    user.latitude = person.getString("latitude");
                    user.longitude = person.getString("longitude");
                    user.userContact = person.getString("user_contact");
                    closestUsers.add(user);
                }
               sendAlerts(currentUser, closestUsers);

            } else {
                System.out.println("Incorrect format of result received");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
       textViewContact.setText("Emergenvy contact:\t" +emergency_contact);
    }

    public class User {
        public String username;
        public String latitude;
        public String longitude;
        public String userContact;
        public String emergencyContact;

        @Override
        public String toString(){
            return "{"+ this.username + " "+ this.latitude + " "+ this.longitude + " "+ this.userContact + " "+ this.emergencyContact +"}";
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        running=true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor !=null){
            sensorManager.registerListener(this,countSensor,SensorManager.SENSOR_DELAY_UI);
        }else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        running=false;
        //sensorManager.unregisterListener(this);
    }



    public void sendAlerts(final User currentUser, final List<User> closertUsers)
    {


                String dlat = currentUser.latitude.toString();
                String dlng = currentUser.longitude.toString();
                String emergencycontact= currentUser.emergencyContact.toString();
                // String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=40.730610,-73.935242 ");
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr="+dlat+","+dlng);

       // sending sms to emergenvy contact
        String sms_emer = "Hi "+username+ " Help is needed at location:"+uri;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emergencycontact, null, sms_emer , null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // getting the phone nearby users phone  number sending sms to them
                for(User user: closertUsers){
                    System.out.println("insidemap: ");
                    System.out.println(user.userContact.toString());

                    String phoneNo =  user.userContact.toString();
                    String username =  user.username.toString();

                    System.out.println(phoneNo);
                    System.out.println(username);
                    String sms = "Hi "+username+ " Help is needed at location:"+uri;
                    System.out.println(sms);
                   // String EmergencyContact=emergencycontact;

//                    Intent url=new Intent(this,SOS_service.class);
//                    url.putExtra("key_smsURL",sms);
//                    url.putExtra("key_phoneNo ",phoneNo);
//                    url.putExtra("key_username",username);
//                    System.out.println("will execute startService service now");
//                    startService(url);
                 //   ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent!",
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "SMS failed, please try again later!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }



                    Log.d("intent", " sos_service is being excecuted!");
//                    try {
//                        SmsManager smsManager = SmsManager.getDefault();
//                        smsManager.sendTextMessage(phoneNo, null, sms, null, null);
//                        Toast.makeText(getApplicationContext(), "SMS Sent!",
//                                Toast.LENGTH_LONG).show();
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(),
//                                "SMS failed, please try again later!",
//                                Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }

                }
            }







//
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//        if(resultCode == RESULT_CANCELED){
//            Toast.makeText(this, "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
//            turnOnBT();
//        }
//        else if(resultCode == RESULT_OK) {
//            Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
//            readIntent = new Intent (LoginActivity.this,readService.class);
//            startService(readIntent);
//            BtIntent = new Intent (LoginActivity.this,BluetoothService.class);
//            startService(BtIntent);
//        }
//    }
//
//    private void turnOnBT(){
//        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(intent,Request_BT_Enable);
//    }
//
//    @Override
//    protected void onStart(){
//        myMainReceiver = new MyMainReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(readService.ACTION_UPDATE_ACCELEROMETER);
//        registerReceiver(myMainReceiver,intentFilter);
//        pulseReceiver = new HrReceiver(); //suraj
//        IntentFilter HRfilter = new IntentFilter();
//        HRfilter.addAction("HR_UPDATE");
//        registerReceiver(pulseReceiver,HRfilter);
//        super.onStart();
//    }
//
//
//    private class MyMainReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context,Intent intent){
//            String action = intent.getAction();
//            if(action.equals(readService.ACTION_UPDATE_ACCELEROMETER)){
//                Bundle getAccUpdate = intent.getExtras();
//                xValue = getAccUpdate.getString("Xaxis");
//                yValue = getAccUpdate.getString("Yaxis");
//                zValue = getAccUpdate.getString("Zaxis");
//                xAccel = getAccUpdate.getDouble("Xacc");
//                yAccel = getAccUpdate.getDouble("Yacc");
//                zAccel = getAccUpdate.getDouble("Zacc");
//
//                predictPosture();
//            }
//        }
//    }
//
//    private class HrReceiver extends BroadcastReceiver { //suraj
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("HR_UPDATE")) {
//                Bundle HrUpdate = intent.getExtras();
//                pulserate = HrUpdate.getFloat("pulse");
//
//                heartCondition();
//            }
//        }
//    }
//
//    public void predictPosture(){
//        if ((xAccel < 0.5) && (yAccel < 0.5) && (zAccel > 8))
//        {
//
//            fall = true;
//            Toast.makeText(this, "Falling centre", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel < 0.5) && (yAccel > 0.5) && (zAccel > 8))
//        {
//
//            fall = false;
//            //Toast.makeText(this, "Sitting", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel>5)&&(yAccel<2)&&(zAccel > 4)){
//
//            fall = true;
//            //Toast.makeText(this, "Falling right", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel<-5)&&(yAccel<2)&&(zAccel<4)){
//
//            fall = true;
//            //Toast.makeText(this, "Falling left", Toast.LENGTH_SHORT).show();
//        }else if ((xAccel<2)&&(yAccel>8)&&(zAccel<5)){
//
//            fall = false;
//           // Toast.makeText(this, "standing", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void heartCondition(){
//        if(fall) {
//            if (pulserate < 500f) {
//                if (first = true) {
//                    first = false;
//                    oldPulse = pulserate;
//                    start = System.currentTimeMillis();
//                } else {
//                    newPulse = pulserate;
//                    end = System.currentTimeMillis();
//                    first = true;
//                    oldPulse = newPulse;
//                }
//                if ((end - start) > 30000) {
//                    Intent interrupt = new Intent();
//                    interrupt.setAction("emergency");
//                    interrupt.putExtra("HeartAttack", SOS);
//                    sendBroadcast(interrupt);
//                }
//
//            }
//        }else  {
//            Toast.makeText(this, "Checks sensors", Toast.LENGTH_SHORT).show();
//        }
//    }




}



