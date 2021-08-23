package com.txznet.adapter.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.txznet.adapter.R;

/**
 * Author: link
 * Create: 2020-2020/1/13 0013-14:50
 * Changes (from 2020/1/13 0013)
 * 2020/1/13 0013 : Create FloatView.java (link);
 **/
public class FloatView {
    private static Context mContext;
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams wmParams;
    private static View mView;
    private static boolean isShow = false;//悬浮框是否已经显示

    private static FloatView.OnClickListener mListener;//view的点击回调listener
    private static String TAG = "FloatView";

    public static void setOnClickListener(FloatView.OnClickListener listener){
        mListener = listener;
    }

    public static Handler handler;

    public interface OnClickListener{
        void onClick(View view);
    }



    /**
     * 显示悬浮框
     */
    public static synchronized void showFloatView(){
        if (handler == null){
            handler = new Handler();
        }
        Log.d(TAG, "showFloatView: isShow = "+isShow);
        if(isShow){
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                mView.setVisibility(View.VISIBLE);
            }
        });

//        ImageView imageView = (ImageView) mView.findViewById(R.id.layout_float_window_img);
//        imageView.setImageResource(R.drawable.animatio_robot);
//        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
//        animationDrawable.start();
//        mWindowManager.addView(mView, wmParams);
        isShow = true;

    }

    public static void init(Context context){
        mContext = context;

        if(isShow){
            return;
        }

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        //需要调整层级
//        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.x = -context.getResources().getDisplayMetrics().widthPixels;
        wmParams.y = 0;

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mView = LayoutInflater.from(context).inflate(R.layout.layout_float_window, null);
        ImageView imageView = (ImageView) mView.findViewById(R.id.layout_float_window_img);
        imageView.setImageResource(R.mipmap.no_access);

//        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
//        animationDrawable.start();
        mView.setVisibility(View.GONE);
        mWindowManager.addView(mView, wmParams);


        mView.setOnTouchListener(new View.OnTouchListener() {
            float downX = 0;
            float downY = 0;
            int oddOffsetX = 0;
            int oddOffsetY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downX =  event.getX();
                        downY =  event.getY();
                        oddOffsetX = wmParams.x;
                        oddOffsetY = wmParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();
                        float moveY =  event.getY();
                        //不除以3，拖动的view抖动的有点厉害
                        wmParams.x += (moveX - downX)/3;
                        wmParams.y += (moveY - downY)/3;
                        if(mView != null){
                            mWindowManager.updateViewLayout(mView,wmParams);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        int newOffsetX = wmParams.x;
                        int newOffsetY = wmParams.y;
                        if(Math.abs(newOffsetX - oddOffsetX) <=20 && Math.abs(newOffsetY - oddOffsetY) <=20){
                            if(mListener != null){
                                mListener.onClick(mView);
                            }
                        }
                        break;
                }
                return true;
            }

        });
    }


    /**
     * 隐藏悬浮窗
     */
    public static synchronized void hideFloatView(){
        if(mWindowManager != null && isShow){
//            mWindowManager.removeView(mView);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mView.setVisibility(View.GONE);
                }
            });
            isShow = false;
        }
    }
}
