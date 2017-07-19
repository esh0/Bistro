package pl.sportdata.beestro.entities.base;

public final class BeestroObjectUtils {

    public static boolean isOpenPriceType(BeestroObject item) {
        return "O".equals(item.type);
    }

    public static boolean isTextType(BeestroObject item) {
        return "T".equals(item.type);
    }

    public static boolean isCardType(BeestroObject item) {
        return "K".equals(item.type);
    }
}
