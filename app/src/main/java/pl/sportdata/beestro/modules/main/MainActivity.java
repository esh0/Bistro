package pl.sportdata.beestro.modules.main;

import android.support.annotation.ColorInt;
import android.support.v4.widget.DrawerLayout;

import pl.sportdata.beestro.entities.bills.Bill;

public interface MainActivity {

    DrawerLayout getDrawerLayout();

    void setDrawerLoggedUserLabel(String label);

    void showBillsMergeRequestDialog(Bill sourceBill, Bill targetBill);

    void setCreateBillFabVisibility(boolean visibile);

    void setSyncButtonColor(@ColorInt int color);

    void showBillBlockedMessage(Bill bill);

    void showNoPermission();

    void showBillExistsForTable(String tableNumber);

    void showInvalidTableNumber();
}
