package com.txznet.adapter.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.txznet.adapter.R;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVHolder> {

    private ArrayList<RVModel> adapterDataList;

    private OnItemClickListener listener;

    public RVAdapter(ArrayList<RVModel> list) {

        adapterDataList = list;
    }

    @Override
    public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_menu, parent, false);

        RVHolder holder = new RVHolder(view);
        holder.setOnItemClickListener(listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(RVHolder holder, int position) {

        holder.bindData(adapterDataList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (adapterDataList == null) {
            adapterDataList = new ArrayList<>();
        }
        return adapterDataList.size();
    }

    public void setOnItemClickListener(OnItemClickListener l) {

        this.listener = l;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

    }
}
