package cn.zswltech.mithras.budget.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.budget.application.port.BudgetExamineWorkflowPort;
import cn.zswltech.mithras.budget.enums.BudgetApprovalStatusEnum;
import cn.zswltech.mithras.budget.mapper.BudgetExamineMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetExamine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

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
    private BudgetExamineWorkflowPort budgetExamineWorkflowPort;

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
        budgetExamineWorkflowPort.startBudgetExamineFlow(budgetExamine.getId(), budgetExamine.getSubmitUserId());
    }

    public void completeFlow(String businessKey, String processInstanceId, boolean passed, boolean canceled) {
        BudgetApprovalStatusEnum processState;
        if (passed) {
            processState = BudgetApprovalStatusEnum.PASS;
            budgetExamineWorkflowPort.ccHumanResourcesSupervisor(processInstanceId);
        } else {
            processState = canceled ? BudgetApprovalStatusEnum.CANCEL : BudgetApprovalStatusEnum.REJECT;
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
