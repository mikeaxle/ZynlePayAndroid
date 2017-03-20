package com.zynle.pay.zynlepay;

/**
 * Created by michaellungu on 3/16/17.
 */

public class PastSale {

    private Long timeStamp;
    private String totalAmount;

    public PastSale(Long time, String amount){

        this.timeStamp = time;
        this.totalAmount = amount;

    }

    public Long getTimeStamp(){
        return this.timeStamp;
    }

    public String getTotalAmount(){
        return this.totalAmount;
    }
}
