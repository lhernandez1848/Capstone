package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.Product;
import io.github.technocrats.capstone.models.Subcategory;

public class CheckInventoryValueSecondLevelAdapter extends BaseExpandableListAdapter {

    private Context context;
    List<List<Product>> data;
    List<Subcategory> headers;

    public CheckInventoryValueSecondLevelAdapter(Context context, List<List<Product>> data, List<Subcategory> headers) {
        this.context = context;
        this.data = data;
        this.headers = headers;
    }

    @Override
    public int getGroupCount() {
        return headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Product> children = data.get(groupPosition);
        return children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers.get(groupPosition);
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

        NumberFormat formatter = new DecimalFormat("#,###.##");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.inventory_list_subcategory_values, null);

        TextView name = (TextView) convertView.findViewById(R.id.tvSubcategoryNames);
        TextView value = (TextView) convertView.findViewById(R.id.tvSubcategoryValues);

        Subcategory parentArray = headers.get(groupPosition);

        float fValue = parentArray.getValue();
        String subValue = "$" + formatter.format(fValue);
        String productName = parentArray.getSubcategory_name();

        name.setText(productName);
        value.setText(subValue);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        NumberFormat formatter = new DecimalFormat("#,###.##");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.inventory_list_product_values, null);

        TextView name = (TextView) convertView.findViewById(R.id.tvListProductName);
        TextView value = (TextView) convertView.findViewById(R.id.tvListProductValue);

        List<Product> childArray = data.get(groupPosition);

        Product product = childArray.get(childPosition);

        float fValue = product.getQuantity() * product.getUnitCost();
        String sValue = "$" + formatter.format(fValue);
        String productName = product.getProductName();

        name.setText(productName);
        value.setText(sValue);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
