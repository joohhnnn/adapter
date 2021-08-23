package com.txznet.adapter.ui;

import com.txznet.adapter.base.activity.BaseDrawerActivity;
import com.txznet.adapter.ui.fragment.FMFragment;
import com.txznet.adapter.ui.fragment.FVMFragment;
import com.txznet.adapter.ui.fragment.TestFragment;
import com.txznet.adapter.ui.fragment.VoiceFragment;

public class DrawerActivity extends BaseDrawerActivity {

    @Override
    protected void initSelfData() {
        getDrawerHelper().addDrawerBean("语音", new VoiceFragment());
        getDrawerHelper().addDrawerBean("fvm", new FVMFragment());
        getDrawerHelper().addDrawerBean("1388", new FMFragment());
//        getDrawerHelper().addDrawerBean("tst", new TestFragment());
    }

}
