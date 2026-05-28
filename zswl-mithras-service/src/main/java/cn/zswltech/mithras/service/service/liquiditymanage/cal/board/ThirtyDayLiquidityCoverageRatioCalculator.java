//package cn.zswltech.mithras.service.service.liquiditymanage.cal.board;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ReflectUtil;
//import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
//import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityIndexDetailRSP;
//import cn.zswltech.mithras.service.enums.basedata.BaseDataBankAccountTypeEnum;
//import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityColorEnum;
//import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
//import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
//import cn.zswltech.mithras.service.others.Util;
//import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorBoardHolder;
//import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
//import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityBoardCalculatorBo;
//import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityIndexCalculatorBo;
//import cn.zswltech.mithras.service.service.liquiditymanage.cal.index.HighLiquidityAssetsCalculator;
//import cn.zswltech.mithras.service.service.liquiditymanage.cal.index.HighLiquidityLiabilityCalculator;
//import cn.zswltech.mithras.service.service.liquiditymanage.cal.index.LiquidityCoverageRatioCalculator;
//import cn.zswltech.mithras.service.util.LongUtil;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//
//
///**
// * 流动性覆盖率（30天）(%)
// *
// * 计算公式同流动性指标中【流动性覆盖率】，时间范围为从当日起，30天内（含今天得30天）
// *
// * 注释：流动性覆盖率 = 高流动性资产/高流动性负债 --> 需要执行这三个指标的计算器获取值
// * @author chenyifei
// * @since 2024/12/20
// */
//@Component
//public class ThirtyDayLiquidityCoverageRatioCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {
//
//    @Resource
//    private HighLiquidityAssetsCalculator highLiquidityAssetsCalculator;
//    @Resource
//    private HighLiquidityLiabilityCalculator highLiquidityLiabilityCalculator;
//    @Resource
//    private LiquidityCoverageRatioCalculator liquidityCoverageRatioCalculator;
//
//    @Override
//    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
//        LocalDate dateEnd = bo.getQueryDate().plusDays(29);
//        LiquidityIndexCalculatorBo indexCalculatorBo = new LiquidityIndexCalculatorBo(bo.getQueryDate(), dateEnd);
//        LiquidityIndexDetailRSP indexRsp = new LiquidityIndexDetailRSP();
//        highLiquidityAssetsCalculator.calculate(indexRsp,indexCalculatorBo);
//        highLiquidityLiabilityCalculator.calculate(indexRsp,indexCalculatorBo);
//        liquidityCoverageRatioCalculator.calculate(indexRsp, indexCalculatorBo);
//        // 结果为百分比
//        LiquidityColorVo liquidityCoverageRatio = indexRsp.getLiquidityCoverageRatio();
//        BigDecimal result = Optional.ofNullable(liquidityCoverageRatio).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
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
//        return "thirtyDayLiquidityCoverageRatio";
//    }
//
//}
