package cn.zswltech.mithras.service.service.liquiditymanage.cal.index;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityColorEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 资产久期
 *
 * 数据范围：资产合同状态=起租，应付日＞计算日的资产合同
 * 1. 单个合同的久期：sum（每期应偿还租金*（应付日-计算日））/计算日（不含计算日当天）以后剩余未付租金合计/365）
 * 2. 单个合同的久期根据未付租金合计值进行加权计算得出资产久期：sum（每个合同计算日（不含计算日当天）以后未付租金合计*合同久期）/全部合同计算日（不含计算日当天）以后未付租金合计
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class DurationAssetsCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        BigDecimal result = BigDecimal.ZERO;
        // 取收款表，改为合同维度
        Map<Long, List<CollectionBaseInfo>> collectionMap = LiquidityIndicatorIndexHolder.COLLECTION_BASE_INFO.entrySet().stream().filter(f -> f.getKey().isAfter(bo.getQueryDateStart()))
                .map(Map.Entry::getValue).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

        // 实际核销记录 合同id，现金流编号为key
        Map<Long, Map<String, List<CollectionRecordInfo>>> recordInfoMap = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, item -> {
            return item.getValue().stream().filter(Objects::nonNull)
                    .collect(Collectors.toMap(CollectionBaseInfo::getCode, collection -> {
                        return LiquidityIndicatorIndexHolder.COLLECTION_RECORD_INFO.getOrDefault(collection.getId(), new ArrayList<>());
                    },(m1,m2) -> m1));
        }));


        Map<Long, BigDecimal> durationMap = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, collection -> {
            // sum(每期应偿还本息 * （应付日-计算日）)
            return collection.getValue().stream().map(m -> {
                long gapDay = ChronoUnit.DAYS.between(bo.getQueryDateStart(), m.getPlanCollectionDate());
                return BigDecimal.valueOf(LongUtil.null2zero(m.getPlanCollectionAmount())).multiply(BigDecimal.valueOf(gapDay));
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
        }));
        // 计算日（不含计算日当天）以后剩余未付本息合计
        Map<Long, BigDecimal> remainingMap = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, contractActual -> {
            Map<String, List<CollectionRecordInfo>> recordMap = recordInfoMap.getOrDefault(contractActual.getKey(), new HashMap<>());
            return contractActual.getValue().stream().map(flowPlan -> {
                BigDecimal remainingAmount = BigDecimal.valueOf(LongUtil.null2zero(flowPlan.getPlanCollectionAmount()));
                // 处理单笔现金流
                List<CollectionRecordInfo> collectionRecordInfoList = recordMap.get(flowPlan.getCode());
                if (CollectionUtil.isNotEmpty(collectionRecordInfoList)) {
                    long alreadyWriteOffAmount = collectionRecordInfoList.stream().filter(f -> Objects.nonNull(f.getCollectionAmount())).mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
                    remainingAmount = BigDecimal.valueOf(LongUtil.null2zero(flowPlan.getPlanCollectionAmount()) - alreadyWriteOffAmount);
                }
                return remainingAmount;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
        }));
        // 每个合同计算日后未付本息合计*每个合同的久期 = 每期应偿还本息*（应付日-计算日））/365
        BigDecimal reduce = durationMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        // 全部合同计算日以后未付本息合计
        BigDecimal remainingSum = remainingMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if(remainingSum.compareTo(BigDecimal.ZERO) != 0) {
            result = reduce.divide(BigDecimal.valueOf(365).multiply(remainingSum), 10, RoundingMode.HALF_UP);
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name()));
    }




    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_INDEX;
    }

    @Override
    public String indexName() {
        return "durationAssets";
    }
}
