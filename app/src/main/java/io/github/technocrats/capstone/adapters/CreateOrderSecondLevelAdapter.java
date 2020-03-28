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

public class CreateOrderSecondLevelAdapter extends BaseExpandableListAdapter {

    private Context context;
    List<List<Product>> data;
    String[] headers;

    public CreateOrderSecondLevelAdapter(Context context, List<List<Product>> data, String[] headers) {
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
        convertView = inflater.inflate(R.layout.order_list_subcategories, null);
        TextView text = (TextView) convertView.findViewById(R.id.lblListSubcategories);
        String groupText = getGroup(groupPosition).toString();
        text.setText(groupText);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.order_list_products, null);

        TextView productNameTextView = (TextView) convertView.findViewById(R.id.productNameTextView);
        TextView unitCostTextView = (TextView) convertView.findViewById(R.id.unitCostTextView);
        TextView quantityTextView = (TextView) convertView.findViewById(R.id.quantityTextView);

        List<Product> childArray = data.get(groupPosition);

        Product product = childArray.get(childPosition);

        float fUnitCost = product.getUnitCost();
        float fQuantity = product.getQuantity();
        // Log.d("SLA", product.getProductName() + " quantity: " + fQuantity);
        String unitCost = "$" + fUnitCost;
        String quantity = Float.toString(fQuantity);
        String productName = product.getProductName();

        productNameTextView.setText(productName);
        unitCostTextView.setText(unitCost);
        quantityTextView.setText(quantity);
        // Log.d("SecondLevelAdapter", text);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
