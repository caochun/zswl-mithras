package cn.zswltech.mithras.service.controller.flow;

import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.beta.FlowImageGenerator;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.flow.ProcessApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.flow.execution.MockStartComplexProcessREQ;
import cn.zswltech.mithras.dto.flow.execution.MockStartProcessREQ;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.service.auth.aop.AdminAuthCheck;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程相关接口
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:44 PM
 */
@RestController
public class ProcessController implements ProcessApi {

    @Autowired
    private HttpServletResponse response;
    @Resource
    private ProcessService processService;
    @Resource
    private MyTaskService myTaskService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private FlowModelApiService modelApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowImageGenerator flowImageGenerator;

    @Override
    public R<List<SelectRSP>> listTransferUser(@Valid TransferUserListREQ req) {
        return R.ok(processService.listTransferUser(req));
    }

    @Override
    public R<PageR<ProcessListRSP>> list(ProcessListREQ req) {
        return R.ok(myTaskService.searchList(req));
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
            // 设置审批人，没有审批人就设成空列表让他跳过
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
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
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
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
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

}
