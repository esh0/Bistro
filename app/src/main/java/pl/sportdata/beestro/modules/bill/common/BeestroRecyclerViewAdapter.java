package pl.sportdata.beestro.modules.bill.common;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.base.BeestroObject;

public class BeestroRecyclerViewAdapter<T extends BeestroObject> extends RecyclerView.Adapter<BeestroRecyclerViewAdapter.ViewHolder> {

    private final List<T> values;
    private String pluFormat;
    private AdapterListener<T> listener;

    public BeestroRecyclerViewAdapter(@Nullable List<T> items) {
        if (items != null) {
            values = items;

            int maxId = 0;
            for (T item : items) {
                maxId = Math.max(maxId, item.id);
            }
            pluFormat = String.format("#%%0%sd", String.valueOf(maxId).length());
        } else {
            values = Collections.emptyList();
        }
    }

    public void setListener(AdapterListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bill_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BeestroRecyclerViewAdapter.ViewHolder holder, int position) {
        T item = values.get(position);
        holder.nameTextView.setText(item.name);
        holder.pluTextView.setText(String.format(pluFormat, item.id));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelected(values.get(holder.getAdapterPosition()));
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView nameTextView;
        public final TextView pluTextView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            pluTextView = (TextView) view.findViewById(R.id.plu_text_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }

    public interface AdapterListener<T extends BeestroObject> {

        void onSelected(T item);
    }
}
