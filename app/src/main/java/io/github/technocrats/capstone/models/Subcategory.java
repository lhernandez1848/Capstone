package io.github.technocrats.capstone.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Subcategory implements Parcelable {

    private int subcategory_id;
    private String subcategory_name;
    private float value;

    public Subcategory(int subcategory_id, String subcategory_name, float value) {
        this.subcategory_id = subcategory_id;
        this.subcategory_name = subcategory_name;
        this.value = value;
    }

    public Subcategory(Parcel parcel) {
        subcategory_id = parcel.readInt();
        subcategory_name = parcel.readString();
        value = parcel.readFloat();
    }

    public static final Creator<Subcategory> CREATOR = new Creator<Subcategory>() {
        @Override
        public Subcategory createFromParcel(Parcel in) {
            return new Subcategory(in);
        }

        @Override
        public Subcategory[] newArray(int size) {
            return new Subcategory[size];
        }
    };

    //
    // getters and setters
    //
    public int getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(int subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
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
        dest.writeInt(subcategory_id);
        dest.writeString(subcategory_name);
        dest.writeFloat(value);
    }
}