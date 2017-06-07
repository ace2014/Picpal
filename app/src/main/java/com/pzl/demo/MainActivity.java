package com.pzl.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pzl.picpal.selector.ImageCropUtils;
import com.pzl.picpal.photoview.PhotoViewerActivity;
import com.pzl.picpal.selector.Selector;
import com.yanzhenjie.album.Album;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.pzl.picpal.Constant.Code.OPEN_CAMERA;
import static com.pzl.picpal.Constant.Code.PHOTO_PICKED_WITH_DATA;
import static com.pzl.picpal.Constant.Code.RESULT_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageCropUtils imageCropUtils;

    ArrayList<String> stringImage;

    ArrayList<BookBean> bookBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageCropUtils = new ImageCropUtils(this);

        stringImage = new ArrayList<>();
        stringImage.add("http://img5.imgtn.bdimg.com/it/u=611483611,2895064642&fm=23&gp=0.jpg");
        stringImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495622715252&di=6daaeffc847f1c8afb6cedf693dcd900&imgtype=0&src=http%3A%2F%2Fimg002.21cnimg.com%2Fphotos%2Falbum%2F20150202%2Fm600%2F5D9A9ADACAA54BABBA8FB1A664D488DF.jpeg");
        stringImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496217463&di=785ad0640dd7beba7b56141d8cbab1cd&imgtype=jpg&er=1&src=http%3A%2F%2Fimg002.21cnimg.com%2Fphotos%2Falbum%2F20150202%2Fm600%2F5EC42BB98DC6997E74F7831A97A48F20.jpeg");
        stringImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495622767087&di=b58645c03f9d59d021550c1f7d3057c0&imgtype=0&src=http%3A%2F%2F58pic.ooopic.com%2F58pic%2F17%2F86%2F12%2F08G58PICvNa.jpg");
        stringImage.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495622794068&di=5fea1efd37ce1ff5a5441272d3be547f&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farchive%2F375bb81c136db59039ded681ecaaf71132e7776b.jpg");

        bookBeanArrayList = new ArrayList<>();

        for (int i = 0; i <= 8; i++) {
            BookBean bookBean = new BookBean();
            bookBean.bookName = "百年孤独";
            bookBean.number = 99;
            bookBean.price = 128;
            bookBean.picUrl = "http://www.iyunshu.com/supp/Public/Uploads/20160503/572873f505b43.png";
            bookBeanArrayList.add(bookBean);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                PhotoViewerActivity.init(this, RESULT_REQUEST_CODE).at(2).edit(true).imageList(stringImage).go();
                break;
            case R.id.btn2:
                PhotoViewerActivity.init(this, RESULT_REQUEST_CODE).at(2).edit(true).imageList(bookBeanArrayList).defaultRes(R.drawable.ic_default).errorRes(R.drawable.ic_error).
                        listener(new PhotoViewerActivity.IPhotoView() {
                            @Override
                            public void onDeletePicture(int index) {
                                Toast.makeText(MainActivity.this, "delete " + index, Toast.LENGTH_SHORT).show();
                            }
                        }).go();
                break;
            case R.id.btn3:
                Selector.init(this, imageCropUtils).cameraPath("pengziliang/lxyy").number(5).statusBarColor(Color.RED).toolbarColor(Color.RED).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case OPEN_CAMERA:// 从相机
                Toast.makeText(this, imageCropUtils.getFilePath(), Toast.LENGTH_LONG).show();
                Log.d("MainActivity", imageCropUtils.getFilePath());
                break;
            case PHOTO_PICKED_WITH_DATA:
                if (null != data) {
                    // 选择好了照片后，调用这个方法解析照片路径的List。
                    List<String> pathList = Album.parseResult(data);
                    Toast.makeText(this, Arrays.toString(pathList.toArray()), Toast.LENGTH_LONG).show();
                    Log.d("MainActivity", Arrays.toString(pathList.toArray()));
                }
                break;
            case RESULT_REQUEST_CODE:
                if (null != data) {
                    // data.getStringArrayListExtra(INTENT_RESULT_PHOTO_LIST);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
