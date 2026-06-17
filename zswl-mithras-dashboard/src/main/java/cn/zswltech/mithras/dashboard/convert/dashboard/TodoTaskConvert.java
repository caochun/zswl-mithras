package cn.zswltech.mithras.dashboard.convert.dashboard;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.mithras.dashboard.application.port.DashboardProcessExtraPort;
import cn.zswltech.mithras.dashboard.application.port.DashboardProcessExtraSnapshot;
import cn.zswltech.mithras.dashboard.enums.DashboardProcessModel;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoProcessRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListRSP;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 任务转换
 *
 * @author zhouning
 * @date 2024/6/19 5:33 PM
 */
@Component
public class TodoTaskConvert {

    @Resource
    private DeptNameResolver deptNameResolver;
    @Resource
    private ClientNameResolver clientNameResolver;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private DashboardProcessExtraPort dashboardProcessExtraPort;

    public DashboardTodoProcessRSP flowResp2TodoProcessRSP(TaskResp resp, ProcessResp processResp) {
        DashboardTodoProcessRSP dashboardTodoProcessRSP = new DashboardTodoProcessRSP();
        copyFlowResp2TodoProcessRSP(resp, dashboardTodoProcessRSP);
        dashboardTodoProcessRSP.setCurTaskNames(processResp.getCurTaskNames());
        dashboardTodoProcessRSP.setCurAssigneeIds(processResp.getCurAssigneeIds());
        return dashboardTodoProcessRSP;
    }

    public void copyFlowResp2TodoProcessRSP(TaskResp resp, DashboardTodoProcessRSP dashboardTodoProcessRSP) {
        dashboardTodoProcessRSP.setTaskId(resp.getTaskId());
        dashboardTodoProcessRSP.setProcessInstanceId(resp.getProcessInstanceId());
        dashboardTodoProcessRSP.setProcessModelType(DashboardProcessModel.displayOf(resp.getModelKey()));
        dashboardTodoProcessRSP.setModelKey(resp.getModelKey());
        dashboardTodoProcessRSP.setAssignee(Optional.ofNullable(resp.getAssignee()).map(Long::valueOf).orElse(null));
        dashboardTodoProcessRSP.setStartUserId(Optional.ofNullable(resp.getStartUserId()).map(Long::valueOf).orElse(null));
        dashboardTodoProcessRSP.setProcessStartTime(LocalDateTimeUtil.of(resp.getProcessStartTime()));
        dashboardTodoProcessRSP.setBusinessKey(resp.getBusinessKey());
        dashboardTodoProcessRSP.setProcessName(resp.getProcessInstanceName());
        dashboardTodoProcessRSP.setProcessEndTime(LocalDateTimeUtil.of(resp.getProcessEndTime()));
        dashboardTodoProcessRSP.setTaskCreateTime(LocalDateTimeUtil.of(resp.getTaskCreateTime()));
        dashboardTodoProcessRSP.setTaskEndTime(LocalDateTimeUtil.of(resp.getTaskEndTime()));
        dashboardTodoProcessRSP.setStartUserDeptId(Optional.ofNullable(resp.getStartUserDeptId()).map(Long::valueOf).orElse(null));
    }

    public DashboardTodoProcessRSP flowResp2BackStepRSP(TaskResp resp) {
        DashboardTodoProcessRSP taskListRSP = new DashboardTodoProcessRSP();
        copyFlowResp2TodoProcessRSP(resp, taskListRSP);
        return taskListRSP;
    }

    public void processPrepare2TodoProcessRSP(ProcessPrepareListRSP processPrepareListRSP, DashboardTodoProcessRSP dashboardTodoProcessRSP) {
        dashboardTodoProcessRSP.setProcessModelType(processPrepareListRSP.getProcessTypeName());
        dashboardTodoProcessRSP.setProcessName(processPrepareListRSP.getFormName());
        dashboardTodoProcessRSP.setProjName(processPrepareListRSP.getProjName());
        dashboardTodoProcessRSP.setProjCode(processPrepareListRSP.getProjCode());
        dashboardTodoProcessRSP.setPrepareId(String.valueOf(processPrepareListRSP.getId()));
        dashboardTodoProcessRSP.setClientName(processPrepareListRSP.getClientName());
        dashboardTodoProcessRSP.setCurTaskNames(processPrepareListRSP.getCurrentNode());
        dashboardTodoProcessRSP.setCurAssigneeIds(processPrepareListRSP.getCurrentAssignee());
        dashboardTodoProcessRSP.setCurTaskNames(processPrepareListRSP.getCurrentNode());
        dashboardTodoProcessRSP.setCurAssigneeNames(processPrepareListRSP.getCurrentAssigneeNames());
        dashboardTodoProcessRSP.setProcessStartTime(processPrepareListRSP.getApplyTime());
    }

    public void receiveTaskListRSPFillName(List<DashboardTodoProcessRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(DashboardTodoProcessRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = deptNameResolver.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, DashboardProcessExtraSnapshot> processExtraMap = dashboardProcessExtraPort.listByProcessInstanceIds(
                rspList.stream().map(DashboardTodoProcessRSP::getProcessInstanceId).collect(Collectors.toSet()));
        Map<Long, String> clientNameMap = clientNameResolver.clientId2Name(processExtraMap.values().stream()
                .map(DashboardProcessExtraSnapshot::getClientId).filter(Objects::nonNull).collect(Collectors.toSet()));

        // 人列表
        Set<Long> userIdSet = rspList.stream()
                .map(rsp -> {
                    Set<Long> set = new HashSet<>();
                    set.add(rsp.getStartUserId());
                    set.add(rsp.getAssignee());
                    if (StringUtils.isNotBlank(rsp.getCurAssigneeIds())) {
                        set.addAll(Stream.of(rsp.getCurAssigneeIds().split(",")).map(Long::valueOf).collect(Collectors.toList()));
                    }
                    return set;
                })
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> userNameMap = userNameResolver.sysUserId2Name(userIdSet);
        rspList.stream().forEach(rsp -> {
            rsp.setStartUserDeptName(deptNameMap.get(rsp.getStartUserDeptId()));
            rsp.setStartUserName(userNameMap.get(rsp.getStartUserId()));
            rsp.setAssineeName(userNameMap.get(rsp.getAssignee()));
            if (StringUtils.isNotBlank(rsp.getCurAssigneeIds())) {
                rsp.setCurAssigneeNames(Stream.of(rsp.getCurAssigneeIds().split(","))
                        .map(Long::valueOf)
                        .map(a -> userNameMap.get(a))
                        .collect(Collectors.joining(",")));
            }
            DashboardProcessExtraSnapshot extra = processExtraMap.get(rsp.getProcessInstanceId());
            rsp.setClientId(Optional.ofNullable(extra).map(DashboardProcessExtraSnapshot::getClientId).orElse(null));
            rsp.setClientName(Optional.ofNullable(rsp.getClientId()).map(i -> clientNameMap.get(i)).orElse(null));
            if (null != extra) {
                rsp.setProjName(extra.getProjName());
                rsp.setProjCode(extra.getProjCode());
                rsp.setContractCode(extra.getContractCode());
            }
        });
    }


    public void baskStepRSPFillName(List<DashboardTodoProcessRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(DashboardTodoProcessRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = deptNameResolver.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, DashboardProcessExtraSnapshot> processExtraMap = dashboardProcessExtraPort.listByProcessInstanceIds(
                rspList.stream().map(DashboardTodoProcessRSP::getProcessInstanceId).collect(Collectors.toSet()));
        Map<Long, String> clientNameMap = clientNameResolver.clientId2Name(processExtraMap.values().stream()
                .map(DashboardProcessExtraSnapshot::getClientId).filter(Objects::nonNull).collect(Collectors.toSet()));


        // 人列表
        Set<Long> userIdSet = rspList.stream()
                .map(rsp -> {
                    Set<Long> set = new HashSet<>();
                    set.add(rsp.getStartUserId());
                    set.add(rsp.getAssignee());
                    return set;
                })
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> userNameMap = userNameResolver.sysUserId2Name(userIdSet);

        rspList.stream().forEach(rsp -> {
            rsp.setStartUserDeptName(deptNameMap.get(rsp.getStartUserDeptId()));
            rsp.setStartUserName(userNameMap.get(rsp.getStartUserId()));
            rsp.setCurAssigneeNames(userNameMap.get(rsp.getAssignee()));
            DashboardProcessExtraSnapshot extra = processExtraMap.get(rsp.getProcessInstanceId());
            rsp.setClientId(Optional.ofNullable(extra).map(DashboardProcessExtraSnapshot::getClientId).orElse(null));
            rsp.setClientName(Optional.ofNullable(rsp.getClientId()).map(i -> clientNameMap.get(i)).orElse(null));
            if (null != extra) {
                rsp.setProjName(extra.getProjName());
                rsp.setProjCode(extra.getProjCode());
                rsp.setContractCode(extra.getContractCode());
            }
        });
    }
}
