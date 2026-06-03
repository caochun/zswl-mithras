//package cn.zswltech.mithras.liquiditymanage.service.cal.board;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ReflectUtil;
//import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
//import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardDetailRSP;
//import cn.zswltech.mithras.liquiditymanage.enums.LiquidityColorEnum;
//import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
//import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
//import cn.zswltech.mithras.service.others.Util;
//import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorBoardHolder;
//import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
//import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityBoardCalculatorBo;
//import cn.zswltech.mithras.service.util.LongUtil;
//import cn.zswltech.mithras.service.util.StringUtil;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.Map;
//import java.util.Optional;
//
//
///**
// * 流动性缺口(当天)(元)
// *
// * 预计回收租金-现金流支出
// * @author chenyifei
// * @since 2024/12/20
// */
//@Component
//public class DailyLiquidityGapCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {
//
//    @Override
//    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
//        LiquidityBoardDetailRSP rsp = (LiquidityBoardDetailRSP)obj;
//        BigDecimal expectedRentRecovery = Optional.ofNullable(rsp.getExpectedRentRecovery()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
//        BigDecimal cashFlowExpenditure = Optional.ofNullable(rsp.getCashFlowExpenditure()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
//        BigDecimal result = expectedRentRecovery.subtract(cashFlowExpenditure);
//        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name()));
//    }
//
//    @Override
//    public LiquidityIndexType model() {
//        return LiquidityIndexType.LIQUIDITY_BOARD;
//    }
//
//    @Override
//    public String indexName() {
//        return "dailyLiquidityGap";
//    }
//
//    @Override
//    public int sort() {
//        return 30;
//    }
//}
