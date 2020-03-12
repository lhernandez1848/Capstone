package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.technocrats.capstone.R;

public class SecondLevelAdapter extends BaseExpandableListAdapter {

    private Context _context;
    String[] listDataSubcategories;
    List<List<String>> listDataProducts;


    public SecondLevelAdapter(Context _context,  String[] listDataSubcategories, List<List<String>> listDataProducts) {
        this._context = _context;
        this.listDataProducts = listDataProducts;
        this.listDataSubcategories = listDataSubcategories;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataSubcategories[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return listDataSubcategories.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_subcategories, null);
        TextView text = (TextView) convertView.findViewById(R.id.lblListSubcategories);
        String groupText = getGroup(groupPosition).toString();
        text.setText(groupText);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> childData;
        childData = listDataProducts.get(groupPosition);

        return childData.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_products, null);
        TextView textView = (TextView) convertView.findViewById(R.id.lblListItem);
        List<String> childArray = listDataProducts.get(groupPosition);
        String text = childArray.get(childPosition);
        textView.setText(text);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> children = listDataProducts.get(groupPosition);

        return children.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
