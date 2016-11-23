package com.corwdfire.wardrode.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rahul on 11/22/16.
 */

public class Item implements Parcelable {
    private int id;
    private String imageUrl;
    private boolean isFavorite;

    protected Item(Parcel in) {
        id = in.readInt();
        imageUrl = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public Item() {

    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
