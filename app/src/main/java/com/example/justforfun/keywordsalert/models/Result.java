package com.example.justforfun.keywordsalert.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

public class Result implements Parcelable{
    public String title;
    public String webLink;
    public Result(String title, String webLink){
        this.title = title;
        this.webLink = webLink;
    }

    protected Result(Parcel in) {
        title = in.readString();
        webLink = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(webLink);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Result)){
            return false;
        }else{
            return ((Result) obj).webLink.equals( this.webLink) && ((Result) obj).title.equals(this.title);
        }
    }

    @Override
    public int hashCode() {
        return (title+webLink).hashCode();
    }
}
