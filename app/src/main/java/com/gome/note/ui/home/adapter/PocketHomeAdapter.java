package com.gome.note.ui.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gome.note.R;
import com.gome.note.base.BaseRcvAdapter;
import com.gome.note.base.BaseRvViewHolder;
import com.gome.note.entity.PocketInfo;
import com.gome.note.utils.DataUtils;
import com.gome.note.utils.ShowStyle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



/**
 * Author：viston on 2017/6/17 10:38
 */
public class PocketHomeAdapter extends BaseRcvAdapter<PocketInfo> {
    public static final String TAG = "PocketHomeAdapter";
    public static boolean isShowDeleteIcon;
    private ItemClickListener mItemClickListener;
    private ViewGroup mCardRelativeLayout, mCardView;
    private String mIconUrl = "";
    private long dateModified;
    private String[] stringArr;
    private CheckBox cbHomeItemCheck;

    public PocketHomeAdapter(Context context, List data, int type, AdapterOnDeleteCheckListener onDeleteCheckListener) {
        super(context, data, type);
        mOnDeleteCheckListener = onDeleteCheckListener;
    }


    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.home_list_item;
    }

    @Override
    protected void onBindData(BaseRvViewHolder holder, int position) {
        PocketInfo pocketInfo = mData.get(position);
        mIconUrl = pocketInfo.getIcon();
        dateModified = pocketInfo.getDateModified();
        View convertView = holder.getConvertView();
        View main = convertView.findViewById(R.id.relative_content);
        mCardRelativeLayout = (ViewGroup) convertView.findViewById(R.id.relaontent_parent);
        mCardView = (ViewGroup) convertView.findViewById(R.id.card_view);

        mCardRelativeLayout.setBackgroundColor(0xffffffff);
        TextView textView = (TextView) convertView.findViewById(R.id.home_item_tile);
        TextView tvContent = (TextView) convertView.findViewById(R.id.home_item_content);
        ImageView image = (ImageView) convertView.findViewById(R.id.home_item_icon);
        ImageView audioImage = (ImageView) convertView.findViewById(R.id.image_audio);
        cbHomeItemCheck = (CheckBox) convertView.findViewById(R.id.cb_home_item_check);

        convertView.setPadding(0, 0, 0, 0);
        int paddingBottom = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68,
                mContext.getResources().getDisplayMetrics()));
        int paddingTop = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                mContext.getResources().getDisplayMetrics()));
        if (mData.size() - 1 == position) {
            convertView.setPadding(0, 0, 0, paddingBottom);
        }
        if (pocketInfo.getHtml() != null && pocketInfo.getHtml().contains("audio")) {
            audioImage.setVisibility(View.VISIBLE);
        } else {
            audioImage.setVisibility(View.GONE);
        }
        String title = pocketInfo.getTitle();
        String summary = pocketInfo.getSummary();
        if (!TextUtils.isEmpty(title) && title.length() != 0) {
            textView.setText(title);
        } else if (!TextUtils.isEmpty(summary) && summary.length() != 0) {
            textView.setText(summary);
        } else {
            if (null == stringArr || stringArr.length == 0) {
                stringArr = mContext.getResources().getStringArray(R.array.home_default_title_arr);
            }
            for (int i = 0; i < stringArr.length; i++) {
                String tempStr = stringArr[i];
                if (null != tempStr) {
                    title = mContext.getString(R.string.home_default_title);
                }
            }
            textView.setText(title);
        }
        if (!TextUtils.isEmpty(mIconUrl)) {

            if (mIconUrl.contains("/")) {
                Glide.with(mContext).load(mIconUrl).thumbnail(0.1f).into
                        (image);
            }
            convertView.findViewById(R.id.home_item_icon).setVisibility(View.VISIBLE);

        } else {
            //convertView.findViewById(R.id.home_item_icon).setVisibility(View.GONE);
            convertView.findViewById(R.id.home_item_icon).setVisibility(View.VISIBLE);
        }
        if (isShowDeleteIcon) {
            convertView.findViewById(R.id.cb_home_item_check).setVisibility(View.VISIBLE);
            onClickDeleteIcon(holder.getConvertView(), holder);
        } else {
            convertView.findViewById(R.id.cb_home_item_check).setVisibility(View.GONE);
            onItemClick(main, holder);
        }
        onLongItemClick(main, holder);
        String date = getDateString(mContext, dateModified);
        if (mType == ShowStyle.LIST_STYLE) {
            holder.setTvText(R.id.home_item_content, date + "   " + pocketInfo.getSummary());
        } else {
            holder.setTvText(R.id.home_item_content, pocketInfo.getSummary());
        }


        if (pocketInfo.isChecked()) {
            cbHomeItemCheck.setChecked(true);
        } else {
            cbHomeItemCheck.setChecked(false);
        }
    }


    public String getDateString(Context context, long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(time);
        String str = formatter.format(curDate);
        String dateString = "";
        try {
            if (DataUtils.IsToday(str)) {
                dateString = context.getString(R.string.home_item_today);
            } else if (DataUtils.IsYesterday(str)) {
                dateString = context.getString(R.string.home_item_yesterday);
            } else {
                dateString = str;
            }
        } catch (ParseException e) {
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
                        if (!isShowDeleteIcon) {
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
                        mItemClickListener.onLongClick(view, holder.getAdapterPosition());
                        return false;
                    }
                });
                break;
        }
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


}
