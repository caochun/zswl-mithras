package cn.zswltech.mithras.service.controller.kpi;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionBaseInfoApi;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionGetProcessRSP;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.kpi.KpiProjectDistributionModifyChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionBaseInfoService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@RestController
public class KpiProjectDistributionBaseInfoController implements KpiProjectDistributionBaseInfoApi {
    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;

    @Resource
    private FlowTaskApiService taskApiService;

    @Override
    public R<KpiProjectDistributionBaseInfoRSP> detail(@Valid KpiProjectDistributionBaseInfoREQ req) {
        return R.ok(kpiProjectDistributionBaseInfoService.detail(req));
    }

    @DataAuthCheck(keyFieldName = "projectDistributionId", paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION, checkerClass = KpiProjectDistributionModifyChecker.class)
    @Override
    public R<Void> modify(@Valid KpiProjectDistributionBaseInfoModifyREQ req) {
        kpiProjectDistributionBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<KpiProjectDistributionGetProcessRSP> getProcess(@Valid KpiProjectDistributionBaseInfoREQ req) {
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setBusinessKey(String.valueOf(req.getProjectDistributionId()));
        flowReq.setModelKey(ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name());
        flowReq.setCurLoginUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        flowReq.setSortType(1);
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        KpiProjectDistributionGetProcessRSP rsp = new KpiProjectDistributionGetProcessRSP();
        if(ObjectUtil.isNotEmpty(flowRespPage) && ObjectUtil.isNotEmpty(flowRespPage.getContents())) {
            rsp.setProcessInstanceId(flowRespPage.getContents().get(0).getProcessInstanceId());
        }
        return R.ok(rsp);
    }
}
