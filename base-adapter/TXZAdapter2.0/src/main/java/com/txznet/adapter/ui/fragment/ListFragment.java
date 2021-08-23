package com.txznet.adapter.ui.fragment;

import com.txznet.adapter.R;
import com.txznet.adapter.base.fragment.BaseListFragment;

public class ListFragment extends BaseListFragment {
    @Override
    public String[] getMenuArray() {
        return getResources().getStringArray(R.array.list_array);
    }
}
