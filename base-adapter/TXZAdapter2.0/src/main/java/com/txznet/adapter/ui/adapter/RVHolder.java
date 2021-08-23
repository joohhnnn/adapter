package com.txznet.adapter.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.txznet.adapter.R;

public class RVHolder extends RecyclerView.ViewHolder {

    public TextView tvText;
    private int holderPosition;

    private RVAdapter.OnItemClickListener listener;

    public RVHolder(View itemView) {
        super(itemView);
        tvText = (TextView) itemView.findViewById(R.id.tvText);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(holderPosition);
            }
        });
    }


    public void bindData(RVModel model, int position) {
        tvText.setText(model.getName());
        this.holderPosition = position;
    }

    public void setOnItemClickListener(RVAdapter.OnItemClickListener l) {

        this.listener = l;
    }

}
