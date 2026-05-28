package cn.zswltech.mithras.blackgray.utils;

/**
 * @ClassName StringUtils
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/1/16 7:18 下午
 * @Version 1.0
 **/
public class StringUtils {

    public static String mysqlLimitOne() {
        return mysqlLimit(0, 1);
    }

    public static String mysqlLimit(int start, int size) {
        return String.format("limit %s, %s", start, size);
    }
}
