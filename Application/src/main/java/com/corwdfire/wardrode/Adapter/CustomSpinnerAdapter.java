package com.corwdfire.wardrode.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.corwdfire.wardrode.R;

import java.util.List;

/**
 * Created by Rahul on 11/22/16.
 */
public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private List<String> mData;
    private LayoutInflater mInflater;
    private int mWidth;

    public CustomSpinnerAdapter(Context context, List<String> items) {
        super(context, R.layout.spinner_row, items);
        mData = items;
        mInflater = LayoutInflater.from(context);
        mWidth = (int) context.getResources().getDimension(R.dimen._300dp);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int position, ViewGroup parent) {
        View row = mInflater.inflate(R.layout.spinner_row, parent, false);
        TextView titleText = (TextView) row.findViewById(R.id.title_view);
        titleText.setTag(mData.get(position));
        titleText.setText(mData.get(position));
        row.getLayoutParams().width = mWidth;
        return row;
    }
}