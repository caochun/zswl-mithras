package cn.zswltech.mithras.application.orchestration.adapter.budget;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.budget.application.port.BudgetExamineWorkflowPort;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.port.UserBizDeptInfoResolver;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BudgetExamineWorkflowPortAdapter implements BudgetExamineWorkflowPort {

    private static final String BUDGET_EXAMINE_FLOW_MODEL_KEY = "BudgetExamineFlow";
    private static final String BUDGET_EXAMINE_PROCESS_INSTANCE_NAME = "预算考核表";

    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private UserBizDeptInfoResolver userBizDeptInfoResolver;
    @Resource
    private UserService userService;
    @Resource
    private ExecutionApi executionApi;

    @Override
    public void startBudgetExamineFlow(Long budgetExamineId, Long submitUserId) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(BUDGET_EXAMINE_FLOW_MODEL_KEY);
        startProcessReq.setProcessInstanceName(BUDGET_EXAMINE_PROCESS_INSTANCE_NAME);
        startProcessReq.setVariables(new HashMap<>(8));
        startProcessReq.setBusinessKey(String.valueOf(budgetExamineId));
        startProcessReq.setStartUserId(String.valueOf(submitUserId));
        OrgDO bizDeptByUserId = userBizDeptInfoResolver.getBizDeptByUserId(submitUserId);
        if (ObjectUtil.isNotEmpty(bizDeptByUserId)) {
            startProcessReq.setStartUserDeptId(String.valueOf(bizDeptByUserId.getId()));
        }
        processApiService.start(startProcessReq);
    }

    @Override
    public void ccHumanResourcesSupervisor(String processInstanceId) {
        List<UserDO> jobUsers = userService.getUsersByjobcod(JobEnum.humanresourcessupervisor.name());
        if (CollectionUtils.isEmpty(jobUsers)) {
            return;
        }
        ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
        req.setProcessInstanceId(processInstanceId);
        req.setCcUserIdList(jobUsers.stream().map(UserDO::getId).distinct().collect(Collectors.toList()));
        executionApi.cc(req);
    }
}
