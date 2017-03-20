package com.zynle.pay.zynlepay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentSuccessActivity extends AppCompatActivity {


    AlertDialog.Builder alert;      //alert dialog
    ProgressDialog progressDialog;  //progress dialog


    String TAG = "ZynlePay";        //Tag for logs

    String sms;                     //sms string
    EditText phoneNumber;           //phone number ui elemet
    String phoneNumberDigits;       //phone number string
    String url;                     //url string
    String ref;                     //reference number
    double totalSale;               //total sale

    TinyDB tinydb;                  //shared preferences helper class
    ArrayList<PastSale>pastSales;   //arraylist to store instance of shared preferences
    PastSale currentPastSale;       //past sale to store current past sale
    Date d;                         //date object to get timestamp
    private DecimalFormat df;       //decimal format for amount




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        d = new Date();

        //get intent from charge card
        Intent previousI = getIntent();
        ref = previousI.getExtras().getString("ref");

        //ref ="placeholder";

        //load ref into text view
        TextView refNumber = (TextView) findViewById(R.id.refTextView);
        refNumber.setText("Reference #: " + ref);

        //load amount total
        totalSale = AppSingleton.getInstance().getTotalSales();

        //load into payment summary text
        TextView paymentSummary = (TextView) findViewById(R.id.paymentSummaryTextView);
        paymentSummary.setText("Payment for " + totalSale + " was successful.");

        //create decimal format
        df = new DecimalFormat("#0.00");

        //initialize edit text ui element
        phoneNumber = (EditText) findViewById(R.id.phoneNumberText);

        //initialize progress dialog
        progressDialog = new ProgressDialog(this);

        //initialize progress alert
        alert = new AlertDialog.Builder(this);

        //instantiate tinydb
        tinydb = new TinyDB(this);

        //load shared preferences history
        pastSales = tinydb.getListObject("history", PastSale.class);

        //create new past sale object with timestamp and total of sale
        currentPastSale = new PastSale(d.getTime(), df.format(totalSale));

        //add into Array list
        pastSales.add(currentPastSale);

        //write to shared storage
        tinydb.putListObject("history",pastSales);

    }


    //on button click
    public void onClick(View view){
        //assign characters from ui element to phone number digits
        phoneNumberDigits = phoneNumber.getText().toString().trim();

        //check if phone number field is empty
        if(phoneNumberDigits.isEmpty() || phoneNumberDigits.equals("") || phoneNumberDigits == null){

            Log.d(TAG,"No phone number was entered");

            //delete sales
            AppSingleton.getInstance().clearSales();

            //send to home page
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        } else {

            //check phone number length
            if(phoneNumberDigits.length() != 10) {

                Toast.makeText(this, "SMS cannot be sent to " + phoneNumberDigits + ". Check phone number.", Toast.LENGTH_SHORT).show();

            } else {

                //assign value to sms
                sms = String.format("Thank you for your order. You receipt number is %s for the amount of K %s", ref.substring(0, 5), totalSale);

                //use string format to add variables to string
                url = String.format("http://www.bulksms.co.zm/smsservice/httpapi?username=zynlepay&password=zynle12&msg=%s&shortcode=2343&sender_id=0955000679&phone=%s&api_key=121231313213123123", sms, phoneNumberDigits);

                //call sms api
                makeStringRequest();

                //delete sales
                AppSingleton.getInstance().clearSales();

                //send to home page
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }
    }

    private void makeStringRequest(){

        //show loading screen
        showProgressDialog();

        //create string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response){
                        //log repsonse
                        Log.d(TAG, response);

                        //show Toast
                        Toast.makeText(PaymentSuccessActivity.this, "Receipt Sent to " + phoneNumberDigits, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

                //log error message. most likely network error
                Log.d(TAG, "Non descript Volley Error: " + error.getMessage());

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
            }
        });

        // Adding request to request queue
        AppSingleton.getInstance().addToRequestQueue(stringRequest);
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

    //override hardware back button
    @Override
    public void onBackPressed(){

        //show alert
        alert.setTitle("Not allowed...");
        alert.setMessage("You cannot go back to the previous screen. You can start a new transaction by tapping the NEXT TRANSACTION button or you can exit the ZynlePay")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setNegativeButton("Exit ZynlePay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alert.create();
        alert.show();
    }
}
