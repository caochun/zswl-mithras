package cn.zswltech.mithras.application.orchestration.adapter.credit;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishProcessInfo;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishProcessPort;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishProcessStartCommand;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
public class GroupCreditEstablishProcessPortAdapter implements GroupCreditEstablishProcessPort {

    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;

    @Override
    public void start(GroupCreditEstablishProcessStartCommand command) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(command.isCreateFlow()
                ? ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name()
                : ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name());
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("projEstablishApprovalType", command.getApprovalType()),
                Pair.of("bizDeptLeader", command.getBizDeptLeaderIds()),
                Pair.of("bizDivisionLeader", command.getBizDivisionLeaderIds()),
                Pair.of("riskControlManager", command.getRiskControlManagerIds())
        ));
        startProcessReq.setStartUserId(command.getStartUserId());
        startProcessReq.setBusinessKey(command.getBusinessKey());
        startProcessReq.setSubModule(command.getSubModule());
        startProcessReq.setProcessInstanceName(command.getProcessInstanceName());
        startProcessReq.setCcUserIdList(command.getCcUserIdList());
        startProcessReq.setStartUserDeptId(command.getStartUserDeptId());
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, command.getClientId());
    }

    @Override
    public GroupCreditEstablishProcessInfo findRelatedProcess(Long groupCreditEstablishId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(groupCreditEstablishId));
        processPageReq.setModelKeyList(Arrays.asList(
                ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(),
                ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name()
        ));
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().map(this::toInfo).orElse(null);
    }

    @Override
    public boolean isProcessPass(Integer endType) {
        return ProcessBusinessStatusEnum.success(endType);
    }

    @Override
    public boolean isStartUserTask(GroupCreditEstablishProcessInfo processInfo) {
        return processInfo != null && FlowConstants.START_USER_TASK.equals(processInfo.getCurrentTaskActivityIds());
    }

    private GroupCreditEstablishProcessInfo toInfo(ProcessResp processResp) {
        GroupCreditEstablishProcessInfo info = new GroupCreditEstablishProcessInfo();
        info.setStartUserId(processResp.getStartUserId());
        info.setProcessInstanceId(processResp.getProcessInstanceId());
        info.setCurrentTaskActivityIds(processResp.getCurTaskActivityIds());
        return info;
    }
}
