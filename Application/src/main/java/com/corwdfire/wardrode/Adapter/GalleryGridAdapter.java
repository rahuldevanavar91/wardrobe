package com.corwdfire.wardrode.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.corwdfire.wardrode.R;
import com.corwdfire.wardrode.Imagedownloader.ImageLoader;
import com.corwdfire.wardrode.Imagedownloader.ScalingUtilities;
import com.corwdfire.wardrode.Model.ImageItem;

import java.util.List;

/**
 * Created by Rahul on 11/22/16.
 */
public class GalleryGridAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final OnItemClickListener mListener;

    private List<ImageItem> mList;


    private int mColumnWidth;
    private ImageItem mSelectedImageItem;

    public GalleryGridAdapter(Context context, List<ImageItem> imagesList, OnItemClickListener listener, float screenWidthInDp) {
        mLayoutInflater = LayoutInflater.from(context);
        mList = imagesList;
        mListener = listener;
        mColumnWidth = (int) screenWidthInDp;
        mContext = context;
        mSelectedImageItem = new ImageItem();
    }

    public int getCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public ImageItem getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.gallery_grid_list_item, parent, false);
            AbsListView.LayoutParams params = (AbsListView.LayoutParams) convertView.getLayoutParams();
            if (params != null) {
                params.height = mColumnWidth;
                convertView.setLayoutParams(params);
            }
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            updateHeaderView(viewHolder, position);
        } else {
            viewHolder.itemView.setTag(position);
            updateItemView(viewHolder, position);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                updateCheckCount(position);
                mListener.onItemClick(view, position, mSelectedImageItem);
            }
        });

        return convertView;
    }

    private void updateCheckCount(int position) {
        if (position == mSelectedImageItem.getPosition()) {
            mSelectedImageItem = new ImageItem();
        } else {
            ImageItem imageItem = mList.get(position);
            imageItem.setPosition(position);
            mSelectedImageItem = imageItem;
        }
        notifyDataSetChanged();
    }
    private void updateCheckBox(CheckBox checkBox, int position) {

        if (mSelectedImageItem.getPosition() == position) {
            checkBox.setChecked(true);

        } else {
            checkBox.setChecked(false);
        }
    }

    private void updateHeaderView(ViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageBitmap(null);
        viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_camera));
        viewHolder.checkBox.setVisibility(View.GONE);
        viewHolder.itemView.setTag(position);
        viewHolder.takePhoto.setVisibility(View.VISIBLE);
    }

    private void updateItemView(final ViewHolder viewHolder, int position) {
        final ImageItem item = mList.get(position);
        viewHolder.imageView.setBackground(null);
        viewHolder.takePhoto.setVisibility(View.GONE);
        viewHolder.imageView.setVisibility(View.VISIBLE);
        viewHolder.checkBox.setVisibility(View.VISIBLE);
        viewHolder.imageView.setTag(item.getImageUrl());
        ImageLoader.getInstance(mContext).displayImage(String.valueOf(item.getImageUrl()), viewHolder.imageView, mColumnWidth, mColumnWidth, ScalingUtilities.ScalingLogic.CROP);
        viewHolder.checkBox.setClickable(false);
        updateCheckBox(viewHolder.checkBox, position);
    }

    public void updateList(List<ImageItem> imageItems) {
        mList = imageItems;
        notifyDataSetChanged();
    }


    public List<ImageItem> getList() {
        return mList;
    }

    private class ViewHolder {
        private View itemView;
        private ImageView imageView;
        private CheckBox checkBox;
        private TextView takePhoto;

        public ViewHolder(View convertView) {
            itemView = convertView.findViewById(R.id.grid_item_view);
            imageView = (ImageView) convertView.findViewById(R.id.grid_image);
            checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
            takePhoto = (TextView) convertView.findViewById(R.id.take_photo);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Object object);
    }
}

