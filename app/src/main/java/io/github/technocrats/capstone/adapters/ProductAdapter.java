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
import io.github.technocrats.capstone.SearchProductActivity;
import io.github.technocrats.capstone.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    private Context context;
    private ArrayList<Product> products;
    private TextView tvProductSelected;

    public ProductAdapter(Context context, ArrayList<Product> products, TextView tvProductSelected) {
        this.context = context;
        this.products = products;
        this.tvProductSelected = tvProductSelected;
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
                    String selectedProduct = tvProductId.getText().toString();

                    for(int i = 0; i < products.size(); i++){
                        if(products.get(i).getProductId().equals(selectedProduct)){
                            Product product = new Product(products.get(i).getProductId(),
                                    products.get(i).getProductName(),
                                    products.get(i).getUnitCost(),
                                    products.get(i).getQuantity(),
                                    products.get(i).getSubcategory(),
                                    products.get(i).getCategory());

                            SearchProductActivity.setProductSelected(product);

                            String displaySelectedProductInfo = product.getProductId() + "  -  "
                                    + product.getProductName()
                                    + "    $" + product.getUnitCost();

                            tvProductSelected.setText(displaySelectedProductInfo);

                            break;
                        }
                    }
                }
            });
        }

        public void setDetails(Product p) {
            String productId = p.getProductId();
            String productName = p.getProductName();
            String cost = Float.toString(p.getUnitCost());

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
