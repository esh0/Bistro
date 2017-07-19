package pl.sportdata.beestro.entities.paymentTypes;

import pl.sportdata.beestro.entities.base.BeestroObject;

public class PaymentType extends BeestroObject {

    public PaymentType(int id, String name, boolean hasExtras, String type, float price, int extras) {
        super(id, name, hasExtras, type, price, extras);
    }
}
