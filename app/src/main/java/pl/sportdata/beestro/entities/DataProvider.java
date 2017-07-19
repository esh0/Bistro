package pl.sportdata.beestro.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import pl.sportdata.beestro.entities.base.BaseItemContainer;
import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.entities.discounts.Discount;
import pl.sportdata.beestro.entities.groups.Group;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.entities.markups.Markup;
import pl.sportdata.beestro.entities.paymentTypes.PaymentType;
import pl.sportdata.beestro.entities.users.User;

public interface DataProvider {

    boolean hasData();

    void cleanUp();

    void registerPattern(int userId, @NonNull String userPass, @NonNull String pattern, @NonNull DataProviderCredentialsListener listener);

    void login(@NonNull String pattern, @NonNull DataProviderCredentialsListener listener);

    void login(int userId, @NonNull String userPass, @NonNull DataProviderCredentialsListener listener);

    void sync(@NonNull DataProviderSyncListener listener);

    void cleanSync(@NonNull DataProviderSyncListener listener);

    void cleanSyncObject(@NonNull DataProviderSyncListener listener);

    void createBillsForSplit(@NonNull List<Bill> bills, @NonNull DataProviderSyncListener listener);

    void moveBillsForSplit(@NonNull List<Bill> bills, int moveTo, @NonNull DataProviderSyncListener listener);

    void mergeBills(@NonNull Bill bill, @NonNull DataProviderSyncListener listener);

    @NonNull
    List<Group> getGroups();

    @NonNull
    List<Item> getItems();

    @NonNull
    List<Item> getItems(int groupId);

    @NonNull
    List<Bill> getBills();

    @NonNull
    List<User> getUsers();

    @Nullable
    User getUser(int userId);

    @NonNull
    BaseItemContainer<Markup> getMarkups();

    @NonNull
    BaseItemContainer<PaymentType> getPaymentTypes();

    @NonNull
    BaseItemContainer<Discount> getDiscounts();

    @Nullable
    Item getItem(int id);

    @Nullable
    Discount getDiscount(int id);

    @Nullable
    PaymentType getPaymentType(int id);

    @Nullable
    Markup getMarkup(int id);

    @Nullable
    Bill getBillByTableId(int tableId);

    @Nullable
    Bill getBillByTableIdGuestNumber(int tableId, int guestNumber);

    @Nullable
    String getMessage();

    @Nullable
    Bill getBillById(int billId);
}
