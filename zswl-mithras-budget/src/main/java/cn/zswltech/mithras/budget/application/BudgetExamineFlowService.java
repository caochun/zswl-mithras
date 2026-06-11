package cn.zswltech.mithras.budget.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessState;
import cn.zswltech.mithras.budget.mapper.BudgetExamineMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetExamine;
import cn.zswltech.mithras.system.user.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * 文件描述
 *
 * @ProductName: Bering PDT
 * @ProjectName: zswl
 * @Package: cn.zswltech.mithras.application.orchestration.budget
 * @Description: note
 * @Author: tangxhmr
 * @CreateDate: 2025/5/19 09:50
 * @UpdateUser: tangxhmr
 * @UpdateDate: 2025/5/19 09:50
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 Bering Technologies Inc. All Rights Reserved
 **/
@Slf4j
@Service
public class BudgetExamineFlowService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private FlowProcessApiService processApiService;

    @Resource
    private UserService userService;

    @Resource
    private BudgetExamineMapper budgetExamineMapper;

    /**
     * 创建流程代办
     *
     * @param budgetExamine 参数
     */
    public void createFlow(BudgetExamine budgetExamine) {
        if (ObjectUtil.isEmpty(budgetExamine)) {
            throw new ArithmeticException("预算考核不存在");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.BudgetExamineFlow.name());
        startProcessReq.setProcessInstanceName("预算考核表");
        Map<String, Object> varMap = new HashMap<>(8);
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(budgetExamine.getId()));
        startProcessReq.setStartUserId(String.valueOf(budgetExamine.getSubmitUserId()));
        final OrgDO bizDeptByUserId = sysUserService.getBizDeptByUserId(budgetExamine.getSubmitUserId());
        if (ObjectUtil.isNotEmpty(bizDeptByUserId)) {
            startProcessReq.setStartUserDeptId(String.valueOf(bizDeptByUserId.getId()));
        }
        processApiService.start(startProcessReq);
    }

    public void copyFlow(ProcessEndContext endContext) {
        boolean passed = ProcessBusinessStatusEnum.success(endContext.getEndType());
        ProcessState processState;
        final String businessKey = endContext.getBusinessKey();
        if (passed) {
            processState = ProcessState.PASS;
            // 抄送给人力负责人
            List<UserDO> jobUsers = userService.getUsersByjobcod(JobEnum.humanresourcessupervisor.name());
            if (CollectionUtils.isNotEmpty(jobUsers)) {
                ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
                req.setProcessInstanceId(endContext.getProcessInstanceId());
                req.setCcUserIdList(jobUsers.stream().map(UserDO::getId).distinct().collect(Collectors.toList()));
                getBean(ExecutionApi.class).cc(req);
            }
        } else {
            processState = ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType()) ? ProcessState.CANCEL : ProcessState.REJECT;
        }
        // 更新状态
        if (StringUtils.isNotEmpty(businessKey)) {
            BudgetExamine originalInfo = budgetExamineMapper.selectById(Integer.valueOf(businessKey));
            if (Objects.nonNull(originalInfo)) {
                originalInfo.setApprovalStatus(processState.name());
                budgetExamineMapper.updateById(originalInfo);
            }
        }
    }
}
