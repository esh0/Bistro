package pl.sportdata.mojito.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import pl.sportdata.mojito.entities.base.BaseItemContainer;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.discounts.Discount;
import pl.sportdata.mojito.entities.groups.Group;
import pl.sportdata.mojito.entities.items.Item;
import pl.sportdata.mojito.entities.markups.Markup;
import pl.sportdata.mojito.entities.paymentTypes.PaymentType;
import pl.sportdata.mojito.entities.users.User;

public class EmptyDataProvider implements DataProvider {

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void registerPattern(int userId, @NonNull String userPass, @NonNull String pattern, @NonNull DataProviderCredentialsListener listener) {

    }

    @Override
    public void login(@NonNull String pattern, @NonNull DataProviderCredentialsListener listener) {

    }

    @Override
    public void login(int userId, @NonNull String userPass, @NonNull DataProviderCredentialsListener listener) {

    }

    @Override
    public void sync(@NonNull DataProviderSyncListener listener) {
        listener.onSyncFinished(null);
    }

    @Override
    public void createBillsForSplit(@NonNull List<Bill> bills, @NonNull DataProviderSyncListener listener) {

    }

    @Override
    public void moveBillsForSplit(@NonNull List<Bill> bills, final int moveTo, @NonNull DataProviderSyncListener listener) {

    }

    @Override
    public void mergeBills(@NonNull List<Bill> bills, @NonNull DataProviderSyncListener listener) {

    }

    @NonNull
    @Override
    public List<Group> getGroups() {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<Item> getItems() {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<Item> getItems(int groupId) {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<Bill> getBills() {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public List<User> getUsers() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public User getUser(int userId) {
        return null;
    }

    @NonNull
    @Override
    public BaseItemContainer<Markup> getMarkups() {
        return new BaseItemContainer<>(Collections.<Markup>emptyList(), 0, null);
    }

    @NonNull
    @Override
    public BaseItemContainer<PaymentType> getPaymentTypes() {
        return new BaseItemContainer<>(Collections.<PaymentType>emptyList(), 0, null);
    }

    @NonNull
    @Override
    public BaseItemContainer<Discount> getDiscounts() {
        return new BaseItemContainer<>(Collections.<Discount>emptyList(), 0, null);
    }

    @Nullable
    @Override
    public Item getItem(int id) {
        return null;
    }

    @Nullable
    @Override
    public Discount getDiscount(int id) {
        return null;
    }

    @Nullable
    @Override
    public PaymentType getPaymentType(int id) {
        return null;
    }

    @Nullable
    @Override
    public Markup getMarkup(int id) {
        return null;
    }

    @Nullable
    @Override
    public Bill getBillByTableId(int tableId) {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public Bill getBillById(int billId) {
        return null;
    }
}
