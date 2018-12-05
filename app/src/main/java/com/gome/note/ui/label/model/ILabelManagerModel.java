package com.gome.note.ui.label.model;

import com.gome.note.base.BaseModel;
import com.gome.note.entity.LabelInfo;

import java.util.ArrayList;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public interface ILabelManagerModel extends BaseModel {


    void updateContentLabels(long mId, ArrayList<LabelInfo> itemLabelInfos);

    void deleteBatchLabel(String s, ArrayList<LabelInfo> itemLabelsDeleteChecked);

    void deleteLabel(String labelName, long labelId);

}
