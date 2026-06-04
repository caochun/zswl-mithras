package cn.zswltech.mithras.service.flow.listener.node_end;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.filingmaterials.domain.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsBusinessTypeEnum;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.FilingMaterials;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author bigbear
 * @date 2025/5/26 15:42
 * @description
 */
@Slf4j
@Component
public class FilingMaterialsNodeEndListener implements ApplicationListener<NodeEndEvent> {

    @Autowired
    private MyTaskService taskService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private FilingMaterialsService filingMaterialsService;
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
        if (!StrUtil.equals(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.FilingMaterialsApplyFlow.name())) {
            return;
        }
        long id = Long.parseLong(nodeCommonContext.getBusinessKey());
        FilingMaterials filingMaterials = filingMaterialsService.getById(id);
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (StrUtil.equalsAny(nodeCommonContext.getActivityId(), FilingMaterialsConstants.TASK_NODE_CODE_2,
                FilingMaterialsConstants.TASK_NODE_CODE_3)) {
            filingMaterialsService.replaceReviewName(Long.parseLong(nodeCommonContext.getBusinessKey()), nodeCommonContext.getProcessInstanceId(),
                    nodeCommonContext.getActivityId(),FilingMaterialsBusinessTypeEnum.getBusinessTypeAll());
            if (Objects.equals(nodeCommonContext.getActivityId(), FilingMaterialsConstants.TASK_NODE_CODE_2)) {
                filingMaterials.setArchivePreReviewLastSubmitTime(LocalDateTime.now());
                /*档案管理员节点审批人更新*/
                List<String> assignUserIdList = filingMaterialsService.getReviewJobUser();
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(FilingMaterialsConstants.TASK_NODE_CODE_3), assignUserIdList));
            } else {
                filingMaterials.setArchiveReviewLastSubmitTime(LocalDateTime.now());
            }
            filingMaterialsService.updateById(filingMaterials);
        } else if (StrUtil.equals(nodeCommonContext.getActivityId(), FilingMaterialsConstants.TASK_NODE_1)) {
            //首岗提交时间赋值，退回再提交不更新
            if (Objects.isNull(filingMaterials.getFirstCommitDate())) {
                filingMaterials.setFirstCommitDate(LocalDateTime.now());
                filingMaterialsService.updateById(filingMaterials);
            }
            //退回再提交更新退回时间
            if (Objects.nonNull(filingMaterials.getReturnDate())) {
                filingMaterialsService.updateFilingMaterialsReturnDate(nodeCommonContext.getBusinessKey(), LocalDate.now(), null);
            }
            /*初审岗 默认葛晓青*/
            List<String> assignUserIdList = filingMaterialsService.getInitApproveUser();
            flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(), MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(FilingMaterialsConstants.TASK_NODE_CODE_2), assignUserIdList));
        }
    }

}
