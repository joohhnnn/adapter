package com.txznet.adapter.ui;

import com.txznet.adapter.R;
import com.txznet.adapter.base.activity.BaseListActivity;

public class ListActivity extends BaseListActivity {
    @Override
    public String[] getMenuArray() {
        return getResources().getStringArray(R.array.menu_array);
    }

    @Override
    public String getToolBarTitle() {
        return getResources().getString(R.string.app_name);
    }

}
