package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.node_end;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.filingmaterials.model.FilingMaterials;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.OtherFilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.MyTaskService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;


/**
 * @author bigbear
 * @date 2025/5/26 15:42
 * @description
 */
@Slf4j
@Component
public class OtherFilingMaterialsNodeEndListener implements ApplicationListener<NodeEndEvent> {

    @Autowired
    private MyTaskService taskService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OtherFilingMaterialsService otherFilingMaterialsService;
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private SystemConfigService systemConfigService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void onApplicationEvent(@NotNull NodeEndEvent nodeEndEvent) {
        // 节点结束
        log.info("节点结束，记录时间. inst id:{}, node:{}", nodeEndEvent.getNodeCommonContext().getProcessInstanceId(), nodeEndEvent.getNodeCommonContext().getActivityId());
        NodeCommonContext nodeCommonContext = nodeEndEvent.getNodeCommonContext();
        if (!StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.OtherFilingMaterialsApplyFlow.name())) {
            return;
        }
        long id = Long.parseLong(nodeCommonContext.getBusinessKey());
        FilingMaterials filingMaterials = otherFilingMaterialsService.getById(id);
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<String> initApproveUser = otherFilingMaterialsService.getInitApproveUser();
        List<String> reviewJobUser = otherFilingMaterialsService.getReviewJobUser();
        flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(FilingMaterialsConstants.USER_TASK_OTHER_REVIEW), reviewJobUser));
        flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(FilingMaterialsConstants.USER_TASK_OTHER_REVIEW_02), reviewJobUser));
        flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(FilingMaterialsConstants.USER_TASK_OTHER_INIT_REVIEW), initApproveUser));


        if (StrUtil.equalsAny(nodeCommonContext.getActivityId(), FilingMaterialsConstants.USER_TASK_OTHER_REVIEW, FilingMaterialsConstants.USER_TASK_OTHER_INIT_REVIEW,FilingMaterialsConstants.USER_TASK_OTHER_REVIEW_02)) {
            otherFilingMaterialsService.replaceReviewName(Long.parseLong(nodeCommonContext.getBusinessKey()), nodeCommonContext.getProcessInstanceId(),
                    nodeCommonContext.getActivityId(), Collections.singletonList(BusinessModuleEnum.OTHER_FILING.name()));
        }
    }

}
