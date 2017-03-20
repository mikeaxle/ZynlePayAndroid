package com.zynle.pay.zynlepay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imagpay.MessageHandler;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeHandler;
import com.imagpay.SwipeListener;

import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

//iMagPay packages
//Volley Packages

public class ChargeCardActivity extends AppCompatActivity {

    //tag for logs
    private static String TAG = "ZynlePay";

    private static final int MY_SCAN_REQUEST_CODE = 100;

    //zynle api respsonse url
    public String urlJsonObj;
    //"http://www.zynlepay.com:8070/zynlepay/zpay/api/runCardReader?api_id=0977547820&merchant_id=45&request_id=1486433684&key=NDRhN2MzNGY0MzNkZjg5ZDQ5OTM5MTNiOWQyZTYyMDkxOThkODM4ZA==&amount=100&cardnumber=4383755000927515&expirymonth=08&expiryyear=22&cvv=549&product=undefined%20product&nameoncard=Michael%20lungu";


    //alert dialog
    AlertDialog.Builder alert;

    //progress dialog
    ProgressDialog progressDialog;

    // temporary string to show the parsed response
    private String jsonResponse;


    //decimal formatting class
    private DecimalFormat decimalFormat;


    //declare card detail variables
    String name = "";
    String cardNumber = "";
    String expiryMonth = "";
    String expiryYear = "";
    String cvv = "";
    String amount;

    //iMagPay objects
    private SwipeHandler _handler;
    private MessageHandler _msg;
    private Handler _ui;
    private boolean _testFlag = false;

    EditText cet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_card);

        //init decimal formatter
        decimalFormat = new DecimalFormat("#0.00");

        //store total amount in local variable
        amount = decimalFormat.format(AppSingleton.getInstance().getTotalSales());

        //set toolbar + title
        Toolbar myToolbar = (Toolbar) findViewById(R.id.chargeCardToolBar);
        myToolbar.setTitle("Charging: K " + amount);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent b = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(b);
            }
        });


        //display text input errors
        //TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.nameLayout);
        //nameLayout.setErrorEnabled(true);
        //nameLayout.setError("Required");


        //initialize progress dialog
        progressDialog = new ProgressDialog(this);

        //initialize progress alert
        alert = new AlertDialog.Builder(this);

        //initialize handler
        _handler = new SwipeHandler(this);
        _handler.setReadonly(true);

        //initialize looper
        _ui = new Handler(Looper.myLooper());

        //add swwipe handler
        _handler.addSwipeListener(new SwipeListener() {
            @Override
            public void onDisconnected(SwipeEvent swipeEvent) {
                Log.d(TAG,"Device is disconnected!");
                toggleConnectStatus();
            }

            @Override
            public void onConnected(SwipeEvent swipeEvent) {
                Log.d(TAG,"Device is connected!");
                checkDevice();
            }

            @Override
            public void onStarted(SwipeEvent swipeEvent) {

                if (_testFlag)
                    return;
                Log.d(TAG,"Device is started");
                toggleConnectStatus();

            }

            @Override
            public void onStopped(SwipeEvent swipeEvent) {
                if (_testFlag)
                    return;
                Log.d(TAG,"Device is stopped");
                toggleConnectStatus();
            }

            @Override
            public void onReadData(SwipeEvent swipeEvent) {

            }

            @Override
            public void onParseData(SwipeEvent event) {
                if (_testFlag)
                    return;
                String result = event.getValue();
                Log.d(TAG, result);
                // hex string message
                Log.d(TAG,"Final(16)=>% " + result);

                String[] tmps = event.getValue().split(" ");
                StringBuffer sbf = new StringBuffer();
                for (String str : tmps) {
                    sbf.append((char) Integer.parseInt(str, 16));
                    sbf.append(" ");
                }
                // char message: b{card number}^{card holder}^{exp date}{other track1 data}?;{track2 data}
                // or            b{card number}&{card holder}&{exp date}{other track1 data}?;{track2 data}
                final String data = sbf.toString().replaceAll(" ", "");
                int idx = data.indexOf("^");
                // plain text of card data
                if (data.toUpperCase().startsWith("B") && idx > 0 && data.indexOf("?") > 0) {
                    Log.d(TAG,"Final(10)=>% " + data);
                    _ui.post(new Runnable() {
                        @Override
                        public void run() {
                            int idx = data.indexOf("^");
                            String cardNo = data.substring(1, idx);
                            String cardHolder = "";
                            String expDate = "";
                            int idx1 = data.indexOf("^", idx + 1);
                            if (idx1 > 0 && idx1 < data.length() - 4) {
                                cardHolder = data.substring(idx + 1, idx1);
                                expDate = data.substring(idx1 + 1, idx1 + 1 + 4);
                            }

                            EditText et = (EditText) findViewById(R.id.cardText);
                            et.setText(cardNo);

                            et = (EditText) findViewById(R.id.nameText);
                            et.setText(cardHolder);

                            et = (EditText) findViewById(R.id.monthText);
                            et.setText(expDate.substring(2));

                            et = (EditText) findViewById(R.id.yearText);
                            et.setText(expDate.substring(0,2));


                        }
                    });
                }
                // encryption data of card data
                else if (data.length() > 20 + 5 + 4) {
                    _ui.post(new Runnable() {
                        @Override
                        public void run() {

                            EditText et = (EditText) findViewById(R.id.cardText);
                            et.setText("****************");

                            et = (EditText) findViewById(R.id.nameText);
                            et.setText("********");

                            et = (EditText) findViewById(R.id.monthText);
                            et.setText("**");

                            et = (EditText) findViewById(R.id.yearText);
                            et.setText("**");
                        }
                    });
                } else {
                    _ui.post(new Runnable() {
                        @Override
                        public void run() {



                            /*EditText et = (EditText) findViewById(R.id.cardText);
                            et.setText("");

                            et = (EditText) findViewById(R.id.nameText);
                            et.setText("");

                            et = (EditText) findViewById(R.id.monthText);
                            et.setText("");

                            et = (EditText) findViewById(R.id.yearText);
                            et.setText("");*/



                        }
                    });
                }
            }

            @Override
            public void onICDetected(SwipeEvent swipeEvent) {

            }
        });

    }

    public void onClick(View view){

        //load text input data into variables and remove trailing and leading whitespaces
        EditText et = (EditText) findViewById(R.id.nameText);
        name = et.getText().toString().trim();

        et = (EditText) findViewById(R.id.cardText);
        cardNumber = et.getText().toString().trim();

        et = (EditText) findViewById(R.id.monthText);
        expiryMonth = et.getText().toString().trim();

        et = (EditText) findViewById(R.id.yearText);
        expiryYear = et.getText().toString().trim();

        et = (EditText) findViewById(R.id.cvvText);
        cvv = et.getText().toString().trim();


        //check which button was pressed
        switch (view.getId()){
            case R.id.chargecardButton:

                //check if all fields are entered
                if(name.isEmpty() || cvv.isEmpty()|| expiryMonth.isEmpty()|| expiryYear.isEmpty() || cardNumber.isEmpty()){

                    //show alert
                    alert.setTitle("Missing Information...");
                    alert.setMessage("You have not filled in all the required fields to perform a transaction.")
                            .setPositiveButton("Correct this", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alert.create();
                    alert.show();

                } else {

                    //call zynlepay api
                    makeJsonObjectRequest();
                }


                break;

            case R.id.scanCardImageButton:
                //call card scan api
                Intent scanIntent = new Intent(this, CardIOActivity.class);

                // customize these values to suit your needs.
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, false);
                scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
                scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);


                // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
                startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);

                break;

            default:
                break;
        }
    }

    //Card IO activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {

            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";


                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );
                cet = (EditText) findViewById(R.id.cardText);
                cet.setText(scanResult.cardNumber);



                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";

                //    cet = (EditText) findViewById(R.id.monthText);
                //    cet.setText(scanResult.expiryMonth);

                //    cet = (EditText) findViewById(R.id.yearText);
                //    cet.setText(scanResult.expiryYear);

                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";

                  //  cet = (EditText) findViewById(R.id.cvvText);
                  //  cet.setText(scanResult.cvv);

                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }

                Log.d(TAG, "card scan result: " + resultDisplayStr);
            }
            else {
                resultDisplayStr = "Scan was canceled.";
                Toast.makeText(this, "Scan was canceled", Toast.LENGTH_SHORT).show();
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

    //check if card reader is connected
    private void checkDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!_handler.isConnected()) {
                    toggleConnectStatus();
                    return;
                }
                if (_handler.isPowerOn()) {
                    toggleConnectStatus();
                    return;
                }
                if (_handler.isReadable()) {
                    Log.d("ZynlePay","Device is ready");
                    _handler.powerOn();
                    _handler.powerOn(10000, 1, (short)0, Short.MAX_VALUE, 22050);
                } else {
                    _testFlag = true;
                    Log.d("ZynlePay","Please wait! testing parameter now");
                    if (_handler.test() && _handler.isReadable()) {
                        _testFlag = false;
                        Log.d("ZynlePay","Device is ready");
                        _handler.powerOn();
                    } else {
                        _testFlag = false;
                        Log.d("ZynlePay","Device is not supported or Please close some audio effects(SRS/DOLBY/BEATS/JAZZ/Classic...) and insert device!");
                    }
                }
                toggleConnectStatus();
            }
        }).start();
    }

    //toggle connection status on screen
    private void toggleConnectStatus() {
        _ui.postDelayed(new Runnable() {
            @Override
            public void run() {

                //load image view and text view into activity
                ImageView connectionImage = (ImageView) findViewById(R.id.connectionImageView);
                TextView cardReaderText = (TextView) findViewById(R.id.cardReaderTextView);

                if (_handler.isConnected() && _handler.isPowerOn()
                        && _handler.isReadable()) {

                    //set image and text to connected
                    connectionImage.setImageResource(R.drawable.ic_check_circle_green);
                    cardReaderText.setText("Card reader is connected");

                } else {

                    //set image and text to disconnected
                    connectionImage.setImageResource(R.drawable.ic_clear_circle_red);
                    cardReaderText.setText("Card reader is disconnected");
                }
            }
        }, 500);
    }


    //start card reader
    public void onStart() {
        super.onStart();
        checkDevice();
    }

    //stop card reader
    public void onStop() {
        _handler.powerOff();
        super.onStop();
    }

    //destroy card reader
    public void onDestroy() {
        _handler.onDestroy();
        super.onDestroy();
    }

    //method to call payment api
    private void makeJsonObjectRequest() {

        //finish adding variables and such

        showProgressDialog();

        //urlJsonObj = createUrlString("100","4383755000927515","08","22","432","product","Michael");

        //create request string
        urlJsonObj = createUrlString(amount.substring(0,amount.length() -3),cardNumber,expiryMonth,expiryYear,cvv,"product",name);

        Log.d(TAG, "url is: " + urlJsonObj);



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

                        //load transaction id into SingleTon
                        String transactionID = response.getString("transactionId");

                        //navigate to next activity
                        Intent i = new Intent(getApplicationContext(), PaymentSuccessActivity.class);
                        i.putExtra("ref", transactionID);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(i);

                    } else {

                        //show alert
                        alert.setTitle("Transaction Failed");
                        alert.setMessage("The card was not charged.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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

    //create url string
    public static String createUrlString(String amount, String cardNo, String expiryMonth, String expiryYear, String cvv, String product, String nameOnCard){


        Date date = new Date();
        double requestId = Math.floor(date.getTime() / 1000);


        String secret = "b1ad6c0262edce80e705c030760951a35530c771";

        //generate key using sha1
        String generate_key = KeyGenerator(secret + (int)requestId);

        String url = "http://www.zynlepay.com:8070/zynlepay/zpay/api/runCardReader?api_id=0977547820&merchant_id=45";

        url += "&request_id=" + (int)requestId;
        url += "&key=" + generate_key;
        url += "&amount=" + amount;
        url += "&cardnumber=" + cardNo;
        url += "&expirymonth=" + expiryMonth;
        url += "&expiryyear=" + expiryYear;
        url += "&cvv=" + cvv;
        url += "&product=" + product;
        url += "&nameoncard=" + URLEncoder.encode(nameOnCard);

        Log.d(TAG, "new Date: " + date.getTime());
        Log.d(TAG, "requestID: " + (int)requestId);
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


}
