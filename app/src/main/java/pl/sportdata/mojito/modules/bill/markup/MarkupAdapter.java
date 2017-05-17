package pl.sportdata.mojito.modules.bill.markup;

import android.content.Context;

import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.markups.Markup;
import pl.sportdata.mojito.modules.bill.common.AbstractBillValueChangeableAdapter;
import pl.sportdata.mojito.modules.bill.common.AbstractSwipeableDataProvider;

class MarkupAdapter extends AbstractBillValueChangeableAdapter<Markup> {

    private final DataProvider entryDataProvider;

    public MarkupAdapter(Context context, AbstractSwipeableDataProvider dataProvider) {
        super(context, dataProvider);
        this.entryDataProvider = DataProviderFactory.getDataProvider(context);
    }

    @Override
    protected Markup getEntryItem(int itemId) {
        return entryDataProvider.getMarkup(itemId);
    }
}