package pl.sportdata.beestro.entities.sync;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pl.sportdata.beestro.entities.base.BaseItemContainer;
import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.entities.configuration.Configuration;
import pl.sportdata.beestro.entities.discounts.Discount;
import pl.sportdata.beestro.entities.groups.Group;
import pl.sportdata.beestro.entities.markups.Markup;
import pl.sportdata.beestro.entities.parameters.Parameters;
import pl.sportdata.beestro.entities.paymentTypes.PaymentType;
import pl.sportdata.beestro.entities.users.User;

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
            List<Group> groups, List<Bill> bills, String messages, Configuration configuration, Parameters parameters) {
        this.users = users;
        this.markups = markups;
        this.discounts = discounts;
        this.paymentTypes = paymentTypes;
        this.groups = groups;
        this.bills = bills;
        this.messages = messages;
        this.configuration = configuration;
        this.parameters = parameters;
    }

    public static final SyncObject getEmpty() {
        return new SyncObject(null, null, null, null, null, null, null, null, null);
    }
}
