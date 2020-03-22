package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    private Context context;
    private ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        private TextView tvProductId, tvProductName, tvUnitCost;

        public ProductHolder(View itemView) {
            super(itemView);

            tvProductId = (TextView) itemView.findViewById(R.id.productIdTextView);
            tvProductName = (TextView) itemView.findViewById(R.id.productNameListTextView);
            tvUnitCost = (TextView) itemView.findViewById(R.id.unitCostTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "Item clicked");

                    String p_id = tvProductId.getText().toString();
                    String p_name = tvProductName.getText().toString();

                    System.out.println("ID: " + p_id);
                    System.out.println("NAME: " + p_name);

                    //tvProductSelected.setText(p_id + "  -  " + p_name);
                }
            });
        }

        public void setDetails(Product product) {
            String productId = product.getProductId();
            String productName = product.getProductName();
            String cost = Float.toString(product.getUnitCost());

            tvProductId.setText(productId);
            tvProductName.setText(productName);
            tvUnitCost.setText(cost);
        }
    }

    @NonNull
    @Override
    public ProductAdapter.ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.product_row, parent, false);
        return new ProductAdapter.ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductHolder holder, int position) {
        Product product = products.get(position);
        holder.setDetails(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
