package com.txznet.adapter.base.activity;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.txznet.adapter.R;
import com.txznet.adapter.base.util.AppUtil;
import com.txznet.adapter.ui.fragment.TestFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDrawerActivity extends BaseActivity {

    protected BaseDrawerActivity mAct;
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private ListView mLvMenu;
    private ActionBarDrawerToggle mDrawerToggle;

    private ArrayAdapter mArrayAdapter;
    private DrawerHelper mDrawerHelper;
    private Fragment mCurrentFragment;


    private List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);
        mAct = this;
        initView();
        initData();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentFragment == null) {
            mCurrentFragment = new TestFragment();

            if (getDrawerHelper().beanList.size() > 0) {
                switchFragment(getDrawerHelper().beanList.get(0).fragment);
                mToolBar.setTitle(getDrawerHelper().beanList.get(0).title);
            } else {
                getSupportFragmentManager().beginTransaction().replace(getFragmentContainer(), mCurrentFragment).commit();

            }
        }
    }

    private void initView() {
        mToolBar = findViewById(R.id.tbTitle);
        mDrawerLayout = findViewById(R.id.dlLeft);
        mLvMenu = findViewById(R.id.lvMenu);

        setDrawerWidth(0.6f);
    }

    private void initData() {
        mToolBar.setTitle("Toolbar");//设置Toolbar标题
        mToolBar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.today_weather, R.string.new_movie) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);


        //设置菜单列表
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList);
        mLvMenu.setAdapter(mArrayAdapter);
        mLvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (getDrawerHelper().beanList != null && position < getDrawerHelper().beanList.size()) {
                    DrawerHelper.DrawerBean drawerBean = getDrawerHelper().beanList.get(position);
                    if (getDrawerHelper().getAutoChange()) {
                        Fragment fragment = drawerBean.fragment;
                        if (fragment != null) {
                            switchFragment(fragment);
                        }
                        mToolBar.setTitle(drawerBean.title);
                    }

                    if (getDrawerHelper().getOnDrawerCheckListener() != null) {
                        getDrawerHelper().getOnDrawerCheckListener().onCheck(position);
                    }
                }

                mDrawerLayout.closeDrawer(findViewById(R.id.llMenu));
            }
        });

        initSelfData();

    }

    protected abstract void initSelfData();

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

    protected int getFragmentContainer() {
        return R.id.flContainer_base;
    }

    protected DrawerHelper getDrawerHelper() {

        if (mDrawerHelper == null) {

            mDrawerHelper = new DrawerHelper(mArrayAdapter, mList);
        }
        return mDrawerHelper;
    }


    /**
     * 设置drawer宽度  0.3 ~1.0
     *
     * @param percentScreenWidth
     */
    public void setDrawerWidth(float percentScreenWidth) {
        if (percentScreenWidth >= 0.3 && percentScreenWidth <= 1.0) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            ViewGroup.LayoutParams layoutParams = findViewById(R.id.llMenu).getLayoutParams();
            layoutParams.width = (int) (outMetrics.widthPixels * percentScreenWidth);
        }
    }

    public class DrawerHelper {

        private BaseAdapter baseAdapter;
        private List<String> dataList;
        private ArrayList<DrawerBean> beanList;


        public DrawerHelper(BaseAdapter adapter, List<String> dataList) {
            this.baseAdapter = adapter;
            this.dataList = dataList;
            this.beanList = new ArrayList<>();
        }

        private OnDrawerCheckListener listener;
        public boolean autoChange = true;


        //添加底部bean
        protected void addDrawerBean(DrawerBean bean) {
            ArrayList<DrawerBean> beaList = new ArrayList<>();
            beaList.add(bean);
            addDrawerBeanList(beaList);
        }

        //添加底部bean
        public void addDrawerBean(String title, Fragment fragment) {
            DrawerBean bean = new DrawerBean();
            bean.title = title;
            bean.fragment = fragment;
            addDrawerBean(bean);
        }

        //添加底部beanList
        protected void addDrawerBeanList(ArrayList<DrawerBean> drawerBeanList) {

            beanList.addAll(drawerBeanList);
            for (DrawerBean bean : drawerBeanList) {
                dataList.add(bean.title);
            }
            baseAdapter.notifyDataSetChanged();

        }

        //设置底部选择listener
        protected void setOnDrawerCheckListener(OnDrawerCheckListener checkListener) {
            this.listener = checkListener;
        }

        protected OnDrawerCheckListener getOnDrawerCheckListener() {
            return this.listener;
        }

        //设置是否自动切换
        protected void setAutoChange(boolean autoChange) {
            this.autoChange = autoChange;
        }

        protected boolean getAutoChange() {
            return this.autoChange;
        }

        protected class DrawerBean {
            protected String title;
            protected Fragment fragment;

        }

    }

    protected interface OnDrawerCheckListener {

        void onCheck(int index);
    }


}
