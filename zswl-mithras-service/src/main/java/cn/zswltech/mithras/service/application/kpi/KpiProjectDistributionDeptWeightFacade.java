package cn.zswltech.mithras.service.application.kpi;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionDeptWeightApplicationService;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightInfo;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightSaveREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionPrevREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.kpi.KpiProjectDistributionModifyChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionDeptLaunchWeightService;
import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionDeptWeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bigbear
 * @date 2025/4/9 15:04
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionDeptWeightFacade implements KpiProjectDistributionDeptWeightApplicationService {

    @Resource
    private KpiProjectDistributionDeptWeightService kpiProjectDistributionDeptWeightService;

    @Resource
    private KpiProjectDistributionDeptLaunchWeightService kpiProjectDistributionDeptLaunchWeightService;

    @Override
    @DataAuthCheck(keyFieldName = "projectDistributionId", paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION, checkerClass = KpiProjectDistributionModifyChecker.class)
    public R<Void> saveDept(KpiProjectDistributionDeptWeightSaveREQ req) {
        kpiProjectDistributionDeptWeightService.saveDept(req);
        return R.ok();
    }

    @Override
    public R<List<KpiProjectDistributionDeptWeightInfo>> prev(KpiProjectDistributionPrevREQ req) {
        return R.ok(kpiProjectDistributionDeptWeightService.prev(req));
    }

    @Override
    public R<List<KpiProjectDistributionDeptLaunchWeightInfo>> launchPrev(KpiProjectDistributionPrevREQ req) {
        return R.ok(kpiProjectDistributionDeptLaunchWeightService.prev(req));
    }

}
