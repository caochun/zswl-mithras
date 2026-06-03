package cn.zswltech.mithras.service.controller.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayRSP;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanPay;
import cn.zswltech.mithras.service.service.SysUserService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

import cn.zswltech.mithras.api.budget.BudgetPlanPayApi;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayListRSP;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayService;

import java.util.Collections;
import java.util.Objects;

/**
* @description 预算管理-预算计划-投放计划
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanPayController implements BudgetPlanPayApi {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BudgetPlanPayService budgetPlanPayService;

    @Override
    public R<PageR<BudgetPlanPayListRSP>> pageList(BudgetPlanPayListREQ req) {
        return R.ok(budgetPlanPayService.pageList(req));
    }

    @Override
    public R<BudgetPlanPayRSP> planInfo(SinglePkREQ req) {
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getById(req.getId());
        BudgetPlanPayRSP rsp = BeanUtil.copyProperties(budgetPlanPay, BudgetPlanPayRSP.class);
        // 查询一下关联流程
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            // 取部门id
            OrgDO org = sysUserService.currentUserBizDept();
            if (Objects.nonNull(org)) {
                ProcessPageReq processPageReq = new ProcessPageReq();
                processPageReq.setBusinessKey(String.format("%s-%s", budgetPlanPay.getId(), org.getId()));
                processPageReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.YearHalfOtherPlanEventFlow.name(), ProcessModelTypeEnum.MonthPlanEventFlow.name()));
                processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
                Page<ProcessResp> processRespPage = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(processPageReq);
                if (Objects.nonNull(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
                    rsp.setProcessInstanceId(processRespPage.getContents().get(0).getProcessInstanceId());
                }
            }
        }
        if (sysUserService.currentUserIsSpecificJob(JobEnum.financialofficer.name(), JobEnum.financialmanager.name())) {
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setBusinessKey(budgetPlanPay.getId().toString());
            processPageReq.setModelKey(ProcessModelTypeEnum.FinalPlanEventFlow.name());
            processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
            Page<ProcessResp> processRespPage = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(processPageReq);
            if (Objects.nonNull(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
                rsp.setProcessInstanceId(processRespPage.getContents().get(0).getProcessInstanceId());
            }
        }
        return R.ok(rsp);
    }
}