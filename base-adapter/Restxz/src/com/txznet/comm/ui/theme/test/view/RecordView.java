package com.txznet.comm.ui.theme.test.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.txznet.comm.remote.GlobalContext;
import com.txznet.comm.remote.util.LogUtil;
import com.txznet.comm.ui.recordwin.RecordWin2Manager;
import com.txznet.comm.ui.resloader.UIResLoader;
import com.txznet.comm.ui.theme.test.config.SceneInfoForward;
import com.txznet.comm.ui.theme.test.config.StyleConfig;
import com.txznet.comm.ui.theme.test.anim.AnimNoneView;
import com.txznet.comm.ui.theme.test.anim.IImageView;
import com.txznet.comm.ui.theme.test.utils.ExtViewAdapter;
import com.txznet.comm.ui.theme.test.winlayout.WinLayout;
import com.txznet.comm.ui.theme.test.winlayout.inner.WinLayoutNone2;
import com.txznet.comm.ui.viewfactory.ViewFactory.ViewAdapter;
import com.txznet.comm.ui.viewfactory.data.ViewData;
import com.txznet.comm.ui.viewfactory.view.IRecordView;
import com.txznet.loader.AppLogicBase;
import com.txznet.resholder.R;
import com.txznet.sdk.TXZRecordWinManager.RecordWin2.RecordWinController;

public class RecordView extends IRecordView {

    private static RecordView sInstance = new RecordView();

    private IImageView animationView;

    private String TAG = "RecordView";

    private RecordView() {
    }

    public static RecordView getInstance() {
        return sInstance;
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public ViewAdapter getView(ViewData data) {
        LogUtil.logd(WinLayout.logTag + "RecordView getView");
        ExtViewAdapter viewAdapter = new ExtViewAdapter();
        viewAdapter.view = animationView;
        // viewAdapter.view.setTag(viewAdapter);
        viewAdapter.object = RecordView.getInstance();
        viewAdapter.isFullContent = false;
        return viewAdapter;
    }

    Runnable normalRun = null;
    Runnable recordRun = null;
    Runnable endRun = null;

    private static int currentState = -1;
    private boolean isSpeaking = false;// ??????????????????

    @Override
    public void init() {

        LogUtil.logd(WinLayout.logTag + "RecordView init");

        GlobalContext.get().registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                currentState = -1;
            }
        }, new IntentFilter("com.txznet.txz.record.show"));

        GlobalContext.get().registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                if (animationView != null) {
                    animationView.playEndAnim(0);
                    animationView.playEndAnim(1);
                    animationView.playEndAnim(2);
                }

            }
        }, new IntentFilter("com.txznet.txz.record.dismiss"));

        normalRun = new Runnable() {
            @Override
            public void run() {
                //animationView.playEndAnim(2);
                animationView.stopAllAnim();
                animationView.playStartAnim(0);
            }
        };

        recordRun = new Runnable() {
            @Override
            public void run() {
                //animationView.playEndAnim(0);
                animationView.stopAllAnim();
                animationView.playStartAnim(1);
            }
        };

        endRun = new Runnable() {
            @Override
            public void run() {
                //animationView.playEndAnim(1);
                animationView.stopAllAnim();
                animationView.playStartAnim(2);

            }
        };
    }

    /**
     * ???????????????
     * @param state
     */
    @Override
    public void updateState(final int state) {
        LogUtil.logd(WinLayout.logTag + "updateState: " + state + "--currentState:" + currentState);
        LogUtil.logd(WinLayout.logTag + "updateState: isSpeaking:" + isSpeaking);
        // if (currentState == state)
        // 	return;
        currentState = state;
        // ??????????????????
        try {
            switch (state) {
                case STATE_NORMAL: // 0: ??????
                    if(!isSpeaking) {// ????????????????????????
                        AppLogicBase.runOnUiGround(recordRun, 0);
                    }
                    break;
                case STATE_RECORD_START: // 1: ??????
                    if(!isSpeaking) {// ????????????????????????
                        AppLogicBase.runOnUiGround(recordRun, 0);
                    }
                    break;
                case STATE_RECORD_END: // 2: ??????
                    if(!isSpeaking) {// ????????????????????????
                        AppLogicBase.runOnUiGround(endRun, 0);
                    }
                    break;
                case STATE_WIN_OPEN:    // 3: ????????????
                    WinLayout.getInstance().openWin();
                    // ??????????????????
                    SceneInfoForward.getInstance().isSpeechBroadcast = false;
                    isSpeaking = false;
                    break;
                case STATE_WIN_CLOSE:    // 4: ????????????
                    break;
                case STATE_SPEAK_START:    // 5: TTS??????
                    isSpeaking = true;
                    LogUtil.logd(WinLayout.logTag + "TTS??????");
                    if (SceneInfoForward.getInstance().isSpeechBroadcast) {
                        LogUtil.logd(WinLayout.logTag + "setSpeechBroadcast():" + true);
                        LogUtil.logd(WinLayout.logTag + "??????????????????");
                        // ??????????????????(?????????)
                        WinLayoutNone2.getInstance().setSpeechBroadcast(true);
                    } else {
//                       // ??????????????????(?????????)
//                       WinLayoutNone2.getInstance().setSpeechBroadcast(false);
                    }
                    // ????????????
                    AppLogicBase.runOnUiGround(normalRun, 0);
                    break;
                case STATE_SPEAK_END:    // 6: TTS????????????
                    isSpeaking = false;
                    LogUtil.logd(WinLayout.logTag + "TTS????????????");
                    LogUtil.logd(WinLayout.logTag + "setSpeechBroadcast():" + false);
                    // SceneInfoForward.getInstance().isSpeechBroadcast = false;

                    // ??????????????????(?????????)
                    WinLayoutNone2.getInstance().setSpeechBroadcast(false);
                    // ????????????
                    AppLogicBase.runOnUiGround(recordRun, 0);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppLogicBase.runOnUiGround(new Runnable() {
            @Override
            public void run() {
                WinLayout.getInstance().onUpdateState(state);
            }
        });

    }

    @Override
    public void updateVolume(int volume) {
    }

    //???????????????????????????
    public void onUpdateAnim() {
        LogUtil.logd(WinLayout.logTag + "onUpdateAnim: ");
        if (animationView != null) {
            animationView.destory();
            animationView = null;
        }

        Context context = UIResLoader.getInstance().getModifyContext();
        View view = LayoutInflater.from(context).inflate(R.layout.record_view, (ViewGroup)null);
        animationView = view.findViewById(R.id.anmView);

        animationView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordWin2Manager.getInstance().operateView(
                        RecordWinController.OPERATE_CLICK,
                        RecordWinController.VIEW_RECORD,
                        0, 0);
            }
        });
    }

}
