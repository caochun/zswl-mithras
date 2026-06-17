package cn.zswltech.mithras.application.orchestration.adapter.ftp;

import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpGuidanceWorkflowKey;
import cn.zswltech.mithras.ftp.oldftp.application.port.model.FtpGuidanceProcessInfo;
import cn.zswltech.mithras.ftp.oldftp.application.port.FtpGuidanceWorkflowPort;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
public class FtpGuidanceWorkflowPortAdapter implements FtpGuidanceWorkflowPort {

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private OrgDOMapper orgDOMapper;

    @Override
    public FtpGuidanceProcessInfo findMonthlyGuidanceProcess(Long guidanceId) {
        return findGuidanceProcess(guidanceId,
                FtpGuidanceWorkflowKey.FTP_MONTHLY_GUIDANCE_CREATE,
                FtpGuidanceWorkflowKey.FTP_MONTHLY_GUIDANCE_MODIFY);
    }

    @Override
    public FtpGuidanceProcessInfo findQuarterlyGuidanceProcess(Long guidanceId) {
        return findGuidanceProcess(guidanceId,
                FtpGuidanceWorkflowKey.FTP_QUARTERLY_GUIDANCE_CREATE,
                FtpGuidanceWorkflowKey.FTP_QUARTERLY_GUIDANCE_MODIFY);
    }

    @Override
    public void startMonthlyGuidanceCreateFlow(Long guidanceId, Integer year, Integer month) {
        startGuidanceFlow(guidanceId, FtpGuidanceWorkflowKey.FTP_MONTHLY_GUIDANCE_CREATE,
                year + "年" + month + "月ftp定价指导");
    }

    @Override
    public void startMonthlyGuidanceModifyFlow(Long guidanceId, Integer year, Integer month) {
        startGuidanceFlow(guidanceId, FtpGuidanceWorkflowKey.FTP_MONTHLY_GUIDANCE_MODIFY,
                year + "年" + month + "月ftp定价指导");
    }

    @Override
    public void startQuarterlyGuidanceCreateFlow(Long guidanceId, Integer year, Integer quarter) {
        startGuidanceFlow(guidanceId, FtpGuidanceWorkflowKey.FTP_QUARTERLY_GUIDANCE_CREATE,
                year + "年第" + quarter + "季度最低收益率指导");
    }

    @Override
    public void startQuarterlyGuidanceModifyFlow(Long guidanceId, Integer year, Integer quarter) {
        startGuidanceFlow(guidanceId, FtpGuidanceWorkflowKey.FTP_QUARTERLY_GUIDANCE_MODIFY,
                year + "年第" + quarter + "季度最低收益率指导");
    }

    private FtpGuidanceProcessInfo findGuidanceProcess(Long guidanceId, String createFlow, String modifyFlow) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(guidanceId));
        processPageReq.setModelKeyList(Arrays.asList(createFlow, modifyFlow));
        processPageReq.setProcessStatusList(Arrays.asList(
                ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream()
                .findFirst()
                .map(this::toProcessInfo)
                .orElse(null);
    }

    private void startGuidanceFlow(Long guidanceId, String modelKey, String processInstanceName) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(modelKey);
        startProcessReq.setStartUserId(getCurrentUserId());
        startProcessReq.setBusinessKey(String.valueOf(guidanceId));
        startProcessReq.setProcessInstanceName(processInstanceName);
        OrgDO jhcwb = orgDOMapper.queryByCode("JHCWB");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(jhcwb)
                .map(OrgDO::getId)
                .map(String::valueOf)
                .orElse(null));
        processApiService.start(startProcessReq);
    }

    private String getCurrentUserId() {
        return Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
    }

    private FtpGuidanceProcessInfo toProcessInfo(ProcessResp processResp) {
        if (Objects.isNull(processResp)) {
            return null;
        }
        FtpGuidanceProcessInfo info = new FtpGuidanceProcessInfo();
        info.setProcessInstanceId(processResp.getProcessInstanceId());
        info.setBusinessKey(processResp.getBusinessKey());
        info.setModelKey(processResp.getModelKey());
        info.setModelDisplayName(Optional.ofNullable(ProcessModelTypeEnum.getByName(processResp.getModelKey()))
                .map(ProcessModelTypeEnum::getDisplay)
                .orElse(processResp.getModelName()));
        info.setProcessStatus(processResp.getProcessStatus());
        info.setStartUserId(processResp.getStartUserId());
        info.setStartUserDeptId(processResp.getStartUserDeptId());
        info.setStartUserNode(FlowUtil.isStartUserNode(processResp));
        return info;
    }
}
