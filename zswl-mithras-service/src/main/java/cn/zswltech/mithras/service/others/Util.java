package cn.zswltech.mithras.service.others;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.validation.ControllerMissParamException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;

/**
 * @author luyi
 */
@Slf4j
public class Util {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");

    public static BigDecimal sumBigDecimal(BigDecimal... candidates) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal b : candidates) {
            sum = sum.add(b);
        }
        return sum;
    }

    public static BigDecimal millimeterLong2WanBigDecimal(Long amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal millimeterLong2YuanBigDecimal(Long amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
    }

    @SneakyThrows
    public static void missRequiredParam(Boolean condition, String paramName) {
        if (Boolean.TRUE.equals(condition)) {
            throw new ControllerMissParamException(paramName);
        }
    }

    public static BigDecimal mithrasLong2BigDecimal(Long v, int scale, RoundingMode roundingMode) {
        if (null == v) {
            return null;
        }
        return BigDecimal.valueOf(v).divide(new BigDecimal(10000L), scale, roundingMode);
    }

    public static BigDecimal mithrasLong2BigDecimal(Long v) {
        if (null == v) {
            return null;
        }
        return new BigDecimal(v).divide(new BigDecimal(10000L));
    }

    public static BigDecimal mithrasLong2BigDecimalWY(Long v) {
        if (null == v) {
            return null;
        }
        return new BigDecimal(v).divide(new BigDecimal(100000000L));
    }

    public static BigDecimal mithrasInteger2BigDecimal(Integer v) {
        if (null == v) {
            return null;
        }
        return new BigDecimal(v).divide(new BigDecimal(10000L));
    }

    /**
     * 保留两位小数
     *
     * @param v
     * @return
     */
    public static Long mithrasLongDecimalTwo(Long v) {
        if (null == v) {
            return null;
        }
        return new BigDecimal(v).divide(new BigDecimal(10000L))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000L))
                .longValue();
    }

    public static Integer mithrasIntegerDecimalTwo(Integer v) {
        if (null == v) {
            return null;
        }
        return new BigDecimal(v).divide(new BigDecimal(10000L))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000L))
                .intValue();
    }

    public static String prefixNumberStr(String str) {
        if (null == str) {
            return null;
        }
        str = str.replace("-", "");
        if (StringUtils.isBlank(str)) {
            return null;
        }

        Matcher m = NUMBER_PATTERN.matcher(str);
        String result = "0";
        if (m.find()) {
            result = m.group(0);
        }
        return result;
    }

    public static Long toMithrasUnit(BigDecimal v) {
        if (null == v) {
            return null;
        }
        return v.multiply(new BigDecimal("10000")).longValue();
    }

    public static Long toMithrasUnit(Long v) {
        if (null == v) {
            return null;
        }
        return v * 10000L;
    }

    public static BigDecimal returnBigDecimal(String str) {
        str = prefixNumberStr(str);
        if (isBlank(str)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(str);
    }

    public static BigDecimal returnBigDecimal(String str, BigDecimal largeScale) {
        str = prefixNumberStr(str);
        if (isBlank(str)) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = new BigDecimal(str);
        if (null != largeScale) {
            result = result.multiply(largeScale);
        }
        return result;
    }

    @SneakyThrows
    public static void errLackData(boolean condition, String msg) {
        if (condition) {
            throw new LackDataException(msg);
        }
    }

    @SneakyThrows
    public static void errMissingParam(boolean condition, String field) {
        if (condition) {
            throw new ControllerMissParamException(field);
        }
    }

    public static void errMithras(boolean condition, String msg) {
        if (condition) {
            throw new MithrasException(msg);
        }
    }


    public static String extractKey(DuplicateKeyException e) {
        try {
            String forKey = "for key '";
            String s = e.getMessage();
            int i = s.indexOf(forKey);
            s = s.substring(i + forKey.length());
            int j = s.indexOf("'");
            return s.substring(0, j);
        } catch (Exception exception) {
            log.error("", exception);
            return "";
        }
    }


    /**
     * 18位身份证号 最后一位校验码 判断方法
     * 逻辑：
     * 1：身份证号前17位数分别乘不同的系数
     * 从第1位到17位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * 2：将乘积之和除以11，余数可能为：0 1 2 3 4 5 6 7 8 9 10
     * 3：根据余数，分别对应最后一位身份证号：1 0 X 9 8 7 6 5 4 3 2
     * 余数与校验码对应关系：0:1,1:0,2:X,3:9,4:8,5:7,6:6,7:5,8:4,9:3:10:2
     *
     * @param idCard 身份证号
     */
    public static boolean checkIDCard(String idCard) {
        if (idCard == null || idCard.equals("") || idCard.length() != 18) {
            return false;
        }

        char[] chars = idCard.toCharArray();
        int charsLength = chars.length - 1;
        int count = 0;
        for (int i = 0; i < charsLength; i++) {
            int charI = Integer.parseInt(String.valueOf(chars[i]));
            count += charI * (Math.pow(2, 17 - i) % 11);
        }
        String idCard18 = String.valueOf(chars[17]).toUpperCase();

        String idCardLast;
        switch (count % 11) {
            case 0:
                idCardLast = "1";
                break;

            case 1:
                idCardLast = "0";
                break;

            case 2:
                idCardLast = "X";
                break;

            default:
                idCardLast = 12 - (count % 11) + "";
                break;
        }
        return idCard18.equals(idCardLast);
    }

    /**
     * 判断第一个日期是否小于等于第二个日期
     *
     * @return
     */
    public static boolean dateLe(LocalDate d1, LocalDate d2) {
        return d1.isBefore(d2) || d1.isEqual(d2);
    }

    /**
     * 判断第一个日期是否大于等于第二个日期
     *
     * @return
     */
    public static boolean dateGe(LocalDate d1, LocalDate d2) {
        return d1.isAfter(d2) || d1.isEqual(d2);
    }

    public static String toYuan(Long dbNumber, boolean appendZero) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        if (appendZero) {
            return NumberUtil.decimalFormat(",##0.00##", bigDecimal);
        } else {
            return NumberUtil.decimalFormat(",##0.####", bigDecimal);
        }
    }

    public static String appendZero(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return NumberUtil.decimalFormat(",##0.00##", BigDecimal.valueOf(dbNumber));
    }

    public static String rateAppendZero(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return NumberUtil.decimalFormat("##0.00##", BigDecimal.valueOf(dbNumber));
    }


    public static String toRateWithoutSplit(Integer dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(1000000), 4, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toYuanWithoutSplit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toWanYuanWithoutSplit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toYiYuanWithoutSplit(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        return BigDecimal.valueOf(dbNumber).divide(new BigDecimal("1000000000000"), 2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toYuan(Long dbNumber) {
        return toYuan(dbNumber, false);
    }

    public static String toWanYuan(Long dbNumber, boolean appendZero) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        if (appendZero) {
            return NumberUtil.decimalFormat(",##0.00", bigDecimal.divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP));
        } else {
            return NumberUtil.decimalFormat(",##0.##", bigDecimal.divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP));
        }
    }

    public static String toWanYuan(Long dbNumber) {
        return toWanYuan(dbNumber, false);
    }

    /**
     * 金额为0时，返回空
     *
     * @param amountString
     * @return
     */
    public static String zeroToBlank(String amountString) {
        return CharSequenceUtil.equalsAny(amountString, "0", "0.0", "0.00") ? "" : amountString;
    }

    public static void validate(Object object) {
        Set<ConstraintViolation<Object>> validateResult = Validation.buildDefaultValidatorFactory().getValidator().validate(object);
        if (!validateResult.isEmpty()) {
            for (ConstraintViolation<Object> constraintViolation : validateResult) {
                throw new LackDataException(String.format("%s[%s]", constraintViolation.getMessage(), constraintViolation.getPropertyPath()));
            }
        }
    }

}
