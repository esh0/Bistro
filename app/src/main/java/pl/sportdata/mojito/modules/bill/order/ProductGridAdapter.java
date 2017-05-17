package pl.sportdata.mojito.modules.bill.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.groups.Group;
import pl.sportdata.mojito.entities.items.Item;

public class ProductGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int BACK_VIEW_TYPE = 0;
    private static final int CATEGORY_VIEW_TYPE = 1;
    private static final int PRODUCT_VIEW_TYPE = 2;
    private static final int NO_CATEGORY = -1;
    private final List<Group> categories;
    private final List<Item> products;
    private final Listener listener;
    private final List<Item> categoryProducts = new ArrayList<>();
    private List<Item> filteredProducts;
    private int categoryId = NO_CATEGORY;
    private String categoryName;

    public ProductGridAdapter(List<Group> categories, List<Item> products, Listener listener) {
        this.categories = categories;
        this.products = products;
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<Item> list = products;

                if (filterString != null && filterString.isEmpty()) {
                    results.values = products;
                    results.count = products.size();
                } else {
                    int count = list.size();
                    Item filterableItem;

                    final ArrayList<Item> filteredStartsWith = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        filterableItem = list.get(i);
                        if (filterableItem.name.toLowerCase().startsWith(filterString)) {
                            filteredStartsWith.add(filterableItem);
                        }
                    }

                    final ArrayList<Item> filteredContains = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        filterableItem = list.get(i);
                        if (filterableItem.name.toLowerCase().contains(filterString) && !filteredStartsWith.contains(filterableItem)) {
                            filteredContains.add(filterableItem);
                        }
                    }

                    final ArrayList<Item> filteredIdContains = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        filterableItem = list.get(i);
                        if (String.valueOf(filterableItem.id).toLowerCase().contains(filterString) && !filteredStartsWith.contains(filterableItem)
                                && !filteredContains.contains(filterableItem)) {
                            filteredIdContains.add(filterableItem);
                        }
                    }

                    Collections.sort(filteredStartsWith, new ItemNameComparator());
                    Collections.sort(filteredIdContains, new ItemIdComparator());

                    results.values = new ArrayList<Item>();
                    ((List<Item>) results.values).addAll(filteredStartsWith);
                    ((List<Item>) results.values).addAll(filteredContains);
                    ((List<Item>) results.values).addAll(filteredIdContains);

                    results.count = ((List<Item>) results.values).size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredProducts = (List<Item>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void clearFilter() {
        filteredProducts = null;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case BACK_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_category_back_item, parent, false);
                BackHolder backHolder = new BackHolder(view);
                backHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearCategory();
                    }
                });
                return backHolder;
            case CATEGORY_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_category_item, parent, false);
                final CategoryHolder categoryHolder = new CategoryHolder(view);
                categoryHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectCategory(categories.get(categoryHolder.getAdapterPosition()));
                    }
                });
                return categoryHolder;
            case PRODUCT_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_product_item, parent, false);
                final ProductHolder productHolder = new ProductHolder(view);
                productHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item product;
                        if (filteredProducts != null) {
                            product = filteredProducts.get(productHolder.getAdapterPosition());
                        } else {
                            product = categoryProducts.get(productHolder.getAdapterPosition() - 1);
                        }
                        selectProduct(product);
                    }
                });
                return productHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BackHolder) {
            ((BackHolder) holder).nameView.setText(categoryName);
        } else if (holder instanceof CategoryHolder) {
            Group category = categories.get(position);
            ((CategoryHolder) holder).nameView.setText(category.name);
        } else if (holder instanceof ProductHolder) {
            Item product;
            if (filteredProducts != null) {
                product = filteredProducts.get(position);
            } else {
                product = categoryProducts.get(position - 1);
            }

            ((ProductHolder) holder).nameView.setText(product.name);
            ((ProductHolder) holder).idView.setText(String.format("#%04d", product.id));
            ((ProductHolder) holder).priceView.setText(String.format("%.2fzł", product.price));
        }
    }

    private void selectProduct(Item product) {
        if (listener != null) {
            listener.onProductSelected(product);
        }
    }

    private void selectCategory(Group category) {
        categoryId = category.id;
        categoryName = category.name;
        for (Item item : products) {
            if (item.groupId == categoryId) {
                categoryProducts.add(item);
            }
        }
        notifyDataSetChanged();
    }

    private void clearCategory() {
        categoryId = NO_CATEGORY;
        categoryProducts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (filteredProducts != null) {
            return filteredProducts.size();
        }

        if (categoryId == NO_CATEGORY) {
            return categories != null ? categories.size() : 0;
        }

        return categoryProducts != null ? categoryProducts.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (filteredProducts != null) {
            return PRODUCT_VIEW_TYPE;
        } else if (categoryId == NO_CATEGORY) {
            return CATEGORY_VIEW_TYPE;
        } else if (position == 0) {
            return BACK_VIEW_TYPE;
        } else {
            return PRODUCT_VIEW_TYPE;
        }
    }

    static class ItemNameComparator implements Comparator<Item> {

        @Override
        public int compare(Item o, Item t1) {
            return o.name.compareTo(t1.name);
        }
    }

    static class ItemIdComparator implements Comparator<Item> {

        @Override
        public int compare(Item o, Item t1) {
            return Integer.compare(o.id, t1.id);
        }
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView nameView;
        public final TextView priceView;
        public final TextView idView;

        public ProductHolder(View view) {
            super(view);
            this.view = view;
            nameView = (TextView) view.findViewById(R.id.name_text_view);
            priceView = (TextView) view.findViewById(R.id.price_text_view);
            idView = (TextView) view.findViewById(R.id.id_text_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameView.getText() + "'";
        }
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView nameView;

        public CategoryHolder(View view) {
            super(view);
            this.view = view;
            nameView = (TextView) view.findViewById(R.id.name_text_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameView.getText() + "'";
        }
    }

    public class BackHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView nameView;

        public BackHolder(View view) {
            super(view);
            this.view = view;
            nameView = (TextView) view.findViewById(R.id.name_text_view);
        }

        @Override
        public String toString() {
            return super.toString() + " 'BACK'";
        }
    }

    public interface Listener {

        void onProductSelected(Item product);
    }
}
