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

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Locale;

import io.github.technocrats.capstone.models.Product;

public class SetInventoryDialog extends AppCompatDialogFragment
        implements View.OnClickListener {

    private EditText etInventoryCount;
    private TextView tvProductName, tvQuantity, tvPar;
    private Button btnAdd, btnSubtract, btnCancelSetInventory, btnSaveSetInventory;
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
        etInventoryCount = view.findViewById(R.id.etInventoryCount);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnSubtract = view.findViewById(R.id.btnSubtract);
        btnCancelSetInventory = view.findViewById(R.id.btnCancelSetInventory);
        btnSaveSetInventory = view.findViewById(R.id.btnSaveSetInventory);

        btnAdd.setOnClickListener(this);
        btnSubtract.setOnClickListener(this);
        btnCancelSetInventory.setOnClickListener(this);
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

        String count = etInventoryCount.getText().toString().trim();

        if (count.isEmpty())
        {
            count = "0.00";
        }

        Float fCount = Float.parseFloat(count);

        switch (v.getId()) {
            case R.id.btnCancelSetInventory:
                dismiss();
                break;
            case R.id.btnSaveSetInventory:
                String sCount = etInventoryCount.getText().toString();
                listener.getInventoryCount(sCount, currentProduct, par);
                dismiss();
                break;
            case R.id.btnAdd:
                Float fIncreaseCount = fCount + 1;
                String increaseCount = String.format(Locale.CANADA,"%.2f", fIncreaseCount);
                etInventoryCount.setText(increaseCount);
                break;
            case R.id.btnSubtract:
                Float fDecreaseCount = fCount - 1;

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