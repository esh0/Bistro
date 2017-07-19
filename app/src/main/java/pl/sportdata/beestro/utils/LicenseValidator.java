package pl.sportdata.beestro.utils;

import java.util.Date;

import pl.sportdata.beestro.BuildConfig;

public class LicenseValidator {

    private static final long SECONDS_IN_MILLI = 1000;
    private static final long MINUTES_IN_MILLI = SECONDS_IN_MILLI * 60;
    private static final long HOURS_IN_MILLI = MINUTES_IN_MILLI * 60;
    private static final long DAYS_IN_MILLI = HOURS_IN_MILLI * 24;

    private static final long CHECK_INTERVAL = DAYS_IN_MILLI;
    private static final long CHECK_MAX_VALUE = 30;

    public static boolean isLicenseValid() {
        boolean isLicenseValid = false;
        try {
            Date buildDate = new Date(Long.parseLong(BuildConfig.BUILD_TIME));
            if (buildDate != null) {
                Date now = new Date();
                long diff = now.getTime() - buildDate.getTime();
                isLicenseValid = diff / CHECK_INTERVAL < CHECK_MAX_VALUE;
            }
        } catch (NumberFormatException ignored) {

        }
        return isLicenseValid;
    }
}
