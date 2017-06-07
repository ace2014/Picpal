package com.pzl.picpal.photoview;

import android.os.Parcelable;

/**
 * Created by zl.peng on 2017/6/6 14:39.
 */

public interface IimageParam extends Parcelable{

    int TYPE_URI = 1;

    int TYPE_STRING = 2;

    int TYPE_RES_ID = 3;

    int TYPE_FILE = 4;

    Object imageParam();

    int imageType();

}
