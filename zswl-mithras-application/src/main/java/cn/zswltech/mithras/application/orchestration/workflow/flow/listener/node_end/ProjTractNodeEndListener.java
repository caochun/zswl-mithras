package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.node_end;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.flow.search.ProcessBaseREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessDetailRSP;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.system.user.bo.UserOrgJobInfoBO;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.MyTaskService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/5/26 15:42
 * @description
 */
@Slf4j
@Component
public class ProjTractNodeEndListener implements ApplicationListener<NodeEndEvent> {

    @Autowired
    private MyTaskService taskService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private FlowVariableApiService flowVariableApiService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void onApplicationEvent(@NotNull NodeEndEvent nodeEndEvent) {
        // 节点结束
        log.info("节点结束，记录时间. inst id:{}, node:{}", nodeEndEvent.getNodeCommonContext().getProcessInstanceId(), nodeEndEvent.getNodeCommonContext().getActivityId());
        process(nodeEndEvent.getNodeCommonContext());
    }

    private void process(NodeCommonContext nodeCommonContext) {
        // 项目跟踪流程
        if (nodeCommonContext.getModelKey().equals(ProcessModelTypeEnum.TrackEventCreateFlow.name())) {
            // 如果当前节点是【处理人】，需要拿到他的部门负责人动态放到变量 【principal】中
            if ("userTask_processor".equals(nodeCommonContext.getActivityId())) {
                ProcessBaseREQ req = new ProcessBaseREQ();
                req.setProcessInstanceId(nodeCommonContext.getProcessInstanceId());
                ProcessDetailRSP processedDetail = taskService.processDetail(req);
                if (Objects.isNull(processedDetail)) {
                    throw new MithrasException("流程记录不存在");
                }

                // 获取处理人
                String[] split = processedDetail.getCurAssigneeIds().split(",");
                //  获取处理人部门
                UserOrgJobInfoBO userOrgJobInfo = sysUserService.getUserOrgJobInfo(Long.valueOf(split[0]));
                if (userOrgJobInfo == null) {
                    throw new MithrasException("流程处理人部门不存在");
                }

                List<Long> collect = userOrgJobInfo.getOrgJobMap().values().stream().flatMap(Collection::stream)
                        .map(OrgDO::getId).distinct().collect(Collectors.toList());
                List<UserDO> orgJobUser = new ArrayList<>();
                for (Long l : collect) {
                    List<UserDO> dos = sysUserService.listSpecificOrgJobUser(l, JobEnum.businesshead.name());
                    orgJobUser.addAll(dos);
                }

                if (CollUtil.isEmpty(orgJobUser)) {
                    throw new MithrasException("流程处理人业务部门负责人不存在");
                }

                // 放进流程变量中
                List<String> assignUserIdList = orgJobUser.stream().map(UserDO::getId).map(String::valueOf).collect(Collectors.toList());
                flowVariableApiService.setVariables(nodeCommonContext.getProcessInstanceId(),
                        MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName("userTask_principal"), assignUserIdList));

            }
        }
    }
}
