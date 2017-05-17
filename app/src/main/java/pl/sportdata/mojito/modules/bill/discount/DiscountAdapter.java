package pl.sportdata.mojito.modules.bill.discount;

import android.content.Context;

import pl.sportdata.mojito.entities.discounts.Discount;
import pl.sportdata.mojito.modules.bill.common.AbstractBillValueChangeableAdapter;
import pl.sportdata.mojito.modules.bill.common.AbstractSwipeableDataProvider;

class DiscountAdapter extends AbstractBillValueChangeableAdapter<Discount> {

    public DiscountAdapter(Context context, AbstractSwipeableDataProvider dataProvider) {
        super(context, dataProvider);
    }

    @Override
    protected Discount getEntryItem(int itemId) {
        return entryDataProvider.getDiscount(itemId);
    }
}