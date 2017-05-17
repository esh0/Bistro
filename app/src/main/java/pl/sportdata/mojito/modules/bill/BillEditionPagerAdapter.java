package pl.sportdata.mojito.modules.bill;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.modules.bill.discount.DiscountFragment;
import pl.sportdata.mojito.modules.bill.markup.MarkupFragment;
import pl.sportdata.mojito.modules.bill.order.OrderFragment;
import pl.sportdata.mojito.modules.bill.payment.PaymentTypeFragment;

public class BillEditionPagerAdapter extends FragmentPagerAdapter {

    private final String[] titles;
    private final Listener listener;

    public BillEditionPagerAdapter(Context context, FragmentManager fm, Listener listener) {
        super(fm);
        titles = new String[]{context.getString(R.string.page_order), context.getString(R.string.page_discount), context.getString(R.string.page_markup),
                context.getString(R.string.page_payment)};
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OrderFragment.newInstance();
            case 1:
                return DiscountFragment.newInstance();
            case 2:
                return MarkupFragment.newInstance();
            case 3:
                return PaymentTypeFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = titles[position];
        if (listener != null) {
            int pageItemsCount = listener.getPageItemsCount(position);
            if (pageItemsCount > 0) {
                title += String.format(Locale.getDefault(), "\n[%s]", pageItemsCount);
            }
        }

        return title;
    }

    public interface Listener {

        int getPageItemsCount(int pageNumber);
    }
}
