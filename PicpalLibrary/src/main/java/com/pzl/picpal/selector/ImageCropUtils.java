package com.pzl.picpal.selector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 系统图片裁剪
 *
 * @author bobo
 */
public class ImageCropUtils {

    private String SDCARD_CROP_IMAGE_DIR = "justonetech_p" + File.separator + "CropImage";
    private String strFilePath = "/mnt/sdcard/justonetech_p/CropImage/";
    private File file = null;
    private String filePath;
    private Context context = null;
    private static final String PHOTO_NAME_STYLE = "yyyyMMddHHmmss";
    private Uri imageUri;
    private Activity activity;
    private final String TAG = ImageCropUtils.class.getSimpleName();

    public ImageCropUtils(Context context) {
        this.context = context;
        activity = (Activity) context;
        //init();
    }

    public String getUri() {
        return imageUri.toString();
    }

    public String getFilePath() {
        return filePath;
    }

    /**
     * 初始化
     */
    private void init() {
        strFilePath = createSDCardFolder(SDCARD_CROP_IMAGE_DIR) + File.separator;
        file = new File(strFilePath);

    }

    /**
     * sd卡根目录下相对路径("justonetech_p" + File.separator + "CropImage")
     *
     * @param path
     */
    public void init(String path) {
        strFilePath = createSDCardFolder(path) + File.separator;
        file = new File(strFilePath);
    }

    /**
     * 检测SD卡是否存在
     *
     * @return true表示存在
     */
    public boolean checkSDCardIsExits() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 创建文件夹
     *
     * @return 返回文件夹路径
     */
    public static String createSDCardFolder(String dirFolder) {
        String extSdCard = getSDPath();
        if (TextUtils.isEmpty(extSdCard)) {
            extSdCard = getExtSDCard();
        }
        File folder = new File(extSdCard + File.separator + dirFolder);
        if (folder.exists() == false) {
            folder.mkdirs(); // 创建文件夹完整路径
        }
        return folder.toString();
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

    public static String getExtSDCard() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            String mount = new String();
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1]);
                    }
                } /*
                 * else if (line.contains("fuse")) { String columns[] =
				 * line.split(" "); if (columns != null && columns.length > 1) {
				 * mount = mount.concat(columns[1] + "\n"); } }
				 */
            }
            return mount;
        } catch (FileNotFoundException e) {
            loadMsg(e.getMessage());
        } catch (IOException e) {
            loadMsg(e.getMessage());
        }
        return null;
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     */
    public static String createNewPhotoName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_NAME_STYLE);
        return dateFormat.format(date) + ".jpg";
    }


    public void openCamera(int resultCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        autoResetImageUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, resultCode);
    }

    public void autoResetImageUri() {
        try {
            String photoName = createNewPhotoName();
            filePath = strFilePath + photoName;
            //android版本在7.0+需要做处理
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                imageUri = Uri.parse(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file) + File.separator + photoName);
            } else {
                imageUri = Uri.parse(Uri.fromFile(file) + File.separator + photoName);
            }
            loadMsg(imageUri + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resetImageUri(Uri uri) {
        imageUri = uri;
        loadMsg(imageUri + "");
    }


    public void openGallery(int resultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        resetImageUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(imageUri, "image/*");
        activity.startActivityForResult(intent, resultCode);
    }


    public void cropBigPhotoByCamera(int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            List resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resInfoList.size() == 0) {
                Toast.makeText(activity, "没有合适的相机应用程序", Toast.LENGTH_SHORT).show();
                return;
            }
            Iterator resInfoIterator = resInfoList.iterator();
            while (resInfoIterator.hasNext()) {
                ResolveInfo resolveInfo = (ResolveInfo) resInfoIterator.next();
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    public void cropSmallPhotoByCamera(int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    public void openGalleryAndCropSmallPhoto(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        autoResetImageUri();
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    public void openGalleryAndCropBigPhoto(int requestCode) {
        autoResetImageUri();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }


    public Bitmap getBitmapByUri() {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri, int CUT_PHOTO) {
        autoResetImageUri();
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的
		 */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            List resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resInfoList.size() == 0) {
                Toast.makeText(activity, "没有合适的相机应用程序", Toast.LENGTH_SHORT).show();
                return;
            }
            Iterator resInfoIterator = resInfoList.iterator();
            while (resInfoIterator.hasNext()) {
                ResolveInfo resolveInfo = (ResolveInfo) resInfoIterator.next();
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false); // no face detection
        activity.startActivityForResult(intent, CUT_PHOTO);
    }

    public Bitmap getBitmapByIntent(Intent data) {
        imageUri = data.getData();

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public InputStream getInPutStreamByIntent(Intent data) {
        Bitmap bitmap = getBitmapByIntent(data);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] b = stream.toByteArray(); // 将图片流以字符串形式存储下来
        return new ByteArrayInputStream(b);
    }


    public byte[] getByteArrayByBitmap(Bitmap bitmap) {
        if (null == bitmap) {
            throw new NullPointerException("Bitmap is Empty");
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
    }

    public InputStream getInPutStreamByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] b = stream.toByteArray(); // 将图片流以字符串形式存储下来
        return new ByteArrayInputStream(b);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    /**
     * 通过URI获取文件
     *
     * @param originalUri
     * @return
     */
    public String getAbsloutePath(Uri originalUri) {
        try {
            //这里开始的第二部分，获取图片的路径：
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = ((Activity) context).managedQuery(originalUri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(column_index);
            //"文件路径：\n"+
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 通过URI获取文件
     *
     * @param uri
     * @return
     */
    public String getSelectPhotoPath(Uri uri) {
        String picturePath = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri, filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (columnIndex >= 0) {
                picturePath = cursor.getString(columnIndex);  //获取照片路径
            }
            cursor.close();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            picturePath = uri.getPath();
        }
        return picturePath;
    }

    public class CameraBuilder {
        private int outputX;
        private int outputY;
        private int aspectX;
        private int aspectY;
        private boolean scale;
        private boolean noFaceDetection;
    }

    /**
     * Log消息打印
     *
     * @param msg
     */
    private static void loadMsg(String msg) {
        Log.i(ImageCropUtils.class.getSimpleName() + "--->", msg);
    }

}
