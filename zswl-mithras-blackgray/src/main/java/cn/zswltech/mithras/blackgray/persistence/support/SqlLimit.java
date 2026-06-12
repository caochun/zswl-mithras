package cn.zswltech.mithras.blackgray.persistence.support;

public final class SqlLimit {

    private SqlLimit() {
    }

    public static String one() {
        return limit(0, 1);
    }

    public static String limit(int start, int size) {
        return String.format("limit %s, %s", start, size);
    }
}
