package com.xwtec.yycoin.info;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ZoomControls;

/**
 * @author ccg
 * @datetime 2010-5-4
 * @see 大头像裁剪
 * 
 * 
 * 
 * */
public class ImageCut extends Activity
{

    // 图片流
    private Bitmap bmp;

    public static Map<String, String> map;

    // 图片流 图片原始大小不变
    private Bitmap staticBmp;

    // 图片dispatchDraw画笔
    private Paint paint;

    /* 声明存储屏幕的分辨率变量 */
    private int intScreenX, intScreenY;

    /* 图片的大小 */
    private int imageWidth, ImageHight;

    /* 一次性加载 图片原始大小不变 */
    private int imageStaticWidth, imageStaticHight;

    /* 中间框和坐标大小 */
    private int borderWidth = 180, borderHight = 180, left, top;

    // 移动位置
    private float mX = 0, mY = 0;

    // 上一次位置
    private float downX = 0, downY = 0;

    // 相对上一次移动位置
    private float moveX = 0, moveY = 0;

    // 中心点位置
    private float centerX = 0, centerY = 0;

    // 画图页面
    private MyView myView;

    // 截图显示
    // ImageView imageView;

    /* 设置图片放大 缩小的比例 */
    private double minScale = 0.9;

    private double maxScale = 1.1;

    private float scaleWidth = 1;

    private float scaleHeight = 1;

    // 保存 退出按钮 预览
    protected final static int MENU_SAVE = Menu.FIRST;

    protected final static int MENU_View = Menu.FIRST + 1;

    protected final static int MENU_EXIT = Menu.FIRST + 2;
    
    private String imageRealPath ;
    private String fileTypeName;

    private ZoomControls zoomControls;
    
    // 横屏后不刷新
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // 放大图片最大宽度1200、最小宽度20、最大高度1900、最小高度30、图片大小200k
    private static int imageMaxWidth = 320 * 3, imageMinWidth = 200, imageMaxHeight = 480 * 3, imageMinHeight = 200;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 清空流
        if (null != bmp && !bmp.isRecycled())
        {
            bmp.recycle();
        }
        if (null != staticBmp && !staticBmp.isRecycled())
        {
            staticBmp.recycle();
        }
        /* 取得屏幕对象 */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        /* 取得屏幕解析像素 */
        intScreenX = dm.widthPixels;
        intScreenY = dm.heightPixels;
        myView = new MyView(ImageCut.this);
        setContentView(myView);
        // 添加放大缩小按钮
        zoomControls = new ZoomControls(ImageCut.this);
        addContentView(zoomControls,
                        new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        // 响应单击放大按钮的事件        zoomControls.setOnZoomInClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                big();
            }
        });

        // 响应单击缩小按钮的事件
        zoomControls.setOnZoomOutClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                small();
            }
        });
    }

    // linearLayout页面
    public class MyView extends LinearLayout
    {
        // View的初始化
        public MyView(Context context)
        {
            super(context);
            // 组件是可触摸的，且能获取焦点
            setFocusable(true);
            setFocusableInTouchMode(true);
            // 加载图片Bitmap对象
            Bundle session = ImageCut.this.getIntent().getExtras();
            imageRealPath = (String) session.get("imageRealPath");
            
            imageStaticWidth = null != bmp ? bmp.getWidth() : 0;
            imageStaticHight = null != bmp ? bmp.getHeight() : 0;

            centerX = intScreenX / 2;
            centerY = intScreenY / 2;

            imageWidth = bmp.getWidth();
            ImageHight = bmp.getHeight();
            // 图片初始位置
            int left_n = intScreenX / 2 - imageWidth / 2;
            int top_n = intScreenY / 2 - ImageHight / 2;
            if (mX == 0)
                mX = left_n;
            if (mY == 0)
                mY = top_n;
        }

        // 在自定义VIEW时，必须实现此方法
        @Override
        protected void dispatchDraw(Canvas canvas)
        {

            // 重写父类的方法时 渲染所有子组件 child components
            super.dispatchDraw(canvas);
            imageWidth = bmp.getWidth();
            ImageHight = bmp.getHeight();
            paint = new Paint();
            // 利用Canvas在View上绘制一个Bitmap
            canvas.drawBitmap(bmp, mX, mY, paint);
            /* 画一个空心正方形 并设置它的样式和颜色 */
            /* 去锯齿 */
            paint.setAntiAlias(false);
            /* 设置paint的颜色 */
            paint.setColor(Color.WHITE);
            /* 设置paint的style为STROKE：空心的 */
            paint.setStyle(Paint.Style.STROKE);
            /* 设置paint的外框宽度 */
            paint.setStrokeWidth(1);
            // 框的位置
            left = intScreenX / 2 - borderWidth / 2;
            top = intScreenY / 2 - borderHight / 2;
            canvas.drawRect(left, top, left + borderWidth, top + borderHight, paint);

        }
    }

    /* 覆盖触控事件 */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        /* 取得手指触控屏幕的位置 */
        float x = event.getX();
        float y = event.getY();
        try
        {
            /* 触控事件的处理 */
            switch (event.getAction())
            {
                /* 点击屏幕 */
                case MotionEvent.ACTION_DOWN:
                    downX = x;
                    downY = y;
                    break;
                /* 移动位置 */
                case MotionEvent.ACTION_MOVE:
                    picMove(x, y);
                    myView.invalidate();
                    break;
                /* 离开屏幕 */
                case MotionEvent.ACTION_UP:
                    moveX = x - downX;
                    moveY = y - downY;
                    centerX = centerX + moveX;
                    centerY = centerY + moveY;
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }

    /* 移动图片的方法 */
    private void picMove(float x, float y)
    {
        /* 默认微调图片与指针的相对位置 */
        float dtmpx = x - downX;
        float dtmpy = y - downY;
        mX = centerX + dtmpx - (imageWidth / 2);
        mY = centerY + dtmpy - (ImageHight / 2);
        /* 通过log 来查看图片位置 */
        // Log.i("坐标(x,y)>>>", Float.toString(x) + "," + Float.toString(y));
    }

    /* 图片缩小的method */
    private void small()
    {
        /* 产生reSize后的Bitmap对象 */
        /* 计算出这次要缩小的比例 */
        float tmpscaleWidth = (float) (scaleWidth * minScale);
        float tmpscaleHeight = (float) (scaleHeight * minScale);
        if (((tmpscaleWidth * staticBmp.getWidth()) >= imageMinWidth)
                        && ((tmpscaleHeight * staticBmp.getHeight()) >= imageMinHeight))
        {
            scaleWidth = tmpscaleWidth;
            scaleHeight = tmpscaleHeight;
            zoomControls.setIsZoomOutEnabled(true);
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            try
            {
                Bitmap tmpBitmap = Bitmap.createBitmap(staticBmp, 0, 0, imageStaticWidth, imageStaticHight, matrix,
                                true);
                bmp = tmpBitmap;
                myView.invalidate();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            zoomControls.setIsZoomOutEnabled(false);
        }
        zoomControls.setIsZoomInEnabled(true);
    }

    /* 图片放大的method */
    private void big()
    {
        /* 计算这次要放大的比例 */
        float tmpscaleWidth = (float) (scaleWidth * maxScale);
        float tmpscaleHeight = (float) (scaleHeight * maxScale);
        if (((tmpscaleWidth * staticBmp.getWidth()) <= imageMaxWidth)
                        && ((tmpscaleHeight * staticBmp.getHeight()) <= imageMaxHeight))
        {
            scaleWidth = tmpscaleWidth;
            scaleHeight = tmpscaleHeight;
            zoomControls.setIsZoomInEnabled(true);
            /* 产生reSize后的Bitmap对象 */
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            try
            {
                Bitmap tmpBitmap = Bitmap.createBitmap(staticBmp, 0, 0, imageStaticWidth, imageStaticHight, matrix,
                                true);
                bmp = tmpBitmap;
                myView.invalidate();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            zoomControls.setIsZoomInEnabled(false);
        }
        zoomControls.setIsZoomOutEnabled(true);
    }

    // 重写系统返回事件
    public void onBackPressed()
    {
        ImageCut.this.setResult(RESULT_OK);
        if (null != bmp && !bmp.isRecycled())
        {
            bmp.recycle();
        }
        if (null != staticBmp && !staticBmp.isRecycled())
        {
            staticBmp.recycle();
        }
        ImageCut.this.finish();
        return;
    }
}
