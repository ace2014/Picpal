package com.pzl.picpal;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.pzl.picpal.photoview.IimageParam;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.List;

/**
 * Created by zl.peng on 2017/6/6 14:45.
 */

public class Util {

    /**
     * 复合
     *
     * @param ctx
     * @param imageParam
     * @return
     */
    private static RequestCreator picassoCompositeImageParam(Context ctx, IimageParam imageParam) {
        Picasso picasso = Picasso.with(ctx);
        RequestCreator requestCreator = null;
        switch (imageParam.imageType()) {
            case IimageParam.TYPE_FILE:
                requestCreator = picasso.load((File) imageParam.imageParam());
                break;
            case IimageParam.TYPE_RES_ID:
                requestCreator = picasso.load((int) imageParam.imageParam());
                break;
            case IimageParam.TYPE_STRING:
                requestCreator = picasso.load((String) imageParam.imageParam());
                break;
            case IimageParam.TYPE_URI:
                requestCreator = picasso.load((Uri) imageParam.imageParam());
                break;
        }
        return requestCreator;
    }

    /**
     * 简单
     *
     * @param ctx
     * @param simpleImageParam
     * @return
     */
    private static RequestCreator picassoSimpleImageParam(Context ctx, Object simpleImageParam) {

        Picasso picasso = Picasso.with(ctx);
        RequestCreator requestCreator = null;

        if (simpleImageParam instanceof Integer) {
            requestCreator = picasso.load((Integer) simpleImageParam);
            return requestCreator;
        }
        if (simpleImageParam instanceof File) {
            requestCreator = picasso.load((File) simpleImageParam);
            return requestCreator;
        }
        if (simpleImageParam instanceof String) {
            requestCreator = picasso.load((String) simpleImageParam);
            return requestCreator;
        }
        if (simpleImageParam instanceof Uri) {
            requestCreator = picasso.load((Uri) simpleImageParam);
            return requestCreator;
        }
        return requestCreator;
    }

    /**
     * @param ctx
     * @param imageParam
     * @return
     */
    public static RequestCreator requestCreator(Context ctx, Object imageParam) {
        if (imageParam instanceof IimageParam) {
            return picassoCompositeImageParam(ctx, (IimageParam) imageParam);
        } else {
            return picassoSimpleImageParam(ctx, imageParam);
        }
    }

    /**
     * @param imageParamList
     * @return
     */
    public static int calcImageParamType(List imageParamList) {
        if (imageParamList == null || imageParamList.isEmpty()) return -1;
        if (imageParamList instanceof IimageParam)
            return Constant.ParamType.COMPOSITE;
        Object item = imageParamList.get(0);
        if (item instanceof Integer) {
            return Constant.ParamType.INTEGER;
        }
        if (item instanceof File) {
            return Constant.ParamType.FILE;
        }
        if (item instanceof String) {
            return Constant.ParamType.STRING;
        }
        if (item instanceof Uri) {
            return Constant.ParamType.URI;
        }
        return -1;
    }

    /**
     * 判断sdcard是否存在，并返回sdcard路径。
     *
     * @return 返回sdcard路径
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        if (sdDir == null) {
            return null;
        }
        return sdDir.toString();
    }

}
