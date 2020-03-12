package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.Order;
import io.github.technocrats.capstone.models.OrderItem;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemHolder>{

    private Context context;
    private ArrayList<OrderItem> orderItems;

    public OrderItemAdapter(Context context, ArrayList<OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvQuantity, tvCost;

        public OrderItemHolder(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.productNameTextView);
            tvQuantity = (TextView) itemView.findViewById(R.id.quantityTextView);
            tvCost = (TextView) itemView.findViewById(R.id.costTextView);
        }

        public void setDetails(OrderItem item) {

            String quantity = Integer.toString(item.getQuantity());
            String cost = Float.toString(item.getUnitCost());

            tvName.setText(item.getProductName());
            tvQuantity.setText(quantity);
            tvCost.setText(cost);
        }
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_row, parent, false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.setDetails(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

}
