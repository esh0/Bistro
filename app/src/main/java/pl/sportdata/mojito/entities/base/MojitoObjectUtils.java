package pl.sportdata.mojito.entities.base;

public final class MojitoObjectUtils {

    public static boolean isOpenPriceType(MojitoObject item) {
        return "O".equals(item.type);
    }

    public static boolean isTextType(MojitoObject item) {
        return "T".equals(item.type);
    }

    public static boolean isCardType(MojitoObject item) {
        return "K".equals(item.type);
    }
}
