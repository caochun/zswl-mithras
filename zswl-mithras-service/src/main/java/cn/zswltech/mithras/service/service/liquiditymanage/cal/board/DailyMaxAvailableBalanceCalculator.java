package cn.zswltech.mithras.liquiditymanage.service.cal.board;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardDetailRSP;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorBoardHolder;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityDailyMaxBalanceCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorBoardHolder.ACCOUNT_BALANCE_BASE_INFO;


/**
 * 当日最大可用余额(元)
 * <p>
 * 取值逻辑详见：流动性管理需求或技术方案
 * 附加逻辑：
 * - 如果金额为负值则显示红色字体
 * - 【当日最大可用余额】支持点击弹窗支持编辑预测范围，并拼接展示到字段里如编辑为10天，则展示：当日最大可用余额（10天内）
 *
 * @author chenyifei
 * @since 2024/12/20
 */
@Component
public class DailyMaxAvailableBalanceCalculator extends AbstractLiquidityCalculator<LiquidityDailyMaxBalanceCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityDailyMaxBalanceCalculatorBo bo) {
        LocalDate now = LocalDate.now();
        BigDecimal result;

        LiquidityBoardDetailRSP detailRsp = bo.getDetailMap().get(bo.getQueryDate());
        // 【非监管户资金】+【监管户资金的负值】20250221 调整：非监管户余额改为非监管户余额-非监管户受限余额的差计算
        BigDecimal nonSupervisedAccountFunds = Optional.ofNullable(detailRsp.getNonSupervisedAccountFunds()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        // 从 LiquidityIndicatorBoardHolder 中取出【非监管户受限余额】减掉
        Map<Long, AccountBalanceBaseInfo> accountInfoMap = ACCOUNT_BALANCE_BASE_INFO.get(bo.getQueryDate());
        // 需要过滤掉监管户
        AtomicReference<BigDecimal> estimateBalanceLimitAmountSum = new AtomicReference<>(BigDecimal.ZERO);
        accountInfoMap.values().stream().filter(account -> !Objects.equals(BaseDataBankAccountTypeEnum.SUPERVISION.name(), account.getAccountType()))
                .map(account -> {
                    // 优先取编辑字段, 这里可能会溢出
                    return new BigDecimal(LongUtil.null2zero(account.getEstimateBalanceLimitEditAmount() != null ? account.getEstimateBalanceLimitEditAmount() : account.getEstimateBalanceLimitAmount()));
                }).forEach(decimal -> estimateBalanceLimitAmountSum.set(estimateBalanceLimitAmountSum.get().add(decimal)));
        BigDecimal negativeSupervisedAccountFunds = Optional.ofNullable(detailRsp.getNegativeSupervisedAccountFunds()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        BigDecimal one = nonSupervisedAccountFunds.add(negativeSupervisedAccountFunds).subtract(estimateBalanceLimitAmountSum.get());
        if (one.compareTo(BigDecimal.ZERO) >= 0) {
            result = methodOne(bo, estimateBalanceLimitAmountSum.get());
        } else {
            LocalDate calculateBeforeDay = bo.getQueryDate().minusDays(1);
            // 系统当天到计算日前一天内【可用余额】为负值之和的计算结果
            BigDecimal balanceAmountSum = bo.getDetailMap().entrySet().stream().filter(f -> {
                return !f.getKey().isBefore(now) && !f.getKey().isAfter(calculateBeforeDay);
            }).map(m -> {
                BigDecimal balanceAmount = Optional.ofNullable(m.getValue().getDailyMaxAvailableBalance()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
                return balanceAmount.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ZERO : balanceAmount;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (one.subtract(balanceAmountSum).compareTo(BigDecimal.ZERO) >= 0) {
                result = methodOne(bo, estimateBalanceLimitAmountSum.get());
            } else {
                //【非监管户资金】+【监管户资金的负值】 - 系统当天到计算日前一天内【可用余额】为负值之和的计算结果 - 安全库存
                BigDecimal saveStock = BigDecimal.valueOf(LongUtil.null2zero(LiquidityIndicatorBoardHolder.PARAMETER_BASE_DETAIL.getSaveStock()));
                result = one.subtract(balanceAmountSum).subtract(saveStock);
            }
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, result.compareTo(BigDecimal.ZERO) >= 0 ? LiquidityColorEnum.BLACK.name() : LiquidityColorEnum.RED.name(), 1));
    }


    public BigDecimal methodOne(LiquidityDailyMaxBalanceCalculatorBo bo, BigDecimal estimateBalanceLimitAmountSum) {
        /**
         * old: 未来n天内（含当天）【非监管户资金】+【监管户资金的负值】之和的最小值 - 安全库存
         * 20250220调整：当日最大可用余额计算逻辑调整：非监管余额改为非监管户余额-非监管户受限余额的差计算
         */
        Map<LocalDate, LiquidityBoardDetailRSP> detailMap = bo.getDetailMap();
        LocalDate endDate = bo.getQueryDate().plusDays(bo.getPredictDay() - 1);
        BigDecimal one = null;
        LocalDate calculateDate = bo.getQueryDate();
        while (!calculateDate.isAfter(endDate)) {
            LiquidityBoardDetailRSP detailRsp = detailMap.get(calculateDate);
            BigDecimal nonSupervisedAccountFunds = Optional.ofNullable(detailRsp.getNonSupervisedAccountFunds()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
            BigDecimal negativeSupervisedAccountFunds = Optional.ofNullable(detailRsp.getNegativeSupervisedAccountFunds()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
            BigDecimal bigdecimal = nonSupervisedAccountFunds.add(negativeSupervisedAccountFunds).subtract(estimateBalanceLimitAmountSum);
            if (one != null) {
                one = one.compareTo(bigdecimal) > 0 ? bigdecimal : one;
            } else {
                one = bigdecimal;
            }
            calculateDate = calculateDate.plusDays(1);
        }

        BigDecimal saveStock = BigDecimal.valueOf(LongUtil.null2zero(LiquidityIndicatorBoardHolder.PARAMETER_BASE_DETAIL.getSaveStock()));
        return one.subtract(saveStock);
    }


    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "dailyMaxAvailableBalance";
    }

    @Override
    public int sort() {
        return 40;
    }
}
