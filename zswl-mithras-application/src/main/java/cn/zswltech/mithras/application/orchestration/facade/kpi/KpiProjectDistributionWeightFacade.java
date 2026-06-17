package cn.zswltech.mithras.application.orchestration.facade.kpi;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionWeightApplicationService;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightSaveREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.kpi.KpiProjectDistributionModifyChecker;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.client.ClientTransferService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@Service
public class KpiProjectDistributionWeightFacade implements KpiProjectDistributionWeightApplicationService {
    @Resource
    private KpiProjectDistributionWeightService kpiProjectDistributionWeightService;

    @Autowired
    private ClientTransferService clientTransferService;

    @Override
    public R<KpiProjectDistributionWeightRSP> detail(@Valid KpiProjectDistributionWeightREQ req) {
        return R.ok(kpiProjectDistributionWeightService.detail(req));
    }

    @DataAuthCheck(keyFieldName = "projectDistributionId", paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "KPI_PROJECT_DISTRIBUTION", checkerClass = KpiProjectDistributionModifyChecker.class)
    @Override
    public R<Void> save(@Valid KpiProjectDistributionWeightSaveREQ req) {
        kpiProjectDistributionWeightService.save(req);
        return R.ok();
    }

    @Override
    public R<Void> test() {
        clientTransferService.timedPass();
        return R.ok();
    }


}
