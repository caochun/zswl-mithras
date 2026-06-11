package cn.zswltech.mithras.dashboard.convert.dashboard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.zswltech.flow.core.domain.resp.CcProcessResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.dashboard.DashBoardProcessCcListRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoProcessRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.mapper.model.FlowQueryExtra;
import cn.zswltech.mithras.workflow.mapper.model.BizProcessData;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.workflow.mapper.FlowQueryExtraMapper;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
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
    private Id2NameService id2NameService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private FlowQueryExtraMapper flowQueryExtraMapper;

    public DashboardTodoProcessRSP flowResp2TodoProcess(ProcessResp resp) {
        DashboardTodoProcessRSP dashboardTodoProcessRSP = new DashboardTodoProcessRSP();
        copyFlowResp2RSP(resp, dashboardTodoProcessRSP);
        return dashboardTodoProcessRSP;
    }

    public void copyFlowResp2RSP(ProcessResp resp, DashboardTodoProcessRSP dashboardTodoProcessRSP) {
        dashboardTodoProcessRSP.setTaskId(resp.getCurTaskIds());
        dashboardTodoProcessRSP.setProcessInstanceId(resp.getProcessInstanceId());
        dashboardTodoProcessRSP.setStartUserId(Optional.ofNullable(resp.getStartUserId()).map(Long::valueOf).orElse(null));
        dashboardTodoProcessRSP.setProcessModelType(FlowUtil.convertModelName(resp.getModelKey()));
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
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, rspList.stream().map(DashboardTodoProcessRSP::getProcessInstanceId).collect(Collectors.toSet())))
                .stream().filter(b -> Objects.nonNull(b.getClientId()))
                .collect(Collectors.toMap(BizProcessData::getProcessInstanceId, BizProcessData::getClientId, (k1, k2) -> k1));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientProcessIdMap.values());


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
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdSet);
        //extra表字段
        List<String> instanceIdList = rspList.stream().map(DashboardTodoProcessRSP::getProcessInstanceId).collect(Collectors.toList());
        List<FlowQueryExtra> extraList = flowQueryExtraMapper.selectList(Wrappers.<FlowQueryExtra>lambdaQuery().in(FlowQueryExtra::getInstanceId, instanceIdList));
        //按照业务是不会出现重复 key 的，但是以防万一
        Map<String, FlowQueryExtra> extraMap = extraList.stream().collect(Collectors.toMap(FlowQueryExtra::getInstanceId, Function.identity(), (k1, k2) -> k1));
        if (rspList.size() > 200) {
            ExecutorService executorService = ThreadUtil.newSingleExecutor();
            executorService.execute(() -> {
                List<List<FlowQueryExtra>> lists = extraList.stream().collect(Collectors.groupingBy(FlowQueryExtra::getInstanceId))
                        .values().stream().filter(a -> a.size() > 1).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(lists)) {
                    log.error("流程列表导出全量数据，存在 instanceId 重复，数据=》{}", lists);
                }
            });
        }

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

    public DashBoardProcessCcListRSP flowCcResp2RSP(CcProcessResp ccProcessResp) {
        DashBoardProcessCcListRSP processCcListRSP = new DashBoardProcessCcListRSP();
        processCcListRSP.setId(ccProcessResp.getId());
        processCcListRSP.setProcessInstanceId(ccProcessResp.getProcessInstanceId());
        processCcListRSP.setBusinessKey(ccProcessResp.getBusinessKey());
        processCcListRSP.setModelName(FlowUtil.convertModelName(ccProcessResp.getModelKey()));
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
        processCcListRSP.setMainModule(Optional.ofNullable(ProcessModelTypeEnum.getByName(processCcListRSP.getModelKey())).map(ProcessModelTypeEnum::getBusinessModuleName).orElse(null));
        return processCcListRSP;
    }


    public void ccProcessRSPFillName(List<DashBoardProcessCcListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(DashBoardProcessCcListRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, rspList.stream().map(DashBoardProcessCcListRSP::getProcessInstanceId).collect(Collectors.toSet())))
                .stream().filter(b -> Objects.nonNull(b.getClientId()))
                .collect(Collectors.toMap(BizProcessData::getProcessInstanceId, BizProcessData::getClientId, (k1, k2) -> k1));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientProcessIdMap.values());

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
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdSet);
        //extra表字段
        List<String> instanceIdList = rspList.stream().map(DashBoardProcessCcListRSP::getProcessInstanceId).collect(Collectors.toList());
        List<FlowQueryExtra> extraList = flowQueryExtraMapper.selectList(Wrappers.<FlowQueryExtra>lambdaQuery().in(FlowQueryExtra::getInstanceId, instanceIdList));
        Map<String, FlowQueryExtra> extraMap = extraList.stream().collect(Collectors.toMap(FlowQueryExtra::getInstanceId, e -> e));


        rspList.stream().forEach(rsp -> {
            rsp.setStartUserDeptName(deptNameMap.get(rsp.getStartUserDeptId()));
            rsp.setStartUserName(userNameMap.get(rsp.getStartUserId()));
            rsp.setSenderName(userNameMap.get(rsp.getSenderId()));
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

}
