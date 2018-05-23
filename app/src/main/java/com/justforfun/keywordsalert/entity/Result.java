package com.justforfun.keywordsalert.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class Result implements Parcelable {
    private String title;
    private String webLink;

    // getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    // constructors
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
}
