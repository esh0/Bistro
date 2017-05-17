package pl.sportdata.mojito.modules.bills;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.bills.BillUtils;
import pl.sportdata.mojito.entities.users.User;

public class BillsRecyclerViewAdapter extends RecyclerView.Adapter<BillsRecyclerViewAdapter.ViewHolder> {

    private final List<Bill> values;
    private final BillsFragment.OnBillsFragmentInteractionListener listener;
    private final DataProvider mojitoDataProvider;
    private final Context context;

    public BillsRecyclerViewAdapter(Context context, List<Bill> items, BillsFragment.OnBillsFragmentInteractionListener listener) {
        this.values = items;
        this.listener = listener;
        this.mojitoDataProvider = DataProviderFactory.getDataProvider(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bill, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBillSelected(values.get(holder.getAdapterPosition()), holder.view);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Bill bill = values.get(position);
        String tableString = String.valueOf(bill.getTableNumber());
        if (bill.getGuestNumber() != 0) {
            tableString += "." + String.valueOf(bill.getGuestNumber());
        }
        holder.tableTextView.setText(tableString);

        String time;
        switch (bill.getTime()) {
            case 1:
                time = "15 min";
                break;
            case 2:
                time = "30 min";
                break;
            case 3:
                time = "45 min";
                break;
            case 4:
                time = "60 min";
                break;
            case 5:
                time = "120 min";
                break;
            case 6:
                time = "180 min";
                break;
            case 7:
                time = "> 180 min";
                break;
            default:
                time = "--- min";
                break;
        }

        holder.timeTextView.setText(time);
        if (BillUtils.getBillDeviceId(bill) == BillUtils.NEW_BILL_DEVICE_ID) {
            holder.billIdTextView.setText(String.format(Locale.getDefault(), context.getString(R.string.new_bill_label), bill.getTableNumber()));
        } else {
            holder.billIdTextView.setText(
                    String.format(Locale.getDefault(), context.getString(R.string.bill_label), BillUtils.getBillDeviceId(bill), BillUtils.getBillId(bill)));
        }

        User user = mojitoDataProvider.getUser(bill.getOwnerId());
        if (user != null) {
            holder.waiterTextView.setText(user.name);
        } else {
            holder.waiterTextView.setText(String.format(Locale.getDefault(), context.getString(R.string.user_label), bill.getOwnerId()));
        }

        holder.valueTextView.setText(String.format(Locale.getDefault(), "%.2fzł", bill.getValue()));

        holder.container.setBackgroundColor(ContextCompat.getColor(context, bill.isNew() || bill.isModified() ? R.color.gray_ee : R.color.white));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final View container;
        public final TextView tableTextView;
        public final TextView billIdTextView;
        public final TextView waiterTextView;
        public final TextView valueTextView;
        public final TextView timeTextView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            container = view.findViewById(R.id.container);
            tableTextView = (TextView) view.findViewById(R.id.table_text_view);
            billIdTextView = (TextView) view.findViewById(R.id.bill_text_view);
            waiterTextView = (TextView) view.findViewById(R.id.waiter_text_view);
            valueTextView = (TextView) view.findViewById(R.id.value_text_view);
            timeTextView = (TextView) view.findViewById(R.id.time_text_view);
        }
    }
}
