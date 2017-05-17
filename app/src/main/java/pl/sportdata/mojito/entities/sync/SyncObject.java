package pl.sportdata.mojito.entities.sync;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pl.sportdata.mojito.entities.base.BaseItemContainer;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.configuration.Configuration;
import pl.sportdata.mojito.entities.discounts.Discount;
import pl.sportdata.mojito.entities.groups.Group;
import pl.sportdata.mojito.entities.markups.Markup;
import pl.sportdata.mojito.entities.parameters.Parameters;
import pl.sportdata.mojito.entities.paymentTypes.PaymentType;
import pl.sportdata.mojito.entities.users.User;

public class SyncObject implements Serializable {

    public final List<User> users;
    public final BaseItemContainer<Markup> markups;
    public final BaseItemContainer<Discount> discounts;
    @SerializedName("payment_types")
    public final BaseItemContainer<PaymentType> paymentTypes;
    public final List<Group> groups;
    public final String messages;
    public final Configuration configuration;
    public List<Bill> bills;
    public Parameters parameters;

    public SyncObject(List<User> users, BaseItemContainer<Markup> markups, BaseItemContainer<Discount> discounts, BaseItemContainer<PaymentType> paymentTypes,
            List<Group> groups, List<Bill> bills, String messages, Configuration configuration) {
        this.users = users;
        this.markups = markups;
        this.discounts = discounts;
        this.paymentTypes = paymentTypes;
        this.groups = groups;
        this.bills = bills;
        this.messages = messages;
        this.configuration = configuration;
    }

    public static final SyncObject getEmpty() {
        return new SyncObject(null, null, null, null, null, null, null, null);
    }
}
