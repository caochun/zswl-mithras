package cn.zswltech.mithras.dashboard.application.util;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @ClassName DashboardExportUtil
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/11 10:50 上午
 * @Version 1.0
 **/
public class DashboardExportUtil {

    private static final String HUNDRED = "%";

    public static String valueUnitDTO2String(ValueUnitDTO valueUnit){
        return Optional.ofNullable(valueUnit).map(ValueUnitDTO::getValue).map(e -> ObjectUtil.isEmpty(e) ? null :  new BigDecimal(e).setScale(2, RoundingMode.HALF_UP).toPlainString()).orElse(null);
    }

    //拼接%
    public static String spliceHundred(Object o){
        if(ObjectUtil.isEmpty(o)){
            return null;
        }
        return o.toString() + HUNDRED;
    }

}
