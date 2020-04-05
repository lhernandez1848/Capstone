package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.RecommendedProduct;

public class RecommendedProductsAdapter extends ArrayAdapter<RecommendedProduct> {

    private Context mcontext;
    private int mResource;

    public RecommendedProductsAdapter(Context context, int resource, ArrayList<RecommendedProduct> objects) {
        super(context, resource, objects);
        mcontext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String product = getItem(position).getProduct();
        double quantity = getItem(position).getQuantity();
        double par = getItem(position).getPar();

        RecommendedProduct recommendedProduct = new RecommendedProduct(product, quantity, par);

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textView1 = (TextView) convertView.findViewById(R.id.textView1);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
        TextView textView3 = (TextView) convertView.findViewById(R.id.textView3);

        textView1.setText(product);
        textView2.setText("Quantity: " + String.format("%.2f", quantity));
        textView3.setText("Par: " + String.format("%.2f", par));

        return convertView;
    }
}
