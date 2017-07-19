package pl.sportdata.beestro.entities;

import android.content.Context;

import pl.sportdata.beestro.BuildConfig;

public class DataProviderFactory {

    private static final String MOCK_FLAVOR = "mock";
    private static final String PALMGIP_FLAVOR = "palmgip";
    private static DataProvider dataProvider;

    public static synchronized DataProvider getDataProvider(Context context) {
        if (dataProvider == null) {
            if (MOCK_FLAVOR.equals(BuildConfig.FLAVOR)) {
                dataProvider = new MockDataProvider(context);
            } else if (PALMGIP_FLAVOR.equals(BuildConfig.FLAVOR)) {
                dataProvider = new PalmGipDataProvider(context);
            } else {
                dataProvider = new EmptyDataProvider();
            }
        }

        return dataProvider;
    }

    public static void cleanUp() {
        dataProvider = null;
    }
}
