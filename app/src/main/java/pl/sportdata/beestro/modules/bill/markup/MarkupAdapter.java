package pl.sportdata.beestro.modules.bill.markup;

import android.content.Context;

import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.markups.Markup;
import pl.sportdata.beestro.modules.bill.common.AbstractBillValueChangeableAdapter;
import pl.sportdata.beestro.modules.bill.common.AbstractSwipeableDataProvider;

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