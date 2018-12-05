package com.gome.note.ui.create.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gome.note.R;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.BackgroundItemInfo;
import com.gome.note.ui.label.adapter.LabelManageAdapter;
import com.gome.note.utils.DpUtils;

import java.util.ArrayList;

/**
 * ProjectName:Note_V1.0
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/6/16
 * DESCRIBE:
 */

public class BackgroundItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<BackgroundItemInfo> mBackgroundItemInfos;
    public ItemOnClickListener mItemOnLongClickListener = null;


    public BackgroundItemsAdapter(Context context, ArrayList<BackgroundItemInfo> backgroundItemInfos, ItemOnClickListener itemOnLongClickListener) {
        mContext = context;
        mBackgroundItemInfos = backgroundItemInfos;
        mItemOnLongClickListener = itemOnLongClickListener;
    }


    public void setData(ArrayList<BackgroundItemInfo> backgroundItemInfos) {
        mBackgroundItemInfos = backgroundItemInfos;
    }

    enum ITEM_TYPE {
        ITEM_BLANK
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE.ITEM_BLANK.ordinal()) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout
                    .background_item, parent, false);
            final ViewHolder viewHolderBlank = new ViewHolder(view);
            return viewHolderBlank;
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {

        return ITEM_TYPE.ITEM_BLANK.ordinal();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (null != mBackgroundItemInfos) {
            int resid = mBackgroundItemInfos.get(position).getCheckListResId();
            //String name = mBackgroundItemInfos.get(position).getName();

            ((ViewHolder) holder).ivBackgroundImage.setImageResource(resid);
            int nameTextResId = NoteConfig.SKIN_BG_TEXT[position];
            ((ViewHolder) holder).tvBackgroundName.setText(nameTextResId);
            ((ViewHolder) holder).mLlBackgroundView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mItemOnLongClickListener) {
                        mItemOnLongClickListener.bgItemOnClick(v, (int) v.getTag());
                    }
                }
            });

            // bind item postion
            ((ViewHolder) holder).mLlBackgroundView.setTag(position);

            if (position == mBackgroundItemInfos.size() - 1) {
                RecyclerView.LayoutParams layoutParams =
                        (RecyclerView.LayoutParams) ((ViewHolder) holder).mLlBackgroundItem.getLayoutParams();
                layoutParams.setMarginEnd(DpUtils.dp2Px(mContext, mContext.getResources().getInteger(R.integer.bg_item_15dp)));
                layoutParams.setMarginStart(DpUtils.dp2Px(mContext, mContext.getResources().getInteger(R.integer.bg_item_15dp)));
            } else {
                RecyclerView.LayoutParams layoutParams =
                        (RecyclerView.LayoutParams) ((ViewHolder) holder).mLlBackgroundItem.getLayoutParams();
                layoutParams.setMarginEnd(DpUtils.dp2Px(mContext, mContext.getResources().getInteger(R.integer.bg_item_0dp)));
                layoutParams.setMarginStart(DpUtils.dp2Px(mContext, mContext.getResources().getInteger(R.integer.bg_item_15dp)));
            }
        }
    }


    @Override
    public int getItemCount() {
        return null == mBackgroundItemInfos ? 0 : mBackgroundItemInfos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView ivBackgroundImage;
        private TextView tvBackgroundName;
        private LinearLayout mLlBackgroundView;
        private LinearLayout mLlBackgroundItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBackgroundImage = (ImageView) itemView.findViewById(R.id.iv_background_image);
            tvBackgroundName = (TextView) itemView.findViewById(R.id.tv_background_name);
            mLlBackgroundView = (LinearLayout) itemView.findViewById(R.id.ll_background_view);
            mLlBackgroundItem = (LinearLayout) itemView.findViewById(R.id.ll_background_item);

        }
    }


    public interface ItemOnClickListener {


        void bgItemOnClick(View view, int postion);

    }

    public ItemOnClickListener getItemOnLongClickListener() {
        return mItemOnLongClickListener;
    }

    public void setItemOnLongClickListener(ItemOnClickListener itemOnLongClickListener) {
        this.mItemOnLongClickListener = itemOnLongClickListener;
    }

}
