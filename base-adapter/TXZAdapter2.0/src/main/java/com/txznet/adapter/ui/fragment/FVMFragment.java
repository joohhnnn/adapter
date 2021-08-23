package com.txznet.adapter.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.txznet.adapter.R;
import com.txznet.adapter.base.fragment.BaseFragment;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.base.util.SPUtil;
import com.txznet.adapter.module.FVMModule;

import java.io.File;

/**
 * 云知声模块fragment
 */
public class FVMFragment extends BaseFragment implements View.OnClickListener {

    private String TAG = FVMFragment.class.getSimpleName();

    public EditText mEtI2c;
    private EditText mEtReset;
    //    private EditText mEtWidth;
//    private EditText mEtAngle;
    private TextView mTvSfs;
    private TextView mTvBin;
    private TextView mTvMsg;
    private TextView mTvVersion;
    private Spinner mSpI2sMode;
    private LinearLayout mLlInit;
    private LinearLayout mLlSet;
    private LinearLayout mLlRoot;
    private ImageButton mIbBackInit;

    private int mBrowserClickId;
    private int mCount = 0;//记秒数

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fvm, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initInitData();
        initSpinner();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (R.id.btnInit == id) {
            initModule();

        }
//        else if (R.id.btnAngleWidth == id) {
//            setAngleWidth();
//        }
        else if (R.id.btnUpdate == id) {
            updateModule();
        } else if (R.id.ibBackInit == id) {
            backInitAnim();
        }
    }

    //region # 初始化界面 数据
    private void initView(View view) {
        mEtI2c = view.findViewById(R.id.etI2cAddr);
//        mEtAngle = view.findViewById(R.id.etAngle);
//        mEtWidth = view.findViewById(R.id.etWidth);
        mTvVersion = view.findViewById(R.id.tvVersion);
        mTvMsg = view.findViewById(R.id.mTvMsg);
        mEtReset = view.findViewById(R.id.etResetAddr);
        mSpI2sMode = view.findViewById(R.id.spI2sMode);
        mLlInit = view.findViewById(R.id.llInit);
        mLlSet = view.findViewById(R.id.llSet);
        mLlRoot = view.findViewById(R.id.llRoot);
        mIbBackInit = view.findViewById(R.id.ibBackInit);
        mTvBin = view.findViewById(R.id.tvBin);
        mTvSfs = view.findViewById(R.id.tvSfs);

        mIbBackInit.setOnClickListener(this);
        mTvBin.setOnClickListener(browseClick);
        mTvSfs.setOnClickListener(browseClick);
        view.findViewById(R.id.btnInit).setOnClickListener(this);
//        view.findViewById(R.id.btnAngleWidth).setOnClickListener(this);
        view.findViewById(R.id.btnUpdate).setOnClickListener(this);
        mLlSet.setVisibility(View.GONE);
    }

    private void initSpinner() {
        final ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.plantes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpI2sMode.setAdapter(adapter);
        mSpI2sMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mode = (String) adapter.getItem(position);
                LogUtil.d(TAG, "select mode::" + mode);
                String currentMode = "";

                if ("正常工作模式_ZVS2".equals(mode)) {
                    currentMode = FVMModule.MODE_NORMAL;
                } else if ("输出MIC信号_ZMP6".equals(mode)) {
                    currentMode = FVMModule.MODE_MIC;
                } else if ("输出参考信号_ZSH2".equals(mode)) {
                    currentMode = FVMModule.MODE_AEC;
                }
                try {
                    boolean ret = FVMModule.getInstance().setFVMMode(currentMode);
                    LogUtil.d(TAG, "setMode::" + currentMode);
                    if (ret) {
                        toast("模式设置成功!!!");
                    } else {
                        toast("模式设置失败!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initInitData() {

        String i2cPath = SPUtil.getString(getContext(), SPUtil.KEY_I2C_PATH, "");
        String resetPath = SPUtil.getString(getContext(), SPUtil.KEY_RESET_PATH, "");
        if (TextUtils.isEmpty(i2cPath)) {
            i2cPath = "/dev/i2c-";
        }
        if (TextUtils.isEmpty(resetPath)) {
            resetPath = "/sys/devices/platform/unisound/uni_rst";
        }
        mEtI2c.setText(i2cPath);
        mEtReset.setText(resetPath);
    }
    //endregion

    //region # 模块相关方法
    //初始化模块
    private void initModule() {

        //region # 校验i2c 和 reset节点
        String i2cStr = mEtI2c.getText().toString();
        String i2cInfo = checkFile777(i2cStr);
        if (!TextUtils.isEmpty(i2cInfo)) {
            toast(i2cInfo);
            return;
        }
        String strReset = mEtReset.getText().toString();
        String resetInfo = checkFile777(strReset);
        if (!TextUtils.isEmpty(resetInfo)) {
            toast(resetInfo);
            return;
        }
        //endregion

        try {
            boolean init = FVMModule.getInstance().init(i2cStr, strReset);
            if (init) {
                toast("初始化成功!!");

                SPUtil.putString(getContext(), SPUtil.KEY_I2C_PATH, i2cStr);
                SPUtil.putString(getContext(), SPUtil.KEY_RESET_PATH, strReset);

                initDoneAnim();
            } else {
                toast("初始化失败!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast(e.getMessage());
        }
    }

    /**
     * 模块初始化完成 布局动画
     * root Layouts上滑  将init layout滑出屏幕
     * init layout 逐渐消失
     * set layout 逐渐显示
     * <p>
     * 动画已去掉
     */
    private void initDoneAnim() {

        //region # del --
//        ObjectAnimator rootAnim = ObjectAnimator.ofFloat(mLlRoot, "translationY", 0, -mLlInit.getHeight());
//
//        ObjectAnimator initAnim = ObjectAnimator.ofFloat(mLlInit, "alpha", 1f, 0f);
//
//        mLlSet.setVisibility(View.VISIBLE);
//        ObjectAnimator setAnim = ObjectAnimator.ofFloat(mLlSet, "alpha", 0f, 1f);
//
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(rootAnim).with(initAnim).with(setAnim);
//        animSet.setDuration(1000);
//        animSet.start();
//        animSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                //显示版本号 显示按钮
//                String info = "FVM version : "+queryFVMVersion()+"\r\n";
//                info += "i2c path : "+mEtI2c.getText().toString()+"\r\n";
//                info += "reset path : "+mEtReset.getText().toString();
//                mTvVersion.setText(info);
//
//                mIbBackInit.setVisibility(View.VISIBLE);
//            }
//        });
        //endregion

        mLlInit.setVisibility(View.GONE);
        mLlSet.setVisibility(View.VISIBLE);
        mIbBackInit.setVisibility(View.VISIBLE);

        setVersionInfo();

    }

    /**
     * 重新初始化参数 布局动画
     * root Layouts下滑  将init layout滑入屏幕
     * init layout 逐渐显示
     * set layout 逐渐消失
     * <p>
     * 已去掉
     */
    private void backInitAnim() {

        mLlInit.setVisibility(View.VISIBLE);
        mLlSet.setVisibility(View.GONE);
        mIbBackInit.setVisibility(View.INVISIBLE);
        mTvVersion.setText("");

        //region # del --
//        ObjectAnimator rootAnim = ObjectAnimator.ofFloat(mLlRoot, "translationY",  -mLlInit.getHeight(),0);
//
//        ObjectAnimator initAnim = ObjectAnimator.ofFloat(mLlInit, "alpha", 0f, 1f);
//
//        ObjectAnimator setAnim = ObjectAnimator.ofFloat(mLlSet, "alpha", 1f, 0f);
//
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(rootAnim).with(initAnim).with(setAnim);
//        animSet.setDuration(1000);
//        animSet.start();
//
//        animSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                mLlSet.setVisibility(View.GONE);
//                mIbBackInit.setVisibility(View.INVISIBLE);
//                mTvVersion.setText("");
//            }
//        });
        //endregion

    }

    //查询版本
    private String queryFVMVersion() {
        String version = "";
        try {
            version = FVMModule.getInstance().getFVMVersion();
            if (TextUtils.isEmpty(version)) {
                version = "query version error!!";
            }
        } catch (Exception e) {
            toast(e.getMessage());
            version = "query version error!!";
        }
        return version;
    }

    //设置角度
    private void setAngleWidth() {
        int angle = 0;
        int width = 0;
        try {
//            angle = Integer.parseInt(mEtAngle.getText().toString());
//            width = Integer.parseInt(mEtWidth.getText().toString());
        } catch (Exception e) {
            toast("angle or width::" + e.getMessage());
            return;
        }
        try {
            boolean result = FVMModule.getInstance().setDirectAngleAndWidth(angle, width);
            if (result) {
                toast("角度设置成功!!");
            } else {
                toast("角度设置失败!!");
            }
        } catch (Exception e) {
            toast(e.getMessage());
        }
    }

    //更新固件
    private void updateModule() {
        String sfsPath = mTvSfs.getText().toString();
        LogUtil.d(TAG, "sfs::" + sfsPath);
        if (TextUtils.isEmpty(sfsPath) || !sfsPath.endsWith(".sfs")) {
            toast("sfs固件未选择或格式不正确!!!");
            return;
        }
        String binPath = mTvBin.getText().toString();
        LogUtil.d(TAG, "bin::" + binPath);
        if (TextUtils.isEmpty(binPath) || !binPath.endsWith(".bin")) {
            toast("bin固件未选择或格式不正确!!!");
            return;
        }
        if (mCount > 0) {
            toast("正在下载固件请稍后重试!!!");
            return;
        }
        mCount++;
        mHandler.sendEmptyMessageDelayed(0, 1000);
        FVMModule.getInstance().updateFVM(sfsPath, binPath, new FVMModule.FVMUpdateListener() {
            @Override
            public void onSuccess() {

                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onFailed() {

                mHandler.sendEmptyMessage(2);
            }
        });

    }

    //endregion

    //region # 文件浏览相关方法
    private View.OnClickListener browseClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mBrowserClickId = v.getId();
            Log.d(TAG, "onClick: currentClickId = " + mBrowserClickId);

            showDialog();

        }
    };

    private ESFileBrowser.OnClickCallback browseClickCallback = new ESFileBrowser.OnClickCallback() {

        @Override
        public void callback(File file) {
            // TODO Auto-generated method stub
            switch (mBrowserClickId) {
                case R.id.tvSfs:
                    if (mTvSfs != null) {
                        mTvSfs.setText(file.getAbsolutePath());
                    }
                    break;
                case R.id.tvBin:
                    if (mTvBin != null) {
                        mTvBin.setText(file.getAbsolutePath());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog() {
        FileBrowserDialog dialog = new FileBrowserDialog(getContext(), "/sdcard/");
        dialog.setOnFileClickListener(new FileBrowserDialog.OnFileClickCallback() {
            @Override
            public void onFileClick(String chooseFilePath) {
                switch (mBrowserClickId) {
                    case R.id.tvSfs:
                        if (mTvSfs != null) {
                            mTvSfs.setText(chooseFilePath);
                        }
                        break;
                    case R.id.tvBin:
                        if (mTvBin != null) {
                            mTvBin.setText(chooseFilePath);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
//        new ESFileBrowser(getActivity()).setOnClickCallback(browseClickCallback).show();
    }
    //endregion

    //region # 工具方法
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //更新完成
                mTvMsg.setText("更新成功！");
                mCount = 0;
                setVersionInfo();
            } else if (msg.what == 2) {
                mTvMsg.setText("更新失败，请查看错误信息！");
                mCount = 0;
            } else if (msg.what == 0) {
                if (mCount == 0) {
                    return;
                }
                mTvMsg.setText("已下载" + mCount + "秒");
                mCount++;
                sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    private void toast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        if (mTvMsg != null) {
            mTvMsg.setText(text);
        }
    }

    private String checkFile777(String path) {

        if (TextUtils.isEmpty(path)) {
            return "i2c或复位节点地址为空，请设置";
        }

        File file = new File(path);
        if (!file.exists()) {
            return file.getName() + "不存在，请重新设置";
        }
        if (!file.canRead()) {
            return file.getName() + "不可读，请设置777权限";
        }
        if (!file.canWrite()) {
            return file.getName() + "不可写，请设置777权限";
        }
        if (!file.canExecute()) {
            return file.getName() + "不可执行，请设置777权限";
        }
        return "";
    }

    private void setVersionInfo() {
        String info = "FVM version : " + queryFVMVersion() + "\r\n";
        info += "i2c path : " + mEtI2c.getText().toString() + "\r\n";
        info += "reset path : " + mEtReset.getText().toString();
        mTvVersion.setText(info);
    }
    //endregion
}
