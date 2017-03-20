package com.zynle.pay.zynlepay;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

//import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static  String TAG = "ZynlePay";
    private String currentSale;
    private Button chargeButton;
    String totalSales;

    //store notes
    String note;
    EditText addNoteEditText;


    private Sale sale;                                              //instantiate sale object


    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;

    private DecimalFormat decimalFormat;                           //decimal format class

    //private ImageButton salesDock;                                //sales Dock imagebutton


    TextView currentSaleDisplay;                                  //current sale ui
    TextView salesCount;
    TextView totalAmount;


    AlertDialog.Builder alert;                                   //alert dialog


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load toolbar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        salesCount = (TextView) findViewById(R.id.numberOfSalesTextView);
        salesCount.setText("" + AppSingleton.getInstance().getSalesCount());

        alert = new AlertDialog.Builder(this);

        //create decimal format
        decimalFormat = new DecimalFormat("#0.00");

        note = "";

        //set total
        if(AppSingleton.getInstance().getTotalSales() == 0){

        } else {
            TextView t = (TextView) findViewById(R.id.totalTextView);
            t.setText("Charge K " + AppSingleton.getInstance().getTotalSales() + "0");
        }


        //resize dock
        /*salesDock = (ImageButton) findViewById(R.id.salesDockImageButton);
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_sales_dock);
        Bitmap bpScaled = Bitmap.createScaledBitmap(bp, 100, 100, true);
        salesDock.setImageBitmap(bpScaled);*/

        //load tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("KEYPAD"));
        tabLayout.addTab(tabLayout.newTab().setText("SALES REPORT"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        
        //load view pager
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager()); //load adapter
        viewPager.setAdapter(adapter); //set adapter
        
        //override tab listener methods
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    //click handler for all buttons on this activity. Switch...case to handle all possible selections
    public void onClick(View view){

        currentSaleDisplay = (TextView) findViewById(R.id.currentSaleDisplayTextView);
        currentSale =  currentSaleDisplay.getText().toString().substring(2);
        currentSale = currentSale.substring(0,currentSale.length() -3);



        switch (view.getId()){

            case R.id.addNoteImageButton:

                LayoutInflater li = LayoutInflater.from(this);
                View propmtsView = li.inflate(R.layout.addnote_prompt, null);

                AlertDialog.Builder addNoteDialog = new AlertDialog.Builder(this);
                addNoteDialog.setView(propmtsView);
                addNoteEditText = (EditText) propmtsView.findViewById(R.id.saveNoteEditText);

                //addNoteEditText.setText(note);

                addNoteDialog
                        .setCancelable(false)
                        .setPositiveButton("Save Note", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //set note
                                note = addNoteEditText.getText().toString();
                                //dialog.cancel();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                        dialog.cancel();
                    }
                });

                addNoteDialog.create();
                addNoteDialog.show();


                break;

            case R.id.viewItemsButton:
                //navigate to sales list page
                if( AppSingleton.getInstance().getSalesCount() != 0){
                    Intent i = new Intent(getApplicationContext(), SaleListActivity.class);
                    startActivity(i);

                } else {

                    Toast.makeText(this, "You have no items in your sale list", Toast.LENGTH_SHORT).show();

                }
                break;



            case R.id.salesDockImageButton:
                //navigate to sales list page
                if( AppSingleton.getInstance().getSalesCount() != 0){
                    Intent i = new Intent(getApplicationContext(), SaleListActivity.class);
                    startActivity(i);

                } else {

                    Toast.makeText(this, "You have no items in your sale list", Toast.LENGTH_SHORT).show();

                }
                break;

            case R.id.buttonCharge:

                //load total sales into local variable
                totalSales = decimalFormat.format(AppSingleton.getInstance().getTotalSales());


                //check if total sales is equal to 0.00 in string
                if(totalSales.equals("0.00")){
                    Toast.makeText(this, "Add an amount", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getApplicationContext(), ChargeCardActivity.class);
                    startActivity(i);
                }

                break;

            case R.id.buttonOne:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("1") + ".00");
                break;

            case R.id.buttonTwo:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("2") + ".00");
                break;

            case R.id.buttonThree:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("3") + ".00");
                break;

            case R.id.buttonFour:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("4") + ".00");
                break;

            case R.id.buttonFive:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("5") + ".00");
                break;

            case R.id.buttonSix:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("6") + ".00");
                break;


            case R.id.buttonSeven:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("7") + ".00");
                break;


            case R.id.buttonEight:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("8") + ".00");
                break;

            case R.id.buttonNine:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("9") + ".00");
                break;

            case R.id.buttonZero:
                currentSaleDisplay.setText("K " + AddNumberToDisplay("0") + ".00");
                break;


            case R.id.buttonClear:
                //set current sale text back to 0
                currentSaleDisplay.setText("K 0.00");
                break;

            case R.id.buttonAdd:
                //set current sale text back to 0


                if(currentSale.equals("0") ){

                    Toast.makeText(this, "Cannot Add amount of 0", Toast.LENGTH_SHORT).show();
      

                } else {
                    currentSaleDisplay.setText("K 0.00");

                    //get total view
                    totalAmount = (TextView) findViewById(R.id.totalTextView);



                    //instantiate new sale object

                    if (note.equals("")){

                        sale = new Sale(currentSale);

                    } else {

                        sale = new Sale(currentSale, note);

                    }

                    //if note was not set
                    if(addNoteEditText != null){

                        //reset note
                        addNoteEditText.setText("");
                        note = "";
                    }

                    //add sale to total sales
                    AppSingleton.getInstance().setSales(sale);

                    //set total price
                    totalSales = decimalFormat.format(AppSingleton.getInstance().getTotalSales());
                    totalAmount.setText("K " + totalSales);

                    //add to sales dock
                    salesCount.setText("" + AppSingleton.getInstance().getSalesCount());

                    Log.d(TAG, "the total sales is: " + AppSingleton.getInstance().getTotalSales());
                    Log.d(TAG, "the number of sales is: " + AppSingleton.getInstance().getSalesCount());
                }
                


                break;

            default:
                break;
        }

    }


    //method to check value of current sale and either append tapped value or overwrite 0 value with tapped value
    public String AddNumberToDisplay(String number){

            if(currentSale.equals("0")){
                currentSale = number;
            } else {
                currentSale += number;
            }

        return currentSale;
    }


    //override hardware back button
    @Override
    public void onBackPressed(){

        //show alert
        alert.setTitle("Caution");
        alert.setMessage("Would You like to exit ZynlePay?")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setNegativeButton("Exit ZynlePay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                //System.exit(0);
            }
        });

        alert.create();
        alert.show();
    }


}
