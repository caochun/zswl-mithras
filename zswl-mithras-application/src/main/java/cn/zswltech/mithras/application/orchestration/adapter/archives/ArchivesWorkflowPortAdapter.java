package cn.zswltech.mithras.application.orchestration.adapter.archives;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.archives.port.ArchivesWorkflowPort;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Component
public class ArchivesWorkflowPortAdapter implements ArchivesWorkflowPort {

    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;

    @Override
    public void startDownloadApproval(String batch) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.ArchivesDownloadFlow.name());
        startProcessReq.setStartUserId(currentUserId());
        startProcessReq.setBusinessKey(batch);
        startProcessReq.setProcessInstanceName("借阅审批");
        processApiService.start(startProcessReq);
    }

    @Override
    public boolean isArchivesApprovalRunning(Long archivesId) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(archivesId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.ArchivesFlow.name()));
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = taskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        return !Objects.isNull(processResp);
    }

    @Override
    public void startArchivesApproval(ArchivesApprovalStartContext context) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.ArchivesFlow.name());
        startProcessReq.setStartUserId(currentUserId());
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(context.getBizDeptLeaderId()) ?
                        ListUtil.toList(String.valueOf(context.getBizDeptLeaderId())) : new ArrayList<>())
        ));
        startProcessReq.setBusinessKey(String.valueOf(context.getArchivesId()));
        startProcessReq.setProcessInstanceName(context.getProjName() + "归档审批");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(context.getBizDeptId())
                .map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, context.getClientId());
    }

    private String currentUserId() {
        return Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
    }
}
