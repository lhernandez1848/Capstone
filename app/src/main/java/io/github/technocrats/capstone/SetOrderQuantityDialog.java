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

import io.github.technocrats.capstone.models.Product;

public class SetOrderQuantityDialog extends AppCompatDialogFragment
        implements View.OnClickListener, View.OnFocusChangeListener{

    private TextView tvProductName, tvProductCost;
    private EditText etOrderQuantity;
    private Button btnAddQuantity, btnSubtractQuantity, btnSave;
    private SetOrderQuantityDialogListener listener;

    NumberFormat formatter;

    private String productName, productID;
    private int subcategory, category;
    private float productPrice, productQuantity, total;
    private Product currentProduct;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_set_order_quantity, null);

        builder.setView(view)
                .setTitle("Set Product Quantity");

        // initialize widgets
        tvProductName = view.findViewById(R.id.tvProductName);
        tvProductCost = view.findViewById(R.id.tvProductCost);
        etOrderQuantity = view.findViewById(R.id.etOrderQuantity);
        btnAddQuantity = view.findViewById(R.id.btnAddQuantity);
        btnSubtractQuantity = view.findViewById(R.id.btnSubtractQuantity);
        btnSave = view.findViewById(R.id.btnSaveSetQuantity);

        btnAddQuantity.setOnClickListener(this);
        btnSubtractQuantity.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        // get attributes from arguments
        productName = getArguments().getString("productName");
        productID = getArguments().getString("productID");
        productPrice = getArguments().getFloat("unitCost");
        productQuantity = getArguments().getFloat("quantity");
        category = getArguments().getInt("category");
        subcategory = getArguments().getInt("subcategory");

        // initialize values
        formatter = new DecimalFormat("#,###.##");
        total = (productPrice * productQuantity);

        // create product
        currentProduct = new Product(productID, productName, productPrice, productQuantity, subcategory, category);

        String sQuantity = "";
        if (currentProduct.getQuantity() > 0) {
            sQuantity = Float.toString(productQuantity);
        }

        // set text
        String sDisplay = "$" + productPrice + " * " + formatter.format(productQuantity) + " = " + "$" + formatter.format(total);

        // display product name
        tvProductName.setText(productName);
        tvProductCost.setText(sDisplay);
        etOrderQuantity.setText(sQuantity);

        return builder.create();
    }

    @Override
    public void onAttach(Context _context) {
        super.onAttach(_context);

        try {
            listener = (SetOrderQuantityDialogListener) _context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_context.toString() + "SetOrderQuantityDialogListener not implemented");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveSetQuantity:
                saveQuantity();
                break;
            case R.id.btnAddQuantity:
                addQuantity();
                break;
            case R.id.btnSubtractQuantity:
                subtractQuantity();
                break;
            default:
                break;
        }
    }

    private void subtractQuantity() {
        String sEditQuantityTemp = etOrderQuantity.getText().toString();
        if(sEditQuantityTemp.equals("")) {
            etOrderQuantity.setText("0");
            Toast.makeText(getContext(), "Quantity cannot be less than zero", Toast.LENGTH_LONG).show();
        }else {
            float iEditQuantityTemp = Float.parseFloat(sEditQuantityTemp);
            if (iEditQuantityTemp <= 0f){
                etOrderQuantity.setText("0");
                Toast.makeText(getContext(), "Quantity cannot be less than zero", Toast.LENGTH_LONG).show();
            }else {
                productQuantity = Float.parseFloat(etOrderQuantity.getText().toString());
                float temp = productQuantity - 1f;
                etOrderQuantity.setText(String.valueOf(temp));
                productQuantity = temp;
                total = productPrice * productQuantity;

                String costDisplay = "$" + productPrice + " * " + formatter.format(productQuantity)
                        + " = " + "$" + formatter.format(total);
                tvProductCost.setText(costDisplay);
            }
        }
    }

    private void addQuantity() {
        String editQuantityTemp = etOrderQuantity.getText().toString();
        if(editQuantityTemp.equals("")){
            etOrderQuantity.setText("0");
        }
        productQuantity = Float.parseFloat(etOrderQuantity.getText().toString());
        float temp = productQuantity+1f;
        etOrderQuantity.setText(String.valueOf(temp));
        productQuantity = temp;

        total = productPrice * productQuantity;
        String costDisplay = "$" + productPrice + " * " + formatter.format(productQuantity)
                + " = " + "$" + formatter.format(total);
        tvProductCost.setText(costDisplay);

    }

    private void saveQuantity() {
        try {
            productQuantity = Float.parseFloat(etOrderQuantity.getText().toString());

            if(productQuantity>0f){
                total = productQuantity * productPrice;
                listener.applyProductOrderQuantity(currentProduct, productQuantity, total);
                dismiss();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "ERROR: Quantity is empty", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFocusChange(View v, boolean b) {
        if(!b) {
            productQuantity = Float.parseFloat(etOrderQuantity.getText().toString());
            total = productPrice * productQuantity;
            String display = "$" + productPrice + " * " + formatter.format(productQuantity) + " = "
                    + "$" + String.format("%.2f", total);
            tvProductCost.setText(display);
        }
    }

    public interface SetOrderQuantityDialogListener{
        void applyProductOrderQuantity (Product product, float updatedQuantity, float total);
    }
}
