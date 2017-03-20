package com.zynle.pay.zynlepay;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryActivity extends Fragment {

    ListView historyListView;                   //list view ui element
    HistoryListAdapter historyListAdapter;      //list adapter
    TinyDB tinydb;                              //tinybd for reader shared preferences
    ArrayList<PastSale> pastSales;              //arraylist of pastsale type
    TextView noHistory;                         //textview

    public HistoryActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment into variable
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        //load shared preferences in local arraylist
        tinydb = new TinyDB(v.getContext());
        pastSales = tinydb.getListObject("history", PastSale.class);

        //check if arraylist is empty
        if(pastSales.isEmpty()){

            //show text
            Log.d("ZynlePay", "the history item is empty");
            noHistory = (TextView) v.findViewById(R.id.noHistoryText);
            noHistory.setVisibility(View.VISIBLE);

        } else {
            //show history
            historyListView = (ListView) v.findViewById(R.id.historyListView);
            historyListView.setVisibility(View.VISIBLE);
            historyListAdapter = new HistoryListAdapter(getActivity(), R.layout.history_list_row);
            historyListAdapter.addAll(pastSales);
            historyListView.setAdapter(historyListAdapter);
        }
        //return view
        return v;

    }
}
