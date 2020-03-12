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

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private String[] listDataCategories;
    private List<String[]> listDataSubcategories;
    private List<LinkedHashMap<String, List<String>>> listDataProducts;

    ThreeLevelListViewListener mThreeLevelListViewListener;

    public ExpandableListAdapter(Context context, String[] listDataCategories,
                                 List<String[]> listDataSubcategories,
                                 List<LinkedHashMap<String, List<String>>> listDataProducts,
                                 ThreeLevelListViewListener listener) {
        this._context = context;
        this.listDataCategories = listDataCategories;
        this.listDataSubcategories = listDataSubcategories;
        this.listDataProducts = listDataProducts;
        mThreeLevelListViewListener = listener;
    }

    @Override
    public int getGroupCount() {
        return listDataCategories.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //TODO
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

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_categories, null);
        TextView text = convertView.findViewById(R.id.lblListCategories);
        text.setText(this.listDataCategories[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(_context);
        final String[] headers = listDataSubcategories.get(groupPosition);

        final ArrayList<List<String>> childData = new ArrayList<>();
        final HashMap<String, List<String>> secondLevelData = listDataProducts.get(groupPosition);

        for(String key : secondLevelData.keySet())
        {
            childData.add(secondLevelData.get(key));
        }

        secondLevelELV.setAdapter(new SecondLevelAdapter(_context, headers, childData));
        secondLevelELV.setGroupIndicator(null);
        /*secondLevelELV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    secondLevelELV.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });*/

        secondLevelELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                int ppos = (int)expandableListView.getTag();
                mThreeLevelListViewListener.onFinalChildClick(ppos, i, i1);
                int plItem = (int) getGroup(ppos);
                SecondLevelAdapter adapter = (SecondLevelAdapter)expandableListView.getExpandableListAdapter();
                String slItem = (String)adapter.getGroup(i);
                String tlItem = (String)adapter.getChild(i, i1);
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
        void onFinalItemClick(int plItem, String slItem, String tlItem);
    }
}
