package com.example.dai.oicq_android.adapter;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dai.oicq_android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Richie on 2017.07.31
 *         改过 Indicator 的 ExpandableListView 的适配器
 */
public class IndicatorExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "IndicatorExpandableList";
    private List<String> fatherList = new ArrayList<>();
    private List<String> onlineList = new ArrayList<>();
    private List<String> offlineList = new ArrayList<>();

    //                用于存放Indicator的集合
    private SparseArray<ImageView> mIndicators;
    private OnGroupExpandedListener mOnGroupExpandedListener;

    public IndicatorExpandableListAdapter(List<String> fatherList, List<String> onlineList, List<String> offlineList) {
        this.fatherList = fatherList;
        this.onlineList = onlineList;
        this.offlineList = offlineList;
        mIndicators = new SparseArray<>();
    }

    public void setOnGroupExpandedListener(OnGroupExpandedListener onGroupExpandedListener) {
        mOnGroupExpandedListener = onGroupExpandedListener;
    }

    //            根据分组的展开闭合状态设置指示器
    public void setIndicatorState(int groupPosition, boolean isExpanded) {
        if (isExpanded) {
            mIndicators.get(groupPosition).setBackgroundResource(R.drawable.ic_expand_less);
        } else {
            mIndicators.get(groupPosition).setBackgroundResource(R.drawable.ic_expand_more);
        }
    }

    @Override
    public int getGroupCount() {
        return fatherList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) {
            return offlineList.size();
        } else if (groupPosition == 1) {
            return onlineList.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return fatherList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0 && offlineList.size() != 0) {
            return offlineList.get(childPosition);
        } else if (groupPosition == 1 && onlineList.size() != 0) {
            return onlineList.get(childPosition);
        }
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_group_indicator, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_group_indicator);
            groupViewHolder.ivIndicator = (ImageView) convertView.findViewById(R.id.iv_indicator);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(fatherList.get(groupPosition));
        //      把位置和图标添加到Map
        mIndicators.put(groupPosition, groupViewHolder.ivIndicator);
        //      根据分组状态设置Indicator
        setIndicatorState(groupPosition, isExpanded);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_child);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        if (groupPosition == 0 && offlineList.size() != 0) {
            childViewHolder.tvTitle.setText(offlineList.get(childPosition));
        } else if (groupPosition == 1 && onlineList.size() != 0) {
            childViewHolder.tvTitle.setText(onlineList.get(childPosition));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        Log.d(TAG, "onGroupExpanded() called with: groupPosition = [" + groupPosition + "]");
        if (mOnGroupExpandedListener != null) {
            mOnGroupExpandedListener.onGroupExpanded(groupPosition);
        }
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        Log.d(TAG, "onGroupCollapsed() called with: groupPosition = [" + groupPosition + "]");
    }

    private static class GroupViewHolder {
        TextView tvTitle;
        ImageView ivIndicator;
    }

    private static class ChildViewHolder {
        TextView tvTitle;
    }
}
