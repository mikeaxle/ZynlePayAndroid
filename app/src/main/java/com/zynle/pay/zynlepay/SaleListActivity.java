package com.zynle.pay.zynlepay;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;



public class SaleListActivity extends AppCompatActivity {


    SaleListAdapter sla;     //custom adapter
    ListView lv;            //list view ui element

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppSingleton.getInstance().getSalesCount() == 0) {

            Intent i = new Intent (getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();

        }
        setContentView(R.layout.activity_sale_list);

        final Toolbar tb = (Toolbar) findViewById(R.id.saleListToolBar);
        tb.setTitle("Total: K " + AppSingleton.getInstance().getTotalSales() + "0");
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent b = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(b);
                //finish();
            }
        });


        //instantiate custom adapter
        sla = new SaleListAdapter(this, R.layout.sale_list_row);

        //register listener
        sla.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {

                super.onChanged();
                //check in the sale list is now empty
                if(AppSingleton.getInstance().getTotalSales() == 0){

                    Intent i = new Intent(getApplicationContext(), MainActivity.class );
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();

                } else {

                    tb.setTitle("Total: K " + AppSingleton.getInstance().getTotalSales() + "0");


                }

            }
        });

        //add global sales object to adapter
        sla.addAll(AppSingleton.getInstance().getSales());

        //get list view
        lv  = (ListView) findViewById(R.id.saleListView);

        //set adapter
        lv.setAdapter(sla);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(getApplicationContext(),SaleItemActivity.class);
                            i.putExtra("sale_id", (int) id);
                            startActivity(i);
                    }
                }
        );

    }

    public void onClick(View view){
        AppSingleton.getInstance().clearSales();
        Intent i = new Intent (getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    //override hardware back button
    @Override
    public void onBackPressed(){

        //super.onBackPressed();
    }
}
