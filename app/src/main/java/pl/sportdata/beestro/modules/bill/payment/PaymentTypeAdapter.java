package pl.sportdata.beestro.modules.bill.payment;

import android.content.Context;

import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.paymentTypes.PaymentType;
import pl.sportdata.beestro.modules.bill.common.AbstractBillValueChangeableAdapter;
import pl.sportdata.beestro.modules.bill.common.AbstractSwipeableDataProvider;

class PaymentTypeAdapter extends AbstractBillValueChangeableAdapter<PaymentType> {

    private final DataProvider entryDataProvider;

    public PaymentTypeAdapter(Context context, AbstractSwipeableDataProvider dataProvider) {
        super(context, dataProvider);
        this.entryDataProvider = DataProviderFactory.getDataProvider(context);
    }

    @Override
    protected PaymentType getEntryItem(int itemId) {
        return entryDataProvider.getPaymentType(itemId);
    }
}