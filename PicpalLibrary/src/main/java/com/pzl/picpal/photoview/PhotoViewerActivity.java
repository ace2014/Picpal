package com.pzl.picpal.photoview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pzl.picpal.Constant;
import com.pzl.picpal.R;
import com.pzl.picpal.Util;
import com.squareup.picasso.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 浏览，删除 不涉及添加（外部添加）
 *
 */
public class PhotoViewerActivity extends AppCompatActivity {

    ImageButton backButton;

    TextView titleText;

    MyViewPager viewPager;

    RelativeLayout bottomLayout;

    Button deleteButton;

    private static Params params;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        getSupportActionBar().hide();

        if (params == null) finish();

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        params = null;
    }

    private void initView() {
        backButton = (ImageButton) findViewById(R.id.left_menu_button);
        titleText = (TextView) findViewById(R.id.title_text);
        viewPager = (MyViewPager) findViewById(R.id.viewpager);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        deleteButton = (Button) findViewById(R.id.btn_delete);
    }


    private void initData() {

        if (params.isEdit) {
            bottomLayout.setVisibility(View.VISIBLE);
        } else {
            bottomLayout.setVisibility(View.GONE);
        }

        viewPager.setAdapter(new MyPagerAdapter(params.imageParamList));
        viewPager.setCurrentItem(params.currentPosition);
        titleText.setText((params.currentPosition + 1) + " / " + (params.imageParamList.size()));
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

    }

    private void initListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (params.photoViewListener != null)
                    params.photoViewListener.onDeletePicture(params.currentPosition);
                Intent resultData = new Intent();
                if (params.imageParamList.size() == 1) {
                    params.imageParamList.remove(params.currentPosition);
                    fillResultData(resultData);
                    finish();
                } else {
                    params.imageParamList.remove(params.currentPosition);
                    viewPager.setAdapter(new MyPagerAdapter(params.imageParamList));
                    if (params.currentPosition == 0) {
                        params.currentPosition = 0;
                    }
                    if (params.currentPosition > 0) {
                        params.currentPosition = params.currentPosition - 1;
                    }
                    viewPager.setCurrentItem(params.currentPosition);
                    titleText.setText((params.currentPosition + 1) + " / " + params.imageParamList.size());
                    fillResultData(resultData);
                }
            }
        });
    }

    private void fillResultData(Intent resultData) {
        if (!params.isEdit) return;
        switch (params.paramType) {
            case Constant.ParamType.COMPOSITE:
                resultData.putParcelableArrayListExtra(Constant.Intent.INTENT_RESULT_PHOTO_LIST, (ArrayList<? extends Parcelable>) params.imageParamList);
                break;
            case Constant.ParamType.FILE:
                resultData.putExtra(Constant.Intent.INTENT_RESULT_PHOTO_LIST, (Serializable) params.imageParamList);
                break;
            case Constant.ParamType.INTEGER:
                resultData.putIntegerArrayListExtra(Constant.Intent.INTENT_RESULT_PHOTO_LIST, (ArrayList<Integer>) params.imageParamList);
                break;
            case Constant.ParamType.STRING:
                resultData.putExtra(Constant.Intent.INTENT_RESULT_PHOTO_LIST, (Serializable) params.imageParamList);
                break;
            case Constant.ParamType.URI:
                resultData.putParcelableArrayListExtra(Constant.Intent.INTENT_RESULT_PHOTO_LIST, (ArrayList<? extends Parcelable>) params.imageParamList);
                break;
        }
        setResult(RESULT_OK, resultData);
    }


    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            params.currentPosition = position;
            titleText.setText((position + 1) + " / " + (params.imageParamList.size()));
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     *
     */
    public static interface IPhotoView {
        void onDeletePicture(int index);
    }

    class MyPagerAdapter extends PagerAdapter {

        private List dataList = new ArrayList();

        public MyPagerAdapter(List dataList) {
            this.dataList = dataList;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

            Util.requestCreator(PhotoViewerActivity.this, dataList.get(position))
                    .placeholder(params.resDefault) //默认初始化图片
                    .error(params.resError) //下载图片出错
                    .into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                        }
                    });

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public static Params init(Activity ctx, int requestCode) {
        params = new Params(ctx, requestCode);
        return params;
    }

}
