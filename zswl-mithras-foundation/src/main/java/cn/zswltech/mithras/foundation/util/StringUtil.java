package cn.zswltech.mithras.foundation.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
public class StringUtil {

    private static Pattern numPattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    public static void main(String[] args) {
        System.out.println(getDecimalDigits("11.983232"));
    }

    public static int getDecimalDigits(String number) {
        if (!number.contains(".")) {
            return 0;
        }
        String[] array = number.split("\\.");
        String decimalPart = array[1];
        return decimalPart.toCharArray().length;
    }

    public static String mysqlLimitOne() {
        return mysqlLimit(0, 1);
    }

    public static String mysqlLimit(int start, int size) {
        return String.format("limit %s, %s", start, size);
    }

    public static String mysqlJsonContain(String columnName, String express) {
        return String.format("JSON_CONTAINS(%s, %s)", columnName, express);
    }

    public static String formatExcelFormula(String express) {
        if (StrUtil.isBlank(express)) {
            return express;
        }
        return express.replaceAll("if", "excel_if").replaceAll("and", "excel_and").replaceAll("or", "excel_or");
    }

    public static boolean isNum(String str) {
        if (str == null || str.length() == 0) {
            return false; // 字符串为空或长度为0，不是数字
        }
        Matcher isNum = numPattern.matcher(str);
        return isNum.matches();
    }

    public static String null2Space(String str) {
        return str == null ? " " : str;
    }

    public static String null2Zero(String str) {
        return str == null ? "0" : str;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static String getFirstLetters(String name) {
        StringBuilder sb = new StringBuilder();
        for (char aChar : name.toCharArray()) {
            sb.append(PinyinUtil.getFirstLetter(aChar));
        }
        return sb.toString();
    }
}
