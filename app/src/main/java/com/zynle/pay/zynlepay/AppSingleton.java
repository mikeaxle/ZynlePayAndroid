package com.zynle.pay.zynlepay;

/**
 * Created by michaellungu on 3/14/17.
 * Application class that acts as a singleton for api reading and session data persistence
 */

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class AppSingleton extends Application {

    public static final String TAG = AppSingleton.class.getSimpleName();
    private RequestQueue mRequestQueue;



    //array list
    private ArrayList<Sale> sales;

    private static AppSingleton mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppSingleton getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    //return sales array list
    public ArrayList<Sale> getSales(){
        return sales;
    }

    //method to set sale
    public void setSales(Sale s){

        //check if sale is null
        if(sales == null){

            //init arraylist
            sales = new ArrayList<Sale>();
        }
        //add item to sale
        sales.add(s);
    }

    //method to edit sale item
    public void editSale(int index, Sale s){

        //change item at index
        sales.set(index, s);
    }

    //method to delete sale item
    public void deleteSale(int index){
        sales.remove(index);
    }

    //method to get single sale
    public Sale getSale(int index){

        return sales.get(index);

    }

    //method to get total number of sales
    public int getSalesCount(){

        if(sales == null) {
            sales = new ArrayList<Sale>();
        }
        return sales.size();
    }

    //method to get total value of sales
    public double getTotalSales(){

        double totalSales = 0.0;

        //check if sales object is null
        if(sales == null){
            sales = new ArrayList<Sale>();
        }

        //iterate and add sales
        for(Sale sale: sales){
            totalSales += Double.parseDouble(sale.getAmount());
        }

        return  totalSales;
    }

    //method to empty sales list
    public void clearSales(){
        sales.clear();
    }





    //save sales history in shared preferences
    /*public void saveHistory(ArrayList<PastSale> pastSales){
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);

        if (pSharedPref != null){

            //JSONObject jsonObject = new JSONObject(inputMap);
           // String jsonString = jsonObject.toString();

            Set<String> set = new HashSet<String>();
            set.addAll(pastSales);

            Editor editor = pSharedPref.edit();
            editor.remove("History_map").commit();

            editor.putString("History_map", jsonString);
            editor.commit();
        }
    }

    //load sales history in shared preferences
    public LinkedHashMap<String,Long> loadHistory(){
        LinkedHashMap<String,Long> outputMap = new LinkedHashMap<String,Long>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){

                String jsonString = pSharedPref.getString("History_map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    Long value = (Long) jsonObject.get(key);
                    outputMap.put(key, value);
                }
                Log.d(TAG, "contents of history: " + jsonString);

            } else {
                Log.d(TAG, "Nothing  in the history");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }*/

}