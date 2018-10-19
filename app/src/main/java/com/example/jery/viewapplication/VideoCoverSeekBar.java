package com.example.jery.viewapplication;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class VideoCoverSeekBar extends RelativeLayout {
    private int mCartCount = 7;
    private int mTotal;
    private float mPerWidth;
    private int mLeft;
    private float mProgress;

    public VideoCoverSeekBar(Context context) {
        super(context);
        init(context);
    }

    public VideoCoverSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoCoverSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.seekbar_cover, this, true);
    }

    public void setCartCount(int mCartCount) {
        this.mCartCount = mCartCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width_size = MeasureSpec.getSize(widthMeasureSpec);
        mPerWidth = width_size / mCartCount;
        mTotal = (int) (width_size - mPerWidth);
        View child = getChildAt(0);
        measureChild(child, MeasureSpec.makeMeasureSpec((int) mPerWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child = getChildAt(0);
        child.layout(mLeft, t, mLeft + child.getMeasuredWidth(), t + child.getMeasuredHeight());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStartTrackingTouch(VideoCoverSeekBar.this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getX() >= mPerWidth / 2 && event.getX() <= mTotal + mPerWidth / 2) {
                    mProgress = (event.getX() - mPerWidth / 2) / mTotal;
                    seek(mProgress);
                } else if (event.getX() > mTotal) {
                    mProgress = 1.0f;
                    seek(mProgress);
                } else if (event.getX() < mPerWidth / 2) {
                    mProgress = 0f;
                    seek(mProgress);
                }
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onProgressChanged(VideoCoverSeekBar.this, mProgress);
                }
            case MotionEvent.ACTION_UP:
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(VideoCoverSeekBar.this);
                }
                break;
        }
        return true;

    }

    public void seek(float progress) {
        mLeft = (int) (progress * mTotal);
        requestLayout();
    }

    public float getProgress() {
        return 1.0f * mLeft / mTotal;
    }

    public int dip2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mOnSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = mOnSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener {


        void onProgressChanged(VideoCoverSeekBar seekBar, float progress);

        void onStartTrackingTouch(VideoCoverSeekBar seekBar);

        void onStopTrackingTouch(VideoCoverSeekBar seekBar);
    }
}