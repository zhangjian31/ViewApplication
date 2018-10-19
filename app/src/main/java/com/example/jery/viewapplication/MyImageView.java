package com.example.jery.viewapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;

public class MyImageView extends AppCompatImageView {
    private static final String TAG = MyImageView.class.getSimpleName();
    private BitmapShader bitmapShader;
    private Bitmap bitmap;
    private Paint mBitmapPaint = new Paint();

    public MyImageView(Context context) {
        this(context, null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                mBitmapPaint.setAntiAlias(true);
                mBitmapPaint.setShader(bitmapShader);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.d(TAG, "title=" + result);
        setMeasuredDimension(114, 114 );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onDraw(Canvas canvas) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getDisplay().getMetrics(displayMetrics);

        Log.d(TAG, "density=" + displayMetrics.density + " " + displayMetrics.densityDpi + " " + displayMetrics.widthPixels + " " + displayMetrics.heightPixels);
        Log.d(TAG, "size1=" + canvas.getWidth() + "  " + canvas.getHeight());
        Log.d(TAG, "size2=" + getWidth() + "  " + getHeight());
//        Resources res = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.mp3);
        Log.d(TAG, "size3=" + bitmap.getWidth() + "  " + bitmap.getHeight());
        Log.d(TAG, "size4=" + getSuggestedMinimumWidth() + "  " + getSuggestedMinimumHeight());
        Log.d(TAG, "size4=" + bitmap.getRowBytes() * bitmap.getHeight());


        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), mBitmapPaint);
    }
}
