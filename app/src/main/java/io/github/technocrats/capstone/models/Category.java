package io.github.technocrats.capstone.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

    private int category_id;
    private String category_name;
    private float value;

    public Category(int category_id, String category_name, float value) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.value = value;
    }

    public Category(Parcel parcel) {
        category_id = parcel.readInt();
        category_name = parcel.readString();
        value = parcel.readFloat();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    //
    // getters and setters
    //
    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // write object values to parcel for storage - follow order of constructor
        dest.writeInt(category_id);
        dest.writeString(category_name);
        dest.writeFloat(value);
    }
}