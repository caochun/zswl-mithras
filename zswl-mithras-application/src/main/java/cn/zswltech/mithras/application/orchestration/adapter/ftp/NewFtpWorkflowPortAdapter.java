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
import cn.zswltech.mithras.foundation.port.CurrentUserOrgResolver;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpBusinessModule;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpWorkflowKey;
import cn.zswltech.mithras.ftp.newftp.application.port.model.NewFtpProcessInfo;
import cn.zswltech.mithras.ftp.newftp.application.port.NewFtpWorkflowPort;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class NewFtpWorkflowPortAdapter implements NewFtpWorkflowPort {

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;

    @Override
    public NewFtpProcessInfo findGuidanceProcess(Long mainId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(mainId));
        processPageReq.setModelKeyList(NewFtpBusinessModule.NEW_FTP_GUIDANCE.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(
                ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream()
                .findFirst()
                .map(this::toProcessInfo)
                .orElse(null);
    }

    @Override
    public void startGuidanceCreateFlow(Long mainId, LocalDate month) {
        startGuidanceFlow(mainId, month, NewFtpWorkflowKey.FTP_MONTHLY_GUIDANCE_CREATE);
    }

    @Override
    public void startGuidanceModifyFlow(Long mainId, LocalDate month) {
        startGuidanceFlow(mainId, month, NewFtpWorkflowKey.FTP_MONTHLY_GUIDANCE_MODIFY);
    }

    @Override
    public String startInterestChangeApplyFlow(Long applyId) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(NewFtpWorkflowKey.FTP_INTEREST_CHANGE_APPLY);
        startProcessReq.setBusinessKey(applyId.toString());
        startProcessReq.setProcessInstanceName("FTP计息变更");
        startProcessReq.setStartUserId(getCurrentUserId());
        List<OrgDO> orgList = currentUserOrgResolver.getUserDeptList();
        if (orgList != null && !orgList.isEmpty()) {
            startProcessReq.setStartUserDeptId(orgList.get(0).getId().toString());
        }
        return processApiService.start(startProcessReq);
    }

    private void startGuidanceFlow(Long mainId, LocalDate month, String modelKey) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(modelKey);
        startProcessReq.setStartUserId(getCurrentUserId());
        startProcessReq.setBusinessKey(String.valueOf(mainId));
        startProcessReq.setProcessInstanceName(month.getYear() + "年" + month.getMonthValue() + "月FTP定价指导审批流程");
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

    private NewFtpProcessInfo toProcessInfo(ProcessResp processResp) {
        if (Objects.isNull(processResp)) {
            return null;
        }
        NewFtpProcessInfo info = new NewFtpProcessInfo();
        info.setProcessInstanceId(processResp.getProcessInstanceId());
        info.setBusinessKey(processResp.getBusinessKey());
        info.setModelKey(processResp.getModelKey());
        info.setProcessStatus(processResp.getProcessStatus());
        info.setStartUserId(processResp.getStartUserId());
        info.setStartUserDeptId(processResp.getStartUserDeptId());
        info.setStartUserNode(FlowUtil.isStartUserNode(processResp));
        return info;
    }
}
