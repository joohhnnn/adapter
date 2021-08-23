package com.txznet.adapter.ui;

import android.graphics.Color;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;

import com.txznet.adapter.R;
import com.txznet.adapter.base.activity.BaseBottomNavActivity;
import com.txznet.adapter.ui.fragment.FMFragment;
import com.txznet.adapter.ui.fragment.FVMFragment;
import com.txznet.adapter.ui.fragment.VoiceFragment;

public class BottomNavActivity extends BaseBottomNavActivity {
//    @Override
//    protected void initSelfData() {
//
//    }


    @Override
    protected void initSelfData() {
//        //region # init tool bar
        Toolbar toolBar = findViewById(R.id.tbTitle);
        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setProgress(1.0f);
        toolBar.setNavigationIcon(drawerArrowDrawable);
        toolBar.setNavigationOnClickListener(v -> finish());
        toolBar.setTitle("语音");
        toolBar.setTitleTextColor(Color.WHITE);
//        //endregion
//
//        ///region # init bottom Nav
        getBottomHelper().addBottomBean(R.drawable.ic_menu_rotate, "语音", new VoiceFragment());
        getBottomHelper().addBottomBean(R.drawable.ic_menu_goto, "FVM", new FVMFragment());
        getBottomHelper().addBottomBean(R.drawable.ic_menu_moreoverflow, "1388", new FMFragment());
        getBottomHelper().setOnBottomCheckListener((index, bottomBean) -> toolBar.setTitle(bottomBean.title));
//        //endregion
    }

    //
    @Override
    protected int getBottomViewId() {
        return R.id.bnv;
    }

    //
    @Override
    protected int getFragmentContainer() {
        return R.id.flContainer;
    }

    //
    @Override
    protected int getLayoutId() {
        return R.layout.activity_bottom_nav;
    }

    //endregion
}
