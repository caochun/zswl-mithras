package cn.zswltech.mithras.application.orchestration.adapter.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.budget.application.port.BudgetKpiFactPort;
import cn.zswltech.mithras.budget.application.port.BudgetPerformanceTargetSnapshot;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceBaseInfoService;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.model.PerformanceBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class BudgetKpiFactPortAdapter implements BudgetKpiFactPort {

    @Resource
    private KpiPerformanceBaseInfoService kpiPerformanceBaseInfoService;

    @Override
    public List<BudgetPerformanceTargetSnapshot> listDepartmentTargets(Integer year) {
        List<PerformanceBaseInfo> performanceBaseInfoList = kpiPerformanceBaseInfoService.list(
                Wrappers.<PerformanceBaseInfo>lambdaQuery()
                        .eq(PerformanceBaseInfo::getYear, year)
                        .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.DEPARTMENT.name())
        );
        return BeanUtil.copyToList(performanceBaseInfoList, BudgetPerformanceTargetSnapshot.class);
    }
}
