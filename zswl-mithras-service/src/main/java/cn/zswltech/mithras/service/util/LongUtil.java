package cn.zswltech.mithras.service.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.others.Util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @create: 2022-08-23
 **/

public class LongUtil {

    public static Long null2zero(Long num) {
        return Optional.ofNullable(num).orElse(0L);
    }

    public static BigDecimal null2zeroBigDecimal(Long v) {
        return Optional.ofNullable(v).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    }

    public static Long keep2decimals(Long num) {
        if (num == null) {
            return 0L;
        }
        return Util.mithrasLong2BigDecimal(num).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10000)).longValue();
    }

    public static Integer null2zero(Integer num) {
        return Optional.ofNullable(num).orElse(0);
    }

    public static Long other2Long(String amount) {
        if (ObjectUtil.isNull(amount)) {
            return 0L;
        }
        return NumberUtil.mul(amount, GlobalConstants.MONEY_MULTIPLE).setScale(4, RoundingMode.HALF_UP).longValue();
    }

    //金额除10000
    public static BigDecimal tenThousand2Dollar(String amount) {
        if (ObjectUtil.isNull(amount) || "null".equals(amount)) {
            return BigDecimal.valueOf(0L).setScale(2, RoundingMode.HALF_UP);
        }
        // 如果是0的情况，会出现展示错误，所以这里做一下处理
        if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(0L).setScale(2, RoundingMode.HALF_UP);
        }
        return NumberUtil.div(amount, GlobalConstants.MONEY_MULTIPLE);
    }
    //金额除10000
    public static BigDecimal tenThousand2Dollar(Long amount){
        if (amount == null) {
            return null;
        }
        return tenThousand2Dollar(String.valueOf(amount));
    }
    public static Long add(Long aLong, Long aLong1) {
        return null2zero(aLong) + null2zero(aLong1);
    }
}
