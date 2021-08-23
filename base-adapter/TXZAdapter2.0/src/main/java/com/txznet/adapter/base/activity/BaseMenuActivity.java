package com.txznet.adapter.base.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.txznet.adapter.ui.BottomNavActivity;
import com.txznet.adapter.ui.DrawerActivity;
import com.txznet.adapter.ui.ListActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@SuppressLint("Registered")
public abstract class BaseMenuActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMenuActivity();
    }

    private void startMenuActivity() {

        Intent intent = new Intent();
        if (getThemeActivityId() == MENU_ACTIVITY_THEME.LIST) {
            intent.setClass(this, ListActivity.class);
        } else if (getThemeActivityId() == MENU_ACTIVITY_THEME.BOTTOM_NAV) {
            intent.setClass(this, BottomNavActivity.class);
        } else if (getThemeActivityId() == MENU_ACTIVITY_THEME.DRAWER) {
            intent.setClass(this, DrawerActivity.class);
        } else {
            intent.setClass(this, BottomNavActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @MENU_ACTIVITY_THEME
    protected abstract int getThemeActivityId();

    @IntDef({MENU_ACTIVITY_THEME.LIST, MENU_ACTIVITY_THEME.DRAWER, MENU_ACTIVITY_THEME.BOTTOM_NAV})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MENU_ACTIVITY_THEME {
        int LIST = 0;
        int DRAWER = 1;
        int BOTTOM_NAV = 2;
    }
}
