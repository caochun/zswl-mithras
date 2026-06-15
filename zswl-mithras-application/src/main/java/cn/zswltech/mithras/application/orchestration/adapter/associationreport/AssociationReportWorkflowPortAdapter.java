package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.associationreport.application.AssociationReportWorkflowPort;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.UserDeptResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.ExecutionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class AssociationReportWorkflowPortAdapter implements AssociationReportWorkflowPort {

    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private UserDeptResolver userDeptResolver;

    @Override
    public void startApplyFlow(Long applyId, String modelKey) {
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        String currentUserName = userNameResolver.sysUserId2NameSingle(currentUserId);
        List<OrgDO> deptList = userDeptResolver.userDeptList(currentUserId);
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(applyId.toString());
        startProcessReq.setModelKey(modelKey);
        startProcessReq.setProcessInstanceName(currentUserName + "发起报表上报申请流程");
        startProcessReq.setStartUserId(currentUserId.toString());
        Long deptId = null;
        Optional<OrgDO> first = deptList.stream().filter(e -> e.getType() != 1).findFirst();
        if (first.isPresent()) {
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        }
        startProcessReq.setStartUserDeptId(deptId.toString());
        flowProcessApiService.start(startProcessReq);
    }

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
