package pl.sportdata.mojito.entities.paymentTypes;

import pl.sportdata.mojito.entities.base.MojitoObject;

public class PaymentType extends MojitoObject {

    public PaymentType(int id, String name, boolean hasExtras, String type, float price, int extras) {
        super(id, name, hasExtras, type, price, extras);
    }
}
