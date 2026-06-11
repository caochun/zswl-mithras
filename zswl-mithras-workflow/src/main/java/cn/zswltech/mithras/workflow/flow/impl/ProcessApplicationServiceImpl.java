package cn.zswltech.mithras.workflow.flow.impl;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.beta.FlowImageGenerator;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.flow.execution.MockStartComplexProcessREQ;
import cn.zswltech.mithras.dto.flow.execution.MockStartProcessREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessBaseREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessListREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessListRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessNodeRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessPictureRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessTraceRSP;
import cn.zswltech.mithras.dto.flow.search.TransferUserListREQ;
import cn.zswltech.mithras.foundation.annotation.AdminAuthCheck;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.port.AdminAuthResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserOrgResolver;
import cn.zswltech.mithras.foundation.port.SystemConfigResolver;
import cn.zswltech.mithras.workflow.flow.ProcessApplicationService;
import cn.zswltech.mithras.workflow.flow.convert.FlowProcessConvert;
import cn.zswltech.mithras.workflow.flow.port.CurrentUserBizDeptResolver;
import cn.zswltech.mithras.workflow.flow.service.ProcessService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.foundation.enums.JobEnum.yinZhangGuanLi;

@Service
public class ProcessApplicationServiceImpl implements ProcessApplicationService {

    @Autowired
    private HttpServletResponse response;
    @Resource
    private ProcessService processService;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private FlowModelApiService modelApiService;
    @Resource
    private CurrentUserBizDeptResolver currentUserBizDeptResolver;
    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;
    @Resource
    private AdminAuthResolver adminAuthResolver;
    @Resource
    private SystemConfigResolver systemConfigResolver;
    @Resource
    private FlowImageGenerator flowImageGenerator;

    @Override
    public R<List<SelectRSP>> listTransferUser(@Valid TransferUserListREQ req) {
        return R.ok(processService.listTransferUser(req));
    }

    @Override
    public R<PageR<ProcessListRSP>> list(ProcessListREQ req) {
        ProcessPageReq flowReq = flowProcessConvert.req2FlowReq(req);
        flowReq.setCurLoginUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        if (controlProcessViewAuth(flowReq)) {
            flowReq.setAuthFlag(1);
        }
        flowReq.setSortType(1);
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (CollectionUtils.isEmpty(flowRespPage.getContents())) {
            return R.ok(PageR.of(new ArrayList<>(), flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize()));
        }
        List<ProcessListRSP> rspList = flowRespPage.getContents().stream().map(flowProcessConvert::flowResp2RSP).collect(Collectors.toList());
        flowProcessConvert.processListRSPFillName(rspList);
        return R.ok(PageR.of(rspList, flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize()));
    }

    @Override
    public R<ProcessTraceRSP> trace(ProcessBaseREQ req) {
        return null;
    }

    @Override
    public R<PageR<ProcessHistoryRSP>> history(ProcessHistoryREQ req) {
        return R.ok(processService.history(req));
    }

    @Override
    public R<List<ProcessNodeRSP>> queryCanJumpNodes(ProcessBaseREQ req) {
        return R.ok(processService.queryCanJumpNodes(req.getProcessInstanceId()));
    }

    @Override
    @AdminAuthCheck
    public R<Void> start(MockStartProcessREQ req) {
        StartProcessReq flowReq = new StartProcessReq();
        flowReq.setProcessInstanceName(req.getProcessName());
        flowReq.setModelKey(ProcessModelTypeEnum.MithrasNewTestModel.name());
        GlobalExt globalExt = modelApiService.findNewestModelExtByModelKey(ProcessModelTypeEnum.MithrasNewTestModel.name());
        Map<String, List<String>> targetAssigneeListMap = new HashMap<>();
        for (UserTaskExt userTaskExt : globalExt.getUserTaskExtMap().values()) {
            if (UserDefineTypeEnum.PROCESS_START_TARGET.getType().equals(userTaskExt.getApproverType())) {
                if (req.getAssigneeListMap().containsKey(userTaskExt.getActivityId())) {
                    targetAssigneeListMap.put(userTaskExt.getActivityId(), req.getAssigneeListMap().get(userTaskExt.getActivityId()).stream().map(String::valueOf).collect(Collectors.toList()));
                } else {
                    targetAssigneeListMap.put(userTaskExt.getActivityId(), new ArrayList<>());
                }
            }
        }
        flowReq.setBusinessKey(String.valueOf(req.getBusinessKey()));
        flowReq.setSubModule(req.getSubModule());
        flowReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        flowReq.setTargetAssigneeListMap(targetAssigneeListMap);
        OrgDO bizOrgDO = currentUserBizDeptResolver.currentUserBizDept();
        flowReq.setStartUserDeptId(String.valueOf(bizOrgDO.getId()));
        flowProcessApiService.start(flowReq);
        return R.ok();
    }

    @Override
    public R<Void> startComplex(MockStartComplexProcessREQ req) {
        StartProcessReq flowReq = new StartProcessReq();
        flowReq.setProcessInstanceName(req.getProcessName());
        flowReq.setModelKey("MithrasFlowComplexTestModel");
        Map<String, Object> map = new HashMap<>();

        map.put("userTask1AssigneeList", req.getUserTask1AssigneeList());
        map.put("userTask2AssigneeList", req.getUserTask2AssigneeList());
        map.put("userTaskPrepareMeetingAssigneeList", req.getUserTaskPrepareMeetingAssigneeList());
        map.put("userTaskVoteAssigneeList", req.getUserTaskVoteAssigneeList());
        map.put("userTaskCollectAssigneeList", req.getUserTaskCollectAssigneeList());
        map.put("userTaskGeneralManagerAssigneeList", req.getUserTaskGeneralManagerAssigneeList());

        flowReq.setVariables(map);
        flowReq.setBusinessKey(req.getBusinessKey());
        flowReq.setSubModule(req.getSubModule());
        flowReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        OrgDO bizOrgDO = currentUserBizDeptResolver.currentUserBizDept();
        flowReq.setStartUserDeptId(String.valueOf(bizOrgDO.getId()));
        flowProcessApiService.start(flowReq);
        return R.ok();
    }

    @Override
    @SneakyThrows
    public void mockTrace(String processInstanceId) {
        response.setHeader("Content-Disposition", "attachment;filename=" + processInstanceId + URLEncoder.encode("流程图.png"));
        flowImageGenerator.generate(processInstanceId, response.getOutputStream());
    }

    @Override
    public byte[] getProcessBpmnXml(String processInstanceId) {
        return processService.getProcessBpmnXml(processInstanceId);
    }

    @Override
    public R<ProcessPictureRSP> getProcessPictureData(String processInstanceId) {
        return R.ok(processService.getProcessPictureData(processInstanceId));
    }

    private boolean controlProcessViewAuth(ProcessPageReq flowReq) {
        if (adminAuthResolver.adminAuth()) {
            return false;
        }
        List<OrgDO> userOrgList = currentUserOrgResolver.getUserDeptList();
        List<String> jobCodeList = currentUserJobResolver.queryUserJobList(AccountUtil.getLoginInfo().getId());
        if (jobCodeList.contains(yinZhangGuanLi.name())) {
            return false;
        }

        boolean specialDeptFlag = userOrgList.stream().anyMatch(o -> equalsAny(o.getCode(),
                "YYGLB", "LDC", "DSH", "FXGLWYH", "XMPSWYH", "FLHGB_ZCBQ"
        ));
        if (specialDeptFlag) {
            return false;
        }
        boolean specialJobFlag = currentUserJobResolver.currentUserIsSpecificJob(JobEnum.riskdeptmanager.name(), JobEnum.loanreviewpost.name());
        if (specialJobFlag) {
            return false;
        }

        String jobModelMapJsonStr = systemConfigResolver.getConfigValue("flow.search.job_model_map");
        if (StringUtils.isBlank(jobModelMapJsonStr)) {
            return true;
        }
        Map<String, List<String>> jobModelMap = JSON.parseObject(jobModelMapJsonStr, new TypeReference<Map<String, List<String>>>() {
        });
        if (CollUtil.isEmpty(jobModelMap)) {
            return true;
        }
        List<String> notAuthModelKeyList = new ArrayList<>();
        jobModelMap.forEach((jobCode, modelList) -> {
            if (currentUserJobResolver.currentUserIsSpecificJob(jobCode)) {
                notAuthModelKeyList.addAll(modelList);
            }
        });
        if (CollectionUtils.isNotEmpty(notAuthModelKeyList)) {
            flowReq.setNotAuthModelKeyList(notAuthModelKeyList);
            return false;
        }
        return true;
    }
}
