//this activity needs validations

package com.zynle.pay.zynlepay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;

public class SaleItemActivity extends AppCompatActivity {

    private static final String TAG = "ZynlePay";
    Sale sale;
    int saleID;
    EditText amount;
    EditText itemName;
    Intent i;

    // public static final String EXTRA_SALE_ID = "saleID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_item);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent b = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(b);
                finish();
            }
        });

        //get saleID from intent
        saleID = (int) getIntent().getExtras().get("sale_id");

        //get Sale
        sale = AppSingleton.getInstance().getSale(saleID);

        //get ui elements
        amount = (EditText) findViewById(R.id.amountEditText);
        itemName = (EditText) findViewById(R.id.noteEditText);

        //get current item name and amount
        amount.setText(sale.getAmount());
        itemName.setText(sale.getItemName());

    }


    public void onClick(View view){

        i = new Intent(getApplicationContext(), SaleListActivity.class);

        switch (view.getId()){
            case R.id.removeButton:
                //delete sale
                AppSingleton.getInstance().deleteSale(saleID);

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                finish();


                Log.d(TAG, "Sale Deleted");
                break;

            case R.id.saveButton:
                //set changes
                sale.setAmount(amount.getText().toString());
                sale.setItemName(itemName.getText().toString());

                //write changes to singlton object
                AppSingleton.getInstance().editSale(saleID, sale);

                Log.d(TAG, "Sale Saved");

                //navigate back
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                finish();


                break;
        }
    }
}