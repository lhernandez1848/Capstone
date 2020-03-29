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

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.SearchProductHolder> {

    private Context context;
    private ArrayList<Product> searchResult;
    private SearchOnItemClickListener clickListener;

    public SearchProductAdapter(Context context, ArrayList<Product> searchResult) {
        this.context = context;
        this.searchResult = searchResult;
    }

    @NonNull
    @Override
    public SearchProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_product_row, parent, false);
        return new SearchProductAdapter.SearchProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProductHolder holder, int position) {
        Product item = searchResult.get(position);
        holder.setDetails(item);

    }

    @Override
    public int getItemCount() {
        return searchResult.size();
    }

    public class SearchProductHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvQuantity, tvCost;

        public SearchProductHolder(@NonNull final View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.productNameTextView);
            tvQuantity = (TextView) itemView.findViewById(R.id.quantityTextView);
            tvCost = (TextView) itemView.findViewById(R.id.unitCostTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "Item clicked");
                    clickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }

        public void setDetails(Product item) {

            String quantity = Float.toString(item.getQuantity());
            String quantityDisplay = "Qty: " + quantity;
            String cost = Float.toString(item.getUnitCost());
            String costDisplay = "$" + cost;

            tvName.setText(item.getProductName());
            tvQuantity.setText(quantityDisplay);
            tvCost.setText(costDisplay);
        }
    }

    public void SetOnItemClickListener(final SearchOnItemClickListener onItemClickListener){
        this.clickListener = onItemClickListener;
    }

    public interface SearchOnItemClickListener {
        void onItemClick(View view, int position);
    }
}
