package cn.zswltech.mithras.application.orchestration.facade.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionApplicationService;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.kpi.KpiProjectDistributionModifyChecker;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionFacade implements KpiProjectDistributionApplicationService {
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;

    @Override
    public R<PageR<KpiProjectDistributionListRSP>> pageList(@Valid KpiProjectDistributionListREQ req) {
        return R.ok(kpiProjectDistributionService.pageList(req));
    }

    @DataAuthCheck(keyFieldName = "projectDistributionId", paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "KPI_PROJECT_DISTRIBUTION", checkerClass = KpiProjectDistributionModifyChecker.class)
    @Override
    public R<Void> submit(@Valid KpiProjectDistributionSubmitREQ req) {
        kpiProjectDistributionService.submit(req.getProjectDistributionId());
        return R.ok();
    }

    @Override
    public R<List<KpiProjectDistributionPrevRSP>> prev(KpiProjectDistributionPrevREQ req) {
        return R.ok(kpiProjectDistributionService.prev(req));
    }

    @Override
    public R<List<KpiProjectDistributionHistoryRSP>> history(@Valid KpiProjectDistributionHistoryREQ req) {
        return R.ok(kpiProjectDistributionService.history(req));
    }

    @Override
    public R<Void> importHistoryData(KpiProjectDistributionImportREQ req) {
        try {
            kpiProjectDistributionService.importHistoryData(req.getFile().getInputStream());
            return R.ok();
        } catch (Exception e) {
            log.error("绩效考核-项目分配-历史数据导入异常", e);
            return R.fail("历史数据导入异常");
        }
    }

    @Override
    public R<Void> init() {
        try {
            kpiProjectDistributionService.init();
            return R.ok();
        } catch (Exception e) {
            log.error("绩效考核-项目分配-数据初始化异常", e);
            return R.fail("数据初始化异常");
        }
    }

    @Override
    public void export(KpiProjectDistributionListREQ req) {
        try {
            kpiProjectDistributionService.export(req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出项目分配台账异常", e);
            throw MithrasException.newException("导出项目分配台账异常");
        }
    }
}
