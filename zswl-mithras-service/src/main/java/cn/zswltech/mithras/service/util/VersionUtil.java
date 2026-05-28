package cn.zswltech.mithras.service.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 版本生成
 *
 * @author wangchuanhao
 * @date 2022/6/22 9:29 PM
 */
public class VersionUtil {

    public static String generateVersion(String oldVersion) {
        String nowDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int versionNum = 1;
        if (StringUtils.isNotBlank(oldVersion) && oldVersion.length() > 8) {
            versionNum = Integer.parseInt(oldVersion.substring(0, oldVersion.length() - 8)) + 1;
        }
        return String.format("%04d%s", versionNum, nowDateString);
    }

}
