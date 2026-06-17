package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.budget.application.port.BudgetCollectionFactPort;
import cn.zswltech.mithras.budget.mapper.EclExecuteClientPromotionResultMapper;
import cn.zswltech.mithras.budget.mapper.model.EclExecuteClientPromotionResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 资产减值客户是否上迁记录表
* @author vico
* @date 2025-12-15
*/
@Service
public class EclExecuteClientPromotionResultService extends ServiceImpl<EclExecuteClientPromotionResultMapper, EclExecuteClientPromotionResult> {

    @Resource
    private BudgetCollectionFactPort budgetCollectionFactPort;
    /**
     * 保存某天
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void savePromotionResult(List<Long> contractIds, Integer interval) {
        if (ObjectUtil.isEmpty(interval)) {
            interval = 6;
        }
        Map<Long, Boolean> contractPromotion = budgetCollectionFactPort.getContractPromotion(contractIds, interval);
        if (ObjectUtil.isEmpty(contractPromotion)) {
            return;
        }
        List<EclExecuteClientPromotionResult> addResults = new ArrayList<>();
        List<EclExecuteClientPromotionResult> updateResults = new ArrayList<>();
        Map<Long, Long> oldClientId2Bean = this.list(Wrappers.<EclExecuteClientPromotionResult>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(contractIds), EclExecuteClientPromotionResult::getContractId, contractIds)
                .eq(EclExecuteClientPromotionResult::getIntervalMonth, interval))
                .stream().collect(Collectors.toMap(EclExecuteClientPromotionResult::getContractId, EclExecuteClientPromotionResult::getId, (a, b) -> b));

        Integer finalInterval = interval;
        contractPromotion.forEach((contractId, b) -> {
                EclExecuteClientPromotionResult result = new EclExecuteClientPromotionResult();
                result.setContractId(contractId);
                result.setIntervalMonth(finalInterval);
                result.setConclusion(b ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
                result.setId(oldClientId2Bean.get(contractId));
                if (ObjectUtil.isEmpty(result.getId())) {
                    addResults.add(result);
                } else {
                    updateResults.add(result);
                }
        });
        //保存
        if(ObjectUtil.isNotEmpty(addResults)) {
            this.saveBatch(addResults);
        }
        if (ObjectUtil.isNotEmpty(updateResults)) {
            this.updateBatchById(updateResults);
        }
    }

    public Map<Long, Integer> getPromotionResult(List<Long> contractIds, Integer interval) {
        if (ObjectUtil.isEmpty(interval)) {
            interval = 6;
        }
        return this.list(Wrappers.<EclExecuteClientPromotionResult>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(contractIds), EclExecuteClientPromotionResult::getContractId, contractIds)
                .eq(EclExecuteClientPromotionResult::getIntervalMonth, interval))
                .stream().collect(Collectors.toMap(EclExecuteClientPromotionResult::getContractId, EclExecuteClientPromotionResult::getConclusion, (a, b) -> b));
    }

}
