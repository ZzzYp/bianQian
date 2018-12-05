package com.gome.note.ui.home.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gome.note.R;
import com.gome.note.base.BaseRcvAdapter;
import com.gome.note.base.BaseRvViewHolder;
import com.gome.note.entity.PocketInfo;
import com.gome.note.utils.DataUtils;
import com.gome.note.view.ComposeTextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Author：viston on 2017/6/17 10:38
 */
public class NoteHomeAdapter extends BaseRcvAdapter<PocketInfo> {
    public static final String TAG = "NoteHomeAdapter";
    public static boolean isShowDeleteIcon;
    private ItemClickListener mItemClickListener;
    private ViewGroup mCardRelativeLayout, mCardView;
    private String mIconUrl = "";
    private long dateModified;
    private String[] stringArr;
    private CheckBox cbHomeItemCheck;
    public static Map<String, Integer> typeMap = new HashMap<>();
    private RelativeLayout mRtSearch;
    private boolean checkedStatus;

    enum ITEM_TYPE {
        ITEM_CLASSIFY, ITEM_COMMON, ITEM_BLANK, ITEM_HEAD, ITEM_COMMON_NO_IMAGE
    }

    public NoteHomeAdapter(Context context, List data, int type, AdapterOnDeleteCheckListener onDeleteCheckListener) {
        super(context, data, type);
        mOnDeleteCheckListener = onDeleteCheckListener;
    }


    @Override
    protected int getLayoutId(int viewType) {
        if (viewType == ITEM_TYPE.ITEM_HEAD.ordinal()) {
            return R.layout.activity_note_search_head;
        } else if (viewType == ITEM_TYPE.ITEM_CLASSIFY.ordinal()) {
            return R.layout.classify_list_item;
        } else if (viewType == ITEM_TYPE.ITEM_COMMON.ordinal()) {
            return R.layout.home_list_item;
        } else if (viewType == ITEM_TYPE.ITEM_COMMON_NO_IMAGE.ordinal()) {
            return R.layout.home_list_item_no_image;
        }

        return 0;

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return ITEM_TYPE.ITEM_HEAD.ordinal();
        } else if (mData.get(position).getId() == -1) {

            return ITEM_TYPE.ITEM_CLASSIFY.ordinal();
        } else if (mData.get(position).getIcon() != null && mData.get(position).getIcon().length() > 0) {
            return ITEM_TYPE.ITEM_COMMON.ordinal();
        } else {
            return ITEM_TYPE.ITEM_COMMON_NO_IMAGE.ordinal();
        }
    }

    @Override
    protected void onBindData(BaseRvViewHolder holder, int position) {

        View convertView = holder.getConvertView();

        if (holder.getItemViewType() == ITEM_TYPE.ITEM_HEAD.ordinal()) {

            mRtSearch = (RelativeLayout) convertView.findViewById(R.id.rt_search);
            if (!checkedStatus) {
                onItemClick(mRtSearch, holder);
            } else {
                onItemNoClick(mRtSearch, holder);
            }


        } else if (holder.getItemViewType() == ITEM_TYPE.ITEM_COMMON.ordinal()) {
            PocketInfo pocketInfo = mData.get(position);
            mIconUrl = pocketInfo.getIcon();
            dateModified = pocketInfo.getDateModified();

            View main = convertView.findViewById(R.id.relative_content);
            mCardRelativeLayout = (ViewGroup) convertView.findViewById(R.id.relaontent_parent);
            mCardView = (ViewGroup) convertView.findViewById(R.id.card_view);

            TextView textView = (TextView) convertView.findViewById(R.id.home_item_tile);
            //TextView tvContent = (TextView) convertView.findViewById(R.id.home_item_content);
            ImageView image = (ImageView) convertView.findViewById(R.id.home_item_icon);
            ImageView audioImage = (ImageView) convertView.findViewById(R.id.image_audio);
            cbHomeItemCheck = (CheckBox) convertView.findViewById(R.id.cb_home_item_check);

            TextView mTvDate = (TextView) convertView.findViewById(R.id.tv_date);
            TextView mTvNoon = (TextView) convertView.findViewById(R.id.tv_noon);
            TextView mTvTime = (TextView) convertView.findViewById(R.id.tv_time);


            convertView.setPadding(0, 0, 0, 0);
            int paddingBottom = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68,
                    mContext.getResources().getDisplayMetrics()));
            int paddingTop = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                    mContext.getResources().getDisplayMetrics()));
            if (mData.size() - 1 == position) {
                convertView.setPadding(0, 0, 0, paddingBottom);
            }
            if (pocketInfo.isHasAudio()) {
                audioImage.setVisibility(View.VISIBLE);
            } else {
                audioImage.setVisibility(View.GONE);
            }
            String title = pocketInfo.getTitle();
            String summary = pocketInfo.getSummary();
            textView.setText(summary);

            if (!TextUtils.isEmpty(mIconUrl)) {

                File file = new File(mIconUrl);
                if (null == file || file.length() == 0) {
                    Glide.with(mContext).load(R.drawable.default_icon).thumbnail(0.1f).error(R.drawable.default_icon).into
                            (image);
                } else {
                    if (mIconUrl.contains("/")) {
                        Glide.with(mContext).load(mIconUrl).thumbnail(0.1f).error(R.drawable.default_icon).into
                                (image);
                    }
                }

                convertView.findViewById(R.id.home_item_icon).setVisibility(View.VISIBLE);
                if (null == summary || summary.length() == 0) {
                    textView.setText(mContext.getString(R.string.image_note));
                }
            } else {
                convertView.findViewById(R.id.home_item_icon).setVisibility(View.GONE);

            }
            if (isShowDeleteIcon) {
                convertView.findViewById(R.id.cb_home_item_check).setVisibility(View.VISIBLE);
                // onClickDeleteIcon(holder.getConvertView(), holder);
                onItemClick(main, holder);
            } else {
                convertView.findViewById(R.id.cb_home_item_check).setVisibility(View.GONE);
                onItemClick(main, holder);
            }
            onLongItemClick(main, holder);

            String date = getDateString(mContext, dateModified);

//            if (mType == ShowStyle.LIST_STYLE) {
//                holder.setTvText(R.id.home_item_content, date + "   " + pocketInfo.getSummary());
//            } else {
//                holder.setTvText(R.id.home_item_content, pocketInfo.getSummary());
//            }

            holder.setTvText(R.id.tv_date, date);


            try {
                if (DataUtils.isCurrentYear(dateModified)) {

                    ContentResolver cv = mContext.getContentResolver();
                    String strTimeFormat = android.provider.Settings.System.getString(cv,
                            android.provider.Settings.System.TIME_12_24);
                    String time = "";
                    if (("24").equals(strTimeFormat)) {
                        time = DataUtils.currentTime24(dateModified);
                        holder.setVisible(R.id.tv_noon, false);
                    } else {
                        time = DataUtils.currentTime12(dateModified);
                        holder.setVisible(R.id.tv_noon, true);
                    }
                    holder.setTvText(R.id.tv_time, time);
                    holder.setVisible(R.id.tv_time, true);

                    int noon = DataUtils.currentNoon(dateModified);
                    if (noon == 0) {
                        holder.setTvText(R.id.tv_noon, mContext.getString(R.string.forenoon));
                    } else {
                        holder.setTvText(R.id.tv_noon, mContext.getString(R.string.afternoon));
                    }
                } else {
                    holder.setVisible(R.id.tv_time, false);
                    holder.setVisible(R.id.tv_noon, false);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (pocketInfo.isChecked()) {
                cbHomeItemCheck.setChecked(true);
            } else {
                cbHomeItemCheck.setChecked(false);
            }

            boolean isStick = pocketInfo.isStick();
            if (isStick) {
                mCardView.setBackgroundColor(mContext.getColor(R.color.home_bg_color));
            } else {
                mCardView.setBackgroundColor(Color.TRANSPARENT);
            }

            boolean isClassifyLast = pocketInfo.isClassifyLast();
            if (isClassifyLast) {
                holder.setVisible(R.id.line, false);
            } else {
                holder.setVisible(R.id.line, true);
            }


        } else if (holder.getItemViewType() == ITEM_TYPE.ITEM_COMMON_NO_IMAGE.ordinal()) {
            PocketInfo pocketInfo = mData.get(position);
            dateModified = pocketInfo.getDateModified();

            View main = convertView.findViewById(R.id.relative_content);
            mCardRelativeLayout = (ViewGroup) convertView.findViewById(R.id.relaontent_parent);
            mCardView = (ViewGroup) convertView.findViewById(R.id.card_view);

            //ComposeTextView textView = (ComposeTextView) convertView.findViewById(R.id.home_item_tile);
            TextView textView = (TextView) convertView.findViewById(R.id.home_item_tile);
            //TextView tvContent = (TextView) convertView.findViewById(R.id.home_item_content);
            ImageView audioImage = (ImageView) convertView.findViewById(R.id.image_audio);
            cbHomeItemCheck = (CheckBox) convertView.findViewById(R.id.cb_home_item_check);

            TextView mTvDate = (TextView) convertView.findViewById(R.id.tv_date);
            TextView mTvNoon = (TextView) convertView.findViewById(R.id.tv_noon);
            TextView mTvTime = (TextView) convertView.findViewById(R.id.tv_time);


            convertView.setPadding(0, 0, 0, 0);
            int paddingBottom = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68,
                    mContext.getResources().getDisplayMetrics()));
            int paddingTop = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                    mContext.getResources().getDisplayMetrics()));
            if (mData.size() - 1 == position) {
                convertView.setPadding(0, 0, 0, paddingBottom);
            }

            String title = pocketInfo.getTitle();
            String summary = pocketInfo.getSummary();
            textView.setText(summary);

            if (pocketInfo.isHasAudio()) {
                audioImage.setVisibility(View.VISIBLE);
                if (null == summary || summary.length() == 0) {
                    textView.setText(mContext.getString(R.string.audio_note));
                }
            } else {
                audioImage.setVisibility(View.GONE);
            }


            if (isShowDeleteIcon) {
                convertView.findViewById(R.id.cb_home_item_check).setVisibility(View.VISIBLE);
                //onClickDeleteIcon(holder.getConvertView(), holder);
                onItemClick(main, holder);
            } else {
                convertView.findViewById(R.id.cb_home_item_check).setVisibility(View.GONE);
                onItemClick(main, holder);
            }
            onLongItemClick(main, holder);

            String date = getDateString(mContext, dateModified);

//            if (mType == ShowStyle.LIST_STYLE) {
//                holder.setTvText(R.id.home_item_content, date + "   " + pocketInfo.getSummary());
//            } else {
//                holder.setTvText(R.id.home_item_content, pocketInfo.getSummary());
//            }

            holder.setTvText(R.id.tv_date, date);


            try {
                if (DataUtils.isCurrentYear(dateModified)) {
                    //holder.setVisible(R.id.tv_time, true);
                    //holder.setVisible(R.id.tv_noon, true);

                    ContentResolver cv = mContext.getContentResolver();
                    String strTimeFormat = android.provider.Settings.System.getString(cv,
                            android.provider.Settings.System.TIME_12_24);
                    String time = "";
                    if ("24".equals(strTimeFormat)) {
                        time = DataUtils.currentTime24(dateModified);
                        holder.setVisible(R.id.tv_noon, false);
                    } else {
                        time = DataUtils.currentTime12(dateModified);
                        holder.setVisible(R.id.tv_noon, true);
                    }
                    holder.setTvText(R.id.tv_time, time);
                    holder.setVisible(R.id.tv_time, true);

                    int noon = DataUtils.currentNoon(dateModified);
                    if (noon == 0) {
                        holder.setTvText(R.id.tv_noon, mContext.getString(R.string.forenoon));
                    } else {
                        holder.setTvText(R.id.tv_noon, mContext.getString(R.string.afternoon));
                    }
                } else {
                    holder.setVisible(R.id.tv_time, false);
                    holder.setVisible(R.id.tv_noon, false);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (pocketInfo.isChecked()) {
                cbHomeItemCheck.setChecked(true);
            } else {
                cbHomeItemCheck.setChecked(false);
            }

            boolean isStick = pocketInfo.isStick();
            if (isStick) {
                mCardView.setBackgroundColor(mContext.getColor(R.color.home_bg_color));
            } else {
                mCardView.setBackgroundColor(Color.TRANSPARENT);
            }

            boolean isClassifyLast = pocketInfo.isClassifyLast();
            if (isClassifyLast) {
                holder.setVisible(R.id.line, false);
            } else {
                holder.setVisible(R.id.line, true);
            }


        } else if (holder.getItemViewType() == ITEM_TYPE.ITEM_CLASSIFY.ordinal()) {
            PocketInfo pocketInfo = mData.get(position);

            String summary = pocketInfo.getSummary();

            if (summary.equals(mContext.getString(R.string.put_top))) {
                holder.setTvText(R.id.tv_classify_date, pocketInfo.getSummary());
                holder.setTvText(R.id.tv_classify_year, "");
                holder.setVisible(R.id.ll_classify_date, false);

            } else if (summary.length() < 3) {
                int mouth = Integer.valueOf(summary);
                String mouthStr = switchMouthNumToStr(mouth);
                holder.setTvText(R.id.tv_classify_date, mouthStr);
                holder.setTvText(R.id.tv_classify_year, "");
                holder.setVisible(R.id.ll_classify_date, true);

            } else {
                holder.setTvText(R.id.tv_classify_date, pocketInfo.getSummary());
                holder.setTvText(R.id.tv_classify_year, mContext.getString(R.string.classify_year));
                holder.setVisible(R.id.ll_classify_date, true);
            }

            if (pocketInfo.isStickNextClassify()) {
                holder.setVisible(R.id.classify_divide, false);
            } else {
                holder.setVisible(R.id.classify_divide, true);
            }
            if (position == 1) {
                holder.setVisible(R.id.classify_divide, false);
            } else {
                holder.setVisible(R.id.classify_divide, true);
            }
        }

    }

    private void onItemNoClick(RelativeLayout v, BaseRvViewHolder holder) {
        switch (v.getId()) {
            case R.id.rt_search:
                v.setSoundEffectsEnabled(false);
                break;
        }
    }


    public String getDateString(Context context, long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(time);
        String str = formatter.format(curDate);
        String dateString = "";

        try {
            int year = DataUtils.getModifyYear(time);
            int mouth = DataUtils.getModifyMouth(time);
            int day = DataUtils.getModifyDay(time);
            if (DataUtils.IsToday(time)) {
                dateString = context.getString(R.string.home_item_today);
            } else if (DataUtils.IsYesterday(time)) {
                dateString = context.getString(R.string.home_item_yesterday);
            } else if (DataUtils.isCurrentYear(time)) {
                dateString = mouth + context.getString(R.string.mouth) + day + context.getString(R.string.day);
            } else {
                dateString = year + context.getString(R.string.year) +
                        mouth + context.getString(R.string.mouth) +
                        day + context.getString(R.string.day);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }


    public void showDeleteIcon(boolean isShowIcon, int position) {
        this.isShowDeleteIcon = isShowIcon;
        if (position >= 0) {
            mData.get(position).setChecked(true);
        }
        this.notifyDataSetChanged();
    }

    public void onClickDeleteIcon(final View view, final BaseRvViewHolder holder) {
        view.findViewById(R.id.cb_home_item_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_home_item_check);
                mOnDeleteCheckListener.adapterOnDeleteCheck(view, checkBox.isChecked(), holder.getAdapterPosition());


            }
        });
    }



    public void onItemClick(final View v, final BaseRvViewHolder holder) {
        switch (v.getId()) {
            case R.id.relative_content:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if (!isShowDeleteIcon) {
                        if (null != mItemClickListener) {
                            mItemClickListener.onItemClick(v, holder.getAdapterPosition());
                        }
                        // }
                    }
                });
                break;


            case R.id.rt_search:
                v.setSoundEffectsEnabled(true);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != mItemClickListener) {
                            mItemClickListener.onItemClick(v, holder.getAdapterPosition());
                        }
                    }
                });
                break;

            case R.id.tv_delete:
//                v.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        boolean isNotShowDiag = SharedPreferencesUtil.getBooleanValue(mContext,
//                                SharedPreferencesUtil.DELETE_HOME_DIALOG_NOT_ALERT, false);
//                        if (!isNotShowDiag && IS_FROM_ACTIVITY) {
//                            LayoutInflater factory = LayoutInflater.from(mContext);
//                            final View dialogCheckbox = factory.inflate(R.layout
//                                    .dialog_delete_to_history, null);
//                            final CheckBox checkboxs = (CheckBox) dialogCheckbox.findViewById(R
//                                    .id.checkbox);
//                            final TextView tvCheckString = (TextView) dialogCheckbox.findViewById
//                                    (R.id.tv_check_string);
//                            tvCheckString.setText(R.string.even_not_alert);
//
//                            dialog = new GomeAlertDialog.Builder(mContext)
//                                    .setView(dialogCheckbox)
//                                    .setCancelable(true)
//                                    .setTitle(R.string.dialog_delete_title)
//                                    .setMessage(R.string.dialog_history_title)
//                                    .setPositiveButton(R.string.confirm, new DialogInterface
//                                            .OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (checkboxs.isChecked()) {
//                                                //no alert
//                                                SharedPreferencesUtil.saveBooelanValue(mContext,
//                                                        SharedPreferencesUtil.DELETE_HOME_DIALOG_NOT_ALERT, true);
//                                            } else {
//                                                //have alert
//                                            }
//                                            int pos = holder.getAdapterPosition();
//                                            mData.get(pos).setDateModified(System.currentTimeMillis());
//                                            PocketDbHandle.insert(mContext, PocketDbHandle
//                                                    .URI_HISTORY, mData.get(pos));
//                                            PocketDbHandle.delete(mContext, URI_POCKET, mData.get
//                                                    (pos).getId());
//                                            mData.remove(mData.get(pos));
//                                            mItemClickListener.onNoItemList(mData);
//                                            //notifyItemRemoved(pos);
//                                            notifyDataSetChanged();
//                                        }
//                                    }).setNegativeButton(R.string.cancel, new DialogInterface
//                                            .OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                        }
//                                    }).setOnKeyListener(new DialogInterface.OnKeyListener() {
//                                        @Override
//                                        public boolean onKey(DialogInterface dialogInterface, int i,
//                                                             KeyEvent keyEvent) {
//                                            if (i == KeyEvent.KEYCODE_BACK
//                                                    && keyEvent.getRepeatCount() == 0) {
//                                                dialogInterface.cancel();
//                                                return true;
//                                            }
//                                            return false;
//                                        }
//                                    }).show();
//                        } else {
//                            int pos = holder.getAdapterPosition();
//                            if (pos >= 0) {
//                                mData.get(pos).setDateModified(System.currentTimeMillis());
//                                PocketDbHandle.insert(mContext, PocketDbHandle.URI_HISTORY, mData
//                                        .get(pos));
//                                PocketDbHandle.delete(mContext, URI_POCKET, mData.get(pos).getId());
//                                mData.remove(mData.get(pos));
//                                mItemClickListener.onNoItemList(mData);
//                                //notifyItemRemoved(pos);
//                                notifyDataSetChanged();
//                            }
//                        }
//                        setDialogInterface(dialog);
//                    }
//                });
                break;
        }
    }


    public void onLongItemClick(View view, final BaseRvViewHolder holder) {
        switch (view.getId()) {
            case R.id.relative_content:
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!checkedStatus && null != mItemClickListener) {
                            mItemClickListener.onLongClick(view, holder.getAdapterPosition());
                        }
                        return false;
                    }
                });
                break;
        }
    }

    private String switchMouthNumToStr(int mouth) {
        switch (mouth) {
            case 1:
                return mContext.getResources().getString(R.string.january);
            case 2:
                return mContext.getResources().getString(R.string.february);
            case 3:
                return mContext.getResources().getString(R.string.march);
            case 4:
                return mContext.getResources().getString(R.string.april);
            case 5:
                return mContext.getResources().getString(R.string.may);
            case 6:
                return mContext.getResources().getString(R.string.june);
            case 7:
                return mContext.getResources().getString(R.string.july);
            case 8:
                return mContext.getResources().getString(R.string.august);
            case 9:
                return mContext.getResources().getString(R.string.september);
            case 10:
                return mContext.getResources().getString(R.string.october);
            case 11:
                return mContext.getResources().getString(R.string.november);
            case 12:
                return mContext.getResources().getString(R.string.december);
        }

        return "";
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;

    }

    public void setOnItemNoListListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;

    }

    public AdapterOnDeleteCheckListener mOnDeleteCheckListener = null;

    public interface AdapterOnDeleteCheckListener {
        void adapterOnDeleteCheck(View view, boolean isChecked, int postion);

    }

    public boolean isCheckedStatus() {
        return checkedStatus;
    }

    public void setCheckedStatus(boolean checkedStatus) {
        this.checkedStatus = checkedStatus;
    }
}
