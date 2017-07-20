package pl.sportdata.beestro.modules.bill.order;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.ExpandableWrapper;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.items.Item;

public class ProductExpandAdapter
        extends ExpandableRecyclerAdapter<ProductExpandAdapter.Group, List<Item>, ProductExpandAdapter.GroupViewHolder, ChildViewHolder>
        implements Filterable {

    private final LayoutInflater inflater;
    private final String pluFormat;
    private final Listener listener;
    private final List<Group> originalParentListItem;
    private final int productColumns;
    private List<Integer> colors;

    public ProductExpandAdapter(@NonNull Context context, @NonNull List<Group> parentItemList, @NonNull final Listener listener, int productColumns) {
        super(parentItemList);
        originalParentListItem = Collections.unmodifiableList(parentItemList);
        this.productColumns = productColumns;
        colors = calculateShades(Color.rgb(0xf5, 0x7f, 0x17), Color.rgb(0xff, 0xfd, 0xe7), parentItemList.size());
        int maxId = 0;
        for (Group group : parentItemList) {
            if (group.getChildList() != null) {
                for (List<Item> itemList : group.getChildList()) {
                    if (itemList != null) {
                        for (Item item : itemList) {
                            maxId = Math.max(maxId, item.id);
                        }
                    }
                }
            }
        }
        pluFormat = String.format("#%%0%sd", String.valueOf(maxId).length());
        inflater = LayoutInflater.from(context);
        setExpandCollapseListener(new ExpandCollapseListener() {
            @Override
            public void onParentExpanded(int parentPosition) {
                notifyParentChanged(parentPosition);
                listener.onGroupSelected(parentPosition);
            }

            @Override
            public void onParentCollapsed(int parentPosition) {
                notifyParentChanged(parentPosition);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = inflater.inflate(R.layout.list_bill_product_group, parentViewGroup, false);
        return new GroupViewHolder(view);
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        if (productColumns == 1) {
            return new ItemViewHolder(inflater.inflate(R.layout.list_bill_product_item, childViewGroup, false));
        } else {
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.list_bill_product_item_container, childViewGroup, false);
            view.setWeightSum(productColumns);
            List<ItemView> itemViews = new ArrayList<>(productColumns);
            for (int i = 0; i < productColumns; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                params.setMargins(1, 1, 1, 1);
                View itemView = inflater.inflate(R.layout.list_bill_product_item_grid, view, false);
                itemViews.add(new ItemView(itemView));
                view.addView(itemView, params);
            }
            return new GridItemViewHolder(view, itemViews);
        }
    }

    @Override
    public void onBindParentViewHolder(@NonNull GroupViewHolder parentViewHolder, int parentPosition, @NonNull Group parent) {
        parentViewHolder.itemView.setBackgroundColor(colors.get(getParentList().indexOf(parent)));
        parentViewHolder.nameTextView.setText(parent.getName());
        parentViewHolder.expandImageView.setImageResource(parentViewHolder.isExpanded() ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull List<Item> child) {
        if (productColumns == 1) {
            ItemView itemView = ((ItemViewHolder) childViewHolder).container;
            final Item item = child.get(0);
            itemView.nameTextView.setText(item.name);
            itemView.pluTextView.setText(String.format(pluFormat, item.id));
            itemView.priceTextView.setText(String.format(Locale.getDefault(), "%.2fzł", item.price));
            ((ItemViewHolder) childViewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProductSelected(item);
                }
            });
        } else {
            for (int i = 0; i < productColumns; i++) {
                View view = ((GridItemViewHolder) childViewHolder).container.getChildAt(i);
                if (i < child.size()) {
                    final Item item = child.get(i);
                    ItemView itemView = ((GridItemViewHolder) childViewHolder).itemViews.get(i);
                    itemView.nameTextView.setText(item.name);
                    itemView.pluTextView.setText(String.format(pluFormat, item.id));
                    itemView.priceTextView.setText(String.format(Locale.getDefault(), "%.2fzł", item.price));

                    view.setVisibility(View.VISIBLE);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onProductSelected(item);
                        }
                    });
                } else {
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private List<Integer> calculateShades(@ColorInt int fromColor, @ColorInt int toColor, int numberShades) {
        if (numberShades <= 0) {
            return Collections.emptyList();
        }
        //decompose color into RGB
        int redMax = Color.red(fromColor);
        int greenMax = Color.green(fromColor);
        int blueMax = Color.blue(fromColor);

        int redMin = Color.red(toColor);
        int greenMin = Color.green(toColor);
        int blueMin = Color.blue(toColor);

        //bin sizes for each color component
        int redDelta = (redMin - redMax) / numberShades;
        int greenDelta = (greenMin - greenMax) / numberShades;
        int blueDelta = (blueMin - blueMax) / numberShades;

        List<Integer> colors = new ArrayList<>();

        int redCurrent = redMax;
        int greenCurrent = greenMax;
        int blueCurrent = blueMax;

        //now step through each shade, and decrease darkness by adding color to it
        for (int i = 0; i < numberShades; i++) {

            //step up by the bin size, but stop at the max color component (255)
            redCurrent = redCurrent + redDelta < redMin ? redCurrent + redDelta : redMin;
            greenCurrent = greenCurrent + greenDelta < greenMin ? greenCurrent + greenDelta : greenMin;
            blueCurrent = blueCurrent + blueDelta < blueMin ? blueCurrent + blueDelta : blueMin;

            int nextShade = Color.rgb(redCurrent, greenCurrent, blueCurrent);
            colors.add(nextShade);
        }

        return colors;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                if (filterString != null && filterString.isEmpty()) {
                    results.values = Collections.unmodifiableList(originalParentListItem);
                    results.count = ((List<Group>) results.values).size();
                } else {
                    int count = originalParentListItem.size();
                    results.values = new ArrayList<Group>();

                    for (int i = 0; i < count; i++) {
                        final Group group = originalParentListItem.get(i);
                        final List<Item> filteredStartsWith = new ArrayList<>(0);
                        final List<Item> filteredContains = new ArrayList<>(0);
                        final List<Item> filteredIdContains = new ArrayList<>(0);

                        for (List<Item> itemList : group.getChildList()) {
                            if (itemList != null) {
                                for (Item item : itemList) {
                                    if (item.name.toLowerCase().startsWith(filterString)) {
                                        filteredStartsWith.add(item);
                                    }
                                    if (item.name.toLowerCase().contains(filterString) && !filteredStartsWith.contains(item)) {
                                        filteredContains.add(item);
                                    }
                                    if (String.valueOf(item.id).toLowerCase().contains(filterString) && !filteredStartsWith.contains(item) && !filteredContains
                                            .contains(item)) {
                                        filteredIdContains.add(item);
                                    }
                                }
                            }
                        }
                        List<Item> filteredItems = new ArrayList<>(0);
                        filteredItems.addAll(filteredStartsWith);
                        filteredItems.addAll(filteredContains);
                        filteredItems.addAll(filteredIdContains);

                        if (!filteredItems.isEmpty()) {
                            ProductExpandAdapter.Group listItem = new ProductExpandAdapter.Group(group.name, group.id, new ArrayList<List<Item>>(), true);
                            for (int j = 0, size = filteredItems.size(); j < size; j++) {
                                List<Item> items = new ArrayList<>(productColumns);
                                for (int k = 0; k < productColumns; k++) {
                                    if (j < size) {
                                        items.add(filteredItems.get(j++));
                                    }
                                }
                                listItem.getChildList().add(items);
                            }
                            ((List<Group>) results.values).add(listItem);
                        }
                    }

                    results.count = ((List<Group>) results.values).size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                colors = calculateShades(Color.rgb(0xf5, 0x7f, 0x17), Color.rgb(0xff, 0xfd, 0xe7), ((List<Group>) filterResults.values).size());
                setParentList((List<Group>) filterResults.values, false);
            }
        };
    }

    public void clearFilter() {
        colors = calculateShades(Color.rgb(0xf5, 0x7f, 0x17), Color.rgb(0xff, 0xfd, 0xe7), originalParentListItem.size());
        setParentList(originalParentListItem, false);
    }

    public List<ExpandableWrapper<Group, List<Item>>> getFlatPositions() {
        return mFlatItemList;
    }

    public static class Group implements Parent<List<Item>> {

        private final String name;
        private final List<List<Item>> itemsList;
        private final int id;
        private boolean initiallyExpanded;

        public Group(String name, int id, List<List<Item>> itemsList) {
            this.name = name;
            this.id = id;
            this.itemsList = itemsList;
        }

        public Group(String name, int id, List<List<Item>> itemsList, boolean initiallyExpanded) {
            this(name, id, itemsList);
            this.initiallyExpanded = initiallyExpanded;
        }

        @Override
        public List<List<Item>> getChildList() {
            return itemsList;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return initiallyExpanded;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Group group = (Group) o;

            if (id != group.id) {
                return false;
            }
            return name != null ? name.equals(group.name) : group.name == null;

        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + id;
            return result;
        }

        public int getId() {
            return id;
        }
    }

    public class GroupViewHolder extends ParentViewHolder {

        public ImageView expandImageView;
        public TextView nameTextView;

        public GroupViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            expandImageView = (ImageView) itemView.findViewById(R.id.expand_image_view);
        }
    }

    public class ItemViewHolder extends ChildViewHolder {

        public final ItemView container;

        public ItemViewHolder(View container) {
            super(container);
            this.container = new ItemView(container);
        }
    }

    public class GridItemViewHolder extends ChildViewHolder {

        public final List<ItemView> itemViews;
        public final LinearLayout container;

        public GridItemViewHolder(LinearLayout container, List<ItemView> itemViews) {
            super(container);
            this.container = container;
            this.itemViews = itemViews;
        }
    }

    public class ItemView {

        public final TextView nameTextView;
        public final TextView pluTextView;
        public final TextView priceTextView;

        public ItemView(View itemView) {
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            pluTextView = (TextView) itemView.findViewById(R.id.plu_text_view);
            priceTextView = (TextView) itemView.findViewById(R.id.price_text_view);
        }
    }

    public interface Listener {

        void onProductSelected(Item product);

        void onGroupSelected(int group);
    }
}
