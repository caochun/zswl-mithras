package cn.zswltech.mithras.ftp.newftp.convert;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/21 13:21
 */
@Component
public class NewFtpTypeConversionWorker {

    @Named("bigDecimalToInt")
    public Integer bigDecimalToInt(BigDecimal value) {
        value = value.multiply(new BigDecimal(10000))
                .setScale(0, RoundingMode.HALF_UP);
        return value.intValue();
    }
}
