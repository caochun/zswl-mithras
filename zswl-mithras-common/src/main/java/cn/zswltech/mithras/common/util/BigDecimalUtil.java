package cn.zswltech.mithras.common.util;

import cn.hutool.core.util.ObjectUtil;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/16 10:56
 */
public class BigDecimalUtil {

    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
        if (a == null || b == null) {
            return BigDecimal.ZERO;
        }
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return a.divide(b, scale, BigDecimal.ROUND_HALF_UP);
    }
    public static BigDecimal valueOf(Integer number){
        if(number == null){
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(number);
    }

    public static BigDecimal null2zero(BigDecimal number) {
        return Optional.ofNullable(number).orElse(BigDecimal.ZERO);
    }


    /**
     * 金额出库处理
     * 默认除以10000
     */
    public static BigDecimal li2Yuan(Long liAmount) {
        return new BigDecimal(liAmount)
                .divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * string类型转BigDecimal
     *
     * @param str
     * @return
     */
    public static BigDecimal stringToBigDecimal(String str) {
        if (ObjectUtil.isEmpty(str)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(str);
    }

}
