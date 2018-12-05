package com.gome.note.ui.search.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gome.note.R;
import com.gome.note.base.BaseRcvAdapter;
import com.gome.note.base.BaseRvViewHolder;
import com.gome.note.entity.PocketInfo;
import com.gome.note.ui.create.NoteCreateActivity;
import com.gome.note.ui.search.adapter.ItemClickListener;
import com.gome.note.utils.DataUtils;

import java.io.File;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Eric on 2018/2/26 11:24
 */

public class SearchResultAdapter extends BaseRcvAdapter<PocketInfo> {

    private String mSearchStr;
    private PocketInfo mPocketInfo;
    private ItemClickListener mItemClickListener;

    public SearchResultAdapter(Context context, List<PocketInfo> data, String searchSting, int type) {
        super(context, data, type);
        this.mSearchStr = searchSting;
    }

    private static final int ITEM_DATE = 1;
    private static final int ITEM_COMMON = 2;
    private static final int ITEM_COMMON_NO_IMAGE = 3;

    @IntDef({ITEM_DATE, ITEM_COMMON, ITEM_COMMON_NO_IMAGE})
    public @interface itemState {

    }

    @Override
    public int getItemViewType(int position) {

        if (mData.get(position).getId() == -1) {

            return ITEM_DATE;
        } else if (mData.get(position).getIcon() != null && mData.get(position).getIcon().length() > 0) {
            return ITEM_COMMON;
        } else {
            return ITEM_COMMON_NO_IMAGE;
        }
    }

    @Override
    protected int getLayoutId(int viewType) {
        if (viewType == ITEM_COMMON) {
            return R.layout.search_list_item;
        } else if (viewType == ITEM_DATE) {
            return R.layout.classify_list_item;
        } else if (viewType == ITEM_COMMON_NO_IMAGE) {
            return R.layout.search_list_item_no_image;
        } else {
            return 0;
        }
    }

    public void setSearchStr(String searchStr) {
        this.mSearchStr = searchStr;
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

                mPocketInfo = mData.get(position);
                long dateModified = mPocketInfo.getDateModified();
                //show title
                String title = mPocketInfo.getSummary();
                boolean isContains = title.contains(mSearchStr);
                if (isContains) {
                    int startIndex = title.indexOf(mSearchStr);
                    SpannableString spannableString = new SpannableString(title);
                    spannableString.setSpan(new ForegroundColorSpan(0XFF2E76FC),
                            startIndex, startIndex + mSearchStr.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    search_item_title.setText(spannableString);
                } else {
                    search_item_title.setText(title);
                }

                if (mPocketInfo.isHasAudio()) {
                    search_item_audioImage.setVisibility(View.VISIBLE);
                } else {
                    search_item_audioImage.setVisibility(View.GONE);
                }

                //show note image
                String mIconUrl = mPocketInfo.getIcon();

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

                //item clicked to modify note
                convertView.setOnClickListener(v -> {
                    mItemClickListener.onItemClick(v, position);
                    Intent intent = new Intent(mContext, NoteCreateActivity.class);
                    long id = mData.get(position).getId();
                    intent.putExtra("id", id);
                    mContext.startActivity(intent);
                });

                String date = DataUtils.getDateString(mContext, dateModified);
                mTvDate.setText(date);

                try {

                    if (DataUtils.isCurrentYear(dateModified)) {

                        ContentResolver cv = mContext.getContentResolver();
                        String strTimeFormat = android.provider.Settings.System.getString(cv,
                                android.provider.Settings.System.TIME_12_24);
                        String time;
                        if ("24".equals(strTimeFormat)) {
                            time = DataUtils.currentTime24(dateModified);
                            holder.setVisible(R.id.tv_noon, false);
                        } else {
                            time = DataUtils.currentTime12(dateModified);
                            holder.setVisible(R.id.tv_noon, true);
                        }
                        mTvTime.setText(time);
                        holder.setVisible(R.id.tv_time, true);

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

                boolean isClassifyLast = mPocketInfo.isClassifyLast();
                if (isClassifyLast) {
                    holder.setVisible(R.id.line, false);
                } else {
                    holder.setVisible(R.id.line, true);
                }

                break;

            case ITEM_COMMON_NO_IMAGE:
                TextView search_item_title_no_image = (TextView) convertView.findViewById(R.id.home_item_tile);
                ImageView search_item_audioImage_no_image = (ImageView) convertView.findViewById(R.id.image_audio);
                TextView mTvDate_no_image = (TextView) convertView.findViewById(R.id.tv_date);
                TextView mTvNoon_no_image = (TextView) convertView.findViewById(R.id.tv_noon);
                TextView mTvTime_no_image = (TextView) convertView.findViewById(R.id.tv_time);

                mPocketInfo = mData.get(position);
                long dateModified_no_image = mPocketInfo.getDateModified();

                //show title
                if (mPocketInfo.getSummary().contains(mSearchStr)) {
                    int startIndex = mPocketInfo.getSummary().indexOf(mSearchStr);
                    SpannableString spannableString = new SpannableString(mPocketInfo.getSummary());
                    spannableString.setSpan(new ForegroundColorSpan(0XFF2E76FC),
                            startIndex, startIndex + mSearchStr.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    search_item_title_no_image.setText(spannableString);
                } else {
                    search_item_title_no_image.setText(mPocketInfo.getSummary());
                }

                if (mPocketInfo.isHasAudio()) {
                    search_item_audioImage_no_image.setVisibility(View.VISIBLE);
                    if (null == mPocketInfo.getSummary() || mPocketInfo.getSummary().length() == 0) {
                        search_item_title_no_image.setText(mContext.getString(R.string.audio_note));
                    }
                } else {
                    search_item_audioImage_no_image.setVisibility(View.GONE);
                }

                //item clicked to modify note
                convertView.setOnClickListener(v -> {
                    mItemClickListener.onItemClick(v, position);
                    Intent intent = new Intent(mContext, NoteCreateActivity.class);
                    long id = mData.get(position).getId();
                    intent.putExtra("id", id);
                    mContext.startActivity(intent);
                });

                String date_no_image = DataUtils.getDateString(mContext, dateModified_no_image);
                mTvDate_no_image.setText(date_no_image);

                try {

                    if (DataUtils.isCurrentYear(dateModified_no_image)) {

                        ContentResolver cv = mContext.getContentResolver();
                        String strTimeFormat = android.provider.Settings.System.getString(cv,
                                android.provider.Settings.System.TIME_12_24);
                        String time;
                        if ("24".equals(strTimeFormat)) {
                            time = DataUtils.currentTime24(dateModified_no_image);
                            holder.setVisible(R.id.tv_noon, false);
                        } else {
                            time = DataUtils.currentTime12(dateModified_no_image);
                            holder.setVisible(R.id.tv_noon, true);
                        }
                        mTvTime_no_image.setText(time);
                        holder.setVisible(R.id.tv_time, true);

                        int noon = DataUtils.currentNoon(dateModified_no_image);
                        if (noon == 0) {
                            mTvNoon_no_image.setText(R.string.forenoon);
                        } else {
                            mTvNoon_no_image.setText(R.string.afternoon);

                        }
                    } else {
                        holder.setVisible(R.id.tv_time, false);
                        holder.setVisible(R.id.tv_noon, false);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                boolean isClassifyLastNoInage = mPocketInfo.isClassifyLast();
                if (isClassifyLastNoInage) {
                    holder.setVisible(R.id.line, false);
                } else {
                    holder.setVisible(R.id.line, true);
                }

                break;
            case ITEM_DATE:
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
                if (position == 0) {
                    holder.setVisible(R.id.classify_divide, false);
                } else {
                    holder.setVisible(R.id.classify_divide, true);
                }

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
}
