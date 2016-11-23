package com.corwdfire.wardrode.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rahul on 11/23/16.
 */

public class FavoriteItem implements Parcelable {
    private long shirtId;
    private long pantId;

    public FavoriteItem() {

    }

    protected FavoriteItem(Parcel in) {
        shirtId = in.readLong();
        pantId = in.readLong();
    }

    public static final Creator<FavoriteItem> CREATOR = new Creator<FavoriteItem>() {
        @Override
        public FavoriteItem createFromParcel(Parcel in) {
            return new FavoriteItem(in);
        }

        @Override
        public FavoriteItem[] newArray(int size) {
            return new FavoriteItem[size];
        }
    };

    public long getPantId() {
        return pantId;
    }

    public long getShirtId() {
        return shirtId;
    }

    public void setPantId(long pantId) {
        this.pantId = pantId;
    }

    public void setShirtId(long shirtId) {
        this.shirtId = shirtId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(shirtId);
        dest.writeLong(pantId);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FavoriteItem) {
            FavoriteItem favoriteItem = (FavoriteItem) o;
            return favoriteItem.getPantId() == this.getPantId() && favoriteItem.getShirtId() == this.getShirtId();
        }
        return false;
    }

}
