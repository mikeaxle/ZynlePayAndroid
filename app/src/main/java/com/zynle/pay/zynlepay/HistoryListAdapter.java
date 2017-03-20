package com.zynle.pay.zynlepay;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.Date;

/**
 * Created by michaellungu on 3/16/17.
 * custom adapter for sale list
 */

public class HistoryListAdapter extends ArrayAdapter<PastSale> {

    private int layoutResourceID;

    public HistoryListAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);

        layoutResourceID = resource;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {

            //get current sale
            PastSale item = getItem(position);


            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(layoutResourceID, null);

            } else {
                v = convertView;
            }

            //get textviews
            TextView date = (TextView) v.findViewById(R.id.historyListDateTextView);
            TextView amount = (TextView) v.findViewById(R.id.historyListAmountTextView);

            Date d = new Date(item.getTimeStamp());

            //assign with values from current item
            date.setText(d.toString());
            amount.setText(item.getTotalAmount());

            return v;
        } catch (Exception ex) {
            Log.e("ZynlePay", "error", ex);
            return null;
        }
    }

}
