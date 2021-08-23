package com.txznet.adapter.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.txznet.adapter.AdpApplication;
import com.txznet.adapter.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carry on 2018/3/23.
 */

public class ESFileBrowser extends Dialog {

	private static final String TAG = ESFileBrowser.class.getSimpleName();

	private static FileTree mRootFileTree;

    private LinearLayout rootView;

    private TextView showCurrentDistery;

    private LinkedList<FileTree> historyStack ;

    private FileTree currentFile;

    private OnClickCallback mOnClickCallback;

    public ESFileBrowser(@NonNull Context context) {
        super(context);
        setTitle("请选择文件");
        initView(context);
        historyStack = new LinkedList<FileTree>();
        //初始化跟文件
        showCurrentDistery.setText("正在加载sdcard文件请稍后……");
        initFileTree();
    }

    private void initFileTree(){
    	if (mRootFileTree != null) {
    		updateFileTree(mRootFileTree);
		    updateView();
			return;
		}
    	AdpApplication.getInstance().runOnUiThread(new Runnable() {

 			@Override
 			public void run() {
 				createFileTree();
                AdpApplication.getInstance().runOnUiThread(new Runnable() {

 					@Override
 					public void run() {
 						// TODO Auto-generated method stub
 						updateFileTree(mRootFileTree);
 				        updateView();
 					}
 				}, 10);
 			}
 		}, 10);
    }

    private void initView(Context context){
    	//添加跟布局
        LinearLayout linear = new LinearLayout(context);
        linear.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams params  = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(linear,params);
        LinearLayout titleLinear = new LinearLayout(context);
        titleLinear.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linear.addView(titleLinear, linearParams);
        initTitleView(titleLinear, context);
        //添加内容布局
        ScrollView view = new ScrollView(context);
        linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        linearParams.weight = 1;
        linear.addView(view, linearParams);
        initContentView(view, context);
        //添加底部布局
        LinearLayout bottomLinear = new LinearLayout(context);
        bottomLinear.setOrientation(LinearLayout.VERTICAL);
        linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linear.addView(bottomLinear,linearParams);
        initBottomView(bottomLinear, context);
    }

    //初始化头部布局
    private void initTitleView(LinearLayout root, Context context){
        showCurrentDistery = new TextView(context);
        showCurrentDistery.setTextSize(25);
        showCurrentDistery.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(5, 5, 0, 5);
        root.addView(showCurrentDistery, linearParams);
        View line = new View(context);
        line.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        root.addView(line, linearParams);
    }
    //初始显示内容布局
    private void initContentView(ScrollView root, Context context){
    	rootView = new LinearLayout(context);
        rootView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        root.addView(rootView,linearParams);
    }
    //初始化底部布局
    private void initBottomView(LinearLayout root, Context context){
    	//添加底部布局
        Button but = new Button(context);
        but.setText("返回");
        but.setTextSize(20);
        but.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        but.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (historyStack != null && historyStack.size() > 1) {
					historyStack.removeLast();
					updateFileTree(historyStack.removeLast());
					updateView();
				}else{
				    dismiss();
                }
			}
		});
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(0, 5, 0, 5);
        root.addView(but, linearParams);
    }

    public ESFileBrowser setOnClickCallback(OnClickCallback callback){
        mOnClickCallback = callback;
        return this;
    }

    private void updateView(){
        if (currentFile != null){
            rootView.removeAllViews();
            if (currentFile.getChildrens().size() > 0){
                for (FileTree tree:
                     currentFile.getChildrens()) {
                	Log.d(TAG, "currentFile childres::" + tree.getFile().getAbsolutePath());
                    new ShowFileView(rootView,tree,false);
                }
            }
        }
    }

    private void updateFileTree(FileTree fileTree){
    	historyStack.add(fileTree);
    	currentFile = fileTree;
    	if (currentFile != null) {
			showCurrentDistery.setText(currentFile.getFile().getAbsolutePath());
		}
    }

    public static interface OnClickCallback{
        void callback(File file);
    }

    private static void createFileTree(){
        File file = new File("/sdcard/");
        mRootFileTree = new FileTree(file);
        createFileTree(file,mRootFileTree);
    }

    private static void createFileTree(File file, FileTree fileTree){
        if (file.isDirectory()){
        	File[] listFiles = file.listFiles();
        	if (listFiles == null) {
				return;
			}
            for (File f: listFiles) {
                if (f == null){
                    continue;
                }
                FileTree tree = new FileTree(f);
                fileTree.addFileTree(tree);
                if (f.isDirectory()){
                    createFileTree(f,tree);
                }
            }
        }
    }

    private class ShowFileView implements View.OnClickListener {

        private FileTree mFileTree;
        private TextView textView;

        public ShowFileView(ViewGroup viewGroup, FileTree tree, boolean isRoot){
            if (tree == null){
                throw new RuntimeException("file is null!!!");
            }
            if (viewGroup == null){
                throw new RuntimeException("viewGroup is null!!!");
            }
            mFileTree = tree;
            textView = new TextView(viewGroup.getContext());
            textView.setText((isRoot?"":"\t") + (isRoot? mFileTree.getFile().getAbsolutePath() : mFileTree.getFile().getName()));
            textView.setTextSize(20);
            textView.setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 10);
            viewGroup.addView(textView,params);
            View v = new View(getContext());
            v.setBackgroundColor(0xff000000);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            viewGroup.addView(v,params);
        }

        @Override
        public void onClick(View v) {
        	Log.d(TAG, "onClick file::" + mFileTree.getFile().getAbsolutePath());
            updateFileTree(mFileTree);
            if (mFileTree.getFile().isDirectory()){
                updateView();
            }else {
                if (mOnClickCallback != null){
                    mOnClickCallback.callback(mFileTree.getFile());
                }
                cancel();
            }
        }

    }

    private static class FileTree{

        private File mFile;

        private List<FileTree> mChildrens;

        public FileTree(File file){
            if (file == null){
                throw new RuntimeException("file is null!!!");
            }
            mFile = file;
            mChildrens = new ArrayList<FileTree>();
            //LogUtil.d(TAG, " createTree::" + file.getAbsolutePath());
        }

        public void addFileTree(FileTree file){
            if (mChildrens != null && checkFileTree(file)){
                mChildrens.add(file);
            }
        }

        public File getFile(){
            return mFile;
        }

        private boolean checkFileTree(FileTree fileTree){
            return findFileTree(fileTree) == null? true : false;
        }

        public FileTree findFileTree(FileTree fileTree){
            if (mChildrens.size() > 0 && fileTree != null){
                for (FileTree file : mChildrens){
                    if (file.equals(fileTree)){
                        return file;
                    }
                }
            }
            return null;
        }

        public List<FileTree> getChildrens(){
            return mChildrens;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof FileTree)){
                return false;
            }
            FileTree tree = (FileTree) obj;
            if (tree.getChildrens().size() == 0){
                return mFile.getAbsoluteFile().equals(tree.getFile().getAbsoluteFile());
            }else{
                if (mFile.getAbsoluteFile().equals(tree.getFile().getAbsoluteFile())){
                    for (FileTree file:
                         getChildrens()) {
                        if (tree.findFileTree(file) == null){
                            return false;
                        }
                    }
                    return true;
                }else {
                    return false;
                }
            }
        }
    }

}
