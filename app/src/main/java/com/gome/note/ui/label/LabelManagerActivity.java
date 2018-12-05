package com.gome.note.ui.label;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gome.note.R;
import com.gome.note.base.BaseActivity;
import com.gome.note.entity.LabelInfo;
import com.gome.note.ui.label.adapter.LabelManageAdapter;
import com.gome.note.ui.label.presenter.LabelManagerPresenter;
import com.gome.note.utils.EditTextClearUtils;
import com.gome.note.utils.SharedPreferencesUtil;
import com.gome.note.view.AlertDialog.CustomAlertDialog;
import com.gome.note.view.FloatActionMenuView.CustomFloatActionMenuView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class LabelManagerActivity extends BaseActivity implements View.OnClickListener,
        LabelManageAdapter.AdapterOnClickListener, TextWatcher, LabelManageAdapter
                .AdapterOnCheckListener, LabelManageAdapter.AdapterOnDeleteCheckListener,
        LabelManageAdapter.ItemOnLongClickListener, CustomFloatActionMenuView.OnFloatActionMenuSelectedListener {

    private Context mContext;
    private LabelManagerActivity mActivity;
    private LabelManagerPresenter presenter;

    private ImageView mIvMenuCancel, mIvMenuBack;
    private TextView mTvComplete, tvLabelAsHave, mTvAllSelected, mTvMainTitle;
    private CustomFloatActionMenuView mFlDeleteLabel;
    private CustomFloatActionMenuView mIbEditLabel;
    private RecyclerView mRvLabelList;
    private LinearLayoutManager mLayoutManager;
    private EditText etInput;
    private Button dialogNegative, dialogPositive;
    private CustomAlertDialog createDialogView;

    private ArrayList<LabelInfo> itemLabelInfos = null;
    private ArrayList<LabelInfo> clickItem;
    private ArrayList<LabelInfo> labelInfos = new ArrayList<>();
    private ArrayList<LabelInfo> itemLabelsDeleteChecked = null;
    private ArrayList<LabelInfo> saveitemLabelsDeleteChecked = null;
    private LabelManageAdapter.AdapterOnClickListener mOnClickListener = null;
    private LabelManageAdapter.AdapterOnCheckListener mOnCheckListener = null;
    private LabelManageAdapter.AdapterOnDeleteCheckListener mOnDeleteCheckListener = null;
    private LabelManageAdapter.ItemOnLongClickListener mItemOnLongClickListener = null;
    private LabelManageAdapter mLabelManageAdapter;
    private EditTextClearUtils editTextClearUtils;
    private Intent mIntent;

    private static final String ZERO = "0";
    private boolean isEditStatus;
    private boolean isAllChecked;
    private long mId;
    private int type;
    private int TYPE_ADD_LABELS = 1;
    private boolean isLongClick;
    private boolean isClickItem;
    private TextView mTvLabelLimitWords;
    private TextView mIvLeftTitle;
    private boolean isHasStickLabel;
    private CheckBox mCbAllSelected;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_manager);
        mActivity = this;
        mContext = this;
        mIntent = getIntent();
        mId = mIntent.getLongExtra("id", -1);
        type = mIntent.getIntExtra("type", -1);
        clickItem = mIntent.getParcelableArrayListExtra("clickItem");
        isClickItem = mIntent.getBooleanExtra("isClickItem", false);
        itemLabelInfos = mIntent.getParcelableArrayListExtra("itemLabelInfos");
        if (null == itemLabelInfos) {
            itemLabelInfos = new ArrayList<>();
        } else {
            for (int i = 0; i < itemLabelInfos.size(); i++) {
                itemLabelInfos.get(i).setChecked(true);
                if (itemLabelInfos.get(i).isStick()) {
                    isHasStickLabel = true;
                }
            }
        }
        mOnClickListener = this;
        mOnCheckListener = this;
        mOnDeleteCheckListener = this;
        mItemOnLongClickListener = this;


        initAppBar();
        initView();
        initListener();
        initPresenter();
    }


    private void initAppBar() {

        mTvMainTitle = (TextView) findViewById(R.id.tv_main_title);
        mIvMenuBack = (ImageView) findViewById(R.id.iv_menu_back);

        mIvMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        VectorDrawable vectorDrawableMenuBack = (VectorDrawable) mIvMenuBack.getDrawable();
        vectorDrawableMenuBack.setTint(getResources().getColor(R.color.common_title_bar_icon_color));
        mIvMenuBack.setImageDrawable(vectorDrawableMenuBack);
    }

    @Override
    public void initPresenter() {
        presenter = new LabelManagerPresenter(mContext, mActivity);
    }

    private void initView() {

        mRvLabelList = (RecyclerView) findViewById(R.id.rv_label_list);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRvLabelList.setLayoutManager(mLayoutManager);


        mLabelManageAdapter = new LabelManageAdapter(mContext, labelInfos, mOnClickListener,
                mOnCheckListener, mOnDeleteCheckListener, mItemOnLongClickListener);
        mLabelManageAdapter.setType(type);
        mLabelManageAdapter.setID(mId);
        mRvLabelList.setAdapter(mLabelManageAdapter);

        mIbEditLabel = (CustomFloatActionMenuView) findViewById(R.id.ib_edit_label_menu);
        mIbEditLabel.setOnFloatActionMenuSelectedListener(this);
        mFlDeleteLabel = (CustomFloatActionMenuView) findViewById(R.id.fl_delete_label_menu);
        mFlDeleteLabel.setOnFloatActionMenuSelectedListener(this);
        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
        mTvAllSelected = (TextView) findViewById(R.id.tv_all_selected);
        mCbAllSelected = (CheckBox) findViewById(R.id.cb_all_selected);
        mIvMenuCancel = (ImageView) findViewById(R.id.iv_menu_cancel);
        //mIvMenuBack = (ImageView) findViewById(R.id.iv_menu_back);
        mTvComplete = (TextView) findViewById(R.id.tv_complete);
        mIvLeftTitle = (TextView) findViewById(R.id.iv_left_title);


        editTextClearUtils = new EditTextClearUtils();

        if (type != TYPE_ADD_LABELS) {
            mTvComplete.setVisibility(View.GONE);
            mIvLeftTitle.setText(getString(R.string.my_label_title));
            mIbEditLabel.setMenuItemsVisible(R.id.tv_compile_labels);
        } else {
            mTvComplete.setVisibility(View.VISIBLE);
            mIvLeftTitle.setText(getString(R.string.add_label));
            mIbEditLabel.setMenuItemsInVisible(R.id.tv_compile_labels);
        }

        VectorDrawable vectorDrawableComplete = (VectorDrawable) mTvComplete.getBackground();
        vectorDrawableComplete.setTint(getResources().getColor(R.color.common_title_bar_icon_color));
        mTvComplete.setBackground(vectorDrawableComplete);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (null != mFlDeleteLabel) {
            mFlDeleteLabel.setMenuItemsEnable(R.id.tv_create_labels);
        }
        initValue();
    }

    private void initValue() {
        presenter.queryLablesList();
    }

    private void initListener() {
        mIbEditLabel.setOnClickListener(this);
        //mTvAllSelected.setOnClickListener(this);
        mCbAllSelected.setOnClickListener(this);
        mIvMenuCancel.setOnClickListener(this);
        mTvComplete.setOnClickListener(this);

        mFlDeleteLabel.setSoundEffectsEnabled(false);
        mFlDeleteLabel.setOnClickListener(this);
    }

    public void setLabelInfos(ArrayList<LabelInfo> labelInfos) {

        if (null != labelInfos) {
            this.labelInfos = labelInfos;
            itemLabelsDeleteChecked = labelInfos;

            if (isLongClick) {
                if (itemLabelsDeleteChecked.size() <= 1) {
                    //mTvAllSelected.setVisibility(View.GONE);
                    mCbAllSelected.setVisibility(View.GONE);
                    mTvMainTitle.setVisibility(View.GONE);
                    mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                } else {
                    //mTvAllSelected.setVisibility(View.VISIBLE);
                    mTvMainTitle.setVisibility(View.VISIBLE);
                    //mTvAllSelected.setText(getString(R.string.all_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                    mCbAllSelected.setVisibility(View.VISIBLE);
                    if (!isAllChecked) {
                        mCbAllSelected.setChecked(false);
                    }
                    String text = getString(R.string.is_checked) + ZERO + getString(R.string.item);
                    //mTvMainTitle.setText(text);
                    mTvMainTitle.setText(R.string.please_check_item);
                    mFlDeleteLabel.setMenuItemsEnable(R.id.tv_delete_labels);
                }
            } else {
                mCbAllSelected.setChecked(false);
            }
            showSelectLables();
        }

    }


    //from onPause to onResume , set label check status
    private void showSelectLables() {
        if (isLongClick && !isEditStatus && null != saveitemLabelsDeleteChecked && saveitemLabelsDeleteChecked.size() > 0
                && null != itemLabelsDeleteChecked && itemLabelsDeleteChecked.size() > 0) {
            int count = 0;

            count = getLongClickSelectCount(count);

            if (saveitemLabelsDeleteChecked.size() != itemLabelsDeleteChecked.size()) {
                isAllChecked = false;
                //mTvAllSelected.setText(getString(R.string.all_selected));
                //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                mCbAllSelected.setChecked(false);
            } else {
                if (isAllChecked) {
                    //mTvAllSelected.setText(getString(R.string.all_no_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
                    mCbAllSelected.setChecked(true);
                }
            }
            if (count == 0) {
                mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                //mTvMainTitle.setVisibility(View.GONE);
                mTvMainTitle.setText(R.string.please_check_item);
            } else {
                if (count == 1) {
                    String text = getString(R.string.is_checked) + count + getString(R.string.item);
                    mTvMainTitle.setText(text);
                } else {
                    String text = getString(R.string.is_checked) + count + getString(R.string.items);
                    mTvMainTitle.setText(text);
                }
            }

            mLabelManageAdapter.setLabelInfos(itemLabelsDeleteChecked);
            mLabelManageAdapter.notifyDataSetChanged();
        } else {
            int selectedCount = 0;
            //set checked status
            selectedCount = getEditClickSelectCount(selectedCount);

            if (type == 1) {
                mTvMainTitle.setVisibility(View.GONE);
                if (selectedCount <= 0) {
                    //mTvMainTitle.setVisibility(View.GONE);
                    mTvMainTitle.setText(R.string.please_check_item);
                } else {
                    if (selectedCount == 1) {
                        String text = getString(R.string.is_checked) + selectedCount + getString(R.string.item);
                        mTvMainTitle.setText(text);
                    } else {
                        String text = getString(R.string.is_checked) + selectedCount + getString(R.string.items);
                        mTvMainTitle.setText(text);
                    }
                    //mTvMainTitle.setVisibility(View.VISIBLE);
                }
            }
            mLabelManageAdapter.setLabelInfos(labelInfos);
            mLabelManageAdapter.notifyDataSetChanged();
        }

    }

    private int getLongClickSelectCount(int count) {
        for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {

            long labelId = itemLabelsDeleteChecked.get(i).getId();
            for (int j = 0; j < saveitemLabelsDeleteChecked.size(); j++) {
                long tempLabelId = saveitemLabelsDeleteChecked.get(j).getId();
                if (labelId == tempLabelId) {
                    itemLabelsDeleteChecked.get(i).setChecked(saveitemLabelsDeleteChecked.get(j).getIsChecked());
                    if (saveitemLabelsDeleteChecked.get(j).getIsChecked()) {
                        count = count + 1;
                    }
                }
            }
        }
        return count;
    }

    private int getEditClickSelectCount(int selectedCount) {
        if (null != itemLabelInfos && itemLabelInfos.size() > 0) {
            for (int i = 0; i < itemLabelInfos.size(); i++) {
                if (null != itemLabelInfos.get(i)) {
                    String labelName = itemLabelInfos.get(i).getTitle();
                    if (!labelName.equals(mContext.getString(R.string.put_top))) {
                        long id = itemLabelInfos.get(i).getId();
                        for (int j = 0; j < labelInfos.size(); j++) {
                            long mid = labelInfos.get(j).getId();
                            if (mid == id) {
                                labelInfos.get(j).setChecked(itemLabelInfos.get(i).getIsChecked());
                                //selectedCount = selectedCount + 1;
                            }
                        }
                    }
                }
            }
        }
        return selectedCount;
    }


    @Override
    public void itemOnLongClick(View view, int postion) {
        if (type != TYPE_ADD_LABELS && !isLongClick) {
            mIvLeftTitle.setVisibility(View.GONE);
            isLongClick = true;
            for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                itemLabelsDeleteChecked.get(i).setChecked(false);
            }
            if (postion != 0) {
                itemLabelsDeleteChecked.get(postion).setChecked(true);
            }
            if (!isEditStatus && !(type == 1)) {
                mIbEditLabel.setVisibility(View.GONE);
                mFlDeleteLabel.setVisibility(View.VISIBLE);
                //mTvAllSelected.setVisibility(View.VISIBLE);
                mCbAllSelected.setVisibility(View.VISIBLE);
                mIvMenuBack.setVisibility(View.GONE);
                mIvMenuCancel.setVisibility(View.VISIBLE);
                mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
                mLabelManageAdapter.setShowDelete(true);
                mLabelManageAdapter.notifyDataSetChanged();

                if (itemLabelsDeleteChecked.size() <= 1) {
                    //mTvAllSelected.setVisibility(View.GONE);
                    mCbAllSelected.setVisibility(View.GONE);
                    //mTvMainTitle.setVisibility(View.GONE);
                    mTvMainTitle.setText(R.string.please_check_item);
                    mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                } else {
                    //mTvAllSelected.setVisibility(View.VISIBLE);
                    mTvMainTitle.setVisibility(View.VISIBLE);
                    //mTvAllSelected.setText(getString(R.string.all_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                    mCbAllSelected.setVisibility(View.VISIBLE);
                    if (itemLabelsDeleteChecked.size() <= 2) {
                        mCbAllSelected.setChecked(true);
                        isAllChecked = true;
                    } else {
                        mCbAllSelected.setChecked(false);
                        isAllChecked = false;
                    }


                    if (postion == 0) {
//                        String text = getString(R.string.is_checked) + "0" + getString(R.string.item);
//                        mTvMainTitle.setText(text);
//                        mTvMainTitle.setVisibility(View.GONE);
                        mTvMainTitle.setText(R.string.please_check_item);
                        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
                    } else {
                        String text = getString(R.string.is_checked) + "1" + getString(R.string.item);
                        mTvMainTitle.setText(text);
                        mTvMainTitle.setVisibility(View.VISIBLE);
                        mFlDeleteLabel.setMenuItemsEnable(R.id.tv_delete_labels);
                        mFlDeleteLabel.setMenuItemsEnable(R.id.tv_edit_label);
                    }
                }

            }
        }
    }

    @Override
    public void itemOnClick(View view, int postion) {
        //into quick query
        if (isClickItem && mIvMenuCancel.getVisibility() == View.GONE) {
            String labelName = labelInfos.get(postion).getTitle();
            Intent intent = new Intent(mContext, TagSearchActivity.class);
            intent.putExtra("labelName", labelName);
            startActivity(intent);
        } else if (((mIvMenuCancel.getVisibility() == View.VISIBLE) || itemLabelInfos != null)) {
            if (mIvMenuCancel.getVisibility() == View.VISIBLE && postion == 0) {
                return;
            }
            boolean isChangeText = true;
            int countTemp = 0;
            itemLabelsDeleteChecked.get(postion).setChecked(!itemLabelsDeleteChecked.get(postion).getIsChecked());
            for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                if (itemLabelsDeleteChecked.get(i).getTitle().equals(getString(R.string.put_top)) && type != 1) {
                    continue;
                }
                if (!itemLabelsDeleteChecked.get(i).getIsChecked()) {
                    isChangeText = false;
                    isAllChecked = false;
                    //mTvAllSelected.setText(getString(R.string.all_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                    mCbAllSelected.setChecked(false);
                    countTemp++;
                }
            }
            int count = itemLabelsDeleteChecked.size() - countTemp - 1;
            if (type == 1) {
                count = itemLabelsDeleteChecked.size() - countTemp;
            }
            if (count >= 1) {
                String text = getString(R.string.is_checked) + count + getString(R.string.item);
                mTvMainTitle.setText(text);
                mFlDeleteLabel.setMenuItemsEnable(R.id.tv_delete_labels);
                if (type == TYPE_ADD_LABELS) {
                    mTvMainTitle.setVisibility(View.GONE);
                } else {
                    mTvMainTitle.setVisibility(View.VISIBLE);
                }
                //mTvMainTitle.setVisibility(View.VISIBLE);
            } else {
                mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                //mTvMainTitle.setVisibility(View.GONE);
                if (type == TYPE_ADD_LABELS) {
                    mTvMainTitle.setVisibility(View.GONE);
                } else {
                    mTvMainTitle.setVisibility(View.VISIBLE);
                }
                mTvMainTitle.setText(R.string.please_check_item);
            }

            if (isChangeText) {
                isAllChecked = true;
                //mTvAllSelected.setText(getString(R.string.all_no_selected));
                //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
                mCbAllSelected.setChecked(true);
                if (type == 1) {
                    count = itemLabelsDeleteChecked.size();
                } else {
                    count = itemLabelsDeleteChecked.size() - 1;
                }

                if (count <= 0) {
                    //mTvMainTitle.setVisibility(View.GONE);
                    mTvMainTitle.setText(R.string.please_check_item);
                } else {
                    if (count == 1) {
                        String changeText = getString(R.string.is_checked) + count + "" + getString(R.string.item);
                        mTvMainTitle.setText(changeText);
                    } else {
                        String changeText = getString(R.string.is_checked) + count + "" + getString(R.string.items);
                        mTvMainTitle.setText(changeText);
                    }
                    mTvMainTitle.setVisibility(View.VISIBLE);
                }
            }
            int editCount = 0;
            for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                if (itemLabelsDeleteChecked.get(i).getIsChecked()) {
                    editCount = editCount + 1;
                }
            }
            if (editCount == labelInfos.size() - 1) {
                //mTvAllSelected.setText(getString(R.string.all_no_selected));
                //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
                mCbAllSelected.setChecked(true);
            } else {
                //mTvAllSelected.setText(getString(R.string.all_selected));
                //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                mCbAllSelected.setChecked(false);
            }

            if (editCount != 1) {
                //color gray
                mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
            } else {
                //color blue
                mFlDeleteLabel.setMenuItemsEnable(R.id.tv_edit_label);
            }

            mLabelManageAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_edit_label:

                for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                    if (itemLabelsDeleteChecked.get(i).getIsChecked()) {
                        String labelName = itemLabelsDeleteChecked.get(i).getTitle().trim();
                        long labelId = labelInfos.get(i).getId();
                        createDialog(labelName, labelId);
                    }

                }


                break;


            case R.id.tv_create_labels:
                //create new label
                createDialog("", 0);

                break;

            case R.id.tv_delete_labels:
                //delete labels
                boolean isHasChecked = false;
                for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                    if (itemLabelsDeleteChecked.get(i).getIsChecked()) {
                        isHasChecked = true;
                    }

                }

                if (isHasChecked) {
                    boolean isNotShowDiag = SharedPreferencesUtil.getBooleanValue(mContext.getApplicationContext(),
                            SharedPreferencesUtil.DELETE_LABEL_DIALOG_NOT_ALERT, false);
                    if (!isNotShowDiag) {
                        removeLabelDialog(0, true);
                    } else {
                        removeLabel(0, true);
                    }
                }

                break;

            case R.id.tv_all_selected:
                //all selected  or not all selected
                if (isAllChecked) {
                    isAllChecked = false;
                    //mTvAllSelected.setText(getString(R.string.all_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                    mCbAllSelected.setChecked(false);
                    if (!isEditStatus) {
                        for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                            if (!itemLabelsDeleteChecked.get(i).isStick()) {
                                itemLabelsDeleteChecked.get(i).setChecked(false);
                            }
                        }
                    }
                    mTvMainTitle.setText(R.string.please_check_item);
                    mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
                    mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                } else {
                    isAllChecked = true;
                    if (!isEditStatus) {
                        for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                            if (!itemLabelsDeleteChecked.get(i).isStick()) {
                                itemLabelsDeleteChecked.get(i).setChecked(true);
                            }
                        }
                    }
                    //mTvAllSelected.setText(getString(R.string.all_no_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
                    mCbAllSelected.setChecked(true);
                    int count = itemLabelsDeleteChecked.size() - 1;

                    if (count <= 0) {
                        //mTvMainTitle.setVisibility(View.GONE);
                        mTvMainTitle.setText(R.string.please_check_item);
                    } else {
                        if (count == 1) {
                            String changeText = getString(R.string.is_checked) + count + getString(R.string.item);
                            mTvMainTitle.setText(changeText);
                        } else {
                            String changeText = getString(R.string.is_checked) + count + getString(R.string.items);
                            mTvMainTitle.setText(changeText);
                        }
                        mTvMainTitle.setVisibility(View.VISIBLE);
                    }

                    if (itemLabelsDeleteChecked.size() - 1 == 1) {
                        mFlDeleteLabel.setMenuItemsEnable(R.id.tv_edit_label);
                    } else {
                        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);

                    }
                    if (itemLabelsDeleteChecked.size() - 1 > 0) {
                        mFlDeleteLabel.setMenuItemsEnable(R.id.tv_delete_labels);
                    } else {
                        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                    }

                }
                mLabelManageAdapter.setShowDelete(true);
                mLabelManageAdapter.setLabelInfos(itemLabelsDeleteChecked);
                mLabelManageAdapter.notifyDataSetChanged();

                break;


            case R.id.cb_all_selected:
                //all selected  or not all selected
                if (isAllChecked) {
                    isAllChecked = false;
                    //mTvAllSelected.setText(getString(R.string.all_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                    mCbAllSelected.setChecked(false);
                    if (!isEditStatus) {
                        for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                            if (!itemLabelsDeleteChecked.get(i).isStick()) {
                                itemLabelsDeleteChecked.get(i).setChecked(false);
                            }
                        }
                    }
                    mTvMainTitle.setText(R.string.please_check_item);
                    mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
                    mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                } else {
                    isAllChecked = true;
                    if (!isEditStatus) {
                        for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                            if (!itemLabelsDeleteChecked.get(i).isStick()) {
                                itemLabelsDeleteChecked.get(i).setChecked(true);
                            }
                        }
                    }
                    //mTvAllSelected.setText(getString(R.string.all_no_selected));
                    //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
                    mCbAllSelected.setChecked(true);
                    int count = itemLabelsDeleteChecked.size() - 1;

                    if (count <= 0) {
                        //mTvMainTitle.setVisibility(View.GONE);
                        mTvMainTitle.setText(R.string.please_check_item);
                    } else {
                        if (count == 1) {
                            String changeText = getString(R.string.is_checked) + count + getString(R.string.item);
                            mTvMainTitle.setText(changeText);
                        } else {
                            String changeText = getString(R.string.is_checked) + count + getString(R.string.items);
                            mTvMainTitle.setText(changeText);
                        }
                        mTvMainTitle.setVisibility(View.VISIBLE);
                    }

                    if (itemLabelsDeleteChecked.size() - 1 == 1) {
                        mFlDeleteLabel.setMenuItemsEnable(R.id.tv_edit_label);
                    } else {
                        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);

                    }
                    if (itemLabelsDeleteChecked.size() - 1 > 0) {
                        mFlDeleteLabel.setMenuItemsEnable(R.id.tv_delete_labels);
                    } else {
                        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                    }

                }
                mLabelManageAdapter.setShowDelete(true);
                mLabelManageAdapter.setLabelInfos(itemLabelsDeleteChecked);
                mLabelManageAdapter.notifyDataSetChanged();

                break;

            case R.id.iv_menu_cancel:
                //  exit all selected

                for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                    itemLabelsDeleteChecked.get(i).setChecked(false);
                }
                exitEditMode();
                break;

            case R.id.tv_complete:
                saveLabels();
                break;

        }
    }

    private void createDialog(final String labelName, final long labelId) {
        if (null != createDialogView && createDialogView.isShowing()) {

            return;
        }
        String voiceMemoLabelName = "";
        LayoutInflater factory = LayoutInflater.from(mContext);
        View dialogEdittext = factory.inflate(R.layout.item_dialog_edittext, null);
        etInput = (EditText) dialogEdittext.findViewById(R.id.et_input);
        dialogNegative = (Button) dialogEdittext.findViewById(R.id.dialog_btn_negative);
        dialogPositive = (Button) dialogEdittext.findViewById(R.id.dialog_btn_positive);
        if (LabelManageUtils.isVoiceMemosLable(mContext, labelName)) {
            voiceMemoLabelName = mContext.getString(R.string.lable_type_record);
            etInput.setText(voiceMemoLabelName);
            etInput.setSelection(0, voiceMemoLabelName.length());
        } else {
            etInput.setText(labelName);
            etInput.setSelection(0, labelName.length());
        }
        etInput.addTextChangedListener(this);
        //where edit labelname ,clear icon can visible
        if (null != labelName && labelName.length() > 0) {
            editTextClearUtils.setClearIconVisible(mContext, true, etInput);
            editTextClearUtils.drawRigthClick(etInput);
        } else {
            editTextClearUtils.setClearIconVisible(mContext, false, etInput);
        }
        tvLabelAsHave = (TextView) dialogEdittext.findViewById(R.id.tv_label_as_have);
        mTvLabelLimitWords = (TextView) dialogEdittext.findViewById(R.id.tv_label_limit_words);
        CustomAlertDialog.Builder alertDialog = new CustomAlertDialog.Builder(this);

        alertDialog.setView(dialogEdittext);
        alertDialog.setTitle((null != labelName && labelName.length() > 0) ? R.string.edit_label_1 : R
                .string.create_label_1);
        createDialogView = alertDialog.create();
        // where dialog is show  ,the keyword is show
        createDialogView.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.showSoftInput(etInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        createDialogView.show();
        createDialogView.setCanceledOnTouchOutside(false);
        Window window = createDialogView.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialogPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etInput.getText().toString().trim();
                try {
                    int num = 0;
                    num = input.toString().getBytes("GBK").length;
                    if (num > 12) {
                        tvLabelAsHave.setVisibility(View.VISIBLE);
                        tvLabelAsHave.setText(R.string.most_input_six_word);
                        return;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                if (null == input || input.length() == 0) {
                    tvLabelAsHave.setVisibility(View.VISIBLE);
                    mTvLabelLimitWords.setVisibility(View.GONE);
                    tvLabelAsHave.setText(R.string.label_name_not_null);
                    return;
                }
                if (presenter.isHasSameLabel(input, labelInfos)) {
                    tvLabelAsHave.setVisibility(View.VISIBLE);
                    mTvLabelLimitWords.setVisibility(View.GONE);
                    tvLabelAsHave.setText(R.string.label_as_have);
                    return;
                } else {
                    tvLabelAsHave.setVisibility(View.GONE);
                    mTvLabelLimitWords.setVisibility(View.GONE);
                    saveCheckedLable();
                    if (null != labelName && labelName.length() > 0) {
                        presenter.updataLabel(input, labelId);
                    } else {
                        presenter.createLabel(input);
                    }
                    createDialogView.dismiss();
                    exitEditMode();
                }
            }
        });

        dialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogView.dismiss();
            }
        });

        //listener backkey
    }

    private void removeLabelDialog(final int postion, final boolean isDeleteAll) {

        LayoutInflater factory = LayoutInflater.from(mContext);
        final View dialogCheckbox = factory.inflate(R.layout.item_dialog_checkbox, null);
        final CheckBox checkboxs = (CheckBox) dialogCheckbox.findViewById(R.id.checkbox);
        final TextView tvCheckString = (TextView) dialogCheckbox.findViewById(R.id.tv_check_string);
        Button btn_confirm = (Button) dialogCheckbox.findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) dialogCheckbox.findViewById(R.id.btn_cancel);

        tvCheckString.setText(R.string.even_not_alert);

        final CustomAlertDialog.Builder alertDialog = new CustomAlertDialog.Builder(this)
                .setView(dialogCheckbox)
                .setTitle(R.string.delete_label)
                .setMessage(R.string.delete_label_dialog)
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (i == KeyEvent.KEYCODE_BACK
                                && keyEvent.getRepeatCount() == 0) {
                            dialogInterface.cancel();
                            return true;
                        }
                        return false;
                    }
                });
        final CustomAlertDialog tempDialog = alertDialog.create();
        tempDialog.show();

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxs.isChecked()) {
                    //no alert
                    SharedPreferencesUtil.saveBooelanValue(mContext.getApplicationContext(),
                            SharedPreferencesUtil.DELETE_LABEL_DIALOG_NOT_ALERT, true);
                } else {
                    //have alert

                }
                if (isDeleteAll) {
                    removeLabel(postion, isDeleteAll);
                } else {
                    removeLabel(postion, isDeleteAll);
                }

                tempDialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempDialog.cancel();
            }
        });
    }

    private void removeLabel(int postion, boolean isDeleteAll) {

        if (isDeleteAll) {
            presenter.deleteBatchLabel("", itemLabelsDeleteChecked);
        } else {

            String labelName = labelInfos.get(postion).getTitle().trim();
            boolean isStick = labelInfos.get(postion).isStick();
            if (isStick) {
                Toast.makeText(mContext, getString(R.string.stick_label_can_not_delete), Toast
                        .LENGTH_SHORT).show();
                return;
            }

            saveCheckedLable();
            long labelId = labelInfos.get(postion).getId();
            presenter.deleteLabel(labelName, labelId);
        }

        exitEditMode();

    }

    private void exitEditMode() {
        isLongClick = false;
        isAllChecked = false;

        mLabelManageAdapter.setShowDelete(false);
        mLabelManageAdapter.setLabelInfos(labelInfos);
        mLabelManageAdapter.notifyDataSetChanged();

        mIbEditLabel.setVisibility(View.VISIBLE);
        mFlDeleteLabel.setVisibility(View.GONE);
        //mTvAllSelected.setVisibility(View.GONE);
        mCbAllSelected.setVisibility(View.GONE);
        mCbAllSelected.setChecked(false);
        mCbAllSelected.jumpDrawablesToCurrentState();
        mIvMenuBack.setVisibility(View.VISIBLE);
        mIvMenuCancel.setVisibility(View.GONE);
        if (type != 1) {
            mTvMainTitle.setVisibility(View.GONE);
        }
        mIvLeftTitle.setVisibility(View.VISIBLE);
    }


    private void saveLabels() {
        saveCheckedLable();
        presenter.updateContentLabels(mId, itemLabelInfos);

    }

    private void saveCheckedLable() {
        if (null != itemLabelInfos) {
            itemLabelInfos.clear();
            for (int i = 0; i < labelInfos.size(); i++) {
                if (labelInfos.get(i).isStick()) {
                    if (isHasStickLabel) {
                        itemLabelInfos.add(labelInfos.get(i));
                    }
                } else {
                    if (labelInfos.get(i).getIsChecked()) {
                        itemLabelInfos.add(labelInfos.get(i));
                    }
                }
            }
        }
    }

    public void selectView(boolean isChecked, int postion) {
        boolean isChangeText = true;
        int countTemp = 0;
        if (isChecked) {
            itemLabelsDeleteChecked.get(postion).setChecked(true);
        } else {
            itemLabelsDeleteChecked.get(postion).setChecked(false);
        }

        for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
            if (itemLabelsDeleteChecked.get(i).getTitle().equals(getString(R.string.put_top)) && type != 1) {
                continue;
            }
            if (!itemLabelsDeleteChecked.get(i).getIsChecked()) {
                isChangeText = false;
                isAllChecked = false;
                //mTvAllSelected.setText(getString(R.string.all_selected));
                //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                mCbAllSelected.setChecked(false);
                countTemp++;
            }

        }
        int count = itemLabelsDeleteChecked.size() - countTemp - 1;
        if (type == 1) {
            count = itemLabelsDeleteChecked.size() - countTemp;
        }


        if (count >= 1) {
            if (count == 1) {
                if (type != 1) {
                    String text = getString(R.string.is_checked) + count + getString(R.string.item);
                    mTvMainTitle.setText(text);
                }
            } else {
                if (type != 1) {
                    String text = getString(R.string.is_checked) + count + getString(R.string.items);
                    mTvMainTitle.setText(text);
                }
            }
            if (type != 1) {
                mTvMainTitle.setVisibility(View.VISIBLE);
            }
            mFlDeleteLabel.setMenuItemsEnable(R.id.tv_delete_labels);
        } else {
            //mTvMainTitle.setVisibility(View.GONE);
            if (type != 1) {
                mTvMainTitle.setText(R.string.please_check_item);
                mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
            }
        }

        if (isChangeText) {
            isAllChecked = true;
            //mTvAllSelected.setText(getString(R.string.all_no_selected));
            //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
            mCbAllSelected.setChecked(true);
            count = itemLabelsDeleteChecked.size() - 1;
            if (type == 1) {
                count = itemLabelsDeleteChecked.size();
            }
            if (count <= 0) {
                //mTvMainTitle.setVisibility(View.GONE);
                mTvMainTitle.setText(R.string.please_check_item);
            } else {
                if (count == 1) {
                    String changeText = getString(R.string.is_checked) + count + "" + getString(R.string.item);
                    mTvMainTitle.setText(changeText);
                } else {
                    String changeText = getString(R.string.is_checked) + count + "" + getString(R.string.items);
                    mTvMainTitle.setText(changeText);
                }
                mTvMainTitle.setVisibility(View.VISIBLE);
            }

        }
        int editCount = 0;
        for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
            if (itemLabelsDeleteChecked.get(i).getIsChecked()) {
                editCount = editCount + 1;
            }
        }
        if (editCount == labelInfos.size() - 1) {
            //mTvAllSelected.setText(getString(R.string.all_no_selected));
            //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
            mCbAllSelected.setChecked(true);
        } else {
            //mTvAllSelected.setText(getString(R.string.all_selected));
            //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
            mCbAllSelected.setChecked(false);
        }
        if (editCount != 1) {

            mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
        } else {
            mFlDeleteLabel.setMenuItemsEnable(R.id.tv_edit_label);

        }
    }

    @Override
    public void adapterOnCheck(View view, boolean isChecked, int postion) {
        if (isChecked) {
            labelInfos.get(postion).setChecked(true);
        } else {
            labelInfos.get(postion).setChecked(false);
        }
        selectView(isChecked, postion);
    }

    @Override
    public void adapterOnDeleteCheck(View view, boolean isChecked, int postion) {
        selectView(isChecked, postion);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            editTextClearUtils.setClearIconVisible(mContext, true, etInput);
            editTextClearUtils.drawRigthClick(etInput);

            try {
                int num = 0;
                num = s.toString().getBytes("GBK").length;
                if (num > 12) {
                    tvLabelAsHave.setVisibility(View.VISIBLE);
                    tvLabelAsHave.setText(R.string.most_input_six_word);

                } else {
                    tvLabelAsHave.setVisibility(View.GONE);
                    tvLabelAsHave.setText("");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            editTextClearUtils.setClearIconVisible(mContext, false, etInput);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)) {
            tvLabelAsHave.setVisibility(View.GONE);
            mTvLabelLimitWords.setVisibility(View.GONE);
        } else {
            if (presenter.isHasSameLabel(s.toString(), labelInfos)) {
                tvLabelAsHave.setVisibility(View.GONE);
                mTvLabelLimitWords.setVisibility(View.GONE);
                tvLabelAsHave.setText(R.string.label_as_have);
            } else {
                try {
                    int num = 0;
                    num = s.toString().getBytes("GBK").length;
                    if (num > 12) {
                        tvLabelAsHave.setVisibility(View.VISIBLE);
                        tvLabelAsHave.setText(R.string.most_input_six_word);
                    } else {
                        tvLabelAsHave.setVisibility(View.GONE);
                        tvLabelAsHave.setText("");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mTvLabelLimitWords.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (mIvMenuCancel.getVisibility() == View.VISIBLE) {
            exitEditMode();
            return;
        }

        //saveLabels();
        super.onBackPressed();
    }


    @Override
    public boolean onFloatActionItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.tv_create_labels:
                if (null != labelInfos && labelInfos.size() > 30) {
                    //Toast.makeText(mContext, getString(R.string.labels_count_not_exceed_thirty), Toast.LENGTH_SHORT).show();
                    if (null != toast) {
                        toast.setText(getString(R.string.labels_count_not_exceed_thirty));
                    } else {
                        toast = Toast.makeText(mContext,
                                getString(R.string.labels_count_not_exceed_thirty),
                                Toast.LENGTH_SHORT);
                    }
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    createDialog("", 0);
                }


                break;
            case R.id.tv_delete_labels:
                //delete labels
                boolean isHasChecked = false;
                for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                    if (itemLabelsDeleteChecked.get(i).getIsChecked()) {
                        isHasChecked = true;
                    }
                }

                if (isHasChecked) {
                    boolean isNotShowDiag = SharedPreferencesUtil.getBooleanValue(mContext.getApplicationContext(),
                            SharedPreferencesUtil.DELETE_LABEL_DIALOG_NOT_ALERT, false);
                    if (!isNotShowDiag) {
                        removeLabelDialog(0, true);
                    } else {
                        removeLabel(0, true);
                    }
                }

                break;
            case R.id.tv_edit_label:

                for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                    if (itemLabelsDeleteChecked.get(i).getIsChecked()) {
                        String labelName = itemLabelsDeleteChecked.get(i).getTitle().trim();
                        long labelId = labelInfos.get(i).getId();
                        createDialog(labelName, labelId);
                    }
                }
                break;
            case R.id.tv_compile_labels:

                if (type != TYPE_ADD_LABELS && !isLongClick) {

                    isLongClick = true;
                    for (int i = 0; i < itemLabelsDeleteChecked.size(); i++) {
                        itemLabelsDeleteChecked.get(i).setChecked(false);
                    }

                    if (!isEditStatus && !(type == 1)) {
                        mIbEditLabel.setVisibility(View.GONE);
                        mFlDeleteLabel.setVisibility(View.VISIBLE);
                        //mTvAllSelected.setVisibility(View.VISIBLE);
                        mCbAllSelected.setVisibility(View.VISIBLE);
                        mIvMenuBack.setVisibility(View.GONE);
                        mIvMenuCancel.setVisibility(View.VISIBLE);
                        mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
                        mLabelManageAdapter.setShowDelete(true);
                        mLabelManageAdapter.notifyDataSetChanged();

                        if (itemLabelsDeleteChecked.size() <= 1) {
                            //mTvAllSelected.setVisibility(View.GONE);
                            mCbAllSelected.setVisibility(View.GONE);
                            mTvMainTitle.setVisibility(View.GONE);
                            mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                        } else {
                            //mTvAllSelected.setVisibility(View.VISIBLE);
                            mCbAllSelected.setVisibility(View.VISIBLE);
                            mTvMainTitle.setVisibility(View.VISIBLE);
                            String text = getString(R.string.is_checked) + ZERO + getString(R.string.item);
                            mTvMainTitle.setText(R.string.please_check_item);
                            //mTvMainTitle.setText(text);
                            mFlDeleteLabel.setMenuItemsDisable(R.id.tv_delete_labels);
                            mFlDeleteLabel.setMenuItemsDisable(R.id.tv_edit_label);
                            //mTvAllSelected.setText(getString(R.string.all_selected));
                            //mTvAllSelected.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                            mCbAllSelected.setChecked(false);
                            mIvLeftTitle.setVisibility(View.GONE);
                        }
                    }
                }
                break;
        }

        return false;
    }

    @Override
    public void adapterOnClick(View view, int postion) {


    }

    public void showError() {
        Toast.makeText(mContext, "labels erroe", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != createDialogView && createDialogView.isShowing()) {
            createDialogView.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        presenter.setActivityNull();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != saveitemLabelsDeleteChecked) {
            saveitemLabelsDeleteChecked.clear();
        }
        //save LongClick label checked status
        saveitemLabelsDeleteChecked = itemLabelsDeleteChecked;
        itemLabelInfos = saveitemLabelsDeleteChecked;
    }

    public void goFinish() {
        finish();
    }

    public void setContentLables() {

        mIntent.putParcelableArrayListExtra("itemLabelInfos", itemLabelInfos);
        mIntent.putExtra("isModified", true);
        setResult(RESULT_OK, mIntent);
        finish();
    }

    public void countResfesh(ArrayList<LabelInfo> labelInfos) {
        //count resfesh
        if (null == mLabelManageAdapter) {
            mLabelManageAdapter = new LabelManageAdapter(mContext, labelInfos, mOnClickListener,
                    mOnCheckListener, mOnDeleteCheckListener, mItemOnLongClickListener);
            mLabelManageAdapter.setType(type);
            mLabelManageAdapter.setID(mId);
            mRvLabelList.setAdapter(mLabelManageAdapter);
        } else {
            mLabelManageAdapter.setLabelInfos(labelInfos);
            mLabelManageAdapter.notifyDataSetChanged();
        }


        if (null == labelInfos || labelInfos.size() == 1) {
            mIbEditLabel.setMenuItemsDisable(R.id.tv_compile_labels);
        } else {
            mIbEditLabel.setMenuItemsEnable(R.id.tv_compile_labels);
        }
    }

}
