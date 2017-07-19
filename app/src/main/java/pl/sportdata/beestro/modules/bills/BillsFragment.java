package pl.sportdata.beestro.modules.bills;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.sportdata.beestro.BeestroApplication;
import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.modules.bills.merge.ItemTouchHelperDragDropCallback;

public class BillsFragment extends Fragment implements ItemTouchHelperDragDropCallback.EventsListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_BILLS_TYPE = "bills-type";
    public static final String TAG = "BillsFragment";
    private final List<Bill> bills = new ArrayList<>();
    private int columnCount = 1;
    private int billsType;
    private OnBillsFragmentInteractionListener listener;
    private BillsRecyclerViewAdapter adapter;
    private TextView labelTextView;
    private TextView countTextView;

    public BillsFragment() {
    }

    public static BillsFragment newInstance(int columnCount, int billsType) {
        BillsFragment fragment = new BillsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_BILLS_TYPE, billsType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            billsType = getArguments().getInt(ARG_BILLS_TYPE);
        }

        createAdapter();
    }

    private void createAdapter() {
        initBills();
        adapter = new BillsRecyclerViewAdapter(getActivity(), bills, listener);
    }

    private void initBills() {
        bills.clear();

        List<Bill> allBills = DataProviderFactory.getDataProvider(getActivity()).getBills();
        switch (billsType) {
            case 0:
                bills.addAll(getOwnBills(getOpenBills(allBills)));
                break;
            case 1:
                bills.addAll(getOpenBills(allBills));
                break;
            case 2:
                bills.addAll(getOwnBills(getClosedBills(allBills)));
                break;
            case 3:
                bills.addAll(getClosedBills(allBills));
                break;
        }
    }

    @NonNull
    private List<Bill> getOwnBills(List<Bill> bills) {
        List<Bill> filteredBills = new ArrayList<>();
        int currentUserId = ((BeestroApplication) getActivity().getApplication()).getLoggedUser().id;
        for (Bill bill : bills) {
            if (bill.getOwnerId() == currentUserId) {
                filteredBills.add(bill);
            }
        }
        return filteredBills;
    }

    @NonNull
    private List<Bill> getOpenBills(List<Bill> bills) {
        List<Bill> filteredBills = new ArrayList<>();
        for (Bill bill : bills) {
            filteredBills.add(bill);
        }
        return filteredBills;
    }

    @NonNull
    private List<Bill> getClosedBills(List<Bill> bills) {
        List<Bill> filteredBills = new ArrayList<>();
        for (Bill bill : bills) {
            filteredBills.add(bill);
        }
        return filteredBills;
    }

    public void setBillsType(int billsType) {
        this.billsType = billsType;
        initBills();
        updateLabels();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills_list, container, false);
        labelTextView = (TextView) view.findViewById(R.id.label);
        countTextView = (TextView) view.findViewById(R.id.count);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        Context context = view.getContext();

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperDragDropCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateLabels();
    }

    private void updateLabels() {
        if (bills != null) {
            @StringRes int label = 0;
            switch (billsType) {
                case 0:
                    label = R.string.bills_own_open;
                    break;
                case 1:
                    label = R.string.bills_all_open;
                    break;
                case 2:
                    label = R.string.bills_own_closed;
                    break;
                case 3:
                    label = R.string.bills_all_closed;
                    break;
            }

            labelTextView.setText(label);
            countTextView.setText(String.valueOf(bills.size()));
        } else {
            labelTextView.setText(R.string.no_bills);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBillsFragmentInteractionListener) {
            listener = (OnBillsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBillsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onItemDropped(int fromPosition, int toPosition) {
        listener.onBillsMergeRequest(bills.get(fromPosition), bills.get(toPosition));
    }

    public interface OnBillsFragmentInteractionListener {

        void onBillSelected(Bill bill, View view);

        void onBillsMergeRequest(Bill sourceBill, Bill targetBill);
    }
}
