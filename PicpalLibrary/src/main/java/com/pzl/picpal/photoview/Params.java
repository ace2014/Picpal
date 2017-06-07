package com.pzl.picpal.photoview;

import android.app.Activity;
import android.content.Intent;


import com.pzl.picpal.R;
import com.pzl.picpal.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zl.peng
 */

public class Params {

    private Activity ctx;

    private int requestCode;

    public Params(Activity ctx, int requestCode) {
        this.ctx = ctx;
        this.requestCode = requestCode;
    }

    /**
     * 当前显示第几张图片
     */
    public int currentPosition;

    public Params at(int showPosition) {
        this.currentPosition = showPosition;
        return this;
    }

    /**
     * 是否开启编辑图片功能（删除）
     */
    public boolean isEdit;

    public Params edit(boolean isEdit) {
        this.isEdit = isEdit;
        return this;
    }

    /**
     * 多种类型相混合(File,Uri,String,Integer)的复合类型参数（实现IimageParam接口的）或者简单类型参数(4种类型的list，如果简单类型的要混合，请转换复合类型的)
     */
    public List imageParamList;
    /**
     * 参数类型
     */
    public int paramType = -1;

    public Params imageList(ArrayList imageParamList) {
        this.imageParamList = imageParamList;
        this.paramType = Util.calcImageParamType(imageParamList);
        return this;
    }

    /**
     * 默认图片资源id
     */
    public int resDefault = R.drawable.ic_default;

    public Params defaultRes(int resDefault) {
        this.resDefault = resDefault;
        return this;
    }

    /**
     * 错误图片资源id
     */
    public int resError = R.drawable.ic_error;

    public Params errorRes(int resError) {
        this.resDefault = resError;
        return this;
    }

    /**
     * 监听
     */
    public PhotoViewerActivity.IPhotoView photoViewListener;

    public Params listener(PhotoViewerActivity.IPhotoView photoViewListener) {
        this.photoViewListener = photoViewListener;
        return this;
    }

    public void go() {
        ctx.startActivityForResult(new Intent(ctx, PhotoViewerActivity.class), requestCode);
    }

}
