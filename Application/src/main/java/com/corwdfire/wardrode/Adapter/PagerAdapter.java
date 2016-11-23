package com.corwdfire.wardrode.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.corwdfire.wardrode.Fragments.PagerItemFragment;
import com.corwdfire.wardrode.Model.ImageItem;

import java.util.ArrayList;

/**
 * Created by rahul on 11/22/16.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ImageItem> mPagerList;

    public PagerAdapter(FragmentManager fm, ArrayList<ImageItem> list) {
        super(fm);
        mPagerList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return PagerItemFragment.newInstance(mPagerList.get(position));
    }


    @Override
    public int getCount() {
        return (mPagerList != null ? mPagerList.size() : 0);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void updateList(ArrayList<ImageItem> list) {
        mPagerList = list;
        notifyDataSetChanged();
    }
}
