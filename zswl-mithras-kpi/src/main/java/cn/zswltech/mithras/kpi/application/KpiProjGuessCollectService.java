package cn.zswltech.mithras.kpi.application;

import cn.zswltech.mithras.kpi.bo.KpiFinanceProjectProfitRecordBo;
import cn.zswltech.mithras.kpi.bo.KpiParameterConfigBo;
import cn.zswltech.mithras.kpi.bo.KpiPaymentAmountBo;
import cn.zswltech.mithras.kpi.bo.KpiProjectDistributionRecordBo;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2024/9/26 09:15
 * 用于收集/管理所有绩效测试表需要的业务数据，
 **/
public interface KpiProjGuessCollectService {

    //项目利润分配表
    List<KpiFinanceProjectProfitRecordBo> getProjectProfitRecords(List<Long> contractId, Integer year, Integer month, boolean flash);

    //提奖比例
    KpiParameterConfigBo getParameterConfig(LocalDate date, boolean flash);

    //项目分配权重
    List<KpiProjectDistributionRecordBo> getProjectDistributionWeight(List<Long> contractIds, LocalDate date, boolean flash);

    //投放额
    List<KpiPaymentAmountBo> getContractPaymentAmount(List<Long> contractId, LocalDate date, boolean flash);

}
