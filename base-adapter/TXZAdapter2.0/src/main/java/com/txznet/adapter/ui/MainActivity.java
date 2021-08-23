package com.txznet.adapter.ui;


import com.txznet.adapter.base.activity.BaseMenuActivity;

public class MainActivity extends BaseMenuActivity {

    @Override
    protected int getThemeActivityId() {
//        return MENU_ACTIVITY_THEME.DRAWER;
        return MENU_ACTIVITY_THEME.BOTTOM_NAV;
//        return MENU_ACTIVITY_THEME.LIST;
    }


}
