package cn.zswltech.mithras.application.orchestration.adapter.budget;

import cn.zswltech.mithras.budget.application.port.BudgetCollectionFactPort;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.collection.application.bo.DeptRemainingPrincipalBO;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BudgetCollectionFactPortAdapter implements BudgetCollectionFactPort {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    @Override
    public Map<Long, Boolean> getContractPromotion(List<Long> contractIds, Integer interval) {
        return collectionBaseInfoService.getContractPromotion(contractIds, interval);
    }

    @Override
    public Map<Long, Long> calculateRemainingPrincipalGroupByDeptId(LocalDate targetDate) {
        List<DeptRemainingPrincipalBO> result = collectionBaseInfoMapper.calculateRemainingPrincipalGroupByDeptId(targetDate);
        if (result == null || result.isEmpty()) {
            return Collections.emptyMap();
        }
        return result.stream().collect(Collectors.toMap(DeptRemainingPrincipalBO::getBizDeptId,
                DeptRemainingPrincipalBO::getRemainingPrincipal, (a, b) -> b));
    }
}
