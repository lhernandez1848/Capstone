package io.github.technocrats.capstone.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.github.technocrats.capstone.OrderDetailsActivity;
import io.github.technocrats.capstone.R;
import io.github.technocrats.capstone.models.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private Context context;
    private ArrayList<Order> orders;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public class OrderHolder extends RecyclerView.ViewHolder {

        private TextView tvOrderNumber, tvStatus, tvDate, tvTotal;

        public OrderHolder(View itemView) {
            super(itemView);

            tvOrderNumber = (TextView) itemView.findViewById(R.id.orderNumberTextView);
            tvStatus = (TextView) itemView.findViewById(R.id.statusTextView);
            tvDate = (TextView) itemView.findViewById(R.id.dateTextView);
            tvTotal = (TextView) itemView.findViewById(R.id.totalTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "Item clicked");

                    // save order details to intent
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderNumber", tvOrderNumber.getText().toString());
                    intent.putExtra("status", tvStatus.getText().toString());
                    intent.putExtra("date", tvDate.getText().toString());
                    intent.putExtra("total", tvTotal.getText().toString());

                    // call order details activity
                    context.startActivity(intent);
                }
            });
        }

        public void setDetails(Order order) {

            String day = Integer.toString(order.getDay());
            String month = Integer.toString(order.getMonth());

            // format month
            switch (month) {
                case "1":
                    month = "Jan";
                    break;
                case "2":
                    month = "Feb";
                    break;
                case "3":
                    month = "Mar";
                    break;
                case "4":
                    month = "Apr";
                    break;
                case "5":
                    month = "May";
                    break;
                case "6":
                    month = "Jun";
                    break;
                case "7":
                    month = "Jul";
                    break;
                case "8":
                    month = "Aug";
                    break;
                case "9":
                    month = "Sept";
                    break;
                case "10":
                    month = "Oct";
                    break;
                case "11":
                    month = "Nov";
                    break;
                case "12":
                    month = "Dec";
                    break;
                default:
                    break;
            }

            String year = Integer.toString(order.getYear());
            String date = day + "-" + month + "-" + year;

            String orderId = order.getOrderId();
            String statusId = Integer.toString(order.getStatusId());

            switch (statusId) {
                case "1":
                    statusId = "Closed";
                    break;
                case "2":
                    statusId = "Open";
                    break;
                    default:
                        break;
            }

            String total = Float.toString(order.getTotalCost());

            tvOrderNumber.setText(orderId);
            tvStatus.setText(statusId);
            tvDate.setText(date);
            tvTotal.setText("$" + total);
        }
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.order_row, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        Order order = orders.get(position);
        holder.setDetails(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
