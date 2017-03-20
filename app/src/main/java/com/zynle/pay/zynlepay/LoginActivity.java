package com.zynle.pay.zynlepay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;

public class LoginActivity extends AppCompatActivity {

    TinyDB tinydb;                              //tinybd for reader shared preferences
    boolean loggedIn;

    EditText phone;
    EditText password;
    EditText merchantID;

    //zynle api respsonse url
    public String urlJsonObj;
    //"http://www.zynlepay.com:8070/zynlepay/zpay/api/runCardReader?api_id=0977547820&merchant_id=45&request_id=1486433684&key=NDRhN2MzNGY0MzNkZjg5ZDQ5OTM5MTNiOWQyZTYyMDkxOThkODM4ZA==&amount=100&cardnumber=4383755000927515&expirymonth=08&expiryyear=22&cvv=549&product=undefined%20product&nameoncard=Michael%20lungu";


    //alert dialog
    AlertDialog.Builder alert;

    //progress dialog
    ProgressDialog progressDialog;

    // temporary string to show the parsed response
    private String jsonResponse;

    //tag for logs
    private static String TAG = "ZynlePay";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //load shared preferences in local arraylist
        tinydb = new TinyDB(getApplicationContext());
        loggedIn = tinydb.getBoolean("loggedIn");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = (EditText) findViewById(R.id.phoneNumberEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        merchantID = (EditText) findViewById(R.id.merchantIDEditText);

        //initialize progress dialog
        progressDialog = new ProgressDialog(this);

        //initialize progress alert
        alert = new AlertDialog.Builder(this);

        //show progress dialog
        showProgressDialog();

        //check if logged in
        if(loggedIn){
            Toast.makeText(this, "You are already logged in.", Toast.LENGTH_LONG).show();
            //navigate to next activity
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(i);
        }

        //hide progress dialog
        hideProgressDialog();
    }

    public void onClick(View view){
        /*if (phone.getText().toString().trim() == ""){

            //phone.setError("Empty");

            Toast.makeText(this, "Phone number is missing", Toast.LENGTH_SHORT).show();

            if (password.getText().toString().trim() == ""){

                if (merchantID.getText().toString().trim() == ""){

                }

            }
        } else {
            //call api

        }*/

        makeJsonObjectRequest();

    }

    private void makeJsonObjectRequest() {


        /**************testing*************** initialize local state

         state = {
         'username': '0977547820',
         'password': '1234',
         'merchant_id': '45',
         'loading': false,
         'error': ''
         }*/


        showProgressDialog();


        //create request string
        urlJsonObj = createUrlString(phone.getText().toString().trim(), password.getText().toString().trim(), merchantID.getText().toString().trim());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    String responseCode = response.getString("responseCode");
                    jsonResponse = response.toString();

                    Log.d(TAG, jsonResponse);

                    //check value of response description
                    if(responseCode.equals("100")){


                        //save logged in status to shared preferences
                        tinydb.putBoolean("loggedIn", true);

                        Toast.makeText(LoginActivity.this, "You are now permanently logged in. ", Toast.LENGTH_SHORT).show();

                        //navigate to next activity
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(i);

                    } else {

                        //show alert
                        alert.setTitle("Something went wrong...");
                        alert.setMessage("Your login details didn't work. If you do not have login credentials, please contact Zynle customer care at 0955000679")
                                .setPositiveButton("Call Zynle now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "0955000679"));
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);


                                    }
                                })
                                .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                    }
                                });

                        alert.create();
                        alert.show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.e(TAG, "Error: " + e.getMessage());

                    //show alert
                    alert.setTitle("An error has occurred");
                    alert.setMessage("Error: " + e.getMessage())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alert.create();
                    alert.show();
                }

                hideProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d(TAG, "Error: " + error.getMessage());

                //show alert
                alert.setTitle("No internet connectivity...");
                alert.setMessage("It appears your phone is not connected to the internet. Please connect to a wifi network or turn on your mobile data")
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alert.create();
                alert.show();

                // hide the progress dialog
                hideProgressDialog();
            }
        });

        // Adding request to request queue
        AppSingleton.getInstance().addToRequestQueue(jsonObjReq);
    }

    //create url string
    public static String createUrlString(String _phone, String _password, String _merchantID){


        Date date = new Date();
        double requestId = Math.floor(date.getTime() / 1000);


        String secret = "b1ad6c0262edce80e705c030760951a35530c771";

        //generate key using sha1
        String generate_key = KeyGenerator(secret + (int)requestId);

        String url = String.format("http://www.zynlepay.com:8070/zynlepay/zpay/api/userLogin?api_id=0977547820&username=%s&password=%s&merchant_id=%s&request_id=%s&key=%s", _phone, _password, _merchantID, (int)requestId, generate_key);

        Log.d(TAG, "url: " + url);

        return url;
    }

    //generated SHA1/MD5
    public static String KeyGenerator(String key){

        String encoded = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(key.getBytes());
            encoded = Base64.encodeAsString(new BigInteger(1, md.digest()).toString(16).getBytes());

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return encoded ;
    }

    //show progress dialog
    private void showProgressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    //hide progress dialog
    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
