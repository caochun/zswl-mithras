package cn.zswltech.mithras.capital.application.writeoff;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowMatchResult;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowTabMainInfo;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowTabRecord;
import cn.zswltech.mithras.third.service.FinanceFlowMatchResultService;
import cn.zswltech.mithras.third.service.FinanceFlowTabRecordService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.tuple.Triple;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author yangxiong
 * @date 2024/9/3/17:23
 * @description 自动核销的通用接口
 */
public interface CommonWriteOffService {
    /**
     * 配置项-自动核销开关
     */
    String FINANCE_FLOW_AUTO_WRITE_OFF_SIGN = "financeFlowAutoWriteOffSign";

    /**
     * 自动核销入口
     *
     * @param allFlowRecordList 此次全部银行流水
     */
    void autoWriteOffEntryPoint(@NotEmpty List<FinanceFlowRecord> allFlowRecordList, int plusDays);

    /**
     * 获取领域流水列表
     *
     * @param allFlowRecordList 此次全部银行流水
     * @return 领域流水列表
     */
    List<FinanceFlowRecord> getDomainFlowRecordList(@NotEmpty List<FinanceFlowRecord> allFlowRecordList);

    /**
     * 领域银行流水分组
     *
     * @param domainFlowRecordList 领域银行流水列表
     * @return 领域银行流水分组
     */
    Object domainBankFlowRecordGrouping(@NotEmpty List<FinanceFlowRecord> domainFlowRecordList);

    /**
     * 银行流水排序
     *
     * @param personalDomainFlowRecordList 个人领域银行流水列表
     */
    void sortBankFlowRecord(@NotEmpty List<FinanceFlowRecord> personalDomainFlowRecordList);

    /**
     * 根据领域银行流水获取账单列表, 未拆分到现金流
     *
     * @param personalDomainFlowRecordList 个人领域银行流水列表
     * @param plusDays                     前置天数
     * @return 领域账单列表
     */
    List<?> getBusinessFlowRecordList(@NotEmpty List<FinanceFlowRecord> personalDomainFlowRecordList, int plusDays);

    /**
     * 领域账单分组
     *
     * @param businessFlowRecords 领域账单列表
     */
    Map<Long, List<FinanceFlowMatchResult>> domainFlowRecordGrouping(@NotEmpty List<?> businessFlowRecords);

    /**
     * 账单列表排序
     *
     * @param businessFlowRecords 账单列表
     * @return 排序后的账单列表
     */
    List<FinanceFlowMatchResult> sortBusinessFlowRecord(@NotEmpty List<FinanceFlowMatchResult> businessFlowRecords);

    /**
     * 根据银行流水和账单匹配，结合【领域账单分组使用】，返回的所有记录不存在ID的，需要自己处理关联关系
     *
     * @param domainFlowRecordMap   银行流水列表
     * @param businessFlowRecordMap 领域账单分组
     * @return 匹配结果
     */
    List<Triple<FinanceFlowTabMainInfo/*tab信息*/, List<FinanceFlowTabRecord>/*tab流水信息*/, List<FinanceFlowMatchResult>>/*账单匹配列表*/> match(@NotNull Object domainFlowRecordMap, Map<Long, @NotEmpty List<FinanceFlowMatchResult>> businessFlowRecordMap);

    /**
     * 将账单和流水金额匹配，直接关联上流水ID和金额
     *
     * @param financeFlowMatchResults 账单列表
     * @param financeFlowTabRecords   银行流水列表
     */
    void matchAmount(@NotEmpty List<FinanceFlowTabRecord> financeFlowTabRecords, @NotEmpty List<FinanceFlowMatchResult> financeFlowMatchResults);

    /**
     * 实际核销 里面已经关联银行流水，直接对冲就可以了
     *
     * @param financeFlowMatchResults 领域账单列表
     * @return 核销完毕的银行流水
     */
    List<Long> actualWriteOff(@NotEmpty List<FinanceFlowTabRecord> financeFlowTabRecords, List<FinanceFlowMatchResult> financeFlowMatchResults);

    /**
     * 校验是否完美匹配
     */
    default Boolean checkPerfectMatch(Long mainId) {
        List<FinanceFlowTabRecord> financeFlowTabRecords = SpringUtil.getBean(FinanceFlowTabRecordService.class).list(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                .eq(FinanceFlowTabRecord::getMainId, mainId));
        List<FinanceFlowMatchResult> financeFlowMatchResults = SpringUtil.getBean(FinanceFlowMatchResultService.class).list(Wrappers.<FinanceFlowMatchResult>lambdaQuery()
                .eq(FinanceFlowMatchResult::getMainId, mainId));
        long financeFlowSum = financeFlowTabRecords.stream().mapToLong(o -> LongUtil.null2zero(o.getSurplusAmount())).summaryStatistics().getSum();
        long businessFlowSum = 0;
        long shouldWriteOffAmount = 0;
        if (CollUtil.isNotEmpty(financeFlowMatchResults)) {
            businessFlowSum = financeFlowMatchResults.stream().mapToLong(o -> LongUtil.null2zero(o.getThisWriteOffAmount())).summaryStatistics().getSum();
            shouldWriteOffAmount = financeFlowMatchResults.stream().mapToLong(o -> LongUtil.null2zero(o.getShouldWriteOffAmount())).summaryStatistics().getSum();
        }

        return financeFlowSum == businessFlowSum && businessFlowSum == shouldWriteOffAmount;
    }
}
