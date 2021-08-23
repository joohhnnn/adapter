package com.txznet.adapter.base.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.txznet.adapter.R;
import com.txznet.adapter.base.activity.BaseListActivity;
import com.txznet.adapter.ui.adapter.RVAdapter;
import com.txznet.adapter.ui.adapter.RVModel;

import java.util.ArrayList;

public abstract class BaseListFragment extends BaseFragment {


    private RecyclerView mRvContainer;

    private ArrayList<RVModel> mDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);
        mRvContainer = view.findViewById(R.id.rvContainer);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData(){
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
        mRvContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvContainer.setHasFixedSize(true);
        adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mDataList != null && position < mDataList.size()) {

                    RVModel model = mDataList.get(position);
                    String classPath = model.getClassPath();
                    try {
                        Class changeClass = Class.forName(classPath);
                        if ("activity".equals(model.getType())) {
                            startActivity(new Intent(getContext(), changeClass));
                        } else if ("fragment".equals(model.getType())) {

                            if (getActivity() instanceof BaseListActivity) {

                                ((BaseListActivity)getActivity()).addTitle(model.getName());
                            }

                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, (Fragment) changeClass.newInstance()).addToBackStack(null).commit();
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public abstract String[] getMenuArray();
}
