package cn.zswltech.mithras.dashboard.convert.dashboard;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.domain.resp.CcProcessResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dashboard.application.port.DashboardProcessExtraPort;
import cn.zswltech.mithras.dashboard.application.port.DashboardProcessExtraSnapshot;
import cn.zswltech.mithras.dashboard.enums.DashboardProcessModel;
import cn.zswltech.mithras.dto.dashboard.DashBoardProcessCcListRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoProcessRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 转换
 *
 * @author zhouning
 * @date 2024/6/19 4:27 PM
 */
@Slf4j
@Component
public class TodoProcessConvert {

    @Resource
    private DeptNameResolver deptNameResolver;
    @Resource
    private ClientNameResolver clientNameResolver;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private DashboardProcessExtraPort dashboardProcessExtraPort;

    public DashboardTodoProcessRSP flowResp2TodoProcess(ProcessResp resp) {
        DashboardTodoProcessRSP dashboardTodoProcessRSP = new DashboardTodoProcessRSP();
        copyFlowResp2RSP(resp, dashboardTodoProcessRSP);
        return dashboardTodoProcessRSP;
    }

    public void copyFlowResp2RSP(ProcessResp resp, DashboardTodoProcessRSP dashboardTodoProcessRSP) {
        dashboardTodoProcessRSP.setTaskId(resp.getCurTaskIds());
        dashboardTodoProcessRSP.setProcessInstanceId(resp.getProcessInstanceId());
        dashboardTodoProcessRSP.setStartUserId(Optional.ofNullable(resp.getStartUserId()).map(Long::valueOf).orElse(null));
        dashboardTodoProcessRSP.setProcessModelType(DashboardProcessModel.displayOf(resp.getModelKey()));
        dashboardTodoProcessRSP.setModelKey(resp.getModelKey());
        dashboardTodoProcessRSP.setBusinessKey(resp.getBusinessKey());
        dashboardTodoProcessRSP.setProcessStartTime(LocalDateTimeUtil.of(resp.getStartTime()));
        dashboardTodoProcessRSP.setProcessEndTime(LocalDateTimeUtil.of(resp.getEndTime()));
        dashboardTodoProcessRSP.setCurTaskNames(resp.getCurTaskNames());
        dashboardTodoProcessRSP.setCurAssigneeIds(resp.getCurAssigneeIds());
        dashboardTodoProcessRSP.setProcessStatus(String.valueOf(resp.getProcessStatus()));
        dashboardTodoProcessRSP.setProcessName(resp.getProcessInstanceName());
        dashboardTodoProcessRSP.setStartUserDeptId(Optional.ofNullable(resp.getStartUserDeptId()).map(Long::valueOf).orElse(null));
    }

    public void processListRSPFillName(List<DashboardTodoProcessRSP> rspList) {
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
                    //set.add(rsp.getFinalAssigneeId());
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
            //rsp.setFinalAssigneeName(userNameMap.get(rsp.getFinalAssigneeId()));
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

    public DashBoardProcessCcListRSP flowCcResp2RSP(CcProcessResp ccProcessResp) {
        DashBoardProcessCcListRSP processCcListRSP = new DashBoardProcessCcListRSP();
        processCcListRSP.setId(ccProcessResp.getId());
        processCcListRSP.setProcessInstanceId(ccProcessResp.getProcessInstanceId());
        processCcListRSP.setBusinessKey(ccProcessResp.getBusinessKey());
        processCcListRSP.setModelName(DashboardProcessModel.displayOf(ccProcessResp.getModelKey()));
        processCcListRSP.setProcessStatus(Optional.ofNullable(ccProcessResp.getProcessStatus()).map(String::valueOf).orElse(null));
        processCcListRSP.setModelKey(ccProcessResp.getModelKey());
        processCcListRSP.setStartUserId(Optional.ofNullable(ccProcessResp.getStartUserId()).map(Long::valueOf).orElse(null));
        processCcListRSP.setStartTime(LocalDateTimeUtil.of(ccProcessResp.getStartTime()));
        processCcListRSP.setEndTime(LocalDateTimeUtil.of(ccProcessResp.getEndTime()));
        processCcListRSP.setProcessName(ccProcessResp.getProcessInstanceName());
        processCcListRSP.setStartUserDeptId(Optional.ofNullable(ccProcessResp.getStartUserDeptId()).map(Long::valueOf).orElse(null));
        processCcListRSP.setSenderId(Optional.ofNullable(ccProcessResp.getSenderId()).map(Long::valueOf).orElse(null));
        processCcListRSP.setReadFlag(ccProcessResp.getReadFlag());
        processCcListRSP.setSubModule(ccProcessResp.getSubModule());
        processCcListRSP.setMainModule(Optional.ofNullable(DashboardProcessModel.getByName(processCcListRSP.getModelKey())).map(DashboardProcessModel::getBusinessModuleName).orElse(null));
        return processCcListRSP;
    }


    public void ccProcessRSPFillName(List<DashBoardProcessCcListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(DashBoardProcessCcListRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = deptNameResolver.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, DashboardProcessExtraSnapshot> processExtraMap = dashboardProcessExtraPort.listByProcessInstanceIds(
                rspList.stream().map(DashBoardProcessCcListRSP::getProcessInstanceId).collect(Collectors.toSet()));
        Map<Long, String> clientNameMap = clientNameResolver.clientId2Name(processExtraMap.values().stream()
                .map(DashboardProcessExtraSnapshot::getClientId).filter(Objects::nonNull).collect(Collectors.toSet()));

        // 人列表
        Set<Long> userIdSet = rspList.stream()
                .map(rsp -> {
                    Set<Long> set = new HashSet<>();
                    set.add(rsp.getStartUserId());
                    set.add(rsp.getSenderId());
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
            rsp.setSenderName(userNameMap.get(rsp.getSenderId()));
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
