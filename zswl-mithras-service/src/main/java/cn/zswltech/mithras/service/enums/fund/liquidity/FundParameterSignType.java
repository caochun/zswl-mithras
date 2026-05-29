package cn.zswltech.mithras.service.enums.fund.liquidity;

import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.factory.enums.RatingLevelEnum;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: chenyifei
 */
@AllArgsConstructor
@Getter
public enum FundParameterSignType implements PullDown {
    /**
     *
     */
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    EQ("="),
    NE("!="),
    ;

    private String display;

    public static FundParameterSignType findByDisplay(String display) {
        for (FundParameterSignType item : values()) {
            if (item.getDisplay().equals(display)) {
                return item;
            }
        }
        return null;
    }


    @Override
    public String display() {
        return display;
    }


    // 赶时间，先写个方法用，后续空了再优化成表达式
    public static Boolean compare(BigDecimal value, BigDecimal targetValue, String sign){
        FundParameterSignType signType = FundParameterSignType.findByDisplay(sign);
        if(signType != null) {
            switch (signType) {
                case GT:
                    return value.compareTo(targetValue) > 0;
                case LT:
                    return value.compareTo(targetValue) < 0;
                case GE:
                    return value.compareTo(targetValue) >= 0;
                case LE:
                    return value.compareTo(targetValue) <= 0;
                case EQ:
                    return value.compareTo(targetValue) == 0;
                case NE:
                    return value.compareTo(targetValue) != 0;
                default:
            }
        }
        throw new MithrasException("流动性管理-表达式计算异常");
    }

    public static String getColor(BigDecimal value, ParameterIndexDetailRSP indexDetail){
        if(FundParameterSignType.compare(value ,indexDetail.getRedValue(), indexDetail.getRedSign())){
            return LiquidityColorEnum.RED.name();
        }else {
            if(FundParameterSignType.compare(value ,indexDetail.getYellowValue(), indexDetail.getYellowSign())){
                return LiquidityColorEnum.ORANGE.name();
            }
        }
        return LiquidityColorEnum.BLACK.name();
    }




}
