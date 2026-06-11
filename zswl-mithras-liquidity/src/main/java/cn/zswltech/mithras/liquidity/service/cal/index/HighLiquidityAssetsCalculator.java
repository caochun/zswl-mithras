package cn.zswltech.mithras.liquidity.service.cal.index;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * 高流动性资产(万元)
 *
 * =计算起始日的非监管户资金余额（实际）+起止日时间范围内非监管户的租金流入合计+灵活授信
 * 备注：时间范围为指标模块左上角限定的起止时间
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class HighLiquidityAssetsCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        Map<Long, AccountBalanceBaseInfo> accountBalanceBaseInfoMap = LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO.get(bo.getQueryDateStart());
        // 计算起始日的非监管户资金余额（实际）无实际取预估
        BigDecimal one = BigDecimal.ZERO;
        if(CollectionUtil.isNotEmpty(accountBalanceBaseInfoMap)){
            one = accountBalanceBaseInfoMap.values().stream().filter(f -> {
                return !Objects.equals(BaseDataBankAccountTypeEnum.SUPERVISION.name(), f.getAccountType());
            }).map(m -> m.getActualBalanceAmount() != null ? m.getActualBalanceAmount() : Optional.ofNullable(m.getEstimateBalanceAmount()).orElse(0L))
                    .map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        // 起止日时间范围内非监管户的租金流入合计
        BigDecimal two = BigDecimal.ZERO;
        two = LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO.entrySet().stream().filter(f -> {
                    return !f.getKey().isBefore(bo.getQueryDateStart()) && !f.getKey().isAfter(bo.getQueryDateEnd());
        }).map(Map.Entry::getValue).map(Map::values).flatMap(Collection::stream).filter(f -> {
                    return !Objects.equals(BaseDataBankAccountTypeEnum.SUPERVISION.name(), f.getAccountType());
                }).map(m -> LongUtil.null2zeroBigDecimal(m.getRentReflowAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 灵活授信
        BigDecimal flexibleCredit = LongUtil.null2zeroBigDecimal(LiquidityIndicatorIndexHolder.PARAMETER_BASE_DETAIL.getFlexibleCredit());

        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(one.add(two).add(flexibleCredit) , LiquidityColorEnum.BLACK.name()));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_INDEX;
    }

    @Override
    public String indexName() {
        return "highLiquidityAssets";
    }

}
