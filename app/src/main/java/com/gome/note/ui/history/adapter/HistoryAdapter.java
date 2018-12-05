package com.gome.note.ui.history.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gome.note.R;
import com.gome.note.base.BaseRcvAdapter;
import com.gome.note.base.BaseRvViewHolder;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.utils.DataUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 2017/6/17 10:38
 */
public class HistoryAdapter extends BaseRcvAdapter<PocketInfo> {
    public static final String TAG = "HistoryAdapter";
    private static boolean isShowDeleteIcon;
    private ItemHistoryClickListener mItemClickListener;
    private ArrayList<PocketInfo> mSelectList = new ArrayList<>();
    //storage the map list of checked boxes
    private Map<Integer, Boolean> map = new HashMap<>();
    private static final int ITEM_COMMON = 1;
    private static final int ITEM_COMMON_NO_IMAGE = 2;
    private PocketInfo mPocketInfo;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ITEM_COMMON, ITEM_COMMON_NO_IMAGE})
    public @interface itemState {

    }


    public HistoryAdapter(Context context, List data, int type) {
        super(context, data, type);
        initMap();
    }

    //initialize the map list ,default is the not selected
    private void initMap() {
        for (int i = 0; i < mData.size(); i++) {
            map.put(i, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getIcon() != null && mData.get(position).getIcon().length() > 0) {
            return ITEM_COMMON;
        } else {
            return ITEM_COMMON_NO_IMAGE;
        }
    }

    @Override
    protected int getLayoutId(int viewType) {
        if (viewType == ITEM_COMMON) {
            return R.layout.history_list_item;
        } else {
            return R.layout.history_list_item_no_image;
        }
    }

    @Override
    protected void onBindData(BaseRvViewHolder holder, int position) {
        View convertView = holder.getConvertView();
        switch (holder.getItemViewType()) {
            case ITEM_COMMON:
                TextView search_item_title = (TextView) convertView.findViewById(R.id.home_item_tile);
                ImageView search_item_image = (ImageView) convertView.findViewById(R.id.home_item_icon);
                ImageView search_item_audioImage = (ImageView) convertView.findViewById(R.id.image_audio);
                TextView mTvDate = (TextView) convertView.findViewById(R.id.tv_date);
                TextView mTvNoon = (TextView) convertView.findViewById(R.id.tv_noon);
                TextView mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
                CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cb_history_item_check);
                View main = convertView.findViewById(R.id.relative_content);

                PocketInfo pocketInfo = mData.get(position);
                long dateModified = pocketInfo.getDateModified();
                //show title
                String title = pocketInfo.getSummary();
                search_item_title.setText(title);

                //is recording weather or not
                if (pocketInfo.isHasAudio()) {
                    search_item_audioImage.setVisibility(View.VISIBLE);
                } else {
                    search_item_audioImage.setVisibility(View.GONE);
                }
                //show note image
                String mIconUrl = pocketInfo.getIcon();

                if (!TextUtils.isEmpty(mIconUrl)) {

                    File file = new File(mIconUrl);
                    if (null == file || file.length() == 0) {
                        Glide.with(mContext).load(R.drawable.default_icon).thumbnail(0.1f).error(R.drawable.default_icon).into
                                (search_item_image);
                    } else {
                        if (mIconUrl.contains("/")) {
                            Glide.with(mContext).load(mIconUrl).thumbnail(0.1f).error(R.drawable.default_icon).into
                                    (search_item_image);
                        }
                    }
//                    if (mIconUrl.contains("/")) {
//                        Glide.with(mContext).load(mIconUrl).thumbnail(0.1f).into
//                                (search_item_image);
//                    }
                    convertView.findViewById(R.id.home_item_icon).setVisibility(View.VISIBLE);

                    if (title.length() == 0) {
                        search_item_title.setText(mContext.getString(R.string.image_note));
                    }
                } else {
                    convertView.findViewById(R.id.home_item_icon).setVisibility(View.GONE);
                }

                String date = DataUtils.getDateString(mContext, dateModified);
                mTvDate.setText(date);

                try {
                    if (DataUtils.isCurrentYear(dateModified)) {
                        ContentResolver cv = mContext.getContentResolver();
                        String strTimeFormat = android.provider.Settings.System.getString(cv,
                                android.provider.Settings.System.TIME_12_24);
                        String time;
                        if (("24").equals(strTimeFormat)) {
                            time = DataUtils.currentTime24(dateModified);
                            holder.setVisible(R.id.tv_noon, false);
                        } else {
                            time = DataUtils.currentTime12(dateModified);
                            holder.setVisible(R.id.tv_noon, true);
                        }
                        mTvTime.setText(time);

                        int noon = DataUtils.currentNoon(dateModified);
                        if (noon == 0) {
                            mTvNoon.setText(R.string.forenoon);
                        } else {
                            mTvNoon.setText(R.string.afternoon);

                        }
                    } else {
                        holder.setVisible(R.id.tv_time, false);
                        holder.setVisible(R.id.tv_noon, false);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //set the status of CheckBox
                map.putIfAbsent(position, false);
                if (isShowDeleteIcon) {
                    checkBox.setVisibility(View.VISIBLE);
                    main.setTag(position);
                    checkBox.setChecked(map.get(position));
                    onItemClick(main, position);
                } else {
                    checkBox.setVisibility(View.GONE);
                    onLongItemClick(main, holder);
                    onItemNoClick(main);
                }

                break;
            case ITEM_COMMON_NO_IMAGE:
                TextView search_item_title_no_img = (TextView) convertView.findViewById(R.id.home_item_tile);
                ImageView search_item_audioImage_no_img = (ImageView) convertView.findViewById(R.id.image_audio);
                TextView mTvDate_no_img = (TextView) convertView.findViewById(R.id.tv_date);
                TextView mTvNoon_no_img = (TextView) convertView.findViewById(R.id.tv_noon);
                TextView mTvTime_no_img = (TextView) convertView.findViewById(R.id.tv_time);
                CheckBox checkBox_no_img = (CheckBox) convertView.findViewById(R.id.cb_history_item_check);
                View main_no_img = convertView.findViewById(R.id.relative_content);

                mPocketInfo = mData.get(position);
                long dateModified_no_img = mPocketInfo.getDateModified();
                //show title
                search_item_title_no_img.setText(mPocketInfo.getSummary());

                if (mPocketInfo.isHasAudio()) {
                    search_item_audioImage_no_img.setVisibility(View.VISIBLE);
                    if (null == mPocketInfo.getSummary() || mPocketInfo.getSummary().length() == 0) {
                        search_item_title_no_img.setText(mContext.getString(R.string.audio_note));
                    }
                } else {
                    search_item_audioImage_no_img.setVisibility(View.GONE);
                }

                String date_no_img = DataUtils.getDateString(mContext, dateModified_no_img);
                mTvDate_no_img.setText(date_no_img);

                try {
                    if (DataUtils.isCurrentYear(dateModified_no_img)) {
                        ContentResolver cv = mContext.getContentResolver();
                        String strTimeFormat = android.provider.Settings.System.getString(cv,
                                android.provider.Settings.System.TIME_12_24);
                        String time;
                        if (("24").equals(strTimeFormat)) {
                            time = DataUtils.currentTime24(dateModified_no_img);
                            holder.setVisible(R.id.tv_noon, false);
                        } else {
                            time = DataUtils.currentTime12(dateModified_no_img);
                            holder.setVisible(R.id.tv_noon, true);
                        }
                        mTvTime_no_img.setText(time);

                        int noon = DataUtils.currentNoon(dateModified_no_img);
                        if (noon == 0) {
                            mTvNoon_no_img.setText(R.string.forenoon);
                        } else {
                            mTvNoon_no_img.setText(R.string.afternoon);

                        }
                    } else {
                        holder.setVisible(R.id.tv_time, false);
                        holder.setVisible(R.id.tv_noon, false);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //set the status of CheckBox
                map.putIfAbsent(position, false);
                if (isShowDeleteIcon) {
                    checkBox_no_img.setVisibility(View.VISIBLE);
                    main_no_img.setTag(position);
                    checkBox_no_img.setChecked(map.get(position));
                    onItemClick(main_no_img, position);
                } else {
                    checkBox_no_img.setVisibility(View.GONE);
                    onLongItemClick(main_no_img, holder);
                    onItemNoClick(main_no_img);
                }

                break;
        }
    }

    //click the item to select the CheckBox
    public void setSelectItem(int position) {
        //get the negation of the current status
        if (map.get(position)) {
            // mSelectList.remove(mData.get(position));
            mSelectList = removeListObject(mSelectList, mData.get(position));
            map.put(position, false);
        } else {
            mSelectList.add(mData.get(position));
            map.put(position, true);
        }
        notifyItemChanged(position);

    }

    //update check status
    public void updateSelectItem(int position) {
        //get the negation of the current status
        map.put(position, true);
        notifyItemChanged(position);
    }

    //clean check status
    public void cleanAllSelectItem() {
        //get the negation of the current status
        map.clear();
    }


    public ArrayList<PocketInfo> getSelectPocketInfo() {
        return mSelectList;
    }

    /**
     * check all and cancel
     */
    public void setSelectItemCheck(boolean isCheck) {
        mSelectList.clear();
        for (int i = 0; i < mData.size(); i++) {
            map.put(i, isCheck);
            if (isCheck) {
                mSelectList.add(mData.get(i));
            } else {
                mSelectList.remove(mData.get(i));
            }
        }
        notifyDataSetChanged();
    }

    //return the map list to MainActivity
    public Map<Integer, Boolean> getMap() {
        return map;
    }

    public void onItemClick(final View v, final int position) {
        v.setSoundEffectsEnabled(true);
        v.findViewById(R.id.relative_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShowDeleteIcon) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, position);
                    }
                } else {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemCheckShowClick(v, (int) v.getTag());
                    }
                }
            }
        });
    }

    private void onItemNoClick(View v) {
        switch (v.getId()) {
            case R.id.relative_content:
                v.setSoundEffectsEnabled(false);
                break;
        }
    }

    private void onLongItemClick(View view, final BaseRvViewHolder holder) {
        switch (view.getId()) {
            case R.id.relative_content:
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onLongClick(view, holder.getAdapterPosition());
                        }
                        return false;
                    }
                });
                break;
        }
    }

    public void clearCheckStatusMap() {
        initMap();
    }

    public void showDeleteIcon(boolean isShowIcon) {
        isShowDeleteIcon = isShowIcon;
        this.notifyDataSetChanged();
    }

    public void setItemClickListener(ItemHistoryClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    private ArrayList<PocketInfo> removeListObject(ArrayList<PocketInfo> mPocketInfos, PocketInfo mPocketInfo) {
        long removerId = mPocketInfo.getId();
//        for (int i = 0; i < mPocketInfos.size(); i++) {
//            long tempId = mPocketInfos.get(i).getId();
//            if (removerId == tempId) {
//                mPocketInfos.remove(i);
//            }
//        }
        Iterator<PocketInfo> it = mPocketInfos.iterator();
        while (it.hasNext()) {
            PocketInfo pocketInfo = it.next();
            long tempId = pocketInfo.getId();
            if (removerId == tempId) {
                it.remove();
            }
        }


        return mPocketInfos;
    }
}
