package com.txznet.adapter.base.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import com.txznet.adapter.R;
import com.txznet.adapter.base.util.AppUtil;
import com.txznet.adapter.ui.fragment.TestFragment;

import java.util.ArrayList;

public abstract class BaseBottomNavActivity extends BaseActivity {

    protected BaseBottomNavActivity mAct;
    private BottomNavigationView mBaseBnv;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mAct = this;
        initView();
        initData();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mCurrentFragment == null) {
            mCurrentFragment = new TestFragment();

            if (getBottomHelper().beanList.size() > 0) {
                switchFragment(getBottomHelper().beanList.get(0).fragment);
            } else {
                getSupportFragmentManager().beginTransaction().replace(getFragmentContainer(), mCurrentFragment).commit();

            }
        }
    }

    private void initView() {
        mBaseBnv = findViewById(getBottomViewId());
        initSelfData();
    }


    protected abstract void initSelfData();

    private void initData() {
        mBaseBnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (getBottomHelper().beanList != null && item.getItemId() < getBottomHelper().beanList.size()) {
                    BottomMenuHelper.BottomBean bottomBean = getBottomHelper().beanList.get(item.getItemId());
                    if (getBottomHelper().getAutoChange()) {
                        Fragment fragment = bottomBean.fragment;
                        if (fragment != null) {
                            switchFragment(fragment);
                        }
                    }

                    if (getBottomHelper().getOnBottomCheckListener() != null) {
                        getBottomHelper().getOnBottomCheckListener().onCheck(item.getItemId(), bottomBean);
                    }

                }
                return true;
            }
        });


    }

    //region # 可以重载的方法
    //自定义的布局id
    protected int getLayoutId() {
        return R.layout.activity_base_bottom_nav;
    }

    //装fragment的容器id
    protected int getFragmentContainer() {
        return R.id.flContainer_base;
    }

    //bottomNavigationId
    protected int getBottomViewId() {

        return R.id.bnv_base;
    }
    //endregion


    //切换fragment
    private void switchFragment(Fragment fragment) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (fragment.isAdded()) {
                transaction.hide(mCurrentFragment).show(fragment).commit();
            } else {
                transaction.hide(mCurrentFragment).add(getFragmentContainer(), fragment).commit();
            }
            mCurrentFragment = fragment;
        }

        AppUtil.hideSoftKeyboard(mAct);
    }

    //region # bottomHelper 工具类
    private BottomMenuHelper mBottomHelper;

    public BottomMenuHelper getBottomHelper() {
        if (mBottomHelper == null) {
            mBottomHelper = new BottomMenuHelper(mBaseBnv);
        }
        return mBottomHelper;
    }

    public class BottomMenuHelper {

        private BottomNavigationView bnv;
        private ArrayList<BottomBean> beanList;
        private OnBottomCheckListener listener;
        public boolean autoChange = true;

        private BottomMenuHelper(BottomNavigationView bnv) {
            this.bnv = bnv;
            this.beanList = new ArrayList<>();
        }

        //添加底部bean
        public void addBottomBean(BottomBean bean) {

            if (beanList.size() == 5) {

                Toast.makeText(mAct, "底部导航最多支持5块哦~", Toast.LENGTH_SHORT).show();
                return;
            }
            bnv.getMenu().add(0, beanList.size(), beanList.size(), bean.title).setIcon(bean.iconId);
            beanList.add(bean);
        }

        //添加底部bean
        public void addBottomBean(int iconId, String title, Fragment fragment) {
            BottomBean bean = new BottomBean();
            bean.iconId = iconId;
            bean.title = title;
            bean.fragment = fragment;
            addBottomBean(bean);
        }

        //添加底部beanList
        public void addBottomBeanList(ArrayList<BottomBean> bottomBeanList) {
            for (BottomBean bean : bottomBeanList) {
                addBottomBean(bean);
            }
        }

        //设置底部选择listener
        public void setOnBottomCheckListener(OnBottomCheckListener checkListener) {
            this.listener = checkListener;
        }

        protected OnBottomCheckListener getOnBottomCheckListener() {
            return this.listener;
        }

        //设置是否自动切换
        protected void setAutoChange(boolean autoChange) {
            this.autoChange = autoChange;
        }

        protected boolean getAutoChange() {
            return this.autoChange;
        }

        public class BottomBean {
            public int iconId;
            public String title;
            public Fragment fragment;

        }

    }

    protected interface OnBottomCheckListener {

        void onCheck(int index, BottomMenuHelper.BottomBean bottomBean);
    }

    //endregion


}
