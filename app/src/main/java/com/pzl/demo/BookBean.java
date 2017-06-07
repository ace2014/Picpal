package com.pzl.demo;

import android.os.Parcel;
import android.os.Parcelable;

import com.pzl.picpal.photoview.IimageParam;


/**
 * Created by zl.peng on 2017/6/6 17:47.
 */

public class BookBean implements IimageParam {

    public String bookName;

    public int price;

    public int number;

    public String picUrl;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookName);
        dest.writeInt(this.price);
        dest.writeInt(this.number);
        dest.writeString(this.picUrl);
    }

    public BookBean() {
    }

    protected BookBean(Parcel in) {
        this.bookName = in.readString();
        this.price = in.readInt();
        this.number = in.readInt();
        this.picUrl = in.readString();
    }

    public static final Parcelable.Creator<BookBean> CREATOR = new Parcelable.Creator<BookBean>() {
        @Override
        public BookBean createFromParcel(Parcel source) {
            return new BookBean(source);
        }

        @Override
        public BookBean[] newArray(int size) {
            return new BookBean[size];
        }
    };

    @Override
    public Object imageParam() {
        return picUrl;
    }

    @Override
    public int imageType() {
        return IimageParam.TYPE_STRING;
    }
}
