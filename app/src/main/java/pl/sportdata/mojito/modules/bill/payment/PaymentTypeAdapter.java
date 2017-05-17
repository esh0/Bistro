package pl.sportdata.mojito.modules.bill.payment;

import android.content.Context;

import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.paymentTypes.PaymentType;
import pl.sportdata.mojito.modules.bill.common.AbstractBillValueChangeableAdapter;
import pl.sportdata.mojito.modules.bill.common.AbstractSwipeableDataProvider;

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