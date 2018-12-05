package com.gome.note.base;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public abstract class BaseRcvAdapter<T> extends RecyclerView.Adapter<BaseRvViewHolder> implements
        View.OnClickListener {

    protected List<T> mData;
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    protected OnItemClickListener mListener;
    protected int mType;

    public interface OnItemClickListener {

        void onClick(int position, View view);
    }

    public BaseRcvAdapter(Context context, List<T> data, int type) {
        mData = data;
        mContext = context;
        mType = type;
    }

    protected abstract int getLayoutId(int viewType);

    protected abstract void onBindData(BaseRvViewHolder holder, int position);


    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public BaseRvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, getLayoutId(viewType), null);
        view.setOnClickListener(this);
        return new BaseRvViewHolder(mContext, view);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public void onBindViewHolder(BaseRvViewHolder holder, int position) {
        onBindData(holder, position);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick(mRecyclerView.getChildAdapterPosition(v), v);
        }
    }

    public T getItemData(int position) {
        return mData.get(position);
    }

    public void setData(List<T> data) {
        this.mData = data;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void add(T data, int position) {
        mData.add(data);
        notifyItemInserted(position);
    }

    public void addAll(List<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(T data, int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void clean() {
        mData.clear();
        notifyDataSetChanged();
    }

    protected TextView setTvText(BaseRvViewHolder holder, int resId, String text) {
        TextView textView = holder.getView(resId);
        textView.setText(text);

        return textView;
    }

    protected Button setBtnText(BaseRvViewHolder holder, int resId, String text) {
        Button button = holder.getView(resId);
        button.setText(text);

        return button;
    }

    protected CheckBox setCbText(BaseRvViewHolder holder, int resId, String text) {
        CheckBox checkBox = holder.getView(resId);
        checkBox.setText(text);

        return checkBox;
    }

}
