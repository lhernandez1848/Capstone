package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.OrderSummary;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderSummaryItemHolder>{

    private Context context;
    private ArrayList<OrderSummary> orderSummary;

    public OrderSummaryAdapter(Context context, ArrayList<OrderSummary> orderSummary) {
        this.context = context;
        this.orderSummary = orderSummary;
    }

    public class OrderSummaryItemHolder extends RecyclerView.ViewHolder {

        private TextView tvItemName;
        private TextView tvItemQuantity;
        private TextView tvItemPrice;

        public OrderSummaryItemHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = (TextView) itemView.findViewById(R.id.lblListProductName);
            tvItemQuantity = (TextView) itemView.findViewById(R.id.lblListProductQuantity);
            tvItemPrice = (TextView) itemView.findViewById(R.id.lblListProductPrice);
        }

        public void setDetails(OrderSummary item) {
            String quantity = Float.toString(item.getQuantity());
            String cost = item.getUnitCost();

            tvItemName.setText(item.getProductName());
            tvItemQuantity.setText(quantity);
            tvItemPrice.setText(cost);
        }
    }

    @NonNull
    @Override
    public OrderSummaryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_summary_item_row, parent, false);
        return new OrderSummaryItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSummaryItemHolder holder, int position) {
        OrderSummary item = orderSummary.get(position);
        holder.setDetails(item);
    }

    @Override
    public int getItemCount() {
        return orderSummary.size();
    }

}
