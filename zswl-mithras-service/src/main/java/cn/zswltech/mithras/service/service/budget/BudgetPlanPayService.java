package cn.zswltech.mithras.service.service.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.domain.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.domain.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.budget.application.BudgetPlanPayApplicationService;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.budget.domain.enums.BudgetStatusEnum;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetPlanPayMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanPay;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
* @description 预算管理-预算计划-投放计划
* @author vico
* @date 2025-04-11
*/
@Service
public class BudgetPlanPayService extends ServiceImpl<BudgetPlanPayMapper, BudgetPlanPay> implements BudgetPlanPayApplicationService {
    @Resource
    private SysUserService sysUserService;

    public PageR<BudgetPlanPayListRSP> pageList(BudgetPlanPayListREQ req) {
        Page<BudgetPlanPay> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<BudgetPlanPay> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.orderByDesc(BudgetPlanPay::getId);
        Page<BudgetPlanPay> dbResult = this.page(pageQuery, conditionQuery);
        List<BudgetPlanPayListRSP> list = BeanUtil.copyToList(dbResult.getRecords(), BudgetPlanPayListRSP.class);
        return PageR.of(list, dbResult.getTotal(), dbResult.getPages(), dbResult.getCurrent(), dbResult.getSize());
    }

    public BudgetPlanPayRSP planInfo(Long id) {
        BudgetPlanPay budgetPlanPay = this.getById(id);
        BudgetPlanPayRSP rsp = BeanUtil.copyProperties(budgetPlanPay, BudgetPlanPayRSP.class);
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            OrgDO org = sysUserService.currentUserBizDept();
            if (Objects.nonNull(org)) {
                ProcessPageReq processPageReq = new ProcessPageReq();
                processPageReq.setBusinessKey(String.format("%s-%s", budgetPlanPay.getId(), org.getId()));
                processPageReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.YearHalfOtherPlanEventFlow.name(), ProcessModelTypeEnum.MonthPlanEventFlow.name()));
                processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
                cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(processPageReq);
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
            cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(processPageReq);
            if (Objects.nonNull(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
                rsp.setProcessInstanceId(processRespPage.getContents().get(0).getProcessInstanceId());
            }
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPay> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPay::getBudgetPlanId, budgetPlanId);
        BudgetPlanPay budgetPlanPay = this.getOne(query);
        if (Objects.isNull(budgetPlanPay)) {
            return;
        }
        this.removeById(budgetPlanPay.getId());
        // 删除明细
        SpringUtil.getBean(BudgetPlanPayDetailService.class).deleteByBudgetPlanId(budgetPlanId);
        // 关闭相关待办流程
        ProcessPageReq req = new ProcessPageReq();
        req.setPageIndex(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setModelKeyList(ListUtil.of(
                ProcessModelTypeEnum.YearHalfOtherPlanEventFlow.name(),
                ProcessModelTypeEnum.MonthPlanEventFlow.name(),
                ProcessModelTypeEnum.FinalPlanEventFlow.name())
        );
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> pageResult = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(req);
        if (Objects.nonNull(pageResult) && CollectionUtil.isNotEmpty(pageResult.getContents())) {
            for (ProcessResp processResp : pageResult.getContents()) {
                boolean close = false;
                if (Objects.equals(processResp.getModelKey(), ProcessModelTypeEnum.FinalPlanEventFlow.name())) {
                    if (Objects.equals(processResp.getBusinessKey(), budgetPlanPay.getId().toString())) {
                        close = true;
                    }
                } else {
                    if (processResp.getBusinessKey().startsWith(budgetPlanPay.getId() + "-")) {
                        close = true;
                    }
                }
                if (close) {
                    ExecutionProcessBaseREQ rejectReq = new ExecutionProcessBaseREQ();
                    rejectReq.setProcessInstanceId(processResp.getProcessInstanceId());
                    rejectReq.setMessage("预算数据被删除，系统自动关闭该流程");
                    SpringUtil.getBean(ExecutionService.class).rejectAll(rejectReq);
                }
            }
        }
    }

    public void confirmByBudgetPlanId(Long budgetPlanId) {
        LambdaUpdateWrapper<BudgetPlanPay> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(BudgetPlanPay::getBudgetStatus, BudgetStatusEnum.CONFIRM.name());
        updateWrapper.eq(BudgetPlanPay::getBudgetPlanId, budgetPlanId);
        this.update(updateWrapper);
    }

    public BudgetPlanPay getByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPay> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPay::getBudgetPlanId, budgetPlanId);
        query.orderByDesc(BudgetPlanPay::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}
