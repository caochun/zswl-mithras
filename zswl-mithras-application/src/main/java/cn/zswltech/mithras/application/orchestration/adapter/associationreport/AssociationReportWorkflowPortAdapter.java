package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.associationreport.application.AssociationReportWorkflowPort;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.ExecutionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class AssociationReportWorkflowPortAdapter implements AssociationReportWorkflowPort {

    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ExecutionService executionService;

    @Override
    public void startPushFlow(Long applyId, String reportCategoryNames) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        OrgDO org = sysUserService.getUserDept();
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.AssociationReportPushFlow.name());
        startProcessReq.setBusinessKey(applyId.toString());
        startProcessReq.setProcessInstanceName(String.format("%s-%s", ProcessModelTypeEnum.AssociationReportPushFlow.getDisplay(), reportCategoryNames));
        startProcessReq.setStartUserId(currentUserId.toString());
        if (Objects.nonNull(org)) {
            startProcessReq.setStartUserDeptId(org.getId().toString());
        }
        flowProcessApiService.start(startProcessReq);
    }

    @Override
    public void ccComprehensiveDept(String processInstanceId) {
        List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.comprehensiveDept.name());
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        ExecutionProcessBaseREQ ccReq = new ExecutionProcessBaseREQ();
        ccReq.setProcessInstanceId(processInstanceId);
        ccReq.setCcUserIdList(userIds);
        executionService.cc(ccReq);
    }
}
