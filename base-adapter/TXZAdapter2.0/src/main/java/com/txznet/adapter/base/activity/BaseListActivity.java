package com.txznet.adapter.base.activity;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.txznet.adapter.ui.adapter.RVAdapter.OnItemClickListener;

import com.txznet.adapter.R;
import com.txznet.adapter.ui.adapter.RVAdapter;
import com.txznet.adapter.ui.adapter.RVModel;

import java.util.ArrayList;

public abstract class BaseListActivity extends BaseActivity {

    private BaseListActivity mAct;

    public RecyclerView mRvContainer;
    private Toolbar mToolBar;

    private ArrayList<RVModel> mDataList;
    private ArrayList<String> mTitleList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        initView();
        initData();
    }

    public void initView() {
        //region # init tool bar
        mToolBar = findViewById(R.id.tbTitle);
        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setProgress(1.0f);
        mToolBar.setNavigationIcon(drawerArrowDrawable);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolBar.setTitleTextColor(Color.WHITE);
        addTitle(getToolBarTitle());

        //endregion
        mRvContainer = findViewById(R.id.rvContainer);
    }

    public void initData() {
        mAct = this;
        mDataList = new ArrayList<>();

        String[] menuArray = getMenuArray();
        if (menuArray == null) {
            return;
        }

        //region # split出activity/fragment和名字
        for (String menu : menuArray) {
            String[] split = menu.split(";");
            if (split.length != 3) {
                continue;
            }
            RVModel model = new RVModel();
            model.setType(split[0]);
            model.setClassPath(split[1]);
            model.setName(split[2]);
            mDataList.add(model);

        }

        //endregion


        RVAdapter adapter = new RVAdapter(mDataList);
        mRvContainer.setAdapter(adapter);
        mRvContainer.setLayoutManager(new LinearLayoutManager(this));
        mRvContainer.setHasFixedSize(true);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mDataList != null && position < mDataList.size()) {

                    RVModel model = mDataList.get(position);
                    String classPath = model.getClassPath();
                    try {
                        Class changeClass = Class.forName(classPath);
                        if ("activity".equals(model.getType())) {
                            startActivity(new Intent(mAct, changeClass));
                        } else if ("fragment".equals(model.getType())) {
                            addTitle(model.getName());
                            getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, (Fragment) changeClass.newInstance()).addToBackStack(null).commit();
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        backTitle();
        super.onBackPressed();
    }

    public void addTitle(String title) {

        mTitleList.add(title);
        setTitle(title);
    }

    private void backTitle() {
        if (mTitleList.size() > 1) {
            mTitleList.remove(mTitleList.size() - 1);
            setTitle(mTitleList.get(mTitleList.size() - 1));
        }
    }

    private void setTitle(String title) {
        mToolBar.setTitle(title);
    }

    public abstract String[] getMenuArray();

    public abstract String getToolBarTitle();


}
