package com.pzl.picpal.selector;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.pzl.picpal.Constant;
import com.pzl.picpal.Util;
import com.yanzhenjie.album.Album;

import static com.pzl.picpal.Constant.Code.PHOTO_PICKED_WITH_DATA;

/**
 * Created by zl.peng on 2017/6/7 11:12.
 */

public class Selector {

    private static Selector instance;

    private Activity activity;

    private ImageCropUtils imageCropUtils;

    private int toolbarColor = Color.CYAN;

    private int statusBarColor = Color.CYAN;

    /**
     * 最多能选择的数目限制
     */
    private int number = Integer.MAX_VALUE;

    private Selector() {
    }

    private Selector(Activity activity, ImageCropUtils imageCropUtils) {
        this.activity = activity;
        this.imageCropUtils = imageCropUtils;
    }

    private void showDialog() {
        new ActionSheetDialog(activity)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Black,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, Constant.Code.PERMISSIONS_REQUEST_CAMERA);
                                    } else {
                                        //拍照
                                        if (Util.getSDPath() == null) {
                                            Toast.makeText(activity, "SD卡不可用", Toast.LENGTH_LONG).show();
                                        } else {
                                            imageCropUtils.openCamera(Constant.Code.OPEN_CAMERA);
                                        }
                                    }
                                } else {
                                    //拍照
                                    if (Util.getSDPath() == null) {
                                        Toast.makeText(activity, "SD卡不可用", Toast.LENGTH_LONG).show();
                                    } else {
                                        imageCropUtils.openCamera(Constant.Code.OPEN_CAMERA);
                                    }
                                }
                            }
                        })
                .addSheetItem("从相册中选取", ActionSheetDialog.SheetItemColor.Black, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        //从相册
                        if (Util.getSDPath() == null) {
                            Toast.makeText(activity, "SD卡不可用", Toast.LENGTH_LONG).show();
                        } else {
                            Album.startAlbum(activity, PHOTO_PICKED_WITH_DATA
                                    , number // 指定选择数量。
                                    , toolbarColor   // 指定Toolbar的颜色。
                                    , statusBarColor);  // 指定状态栏的颜色。
                        }
                    }
                }).show();
    }


    public static Selector init(Activity ctx, ImageCropUtils imageCropUtils) {
        if (instance == null) instance = new Selector(ctx, imageCropUtils);
        return instance;
    }

    /**
     * 照相机照片储存，sd卡根目录下相对路径("justonetech_p" + File.separator + "CropImage")
     * @param cameraPath
     * @return
     */
    public Selector cameraPath(String cameraPath){
        instance.imageCropUtils.init(cameraPath);
        return instance;
    }

    public Selector toolbarColor(@ColorInt int toolbarColor) {
        instance.toolbarColor = toolbarColor;
        return instance;
    }

    public Selector statusBarColor(@ColorInt int statusBarColor) {
        instance.statusBarColor = statusBarColor;
        return instance;
    }

    public Selector number(int number) {
        instance.number = number;
        return instance;
    }

    public void show() {
        instance.showDialog();
    }


}
