package pl.sportdata.mojito.modules.bill;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.WindowManager;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.bills.BillUtils;

import static pl.sportdata.mojito.entities.bills.BillUtils.BY_GUEST_SPLIT_OPTION;
import static pl.sportdata.mojito.entities.bills.BillUtils.EQUAL_SPLIT_OPTION;
import static pl.sportdata.mojito.entities.bills.BillUtils.NONE;
import static pl.sportdata.mojito.entities.bills.BillUtils.PROPORTION_SPLIT_OPTION;
import static pl.sportdata.mojito.entities.bills.BillUtils.VALUE_SPLIT_OPTION;

public class BillSplitDialogFragment extends AppCompatDialogFragment {

    private BillSplitDialogListener listener;
    @BillUtils.SplitOption
    private int option = NONE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        option = BY_GUEST_SPLIT_OPTION;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MojitoAlertDialogTheme);
        builder.setTitle(getString(R.string.bill_split));
        String[] items = {getString(R.string.bill_split_guests), getString(R.string.bill_split_equal), getString(R.string.proportion),
                getString(R.string.bill_split_amount)};
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        option = BY_GUEST_SPLIT_OPTION;
                        break;
                    case 1:
                        option = EQUAL_SPLIT_OPTION;
                        break;
                    case 2:
                        option = PROPORTION_SPLIT_OPTION;
                        break;
                    case 3:
                        option = VALUE_SPLIT_OPTION;
                        break;
                    default:
                        option = NONE;
                }
            }
        });
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    if (option != NONE) {
                        listener.onBillSplitSelected(option);
                    } else {
                        listener.onBillSplitCancelled();
                    }
                }
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BillSplitDialogListener) {
            listener = (BillSplitDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BillSplitDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface BillSplitDialogListener {

        void onBillSplitCancelled();

        void onBillSplitSelected(@BillUtils.SplitOption int option);
    }
}
