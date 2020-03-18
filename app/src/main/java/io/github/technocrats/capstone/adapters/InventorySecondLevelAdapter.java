package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.Product;

public class InventorySecondLevelAdapter extends BaseExpandableListAdapter {

    private Context context;
    List<List<Product>> data;
    String[] headers;

    public InventorySecondLevelAdapter(Context context, List<List<Product>> data, String[] headers) {
        this.context = context;
        this.data = data;
        this.headers = headers;
    }

    @Override
    public int getGroupCount() {
        return headers.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Product> children = data.get(groupPosition);
        return children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Product> childData;
        childData = data.get(groupPosition);
        return childData.get(childPosition);
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
        convertView = inflater.inflate(R.layout.inventory_row_second, null);
        TextView text = (TextView) convertView.findViewById(R.id.rowSubcategoriesText);
        String groupText = getGroup(groupPosition).toString();
        text.setText(groupText);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.inventory_row_third, null);

        TextView textView = (TextView) convertView.findViewById(R.id.rowProductsText);

        List<Product> childArray = data.get(groupPosition);

        Product product = childArray.get(childPosition);
        String text = product.getProductName();
        textView.setText(text);
        // Log.d("SecondLevelAdapter", text);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
