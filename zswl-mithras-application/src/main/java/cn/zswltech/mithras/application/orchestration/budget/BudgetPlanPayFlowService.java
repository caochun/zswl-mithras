package cn.zswltech.mithras.application.orchestration.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayFlowApplicationService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.budget.enums.BudgetStatusEnum;
import cn.zswltech.mithras.budget.mapper.*;
import cn.zswltech.mithras.budget.mapper.model.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tangxh
 * @Description 投放计划流程服务类
 * @ClassName BudgetPlanPayFlowService.java
 * @Date 16:28
 * @Version 1.0
 **/
@Slf4j
@Service
public class BudgetPlanPayFlowService implements BudgetPlanPayFlowApplicationService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private FlowProcessApiService processApiService;

    @Resource
    private BudgetPlanPayMapper budgetPlanPayMapper;

    @Resource
    private BudgetPlanPayService budgetPlanPayService;

    @Resource
    private BudgetPlanService budgetPlanService;

    @Resource
    private BudgetPlanPayDetailService budgetPlanPayDetailService;

    @Resource
    private BudgetPlanPayDetailMapper budgetPlanPayDetailMapper;

    @Resource
    private BudgetPlanCostMapper budgetPlanCostMapper;

    @Resource
    private BudgetPlanProfitMapper budgetPlanProfitMapper;

    @Resource
    private FlowTaskApiService taskApiService;

    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Resource
    private BudgetPlanPayProcessInfoService budgetPlanPayProcessInfoService;

    @Resource
    private Validator validator;

    @Resource
    private OrgDOMapper orgDOMapper;

    @Transactional(rollbackFor = Throwable.class)
    public Map<Long, String> createYearFlow(Long planId) {
        return createFlow(planId, ProcessModelTypeEnum.YearHalfOtherPlanEventFlow.name());
    }


    @Transactional(rollbackFor = Throwable.class)
    public Map<Long, String> createMonthFlow(Long planId) {
        return createFlow(planId, ProcessModelTypeEnum.MonthPlanEventFlow.name());
    }

    public Map<Long, String> createFlow(Long planId, String modelKey) {
        Map<Long, String> resultMap = new HashMap<>();
        // 根据id查询投放计划
        final BudgetPlanPay budgetPlanPay = budgetPlanPayMapper.selectById(planId);
        if (Objects.isNull(budgetPlanPay)) {
            throw new MithrasException("未找到该投放计划!");
        }
        final List<OrgDO> orgDOS = sysUserService.listEffectBizDept();
        for (OrgDO org : orgDOS) {
            final Long belongDeptId = org.getId();
            // 表单名称：“利润预算计划名称”投放计划-部门名称
            StartProcessReq startProcessReq = new StartProcessReq();
            startProcessReq.setModelKey(modelKey);
            List<UserDO> projectList = sysUserService.listEffectSpecificOrgJobUser(belongDeptId, JobEnum.projmanager.name());
            if (CollectionUtils.isNotEmpty(projectList)) {
                String processName = String.format("\"%s\"投放计划-%s", budgetPlanPay.getBudgetPlanName(), org.getName());
                startProcessReq.setProcessInstanceName(processName);
                Map<String, Object> varMap = new HashMap<>(8);
                List<Long> projectManagerIds = projectList.stream().map(UserDO::getId).distinct().collect(Collectors.toList());
                // 项目经理(利润预算计划创建后发送待办给全部在职的具有项目经理角色且无业务负责人角色的人)
                List<UserDO> bizDeptLeaderUserList = sysUserService.listEffectSpecificOrgJobUser(belongDeptId, JobEnum.businesshead.name());
                if (CollectionUtils.isNotEmpty(bizDeptLeaderUserList)) {
                    List<Long> businessIds = bizDeptLeaderUserList.stream().map(UserDO::getId).distinct().collect(Collectors.toList());
                    projectManagerIds = projectManagerIds.stream()
                            .filter(o -> !businessIds.contains(o))
                            .collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(projectManagerIds)) {
                    varMap.put("projectManager", projectManagerIds.stream().map(String::valueOf).collect(Collectors.toList()));
                } else {
                    varMap.put("projectManager", new ArrayList<>());
                }
                // 业务部门
                varMap.put("belongDeptId", belongDeptId);
                // 业务部门负责人
                varMap.put("bizDeptLeader", bizDeptLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
                // 分管领导
                List<UserDO> divisionLeaderUserList = sysUserService.listEffectSpecificOrgJobUser(belongDeptId, JobEnum.leaderincharge.name());
                varMap.put("bizDivisionLeader", divisionLeaderUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
                startProcessReq.setVariables(varMap);
                final Long userId = AccountUtil.getLoginInfo().getId();
                startProcessReq.setBusinessKey(String.format("%s-%s", budgetPlanPay.getId(), belongDeptId));
                startProcessReq.setStartUserId(String.valueOf(userId));
                final OrgDO orgDO = sysUserService.getBizDeptByUserId(userId);
                if (orgDO != null) {
                    startProcessReq.setStartUserDeptId(String.valueOf(orgDO.getId()));
                }
                String processInstanceId = processApiService.start(startProcessReq);
                resultMap.put(belongDeptId, processInstanceId);
            }
        }
        return resultMap;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(ProcessEndContext endContext, boolean isMonthFlag) {
        String businessKey = endContext.getBusinessKey();
        Integer endType = endContext.getEndType();

        String[] split = businessKey.split("-");
        if (split.length != 2) {
            return;
        }
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        Long planId = Long.valueOf(split[0]);
        Long deptId = Long.valueOf(split[1]);

        final BudgetPlanPay budgetPlanPay = budgetPlanPayMapper.selectById(planId);
        if (Objects.isNull(budgetPlanPay)) {
            log.error("投放计划不存在");
            return;
        }
        if (processPass) {
            // 修改流程信息表状态
            LambdaUpdateWrapper<BudgetPlanPayProcessInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BudgetPlanPayProcessInfo::getBudgetPlanPayId, planId);
            updateWrapper.eq(BudgetPlanPayProcessInfo::getBelongDeptId, deptId);
            updateWrapper.set(BudgetPlanPayProcessInfo::getIsProcessPass, YesOrNoNumberEnum.YES.getCode());
            budgetPlanPayProcessInfoService.update(null, updateWrapper);
            // 统计本次投放计划未通过的流程数量
            // FIXME 可能存在并发问题，多个财务经理同时点击流程通过，因为事务可见性会使统计结果出现错误，从而导致无法开启下一个流程，可以通过 cn.zswltech.mithras.application.orchestration.job.SystemJob.createBudgetFinancialFlow 手动启动流程
            int noPassCount = budgetPlanPayProcessInfoService.count(
                    Wrappers.<BudgetPlanPayProcessInfo>lambdaQuery()
                            .eq(BudgetPlanPayProcessInfo::getBudgetPlanPayId, planId)
                            .eq(BudgetPlanPayProcessInfo::getIsProcessPass, YesOrNoNumberEnum.NO.getCode())
            );
            if (noPassCount == 0) {
                this.createBudgetFinancialFlow(budgetPlanPay, isMonthFlag, AccountUtil.getLoginInfo().getId());
            }
        }
    }

    public void createBudgetFinancialFlow(BudgetPlanPay budgetPlanPay, boolean isMonthFlag, Long startUserId) {
        // 所有流程都通过了则启动一个新流程代办
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.FinalPlanEventFlow.name());
        String processName = String.format("\"%s\"投放计划-%s", budgetPlanPay.getBudgetPlanName(), "浙商租赁");
        startProcessReq.setProcessInstanceName(processName);
        // XMX-58 添加 计划财务部分管领导 审批节点
        OrgDO jhcwb = orgDOMapper.queryByCode("JHCWB");
        List<UserDO> leaderinchargeUserList = sysUserService.listEffectSpecificOrgJobUser(jhcwb.getId(), JobEnum.leaderincharge.name());

        Map<String, Object> varMap = new HashMap<>(8);
        varMap.put("isMonthFlag", isMonthFlag);
        varMap.put("leaderincharge",leaderinchargeUserList.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList()));
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(budgetPlanPay.getId()));
//        String modelKey;
//        if (isMonthFlag) {
//            modelKey = ProcessModelTypeEnum.MonthPlanEventFlow.name();
//        } else {
//            modelKey = ProcessModelTypeEnum.YearHalfOtherPlanEventFlow.name();
//        }
//        startProcessReq.setStartUserId(getFundManageProcess(businessKey, modelKey));
        // 业务上来说一定是财务经理节点，如果修改流程模型则此处需要同步调整
        startProcessReq.setStartUserId(startUserId.toString());
        processApiService.start(startProcessReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processFinalEnd(ProcessEndContext endContext) {
        String businessKey = endContext.getBusinessKey();
        String processId = endContext.getProcessInstanceId();
        Integer endType = endContext.getEndType();
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        Long planId = Long.valueOf(businessKey);
        final BudgetPlanPay budgetPlanPay = budgetPlanPayMapper.selectById(planId);
        if (Objects.isNull(budgetPlanPay)) {
            log.error("投放计划不存在");
            return;
        }
        if (processPass) {
            changeStatus(budgetPlanPay, BudgetStatusEnum.CONFIRM.name());
        }
    }

    /**
     * 业务负责人确认
     *
     * @param nodeCommonContext 参数
     */
    @Transactional(rollbackFor = Throwable.class)
    public void bizDeptLeaderConfirm(NodeCommonContext nodeCommonContext) {
        String businessKey = nodeCommonContext.getBusinessKey();
        String[] split = businessKey.split("-");
        if (split.length != 2) {
            return;
        }
        Long planId = Long.valueOf(split[0]);
        Long deptId = Long.valueOf(split[1]);

        LambdaUpdateWrapper<BudgetPlanPayDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BudgetPlanPayDetail::getBudgetPlanPayId, planId);
        updateWrapper.eq(BudgetPlanPayDetail::getBelongDeptId, deptId);
        updateWrapper.set(BudgetPlanPayDetail::getIsBusinessheadConfirm, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.set(BudgetPlanPayDetail::getBusinessheadConfirmDate, LocalDate.now());
        budgetPlanPayDetailMapper.update(null, updateWrapper);
    }

    /**
     * 分管领导确认
     *
     * @param nodeCommonContext 参数
     */
    @Transactional(rollbackFor = Throwable.class)
    public void bizDivisionLeaderConfirm(NodeCommonContext nodeCommonContext) {
        String businessKey = nodeCommonContext.getBusinessKey();
        String[] split = businessKey.split("-");
        if (split.length != 2) {
            return;
        }
        Long planId = Long.valueOf(split[0]);
        Long deptId = Long.valueOf(split[1]);

        final BudgetPlanPay budgetPlanPay = budgetPlanPayMapper.selectById(planId);
        if (Objects.isNull(budgetPlanPay)) {
            log.error("投放计划不存在");
            return;
        }

        LambdaUpdateWrapper<BudgetPlanPayDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BudgetPlanPayDetail::getBudgetPlanPayId, planId);
        updateWrapper.eq(BudgetPlanPayDetail::getBelongDeptId, deptId);
        updateWrapper.set(BudgetPlanPayDetail::getIsBusinessheadConfirm, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.set(BudgetPlanPayDetail::getIsLeaderinchargeConfirm, YesOrNoNumberEnum.YES.getCode());
        budgetPlanPayDetailMapper.update(null, updateWrapper);

        // 分管总流程提交后，将流程信息表的数据更新为“收集完毕”
        LambdaUpdateWrapper<BudgetPlanPayProcessInfo> updateProcessInfoWrapper = new LambdaUpdateWrapper<>();
        updateProcessInfoWrapper.eq(BudgetPlanPayProcessInfo::getProcessInstanceId, nodeCommonContext.getProcessInstanceId());
        updateProcessInfoWrapper.eq(BudgetPlanPayProcessInfo::getBudgetPlanPayId, budgetPlanPay.getId());
        updateProcessInfoWrapper.set(BudgetPlanPayProcessInfo::getIsCollectFinish, YesOrNoNumberEnum.YES.getCode());
        budgetPlanPayProcessInfoService.update(null, updateProcessInfoWrapper);

        // 所有流程都已确认的话变更预算计划的状态
        List<BudgetPlanPayProcessInfo> budgetPlanPayProcessInfoList = budgetPlanPayProcessInfoService.list(
                Wrappers.<BudgetPlanPayProcessInfo>lambdaQuery()
                        .eq(BudgetPlanPayProcessInfo::getBudgetPlanId, budgetPlanPay.getId())
        );
        boolean allConfirm = true;
        for (BudgetPlanPayProcessInfo info : budgetPlanPayProcessInfoList) {
            if (Objects.equals(info.getIsCollectFinish(), YesOrNoNumberEnum.NO.getCode())) {
                allConfirm = false;
                break;
            }
        }
        if (allConfirm) {
            changeStatus(budgetPlanPay, BudgetStatusEnum.COLLECT_FINISH.name());
        }
    }

    public void restartConfirmStatus(TaskResp taskResp) {
        String businessKey = taskResp.getBusinessKey();
        String[] split = businessKey.split("-");
        if (split.length != 2) {
            return;
        }
        Long planId = Long.valueOf(split[0]);
        Long deptId = Long.valueOf(split[1]);
        final BudgetPlanPay budgetPlanPay = budgetPlanPayMapper.selectById(planId);
        if (Objects.isNull(budgetPlanPay)) {
            log.error("投放计划不存在");
            return;
        }
        LambdaUpdateWrapper<BudgetPlanPayDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BudgetPlanPayDetail::getBudgetPlanPayId, planId);
        updateWrapper.eq(BudgetPlanPayDetail::getBelongDeptId, deptId);
        updateWrapper.set(BudgetPlanPayDetail::getIsBusinessheadConfirm, YesOrNoNumberEnum.NO.getCode());
        updateWrapper.set(BudgetPlanPayDetail::getIsLeaderinchargeConfirm, YesOrNoNumberEnum.NO.getCode());
        budgetPlanPayDetailMapper.update(null, updateWrapper);

        changeStatus(budgetPlanPay, BudgetStatusEnum.COLLECTING.name());
    }

    private void changeStatus(BudgetPlanPay budgetPlanPay, String budgetStatus) {
        final Long budgetPlanId = budgetPlanPay.getBudgetPlanId();
        // 处理投放计划状态
        budgetPlanPay.setBudgetStatus(budgetStatus);
        budgetPlanPayService.updateById(budgetPlanPay);
        // 处理预算计划
        final BudgetPlan budgetPlan = new BudgetPlan();
        budgetPlan.setBudgetStatus(budgetStatus);
        budgetPlan.setId(budgetPlanId);
        budgetPlanService.updateById(budgetPlan);
        // 处理成本预算状态
        LambdaUpdateWrapper<BudgetPlanCost> updateBudgetPlanCostWrapper = new LambdaUpdateWrapper<>();
        updateBudgetPlanCostWrapper.eq(BudgetPlanCost::getBudgetPlanId, budgetPlanId);
        updateBudgetPlanCostWrapper.set(BudgetPlanCost::getBudgetStatus, budgetStatus);
        budgetPlanCostMapper.update(null, updateBudgetPlanCostWrapper);
        // 处理利润预算状态
        LambdaUpdateWrapper<BudgetPlanProfit> updateBudgetPlanProfitWrapper = new LambdaUpdateWrapper<>();
        updateBudgetPlanProfitWrapper.eq(BudgetPlanProfit::getBudgetPlanId, budgetPlanId);
        updateBudgetPlanProfitWrapper.set(BudgetPlanProfit::getBudgetStatus, budgetStatus);
        budgetPlanProfitMapper.update(null, updateBudgetPlanProfitWrapper);
    }

    public void checkDataForYy(long planId) {
        // 获取到所有明细
        final List<BudgetPlanPayDetail> list = budgetPlanPayDetailService.list(Wrappers.<BudgetPlanPayDetail>lambdaQuery().eq(BudgetPlanPayDetail::getBudgetPlanPayId, planId));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                if (StringUtils.isEmpty(item.getYunyingFeedback())) {
                    throw new MithrasException("运营进度反馈必须填写");
                }
                if (Objects.isNull(item.getYunyingPriority())) {
                    throw new MithrasException("运营优先级必须填写");
                }
            });
        }
    }

    public void checkDataForFz(long planId) {
        final List<BudgetPlanPayDetail> list = budgetPlanPayDetailService.list(Wrappers.<BudgetPlanPayDetail>lambdaQuery().eq(BudgetPlanPayDetail::getBudgetPlanPayId, planId));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                if (StringUtils.isEmpty(item.getBringIntoFundPlan())) {
                    throw new MithrasException("是否纳入资金计划必须填写");
                }
                if (Objects.isNull(item.getFundPlanPayAmount())) {
                    throw new MithrasException("资金拟投放金额必须填写");
                }
            });
        }
    }

    public void checkDataForCw(long planId,Long belongDeptId) {
        final List<BudgetPlanPayDetail> list = budgetPlanPayDetailService.list(Wrappers.<BudgetPlanPayDetail>lambdaQuery()
                .eq(BudgetPlanPayDetail::getBudgetPlanPayId, planId).eq(BudgetPlanPayDetail::getBelongDeptId,belongDeptId));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                if (Objects.isNull(item.getFtp())) {
                    throw new MithrasException("FTP必须填写");
                }
            });
        }
    }

    public void checkData(long planId,Long belongDeptId) {
        // 此处只校验创建人提交的数据
        final List<BudgetPlanPayDetail> list = budgetPlanPayDetailService.list(Wrappers.<BudgetPlanPayDetail>lambdaQuery()
                .eq(BudgetPlanPayDetail::getBudgetPlanPayId, planId)
                .eq(Objects.nonNull(belongDeptId),BudgetPlanPayDetail::getBelongDeptId,belongDeptId)
                .eq(Objects.isNull(belongDeptId),BudgetPlanPayDetail::getCreateBy, AccountUtil.getLoginInfo().getId())
        );
        if (CollectionUtils.isNotEmpty(list)) {
            for (BudgetPlanPayDetail item : list) {
                Set<ConstraintViolation<BudgetPlanPayDetail>> violations =
                        validator.validate(item);
                if (!violations.isEmpty()) {
                    throw new MithrasException("详细信息页面存在必填字段未录入，请完成后再提交！");
                }
            }
        }
    }

    /**
     * 获取财务经理
     *
     * @param businessKey 流程业务主键
     * @param modelKey    流程类型
     * @return
     */
    private String getFundManageProcess(String businessKey, String modelKey) {
        String lastOperatorId = null;
        ProcessPageReq req = new ProcessPageReq();
        req.setPageIndex(1);
        req.setPageSize(100);
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.PASS.getType()));
        req.setModelKey(modelKey);
        req.setBusinessKeyList(Arrays.asList(businessKey));
        //查询立项审批中的流程
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(req);
        if (ObjectUtil.isNotEmpty(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
            final ProcessResp first = processRespPage.getContents().stream().findFirst().orElse(null);
            if (Objects.nonNull(first)) {
                lastOperatorId = first.getLastOperatorId();
            }
        }
        return lastOperatorId;
    }
}
