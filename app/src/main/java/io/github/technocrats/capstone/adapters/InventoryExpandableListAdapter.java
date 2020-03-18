package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.Product;

public class InventoryExpandableListAdapter extends BaseExpandableListAdapter {

    Context context;
    String[] parentHeaders;
    List<String[]> secondLevel;
    List<LinkedHashMap<String, List<Product>>> data;
    ThreeLevelListViewListener mThreeLevelListViewListener;

    public InventoryExpandableListAdapter(Context context, String[] parentHeaders, List<String[]> secondLevel,
                                          List<LinkedHashMap<String, List<Product>>> data,
                                          ThreeLevelListViewListener mThreeLevelListViewListener) {
        this.context = context;
        this.parentHeaders = parentHeaders;
        this.secondLevel = secondLevel;
        this.data = data;
        this.mThreeLevelListViewListener = mThreeLevelListViewListener;
    }

    @Override
    public int getGroupCount() {
        return parentHeaders.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        // no idea why this code is working
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int group, int child) {
        return child;
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.inventory_row_first, null);
        TextView text = (TextView) convertView.findViewById(R.id.rowCategoriesText);
        text.setText(this.parentHeaders[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(context);

        String[] headers = secondLevel.get(groupPosition);


        ArrayList<List<Product>> childData = new ArrayList<>();
        HashMap<String, List<Product>> secondLevelData = data.get(groupPosition);

        for(String key : secondLevelData.keySet())
        {
            childData.add(secondLevelData.get(key));

        }

        secondLevelELV.setAdapter(new InventorySecondLevelAdapter(context, childData, headers));
        secondLevelELV.setGroupIndicator(null);


        secondLevelELV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    secondLevelELV.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        secondLevelELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                int ppos = (int) expandableListView.getTag();
                String plItem = Integer.toString(ppos);

                InventorySecondLevelAdapter adapter = (InventorySecondLevelAdapter) expandableListView.getExpandableListAdapter();
                String slItem = (String) adapter.getGroup(i);

                Product tlItem = (Product) adapter.getChild(i, i1);

                mThreeLevelListViewListener.onFinalChildClick(ppos, i, i1);
                mThreeLevelListViewListener.onFinalItemClick(plItem, slItem, tlItem);

                return true;
            }
        });
        secondLevelELV.setTag(groupPosition);

        return secondLevelELV;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface ThreeLevelListViewListener{
        void onFinalChildClick(int plpos, int slpos, int tlpos);
        void onFinalItemClick(String plItem, String slItem, Product tlItem);
    }

}
