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
import io.github.technocrats.capstone.models.ProductQuantity;

public class ProductQuantityListAdapter extends ArrayAdapter<ProductQuantity>
{
    private Context mContext;
    private int mResource;

    public ProductQuantityListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProductQuantity> objects)
    {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textView1 = (TextView) convertView.findViewById(R.id.textView1);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);

        textView1.setText(getItem(position).getProduct());
        textView2.setText(getItem(position).getQuantity());

        return convertView;
    }
}