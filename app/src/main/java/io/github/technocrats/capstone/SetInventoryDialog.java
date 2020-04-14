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
import io.github.technocrats.capstone.models.Product;

import java.util.Locale;

public class SetInventoryDialog extends AppCompatDialogFragment
        implements View.OnClickListener {

    private EditText etInventoryCount;
    private TextView tvProductName, tvQuantity, tvPar, tvError;
    private Button btnAdd, btnSubtract, btnSaveSetInventory;
    private SetInventoryDialogListener listener;
    String productName, productId, date;

    float productUnitCost, quantity, par;
    int subcategory_id, category_id;
    Product currentProduct;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // return super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_set_inventory, null);

        builder.setView(view)
                .setTitle("Set Inventory Count");

        // initialize widgets
        tvProductName = view.findViewById(R.id.tvProductName);
        tvPar = view.findViewById(R.id.tvPar);
        tvQuantity = view.findViewById(R.id.tvLatestQuantity);
        tvError = view.findViewById(R.id.tvInventoryQuantityError);
        etInventoryCount = view.findViewById(R.id.etInventoryCount);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnSubtract = view.findViewById(R.id.btnSubtract);
        btnSaveSetInventory = view.findViewById(R.id.btnSaveSetInventory);

        btnAdd.setOnClickListener(this);
        btnSubtract.setOnClickListener(this);
        btnSaveSetInventory.setOnClickListener(this);

        // get attributes from arguments
        productName = getArguments().getString("productName");
        productId = getArguments().getString("productId");
        productUnitCost = getArguments().getFloat("unitCost");
        date = getArguments().getString("date");
        quantity = getArguments().getFloat("quantity");
        par = getArguments().getFloat("par");
        subcategory_id = getArguments().getInt("subcategory_id");
        category_id = getArguments().getInt("category_id");

        // create product
        currentProduct = new Product(productId, productName, productUnitCost, quantity, subcategory_id, category_id);

        // set text
        String sQuantity = String.format(Locale.CANADA,"%.3f", quantity);
        String sPar = String.format(Locale.CANADA, "%.3f", par);
        String sDisplay = sQuantity + " (" + date + ")";

        // display product name
        tvProductName.setText(productName);
        tvQuantity.setText(sDisplay);
        tvPar.setText(sPar);
        tvError.setText("");

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SetInventoryDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SetInventoryDialogListener");
        }
    }

    @Override
    public void onClick(View v) {
        // get user input
        String count = etInventoryCount.getText().toString().trim();

        // initialize to 0 if empty
        if (count.isEmpty())
        {
            count = "0.00";
        }

        switch (v.getId()) {
            case R.id.btnSaveSetInventory:
                String sCount = etInventoryCount.getText().toString().trim();
                if (!sCount.isEmpty()) {
                    listener.getInventoryCount(sCount, currentProduct, par);
                    dismiss();
                }
                else {
                    tvError.setText("ERROR: Quantity cannot be empty.");
                }
                break;
            case R.id.btnAdd:
                Float fAddCount = Float.parseFloat(count);
                Float fIncreaseCount = fAddCount + 1;
                String increaseCount = String.format(Locale.CANADA,"%.2f", fIncreaseCount);
                etInventoryCount.setText(increaseCount);
                break;
            case R.id.btnSubtract:
                Float fMinusCount = Float.parseFloat(count);
                Float fDecreaseCount = fMinusCount - 1;

                // quantity cannot be negative
                if (fDecreaseCount < 0)
                {
                    fDecreaseCount = 0.00f;
                }

                String decreaseCount = String.format(Locale.CANADA,"%.2f", fDecreaseCount);
                etInventoryCount.setText(decreaseCount);
                break;
            default:
                break;
        }

    }

    public interface SetInventoryDialogListener {
        void getInventoryCount(String count, Product product, float par);
    }
}