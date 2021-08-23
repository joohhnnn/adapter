package com.txznet.adapter.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.txznet.adapter.R;
import com.txznet.adapter.aidl.TXZAIDLManager;
import com.txznet.adapter.base.fragment.BaseFragment;
import com.txznet.adapter.base.util.AppUtil;
import com.txznet.sdk.TXZAsrManager;

/**
 * 语音功能fragment
 */
public class VoiceFragment extends BaseFragment implements View.OnClickListener {


    private EditText mEtRecognize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_voice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEtRecognize = view.findViewById(R.id.etRecognize);
        view.findViewById(R.id.btnSendAll).setOnClickListener(this);
        view.findViewById(R.id.btnTrigger).setOnClickListener(this);
        view.findViewById(R.id.btnWeather).setOnClickListener(this);
        view.findViewById(R.id.btnMovie).setOnClickListener(this);
        view.findViewById(R.id.btnShare).setOnClickListener(this);
        view.findViewById(R.id.btnNav).setOnClickListener(this);
        view.findViewById(R.id.btnHelp).setOnClickListener(this);
        view.findViewById(R.id.btnJoke).setOnClickListener(this);
        view.findViewById(R.id.btnMakeMeat).setOnClickListener(this);
        view.findViewById(R.id.btnRecognize).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSendAll) {
            TXZAIDLManager.getInstance().sendCommand(4545, "test.mmm", "来自同行者的测试消息".getBytes(), false);
//            new FileBrowserDialog(getContext(),"/sdcard/").show();
        } else if (v.getId() == R.id.btnTrigger) {
            TXZAsrManager.getInstance().triggerRecordButton();
        } else if (v.getId() == R.id.btnWeather) {
            TXZAsrManager.getInstance().startWithRawText(getResources().getString(R.string.today_weather));
        } else if (v.getId() == R.id.btnMovie) {
            TXZAsrManager.getInstance().startWithRawText(getResources().getString(R.string.new_movie));
        } else if (v.getId() == R.id.btnShare) {
            TXZAsrManager.getInstance().startWithRawText(getResources().getString(R.string.tencent_share));
        } else if (v.getId() == R.id.btnNav) {
            TXZAsrManager.getInstance().startWithRawText(getResources().getString(R.string.go_to_world_window));
        } else if (v.getId() == R.id.btnHelp) {
            TXZAsrManager.getInstance().startWithRawText(getResources().getString(R.string.open_help));
        } else if (v.getId() == R.id.btnJoke) {
            TXZAsrManager.getInstance().startWithRawText(getResources().getString(R.string.joke));
        } else if (v.getId() == R.id.btnMakeMeat) {
            TXZAsrManager.getInstance().startWithRawText(getResources().getString(R.string.make_meat));
        } else if (v.getId() == R.id.btnRecognize) {
            String strRecognize = mEtRecognize.getText().toString();
            if (TextUtils.isEmpty(strRecognize)) {
                Toast.makeText(getContext(), "请输入文字！", Toast.LENGTH_SHORT).show();
                return;
            }
            TXZAsrManager.getInstance().startWithRawText(strRecognize);
            AppUtil.hideSoftKeyboard(getActivity());
        }
    }
}
