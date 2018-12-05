package com.gome.note.ui.label.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gome.note.R;
import com.gome.note.entity.LabelInfo;
import com.gome.note.ui.label.LabelManageUtils;

import java.util.ArrayList;

/**
 * ProjectName:Note_V1.0
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/6/16
 * DESCRIBE:
 */

public class LabelManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<LabelInfo> labelInfos = new ArrayList<>();
    private boolean isShowDelete = false;
    private boolean isShowEdit = false;
    private AdapterOnClickListener mOnClickListener = null;
    private long mID;
    private int type;
    private AdapterOnCheckListener mOnCheckListener = null;
    private boolean isCheckedStatus = false;
    public ItemOnLongClickListener mItemOnLongClickListener = null;
    public AdapterOnDeleteCheckListener mOnDeleteCheckListener = null;
    private int TYPE_ADD_LABELS = 1;

    enum ITEM_TYPE {
        ITEM_STICK, ITEM_COMMON, ITEM_BLANK
    }

    public LabelManageAdapter(Context context, ArrayList<LabelInfo> labelInfos,
                              AdapterOnClickListener onClickListener, AdapterOnCheckListener
                                      onCheckListener, AdapterOnDeleteCheckListener
                                      onDeleteCheckListener, ItemOnLongClickListener
                                      itemOnLongClickListener) {
        mContext = context;
        this.labelInfos = labelInfos;
        mOnClickListener = onClickListener;
        mOnCheckListener = onCheckListener;
        mOnDeleteCheckListener = onDeleteCheckListener;
        mItemOnLongClickListener = itemOnLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_STICK.ordinal()) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout
                    .stick_label_manage_list_adapter, parent, false);
            final StickViewHolder stickViewHolder = new StickViewHolder(view);

            return stickViewHolder;
        } else if (viewType == ITEM_TYPE.ITEM_COMMON.ordinal()) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout
                    .label_manage_list_adapter, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);


            return viewHolder;
        } else if (viewType == ITEM_TYPE.ITEM_BLANK.ordinal()) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout
                    .label_manage_list_blank_adapter, parent, false);
            final ViewHolder viewHolderBlank = new ViewHolder(view);


            return viewHolderBlank;
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.ITEM_STICK.ordinal();
        } else if ((position + 1) == labelInfos.size() + 1) {
            return ITEM_TYPE.ITEM_BLANK.ordinal();
        } else {
            return ITEM_TYPE.ITEM_COMMON.ordinal();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if ((position + 1) != labelInfos.size() + 1) {
            if (null != labelInfos && labelInfos.size() > 0) {
                if (position == 0) {
                    ((StickViewHolder) holder).mTvLabelName.setText(labelInfos.get(position).getTitle().trim());
                    ((StickViewHolder) holder).mTvCardCount.setText(labelInfos.get(position).getCount() + "");

                    boolean isCheck = labelInfos.get(position).getIsChecked();

                    if (isCheck) {
                        ((StickViewHolder) holder).mCbLabelCheck.setChecked(true);
                    } else {
                        ((StickViewHolder) holder).mCbLabelCheck.setChecked(false);
                    }


                    if (isCheckedStatus) {
                        ((StickViewHolder) holder).mCbLabelCheck.setVisibility(View.VISIBLE);
                        ((StickViewHolder) holder).mTvCardCount.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvArrowRight.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);

                    } else if (isShowEdit) {
                        ((StickViewHolder) holder).mCbLabelCheck.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mTvCardCount.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvArrowRight.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);
                        if (labelInfos.get(position).isStick()) {
                            ((StickViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                            ((StickViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        } else {
                            ((StickViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                            ((StickViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        }
                    } else if (isShowDelete) {
                        ((StickViewHolder) holder).mCbLabelCheck.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mTvCardCount.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvArrowRight.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mCbLabelsDelete.setVisibility(View.VISIBLE);
                        if (labelInfos.get(position).isStick()) {
                            ((StickViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);
                        } else {
                            ((StickViewHolder) holder).mCbLabelsDelete.setVisibility(View.VISIBLE);
                        }
                    } else {

                        ((StickViewHolder) holder).mCbLabelCheck.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mTvCardCount.setVisibility(View.VISIBLE);
                        ((StickViewHolder) holder).mIvArrowRight.setVisibility(View.VISIBLE);
                        ((StickViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((StickViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);
                    }

                    ((StickViewHolder) holder).mRlLabelItem.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            mItemOnLongClickListener.itemOnLongClick(v, (int) v.getTag());

                            return true;
                        }
                    });


                    ((StickViewHolder) holder).mRlLabelItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemOnLongClickListener.itemOnClick(v, (int) v.getTag());

                        }
                    });


                    ((StickViewHolder) holder).mCbLabelCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnCheckListener.adapterOnCheck(v, ((StickViewHolder) holder).mCbLabelCheck.isChecked(), (int) v
                                    .getTag());
                        }
                    });

                    ((StickViewHolder) holder).mRlLabelItem.setTag(position);
                    ((StickViewHolder) holder).mCbLabelCheck.setTag(position);

                } else {
                    String title = labelInfos.get(position).getTitle().trim();
                    if (TextUtils.isEmpty(title)) {
                        ((ViewHolder) holder).mTvLabelName.setText("");
                    } else {
                        if (LabelManageUtils.isVoiceMemosLable(mContext.getApplicationContext(), title)) {
                            ((ViewHolder) holder).mTvLabelName.setText(mContext.getString(R.string.lable_type_record));
                        } else {
                            ((ViewHolder) holder).mTvLabelName.setText(labelInfos.get(position).getTitle().trim());
                        }
                    }
                    //((ViewHolder) holder).mTvLabelName.setText(labelInfos.get(position).getTitle().trim());
                    ((ViewHolder) holder).mTvCardCount.setText(labelInfos.get(position).getCount() + "");

                    boolean isCheck = labelInfos.get(position).getIsChecked();

                    if (isCheck) {
                        ((ViewHolder) holder).mCbLabelCheck.setChecked(true);
                    } else {
                        ((ViewHolder) holder).mCbLabelCheck.setChecked(false);
                    }

                    if (isShowDelete) {
                        if (isCheck) {
                            ((ViewHolder) holder).mCbLabelsDelete.setChecked(true);
                        } else {
                            ((ViewHolder) holder).mCbLabelsDelete.setChecked(false);
                        }
                    }


                    if (isCheckedStatus) {

                        ((ViewHolder) holder).mCbLabelCheck.setVisibility(View.VISIBLE);
                        ((ViewHolder) holder).mTvCardCount.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvArrowRight.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((ViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);

                    } else if (isShowEdit) {
                        ((ViewHolder) holder).mCbLabelCheck.setVisibility(View.GONE);
                        ((ViewHolder) holder).mTvCardCount.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvArrowRight.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((ViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);
                        if (labelInfos.get(position).isStick()) {
                            ((ViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                            ((ViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        } else {
                            ((ViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                            ((ViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        }
                    } else if (isShowDelete) {
                        ((ViewHolder) holder).mCbLabelCheck.setVisibility(View.GONE);
                        ((ViewHolder) holder).mTvCardCount.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvArrowRight.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((ViewHolder) holder).mCbLabelsDelete.setVisibility(View.VISIBLE);
                        if (labelInfos.get(position).isStick()) {
                            ((ViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);
                        } else {
                            ((ViewHolder) holder).mCbLabelsDelete.setVisibility(View.VISIBLE);
                        }
                    } else {

                        ((ViewHolder) holder).mCbLabelCheck.setVisibility(View.GONE);

                        ((ViewHolder) holder).mTvCardCount.setVisibility(View.VISIBLE);
                        ((ViewHolder) holder).mIvArrowRight.setVisibility(View.VISIBLE);
                        ((ViewHolder) holder).mIvLabelEdit.setVisibility(View.GONE);
                        ((ViewHolder) holder).mIvLabelRemove.setVisibility(View.GONE);
                        ((ViewHolder) holder).mCbLabelsDelete.setVisibility(View.GONE);
                    }


                    ((ViewHolder) holder).mIvLabelEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // edit label
                            mOnClickListener.adapterOnClick(v, (int) v.getTag());

                        }
                    });
                    ((ViewHolder) holder).mIvLabelRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // delete label
                            mOnClickListener.adapterOnClick(v, (int) v.getTag());
                        }
                    });


                    ((ViewHolder) holder).mCbLabelCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnCheckListener.adapterOnCheck(v, ((ViewHolder) holder).mCbLabelCheck.isChecked(), (int) v
                                    .getTag());
                        }
                    });

                    ((ViewHolder) holder).mRlLabelItem.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            mItemOnLongClickListener.itemOnLongClick(v, (int) v.getTag());

                            return true;
                        }
                    });


                    ((ViewHolder) holder).mRlLabelItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemOnLongClickListener.itemOnClick(v, (int) v.getTag());

                        }
                    });

                    ((ViewHolder) holder).mCbLabelsDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnDeleteCheckListener.adapterOnDeleteCheck(v, ((ViewHolder) holder).mCbLabelsDelete
                                    .isChecked(), (int) v.getTag());
                        }
                    });


                    // bind item postion
                    ((ViewHolder) holder).itemView.setTag(position);
                    ((ViewHolder) holder).mIvLabelEdit.setTag(position);
                    ((ViewHolder) holder).mIvLabelRemove.setTag(position);
                    ((ViewHolder) holder).mCbLabelCheck.setTag(position);
                    ((ViewHolder) holder).mRlLabelItem.setTag(position);
                    ((ViewHolder) holder).mCbLabelsDelete.setTag(position);
                    ((ViewHolder) holder).LabelLayout.setPadding(0, 0, 0, 0);

                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return labelInfos.size() + 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView mTvLabelName;
        public TextView mTvCardCount;
        public ImageView mIvArrowRight;
        public ImageView mIvLabelEdit;
        public ImageView mIvLabelRemove;
        private CheckBox mCbLabelCheck;
        private RelativeLayout mRlTextviewItem;
        private CheckBox mCbLabelsDelete;
        private RelativeLayout mRlLabelItem, LabelLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mTvLabelName = (TextView) itemView.findViewById(R.id.tv_label_name);
            mTvCardCount = (TextView) itemView.findViewById(R.id.tv_card_count);
            mIvArrowRight = (ImageView) itemView.findViewById(R.id.iv_arrow_right);
            mIvLabelEdit = (ImageView) itemView.findViewById(R.id.iv_label_edit);
            mIvLabelRemove = (ImageView) itemView.findViewById(R.id.iv_label_remove);
            mCbLabelCheck = (CheckBox) itemView.findViewById(R.id.cb_label_check);

            mRlTextviewItem = (RelativeLayout) itemView.findViewById(R.id.rl_textview_item);
            mCbLabelsDelete = (CheckBox) itemView.findViewById(R.id.cb_labels_delete);

            mRlLabelItem = (RelativeLayout) itemView.findViewById(R.id.rl_label_item);
            LabelLayout = (RelativeLayout) itemView.findViewById(R.id.label_main);

        }
    }

    public class StickViewHolder extends RecyclerView.ViewHolder {


        public TextView mTvLabelName;
        public TextView mTvCardCount;
        public ImageView mIvArrowRight;
        public ImageView mIvLabelEdit;
        public ImageView mIvLabelRemove;
        private CheckBox mCbLabelCheck;
        private RelativeLayout mRlTextviewItem;
        private CheckBox mCbLabelsDelete;
        private RelativeLayout mRlLabelItem, LabelLayout;

        public StickViewHolder(View itemView) {
            super(itemView);

            mTvLabelName = (TextView) itemView.findViewById(R.id.tv_label_name);
            mTvCardCount = (TextView) itemView.findViewById(R.id.tv_card_count);
            mIvArrowRight = (ImageView) itemView.findViewById(R.id.iv_arrow_right);
            mIvLabelEdit = (ImageView) itemView.findViewById(R.id.iv_label_edit);
            mIvLabelRemove = (ImageView) itemView.findViewById(R.id.iv_label_remove);
            mCbLabelCheck = (CheckBox) itemView.findViewById(R.id.cb_label_check);

            mRlTextviewItem = (RelativeLayout) itemView.findViewById(R.id.rl_textview_item);
            mCbLabelsDelete = (CheckBox) itemView.findViewById(R.id.cb_labels_delete);

            mRlLabelItem = (RelativeLayout) itemView.findViewById(R.id.rl_label_item);
            LabelLayout = (RelativeLayout) itemView.findViewById(R.id.label_main);

        }
    }

    public interface AdapterOnClickListener {
        void adapterOnClick(View view, int postion);
    }

    public interface AdapterOnCheckListener {
        void adapterOnCheck(View view, boolean isChecked, int postion);

    }

    public interface AdapterOnDeleteCheckListener {
        void adapterOnDeleteCheck(View view, boolean isChecked, int postion);

    }

    public interface ItemOnLongClickListener {

        void itemOnLongClick(View view, int postion);

        void itemOnClick(View view, int postion);

    }

    public ItemOnLongClickListener getItemOnLongClickListener() {
        return mItemOnLongClickListener;
    }

    public void setItemOnLongClickListener(ItemOnLongClickListener itemOnLongClickListener) {
        this.mItemOnLongClickListener = itemOnLongClickListener;
    }

    public ArrayList<LabelInfo> getLabelInfos() {
        return labelInfos;
    }

    public void setLabelInfos(ArrayList<LabelInfo> labelInfos) {
        this.labelInfos = labelInfos;
    }

    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
    }

    public boolean getIsShowDelete() {
        return isShowDelete;
    }

    public boolean getShowEdit() {
        return isShowEdit;
    }

    public void setShowEdit(boolean showEdit) {
        isShowEdit = showEdit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        if (type == TYPE_ADD_LABELS) {
            isCheckedStatus = true;
        } else {
            isCheckedStatus = false;
        }
    }

    public long getID() {
        return mID;
    }

    public void setID(long mID) {
        this.mID = mID;
    }

    public boolean isCheckedStatus() {
        return isCheckedStatus;
    }

    public void setCheckedStatus(boolean checkedStatus) {
        isCheckedStatus = checkedStatus;
    }

    public AdapterOnDeleteCheckListener getOnDeleteCheckListener() {
        return mOnDeleteCheckListener;
    }

    public void setOnDeleteCheckListener(AdapterOnDeleteCheckListener mOnDeleteCheckListener) {
        this.mOnDeleteCheckListener = mOnDeleteCheckListener;
    }
}
