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
import io.github.technocrats.capstone.models.OrderProduct;

public class OrderProductAdapter  extends RecyclerView.Adapter<OrderProductAdapter.OrderProductHolder>{

    private Context context;
    private ArrayList<OrderProduct> orderItems;

    public OrderProductAdapter(Context context, ArrayList<OrderProduct> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    public class OrderProductHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvQuantity, tvCost;

        public OrderProductHolder(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.productNameTextView);
            tvQuantity = (TextView) itemView.findViewById(R.id.quantityTextView);
            tvCost = (TextView) itemView.findViewById(R.id.costTextView);
        }

        public void setDetails(OrderProduct item) {

            String quantity = Float.toString(item.getQuantity());
            String cost = Float.toString(item.getUnitCost());

            tvName.setText(item.getProductName());
            tvQuantity.setText(quantity);
            tvCost.setText(cost);
        }
    }

    @NonNull
    @Override
    public OrderProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_row, parent, false);
        return new OrderProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductHolder holder, int position) {
        OrderProduct item = orderItems.get(position);
        holder.setDetails(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

}

