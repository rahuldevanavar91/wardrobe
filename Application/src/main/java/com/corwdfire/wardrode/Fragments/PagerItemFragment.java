package com.corwdfire.wardrode.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.corwdfire.wardrode.Activity.MainActivity;
import com.corwdfire.wardrode.R;
import com.corwdfire.wardrode.Imagedownloader.ImageLoader;
import com.corwdfire.wardrode.Imagedownloader.ScalingUtilities;
import com.corwdfire.wardrode.Model.ImageItem;

/**
 * Created by rahul on 11/22/16.
 */

public class PagerItemFragment extends Fragment {
    public static final int SHIRT_ADD_PHOTO_REQUEST = 100;
    private ImageView mImage;
    private int mPostion;
    private int mTYpe;

    public static PagerItemFragment newInstance(ImageItem item) {
        PagerItemFragment f = new PagerItemFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        f.setArguments(args);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageItem imageItem = getArguments().getParcelable("item");
        mTYpe = imageItem.getType();
        mPostion = imageItem.getPosition();
        View view;
        if (mTYpe == MainActivity.SHIRT_TYPE) {
            view = inflater.inflate(R.layout.shirt_pager_item, container, false);
        } else {
            view = inflater.inflate(R.layout.pant_pager_item, container, false);
        }
        getWidgets(view);
        if (imageItem.getImageUrl() > 0) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            ImageLoader.getInstance(getContext()).displayImage(String.valueOf(imageItem.getImageUrl()), mImage, screenWidth, screenHeight / 2, ScalingUtilities.ScalingLogic.FIT);
        }
        return view;
    }

    private void getWidgets(View view) {
        ImageView addButton = (ImageView) view.findViewById(R.id.add_button);
        mImage = (ImageView) view.findViewById(R.id.image);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).startGalleryActivity(mPostion, mTYpe);
                }
            }
        });
    }
}

