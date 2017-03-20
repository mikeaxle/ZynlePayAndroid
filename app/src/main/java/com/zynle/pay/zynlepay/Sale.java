package com.zynle.pay.zynlepay;

/**
 * Created by michaellungu on 3/14/17.
 */

public class Sale {

    //declare variables
     String itemName;
     String amount;



    //constructor
    public  Sale(String amt, String itm ){

        amount = amt;
        itemName = itm;
    }

    //overloaded constructor
    public Sale(String amt){

        amount = amt;
        itemName = "product";
    }

    //setter methods
    public void setItemName(String itm){
        this.itemName = itm;
    }

    public void setAmount(String amt){
        this.amount = amt;
    }


    //getter methods
    public String getItemName(){
        return this.itemName;
    }

    public String getAmount(){
        return this.amount;
    }

}
