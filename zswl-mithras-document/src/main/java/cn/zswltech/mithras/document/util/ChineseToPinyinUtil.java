package cn.zswltech.mithras.document.util;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author bigbear
 * @date 2024/12/26 10:36
 * @description
 */
public class ChineseToPinyinUtil {
    public static String convertToPinyin(String chinese) {
        StringBuilder pinyin = new StringBuilder();
        char[] chars = chinese.toCharArray();

        for (char c : chars) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);

            if (pinyinArray != null) {
                String s = pinyinArray[0];
                pinyin.append(s.substring(0, s.length() - 1).toUpperCase());
            } else {
                pinyin.append(c);
            }
        }

        return pinyin.toString();
    }

    /**
     * @Author: ljh
     * @Description: 提取每个字符的首字母(大写)
     * @DateTime: 17:20 2023/4/27
     * @Params:
     * @Return
     */
    public static String getPinYinHeadChar(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        StringBuilder convert = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取字符的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert.append(pinyinArray[0].charAt(0));
            } else {
                convert.append(word);
            }
        }
//        去除字符中包含的空格
//        convert = convert.replace(" ","");
//        字符转小写
//        convert.toLowerCase();
        return convert.toString().toUpperCase();
    }
}
