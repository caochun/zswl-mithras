package cn.zswltech.mithras.workflow.flow.convert;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.mithras.dto.flow.search.BackToStepTaskListRSP;
import cn.zswltech.mithras.dto.flow.search.ReceiveTaskListRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.dto.flow.search.TaskListREQ;
import cn.zswltech.mithras.dto.flow.search.TaskListRSP;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.model.FlowQueryExtra;
import cn.zswltech.mithras.workflow.model.BizProcessData;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.workflow.mapper.FlowQueryExtraMapper;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 任务转换
 *
 * @author wangchuanhao
 * @date 2022/8/2 5:33 PM
 */
@Component
public class FlowTaskConvert {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private FlowQueryExtraMapper flowQueryExtraMapper;

    public TaskSystemPageReq req2FlowReq(TaskListREQ req) {
        TaskSystemPageReq taskSystemPageReq = new TaskSystemPageReq();
        taskSystemPageReq.setProcessInstanceIdLike(req.getProcessInstanceId());
        taskSystemPageReq.setDynamicFilterParam(new HashMap<>());

        taskSystemPageReq.setStartUserId(Optional.ofNullable(req.getStartUserId()).map(String::valueOf).orElse(null));
        taskSystemPageReq.setStartUserDeptId(Optional.ofNullable(req.getStartUserDeptId()).map(String::valueOf).orElse(null));
        taskSystemPageReq.setProcessInstanceName(req.getProcessName());
        taskSystemPageReq.setModelKey(req.getModelKey());
        taskSystemPageReq.setModelKeyList(req.getModelKeyList());

        taskSystemPageReq.setProcessCreateTimeFrom(Optional.ofNullable(req.getProcessCreateTimeFrom()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));
        taskSystemPageReq.setProcessCreateTimeTo(Optional.ofNullable(req.getProcessCreateTimeTo()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));
        taskSystemPageReq.setTaskEndTimeFrom(Optional.ofNullable(req.getTaskEndTimeFrom()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));
        taskSystemPageReq.setTaskEndTimeTo(Optional.ofNullable(req.getTaskEndTimeTo()).map(t -> DateUtil.endOfDay(new Date(LocalDateTimeUtil.toEpochMilli(t)))).orElse(null));

        taskSystemPageReq.setPageIndex(req.getPage());
        taskSystemPageReq.setPageSize(req.getPageSize());

        if (Objects.nonNull(req.getClientId()) && Objects.isNull(req.getBelongDeptId())) {
            taskSystemPageReq.setDynamicFilterSql(bizProcessDataService.taskDynamicSqlClient());
            bizProcessDataService.dynamicParamClient(taskSystemPageReq.getDynamicFilterParam(), req.getClientId());
        }
        if (Objects.nonNull(req.getExtra())) {
            taskSystemPageReq.setQueryExtraCondition(bizProcessDataService.extraQueryCondition(req.getExtra(), "t3"));
        }
        // 增加客户所属部门筛选
        if (Objects.nonNull(req.getBelongDeptId()) && Objects.isNull(req.getClientId())) {
            taskSystemPageReq.setDynamicFilterSql(bizProcessDataService.taskDynamicSqlClientDept());
            bizProcessDataService.dynamicParamClientDept(taskSystemPageReq.getDynamicFilterParam(), req.getBelongDeptId());
        }
        if (Objects.nonNull(req.getBelongDeptId()) && Objects.nonNull(req.getClientId())) {
            taskSystemPageReq.setDynamicFilterSql(bizProcessDataService.taskDynamicSqlClientDeptAndId());
            bizProcessDataService.dynamicParamClientDeptAndId(taskSystemPageReq.getDynamicFilterParam(), req.getBelongDeptId(),req.getClientId());
        }

        return taskSystemPageReq;
    }

    public BackToStepTaskListRSP flowResp2BackStepRSP(TaskResp resp) {
        BackToStepTaskListRSP taskListRSP = new BackToStepTaskListRSP();
        copyFlowResp2RSP(resp, taskListRSP);
        return taskListRSP;
    }

    public TaskListRSP flowResp2RSP(TaskResp resp) {
        TaskListRSP taskListRSP = new TaskListRSP();
        copyFlowResp2RSP(resp, taskListRSP);
        return taskListRSP;
    }

    public ReceiveTaskListRSP flowResp2ReceiveRSP(TaskResp resp, ProcessResp processResp) {
        ReceiveTaskListRSP taskListRSP = new ReceiveTaskListRSP();
        copyFlowResp2RSP(resp, taskListRSP);
        taskListRSP.setCurTaskIds(processResp.getCurTaskIds());
        taskListRSP.setCurTaskActivityIds(processResp.getCurTaskActivityIds());
        taskListRSP.setCurTaskNames(processResp.getCurTaskNames());
        taskListRSP.setCurAssigneeIds(processResp.getCurAssigneeIds());
        return taskListRSP;
    }

    public void copyFlowResp2RSP(TaskResp resp, TaskListRSP taskListRSP) {
        taskListRSP.setTaskId(resp.getTaskId());
        taskListRSP.setProcessInstanceId(resp.getProcessInstanceId());
        taskListRSP.setModelName(FlowUtil.convertModelName(resp.getModelKey()));
        taskListRSP.setModelKey(resp.getModelKey());
        taskListRSP.setTaskActivityId(resp.getTaskActivityId());
        taskListRSP.setTaskName(resp.getTaskName());
        taskListRSP.setTaskStatus(Optional.ofNullable(resp.getTaskStatus()).map(String::valueOf).orElse(null));
        taskListRSP.setProcessStatus(Optional.ofNullable(resp.getProcessStatus()).map(String::valueOf).orElse(null));
        taskListRSP.setAssignee(Optional.ofNullable(resp.getAssignee()).map(Long::valueOf).orElse(null));
        taskListRSP.setStartUserId(Optional.ofNullable(resp.getStartUserId()).map(Long::valueOf).orElse(null));
        taskListRSP.setTaskCreateTime(LocalDateTimeUtil.of(resp.getTaskCreateTime()));
        taskListRSP.setTaskEndTime(LocalDateTimeUtil.of(resp.getTaskEndTime()));
        taskListRSP.setBusinessKey(resp.getBusinessKey());
        taskListRSP.setProcessStartTime(LocalDateTimeUtil.of(resp.getProcessStartTime()));
        taskListRSP.setProcessName(resp.getProcessInstanceName());
        taskListRSP.setStartUserDeptId(Optional.ofNullable(resp.getStartUserDeptId()).map(Long::valueOf).orElse(null));
        taskListRSP.setSubModule(resp.getSubModule());
        taskListRSP.setMainModule(Optional.ofNullable(ProcessModelTypeEnum.getByName(taskListRSP.getModelKey())).map(ProcessModelTypeEnum::getBusinessModuleName).orElse(null));
        taskListRSP.setProcessDefineId(resp.getProcessDefineId());
    }

    public void taskListRSPFillName(List<TaskListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(TaskListRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, rspList.stream().map(TaskListRSP::getProcessInstanceId).collect(Collectors.toSet())))
                .stream().filter(b -> Objects.nonNull(b.getClientId()))
                .collect(Collectors.toMap(BizProcessData::getProcessInstanceId, BizProcessData::getClientId, (k1, k2) -> k1));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientProcessIdMap.values());


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
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdSet);
        //extra表字段
        List<String> instanceIdList = rspList.stream().map(TaskListRSP::getProcessInstanceId).collect(Collectors.toList());
        List<FlowQueryExtra> extraList = flowQueryExtraMapper.selectList(Wrappers.<FlowQueryExtra>lambdaQuery().in(FlowQueryExtra::getInstanceId, instanceIdList));
        Map<String, FlowQueryExtra> extraMap = extraList.stream().collect(Collectors.toMap(FlowQueryExtra::getInstanceId, Function.identity(), (k1, k2) -> k1));


        rspList.stream().forEach(rsp -> {
            rsp.setStartUserDeptName(deptNameMap.get(rsp.getStartUserDeptId()));
            rsp.setStartUserName(userNameMap.get(rsp.getStartUserId()));
            rsp.setAssineeName(userNameMap.get(rsp.getAssignee()));
            rsp.setClientId(clientProcessIdMap.get(rsp.getProcessInstanceId()));
            rsp.setClientName(Optional.ofNullable(rsp.getClientId()).map(i -> clientNameMap.get(i)).orElse(null));
            //
            FlowQueryExtra extra = extraMap.get(rsp.getProcessInstanceId());
            if (null != extra) {
                rsp.setProjName(extra.getProjName());
                rsp.setProjCode(extra.getProjCode());
                rsp.setContractCode(extra.getContractCode());
            }
        });
    }

    public void baskStepRSPFillName(List<BackToStepTaskListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(TaskListRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, rspList.stream().map(BackToStepTaskListRSP::getProcessInstanceId).collect(Collectors.toSet())))
                .stream().filter(b -> Objects.nonNull(b.getClientId()))
                .collect(Collectors.toMap(BizProcessData::getProcessInstanceId, BizProcessData::getClientId, (k1, k2) -> k1));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientProcessIdMap.values());


        // 人列表
        Set<Long> userIdSet = rspList.stream()
                .map(rsp -> {
                    Set<Long> set = new HashSet<>();
                    set.add(rsp.getStartUserId());
                    set.add(rsp.getAssignee());
                    set.add(rsp.getBackUserId());
                    return set;
                })
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdSet);

        //extra表字段
        List<String> instanceIdList = rspList.stream().map(TaskListRSP::getProcessInstanceId).collect(Collectors.toList());
        List<FlowQueryExtra> extraList = flowQueryExtraMapper.selectList(Wrappers.<FlowQueryExtra>lambdaQuery().in(FlowQueryExtra::getInstanceId, instanceIdList));
        Map<String, FlowQueryExtra> extraMap = extraList.stream().collect(Collectors.toMap(FlowQueryExtra::getInstanceId, Function.identity(), (k1, k2) -> k1));


        rspList.stream().forEach(rsp -> {
            rsp.setStartUserDeptName(deptNameMap.get(rsp.getStartUserDeptId()));
            rsp.setStartUserName(userNameMap.get(rsp.getStartUserId()));
            rsp.setAssineeName(userNameMap.get(rsp.getAssignee()));
            rsp.setBackUserName(userNameMap.get(rsp.getBackUserId()));
            rsp.setClientId(clientProcessIdMap.get(rsp.getProcessInstanceId()));
            rsp.setClientName(Optional.ofNullable(rsp.getClientId()).map(i -> clientNameMap.get(i)).orElse(null));
            //
            FlowQueryExtra extra = extraMap.get(rsp.getProcessInstanceId());
            if (null != extra) {
                rsp.setProjName(extra.getProjName());
                rsp.setProjCode(extra.getProjCode());
                rsp.setContractCode(extra.getContractCode());
            }
        });
    }

    public void receiveTaskListRSPFillName(List<ReceiveTaskListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(TaskListRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, rspList.stream().map(ReceiveTaskListRSP::getProcessInstanceId).collect(Collectors.toSet())))
                .stream().filter(b -> Objects.nonNull(b.getClientId()))
                .collect(Collectors.toMap(BizProcessData::getProcessInstanceId, BizProcessData::getClientId, (k1, k2) -> k1));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientProcessIdMap.values());
        // 客户所属部门
        Map<Long, Long> clientDeptIdMap = id2NameService.clientId2DeptId(clientProcessIdMap.values());
        Map<Long, String> clientBelongDeptMap = id2NameService.deptId2Name(clientDeptIdMap.values());

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
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdSet);
        //extra表字段
        List<String> instanceIdList = rspList.stream().map(TaskListRSP::getProcessInstanceId).collect(Collectors.toList());
        List<FlowQueryExtra> extraList = flowQueryExtraMapper.selectList(Wrappers.<FlowQueryExtra>lambdaQuery().in(FlowQueryExtra::getInstanceId, instanceIdList));
        Map<String, FlowQueryExtra> extraMap = extraList.stream().collect(Collectors.toMap(FlowQueryExtra::getInstanceId, Function.identity(), (k1, k2) -> k1));


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
            rsp.setClientId(clientProcessIdMap.get(rsp.getProcessInstanceId()));
            rsp.setClientName(Optional.ofNullable(rsp.getClientId()).map(i -> clientNameMap.get(i)).orElse(null));
            //
            FlowQueryExtra extra = extraMap.get(rsp.getProcessInstanceId());
            if (null != extra) {
                rsp.setProjName(extra.getProjName());
                rsp.setProjCode(extra.getProjCode());
                rsp.setContractCode(extra.getContractCode());
            }
            // 增加返回客户所属部门
            rsp.setBelongDeptId(rsp.getClientId() != null ? clientDeptIdMap.get(rsp.getClientId()) : null);
            rsp.setBelongDeptName(rsp.getBelongDeptId() != null ? clientBelongDeptMap.get(rsp.getBelongDeptId()) : null);
        });
    }

    public TaskDetailRSP flowResp2Detail(TaskResp flowResp) {
        TaskDetailRSP detailRSP = new TaskDetailRSP();
        copyFlowResp2RSP(flowResp, detailRSP);
        return detailRSP;
    }
}
