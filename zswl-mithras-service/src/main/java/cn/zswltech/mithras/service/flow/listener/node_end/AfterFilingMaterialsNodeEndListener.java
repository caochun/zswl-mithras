package cn.zswltech.mithras.service.flow.listener.node_end;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.mithras.filingmaterials.domain.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.FilingMaterials;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.filingmaterials.AfterFilingMaterialsService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author lllin
 * @date 2026/1/23
 * @description
 */
@Slf4j
@Component
public class AfterFilingMaterialsNodeEndListener implements ApplicationListener<NodeEndEvent> {

    @Autowired
    private MyTaskService taskService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AfterFilingMaterialsService afterFilingMaterialsService;
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
        if (!StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.AfterFilingMaterialsApplyFlow.name())) {
            return;
        }
        long id = Long.parseLong(nodeCommonContext.getBusinessKey());
        FilingMaterials filingMaterials = afterFilingMaterialsService.getById(id);
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (StrUtil.equals(nodeCommonContext.getActivityId(), FilingMaterialsConstants.USER_TASK_ASSET_MANAGER)) {
            if (Objects.isNull(filingMaterials.getFirstCommitDate())) {
                filingMaterials.setFirstCommitDate(LocalDateTime.now());
                afterFilingMaterialsService.updateById(filingMaterials);
            }
            List<String> reviewJobUser = afterFilingMaterialsService.getReviewJobUser();
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(FilingMaterialsConstants.USER_TASK_AFTER_REVIEW), reviewJobUser));
        } else if(StrUtil.equals(nodeCommonContext.getActivityId(), FilingMaterialsConstants.USER_TASK_AFTER_REVIEW)){
            afterFilingMaterialsService.replaceReviewName(Long.parseLong(nodeCommonContext.getBusinessKey()), nodeCommonContext.getProcessInstanceId(),
                    nodeCommonContext.getActivityId(), Collections.singletonList(BusinessModuleEnum.AFTER_LEASING_FILING.name()));
        }
    }

}
