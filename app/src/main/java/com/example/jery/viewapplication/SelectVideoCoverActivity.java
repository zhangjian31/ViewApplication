package com.example.jery.viewapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;


import java.io.File;

public class SelectVideoCoverActivity extends AppCompatActivity implements View.OnClickListener, VideoCoverSeekBar.OnSeekBarChangeListener {
    private final static int PIC_THUMB_NUM = 8;
    private TextView mBackTv, mOKTv;
    private VideoCoverSeekBar mSelectSeekBar;
    private LinearLayout mPicContainerLayout;
    private ImageView mCenterIv;
    private VideoView mVideoview;
    private int mInitTime;
    private int mCurrentTime;
    private String mVideoPath;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoDuration;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video_cover);
        onFindView();
        requestPermision();
    }

    protected void onFindView() {
        mInitTime = 100;
        mVideoPath = "/storage/emulated/0/DCIM/hetun/Video/03a5e5e1585d6c3df5b6a52229b15096.mp4";
        mVideoWidth = 540;
        mVideoHeight = 960;
        mVideoDuration = 105326;
        mBackTv = (TextView) findViewById(R.id.tv_back);
        mOKTv = (TextView) findViewById(R.id.tv_ok);
        mPicContainerLayout = (LinearLayout) findViewById(R.id.bottom_pic_layout);
        mSelectSeekBar = (VideoCoverSeekBar) findViewById(R.id.select_seekBar);
        mCenterIv = (ImageView) findViewById(R.id.iv_center);
        mVideoview = (VideoView) findViewById(R.id.videoview);
//        mSelectSeekBar.setMax(mVideoDuration);
//        mSelectSeekBar.setProgress(mInitTime);
        mSelectSeekBar.setOnSeekBarChangeListener(this);
        mSelectSeekBar.setCartCount(PIC_THUMB_NUM);
        mSelectSeekBar.seek(1.0f * mInitTime / mVideoDuration);
        mBackTv.setOnClickListener(this);
        mOKTv.setOnClickListener(this);
    }

    protected boolean onInitData() {
        ImageLoadUtil.simpleLoad(SelectVideoCoverActivity.this, Uri.fromFile(new File(mVideoPath)), mCenterIv, mInitTime * 1000);
        mVideoview.setVideoPath(mVideoPath);
        loadCoverBitmaps();
        return true;
    }

    private void loadCoverBitmaps() {
        int perDuration = (mVideoDuration - 200) / PIC_THUMB_NUM;
        for (int i = 0; i < PIC_THUMB_NUM; i++) {
            ImageView imageView = new ImageView(SelectVideoCoverActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            mPicContainerLayout.addView(imageView, lp);
            ImageLoadUtil.simpleLoad(SelectVideoCoverActivity.this, Uri.fromFile(new File(mVideoPath)), imageView, (100 + i * perDuration) * 1000);
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_back) {
            finish();
        } else if (view.getId() == R.id.tv_ok) {
            Intent intent = new Intent();
            intent.putExtra("init_time", mCurrentTime);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


    @Override
    public void onProgressChanged(VideoCoverSeekBar seekBar, float progress) {
        mCurrentTime = (int) (progress * mVideoDuration);
        mVideoview.seekTo(mCurrentTime);
    }

    @Override
    public void onStartTrackingTouch(VideoCoverSeekBar seekBar) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterIv.setVisibility(View.INVISIBLE);
            }
        }, 100);
    }

    @Override
    public void onStopTrackingTouch(VideoCoverSeekBar seekBar) {
        ImageLoadUtil.simpleLoad(SelectVideoCoverActivity.this, Uri.fromFile(new File(mVideoPath)), mCenterIv, mCurrentTime * 1000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterIv.setVisibility(View.VISIBLE);
            }
        }, 100);
    }


    protected void requestPermision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasRStoragePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasWStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasRStoragePermission != PackageManager.PERMISSION_GRANTED || hasWStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
            onInitData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onInitData();
    }
}