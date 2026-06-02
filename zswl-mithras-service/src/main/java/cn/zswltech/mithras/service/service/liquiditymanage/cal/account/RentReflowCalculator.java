package cn.zswltech.mithras.service.service.liquiditymanage.cal.account;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.liquiditymanage.LiquidityIndicatorHolder;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.service.service.liquiditymanage.cal.bo.LiquidityAccountCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 租金回流
 *
 * 1. 资产合同状态=起息，租金回款账户=此账户，租金应付日=当天，逾期状态=未逾期的租金金额之和（如当天这期租金是该合同最后一期，需扣减剩余保证金，最小值为0）
 * 2. 资产合同状态=起息，租金应付日=当天，逾期状态=未逾期，核销的交易日在当天以前的已核销金额之和
 * 1-2得出租金回流金额
 * 备注：
 * 1. 如租金应付日是法定节假日（需增加法定节假日表）需归为节前最后一个工作日
 * 2. 除监管户外，其余默认回款至4595基本户（租后回款账户查询需落表或有公共方法）
 *
 * @author chenyifei
 * @since 2024/12/12
 */
@Component
public class RentReflowCalculator extends AbstractLiquidityCalculator<LiquidityAccountCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityAccountCalculatorBo bo) {
        List<LocalDate> localDateList = SpringContextHolder.getBean(BaseDataSpecialDateService.class).handleHoliday(LiquidityIndicatorHolder.BASE_DATA_SPECIAL_DATE, bo.getLocalDate());
        long resultSum = 0;
        // 休息日无租金回流
        if (CollectionUtil.isNotEmpty(localDateList)) {
            // 资产合同状态=起息，租金应付日=当天，逾期状态=未逾期，租金回款账户=此账户
            List<CollectionBaseInfo> collectionBaseInfoList = Optional.ofNullable(localDateList.stream().map(LiquidityIndicatorHolder.COLLECTION_BASE_INFO::get).filter(Objects::nonNull).collect(Collectors.toList()))
                .map(m -> m.stream().flatMap(Collection::stream).filter(f -> {
                      return !LiquidityIndicatorHolder.CONTRACT_IS_OVERDUE.contains(f.getContractId()) &&
                              Objects.equals(bo.getAccountBankId(), LiquidityIndicatorHolder.CONTRACT_ACCOUNT.get(f.getContractId()));
                }).collect(Collectors.toList())).orElse(null);

            if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
                long sum1 = collectionBaseInfoList.stream().mapToLong(m -> {
                    Integer maxPhase = LiquidityIndicatorHolder.COLLECTION_BASE_INFO_MAX_PHASE.get(m.getContractId());
                    if (Objects.equals(m.getPhase(), maxPhase)) {
                        // 扣除保证金
                        ContractPriceDetailRSP contractPriceDetail = LiquidityIndicatorHolder.CONTRACT_PRICE_DETAIL.get(m.getContractId());
                        Long earnestMoney = LongUtil.null2zero(contractPriceDetail.getEarnestMoney());
                        long result = LongUtil.null2zero(m.getPlanCollectionAmount()) - LongUtil.null2zero(earnestMoney);
                        return result >= 0 ? result : 0;
                    }
                    return LongUtil.null2zero(m.getPlanCollectionAmount());
                }).sum();

                // 实际核销记录 合同id，现金流编号为key
                Map<Long, Map<String, List<CollectionRecordInfo>>> recordInfoMap = collectionBaseInfoList.stream().distinct().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId,
                        Collectors.toMap(CollectionBaseInfo::getCode, collection -> {
                            return LiquidityIndicatorHolder.COLLECTION_RECORD_INFO.getOrDefault(collection.getId(), new ArrayList<>());
                            },(m1,m2) -> m1)));

                long sum2 = collectionBaseInfoList.stream().mapToLong(m -> {
                    List<CollectionRecordInfo> collectionRecordInfoList = recordInfoMap.getOrDefault(m.getContractId(), new HashMap<>()).get(m.getCode());
                    if(CollectionUtil.isNotEmpty(collectionRecordInfoList)){
                        return collectionRecordInfoList.stream().filter(f -> {
                            return f.getCollectionDate().isBefore(bo.getLocalDate());
                        }).mapToLong(m2 -> LongUtil.null2zero(m2.getCollectionAmount())).sum();
                    }
                    return 0;
                }).sum();
                resultSum = sum1 - sum2;
            }
        }
        ReflectUtil.setFieldValue(obj, indexName(), resultSum);

    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.ACCOUNT_BALANCE;
    }

    @Override
    public String indexName() {
        return "rentReflowAmount";
    }
}
