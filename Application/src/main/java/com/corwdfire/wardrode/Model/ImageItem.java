package com.corwdfire.wardrode.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rahul on 11/22/16.
 */

public class ImageItem implements Parcelable {
    private long id;
    private long imageUrl;
    private int position;
    private int type;

    public ImageItem() {

    }

    private ImageItem(Parcel in) {
        id = in.readLong();
        imageUrl = in.readLong();
        position = in.readInt();
        type = in.readInt();
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public long getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(long imageUrl) {
        this.imageUrl = imageUrl;
    }


    public int getPosition() {
        return position;
    }


    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(imageUrl);
        dest.writeInt(position);
        dest.writeInt(type);
    }

}
