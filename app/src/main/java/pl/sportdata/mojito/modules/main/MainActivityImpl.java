package pl.sportdata.mojito.modules.main;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import pl.sportdata.mojito.BuildConfig;
import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.bills.BillUtils;
import pl.sportdata.mojito.modules.base.BasePresenterActivity;
import pl.sportdata.mojito.modules.bills.BillsFragment;
import pl.sportdata.mojito.modules.sync.MergeBillsDialogFragment;
import pl.sportdata.mojito.modules.sync.SyncDialogFragment;
import pl.sportdata.mojito.widgets.NumberInputDialogFragment;

public class MainActivityImpl extends BasePresenterActivity
        implements MainActivity, NavigationView.OnNavigationItemSelectedListener, NumberInputDialogFragment.OnNumberInputDialogFragmentListener,
        BillsFragment.OnBillsFragmentInteractionListener, SyncDialogFragment.Listener, MergeBillsDialogFragment.Listener {

    private final MainActivityPresenter presenter = new MainActivityPresenter();
    private FloatingActionButton fab;
    private MenuItem syncMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().onFabClicked();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.version_text))
                .setText(String.format(Locale.getDefault(), "%s (%s)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    @Override
    protected MainActivityPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        syncMenuItem = menu.findItem(R.id.action_sync);
        getPresenter().onCreatedOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item.getItemId());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return presenter.onNavigationItemSelected(item.getItemId());
    }

    @Override
    public DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    @Override
    public void setDrawerLoggedUserLabel(String label) {
        ((TextView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.logged_user_text_view)).setText(label);
    }

    @Override
    public void showBillsMergeRequestDialog(final Bill sourceBill, final Bill targetBill) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setTitle(R.string.merge_bills)
                .setMessage(String.format(getString(R.string.bills_merge_prompt), BillUtils.getBillId(sourceBill), BillUtils.getBillId(targetBill)))
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onBillsMergeConfirmed(sourceBill, targetBill);
                    }
                }).setNegativeButton(R.string.undo, null);

        dialogBuilder.show();
    }

    @Override
    public void setCreateBillFabVisibility(boolean visibile) {
        fab.setVisibility(visibile ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setSyncButtonColor(@ColorInt int color) {
        if (syncMenuItem != null) {
            Drawable drawable = syncMenuItem.getIcon();
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, color);
                syncMenuItem.setIcon(drawable);
            }
        }
    }

    @Override
    public void showBillBlockedMessage(Bill bill) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setTitle(R.string.error)
                .setMessage(String.format(getString(R.string.bill_blocked), BillUtils.getBillId(bill))).setPositiveButton(R.string.ok, null);

        dialogBuilder.show();
    }

    @Override
    public void showNoPermission() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setTitle(R.string.error).setMessage(R.string.no_permissions)
                .setPositiveButton(R.string.ok, null);

        dialogBuilder.show();
    }

    @Override
    public void onPositiveButtonClicked(String value, String numberInputFragmentTag) {
        presenter.onPositiveButtonClicked(value, numberInputFragmentTag);
    }

    @Override
    public void onNegativeButtonClicked(String numberInputFragmentTag) {
        presenter.onNegativeButtonClicked(numberInputFragmentTag);
    }

    @Override
    public void onNeutralButtonClicked(String numberInputFragmentTag) {
        presenter.onNeutralButtonClicked(numberInputFragmentTag);
    }

    @Override
    public void onBillSelected(Bill bill, View view) {
        presenter.onBillSelected(bill, view);
    }

    @Override
    public void onBillsMergeRequest(Bill sourceBill, Bill targetBill) {
        presenter.onBillsMergeRequest(sourceBill, targetBill);
    }

    @Override
    public void onSyncFinished(@Nullable String error) {
        presenter.onSyncFinished(error);
    }

    @Override
    public void onMergeBillsFinished(@Nullable String error) {
        presenter.onMergeBillsFinished(error);
    }
}
