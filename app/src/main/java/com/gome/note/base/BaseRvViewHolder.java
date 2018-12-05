package com.gome.note.base;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class BaseRvViewHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> mViews;
    private final Context mContext;

    private View mConvertView;

    public BaseRvViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        mViews = new SparseArray<>();
        mConvertView = view;
    }

    protected <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (null == view) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }

        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public TextView setTvText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);

        return view;
    }


    public TextView setTvTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);

        return view;
    }

    public ImageView setImageResource(int viewId, int imageResId) {
        ImageView view = getView(viewId);
        view.setBackgroundResource(imageResId);

        return view;
    }

    public View setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);

        return view;
    }

    public View setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);

        return view;
    }

    public BaseRvViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);

        return this;
    }

}
