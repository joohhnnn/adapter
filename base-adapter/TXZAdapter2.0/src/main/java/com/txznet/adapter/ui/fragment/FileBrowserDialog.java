package com.txznet.adapter.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.txznet.adapter.R;


import java.io.File;

/**
 * 文件浏览器dialog
 */
public class FileBrowserDialog extends Dialog {

    private TextView mTvTitle;
    private LinearLayout mContentLayout;
    private String mInitPath;
    private String mCurrentPath;

    private OnFileClickCallback mListener;

    public FileBrowserDialog(Context context, String initPath) {
        super(context);
        mInitPath = initPath;
        initView();
        updateView(initPath);
    }

    //初始化view
    private void initView() {

        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams rootParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(rootLayout, rootParams);

        //标题
        mTvTitle = new TextView(getContext());
        mTvTitle.setTextSize(25);
        mTvTitle.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llParams.setMargins(5, 5, 0, 5);
        rootLayout.addView(mTvTitle, llParams);

        View line = new View(getContext());
        line.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        rootLayout.addView(line, llParams);

        //内容
        ScrollView scrollView = new ScrollView(getContext());
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        llParams.weight = 1;
        rootLayout.addView(scrollView, llParams);

        mContentLayout = new LinearLayout(getContext());
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scrollView.addView(mContentLayout, llParams);

        //底部返回按钮
        Button but = new Button(getContext());
        but.setText("返回");
        but.setTextSize(20);
        but.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                File initFile = new File(mInitPath);
                File currentFile = new File(mCurrentPath);
                if(!initFile.getAbsolutePath().equals(currentFile.getAbsolutePath())){
                    updateView(currentFile.getParentFile().getAbsolutePath());
                }else{
                    dismiss();
                }


            }
        });
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(0, 5, 0, 5);
        rootLayout.addView(but, linearParams);
    }

    //更新view
    private void updateView(String path) {

        mCurrentPath = path;
        //title change
        mTvTitle.setText(path);

        //content change
        mContentLayout.removeAllViews();
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return;
        }
        File[] childFiles = rootFile.listFiles();


        LinearLayout.LayoutParams params;
        if(childFiles.length == 0){//没有child时 给一个高度占位
            View topView = new View(getContext());
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10);
            mContentLayout.addView(topView, params);
        }

        for(File childFile :childFiles){
            TextView textView = new TextView(getContext());
            textView.setText(childFile.getName());
            textView.setTextSize(20);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(childFile.isDirectory()){
                        updateView(childFile.getAbsolutePath());
                    }else{
                        if(mListener!=null){
                            mListener.onFileClick(childFile.getAbsolutePath());
                        }
                        dismiss();
                    }
                }
            });
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 10, 0, 10);
            mContentLayout.addView(textView, params);

            View bottomLine = new View(getContext());
            bottomLine.setBackgroundColor(0xff000000);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            mContentLayout.addView(bottomLine, params);
        }


    }


    //回调listener
    public interface OnFileClickCallback {
        void onFileClick(String chooseFilePath);
    }

    public void setOnFileClickListener(OnFileClickCallback listener){
        this.mListener = listener;
    }

}
