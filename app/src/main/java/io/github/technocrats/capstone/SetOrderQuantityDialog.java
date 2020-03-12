package io.github.technocrats.capstone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SetOrderQuantityDialog extends AppCompatDialogFragment implements View.OnClickListener, View.OnFocusChangeListener {

    private TextView tvProductName;
    private EditText editTextOrderQuantity;
    private Button btnAddQuantity, btnSubtractQuantity, btnCancel, btnSave;

    private String productName, productID;
    private float productPrice, quantity, total, productQuantityFromCreate;

    NumberFormat formatter;

    private SetOrderQuantityDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_set_order_quantity, null);

        formatter = new DecimalFormat("#,###.##");

        btnAddQuantity = view.findViewById(R.id.btnAddQuantity);
        btnSubtractQuantity = view.findViewById(R.id.btnSubtractQuantity);
        btnCancel = view.findViewById(R.id.btnCancelSetQuantity);
        btnSave = view.findViewById(R.id.btnSaveSetQuantity);
        btnAddQuantity.setOnClickListener(this);
        btnSubtractQuantity.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        productName = getArguments().getString("productName");
        productID = getArguments().getString("productID");
        productPrice = Float.parseFloat(getArguments().getString("productPrice"));
        productQuantityFromCreate = Float.parseFloat(getArguments().getString("productQuantity"));
        quantity = productQuantityFromCreate;
        total = 0f;

        builder.setView(view).setTitle(productName);

        tvProductName = view.findViewById(R.id.tvShowProductSelected);
        editTextOrderQuantity = view.findViewById(R.id.etProductQuantity);
        editTextOrderQuantity.setOnFocusChangeListener(this);
        editTextOrderQuantity.setText(String.valueOf(productQuantityFromCreate));
        tvProductName.setText("$" + productPrice + " * " + formatter.format(quantity) + " = " + "$" + formatter.format(total));

        return builder.create();
    }

    @Override
    public void onAttach(Context _context) {
        super.onAttach(_context);

        try {
            listener = (SetOrderQuantityDialogListener) _context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_context.toString() + "Dialog Listener not implemented");
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnCancelSetQuantity){
            dismiss();
        } else if(view.getId() == R.id.btnSaveSetQuantity){
            quantity = Float.parseFloat(editTextOrderQuantity.getText().toString());
            if(quantity>0f){
                total = quantity * productPrice;
                listener.applyProductOrderQuantity(productID, productName, quantity, productPrice, total);
                dismiss();
            }
        } else if(view.getId() == R.id.btnAddQuantity){
            String editQuantityTemp = editTextOrderQuantity.getText().toString();
            if(editQuantityTemp.equals("")){
                editTextOrderQuantity.setText("0");
            }
            quantity = Float.parseFloat(editTextOrderQuantity.getText().toString());
            float temp = quantity+1f;
            editTextOrderQuantity.setText(String.valueOf(temp));
            quantity = temp;
            total = productPrice * quantity;
            tvProductName.setText("$" + productPrice + " * " + formatter.format(quantity) + " = " + "$" + formatter.format(total));
        } else if(view.getId() == R.id.btnSubtractQuantity){
            String sEditQuantityTemp = editTextOrderQuantity.getText().toString();
            if(sEditQuantityTemp.equals("")) {
                    editTextOrderQuantity.setText("0");
                    Toast.makeText(getContext(), "Quantity cannot be less than zero", Toast.LENGTH_LONG).show();
            }else {
                float iEditQuantityTemp = Float.parseFloat(sEditQuantityTemp);
                if (iEditQuantityTemp <= 0f){
                    editTextOrderQuantity.setText("0");
                    Toast.makeText(getContext(), "Quantity cannot be less than zero", Toast.LENGTH_LONG).show();
                }else {
                    quantity = Float.parseFloat(editTextOrderQuantity.getText().toString());
                    float temp = quantity - 1f;
                    editTextOrderQuantity.setText(String.valueOf(temp));
                    quantity = temp;
                    total = productPrice * quantity;
                    tvProductName.setText("$" + productPrice + " * " + formatter.format(quantity) + " = " + "$" + formatter.format(total));
                }
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b) {
            quantity = Float.parseFloat(editTextOrderQuantity.getText().toString());
            total = productPrice * quantity;
            tvProductName.setText("$" + productPrice + " * " + formatter.format(quantity) + " = " + "$" + String.format("%.2f", total));
        }
    }

    public interface SetOrderQuantityDialogListener{
        void applyProductOrderQuantity(String product_id, String product, float quantity, float price, float total);
    }

}
