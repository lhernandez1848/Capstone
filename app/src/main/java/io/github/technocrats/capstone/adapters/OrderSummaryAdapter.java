package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.Product;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderItemHolder> {

    private Context context;
    private ArrayList<Product> orderItems;
    private OnItemClickListener onItemClickListener;

    public OrderSummaryAdapter(Context context, ArrayList<Product> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName, tvQuantity, tvCost;
        private ImageButton btnDelete;

        public OrderItemHolder(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.productNameTextView);
            tvQuantity = (TextView) itemView.findViewById(R.id.quantityTextView);
            tvCost = (TextView) itemView.findViewById(R.id.costTextView);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnRemoveProduct);
            btnDelete.setOnClickListener(this);
        }

        public void setDetails(Product item) {

            String quantity = "Qty: " + Float.toString(item.getQuantity());
            String cost = Float.toString(item.getUnitCost());
            String costDisplay = "Unit Cost: $" + cost;

            tvName.setText(item.getProductName());
            tvQuantity.setText(quantity);
            tvCost.setText(costDisplay);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnRemoveProduct:
                    onItemClickListener.onDelete(v, getAdapterPosition());
                    break;
            }

            if (onItemClickListener != null){
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }

        }
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_summary_row, parent, false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, int position) {
        Product item = orderItems.get(position);
        holder.setDetails(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onDelete(View view, int position);
    }
}