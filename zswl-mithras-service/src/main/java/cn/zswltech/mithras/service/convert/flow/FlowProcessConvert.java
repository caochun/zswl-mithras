package cn.zswltech.mithras.service.convert.flow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.req.task.CcProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.CcProcessResp;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.flow.model.FlowQueryExtra;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.BizProcessData;
import cn.zswltech.mithras.system.service.BizProcessDataService;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.flow.FlowQueryExtraService;
import cn.zswltech.mithras.service.util.FlowUtil;
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

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * 转换
 *
 * @author wangchuanhao
 * @date 2022/8/2 4:27 PM
 */
@Slf4j
@Component
public class FlowProcessConvert {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BizProcessDataService bizProcessDataService;

    public ProcessPageReq req2FlowReq(ProcessListREQ req) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(req.getPage());
        processPageReq.setPageSize(req.getPageSize());
        processPageReq.setDynamicFilterParam(new HashMap<>());

        processPageReq.setStartUserId(Optional.ofNullable(req.getStartUserId()).map(String::valueOf).orElse(null));
        processPageReq.setStartUserDeptId(Optional.ofNullable(req.getStartUserDeptId()).map(String::valueOf).orElse(null));
        processPageReq.setModelKey(req.getModelKey());
        processPageReq.setModelKeyList(req.getModelKeyList());
        processPageReq.setBusinessKeyList(req.getBusinessKeyList());

        processPageReq.setProcessInstanceIdLike(req.getProcessInstanceId());
        List<String> processInstanceIdList = req.getProcessInstanceIdList();
        if(CollectionUtils.isNotEmpty(processInstanceIdList)) {
            processPageReq.setProcessInstanceIdList(processInstanceIdList);
        }

        if (StringUtils.isNotBlank(req.getProcessStatus())) {
            processPageReq.setProcessStatusList(Arrays.asList(Integer.valueOf(req.getProcessStatus())));
        }

        processPageReq.setProcessInstanceName(req.getProcessName());
        processPageReq.setProcessCreateTimeFrom(Optional.ofNullable(req.getProcessCreateTimeFrom()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));
        processPageReq.setProcessCreateTimeTo(Optional.ofNullable(req.getProcessCreateTimeTo()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));


        if (Objects.nonNull(req.getClientId())) {
            processPageReq.setDynamicFilterSql(bizProcessDataService.processDynamicSqlClient());
            bizProcessDataService.dynamicParamClient(processPageReq.getDynamicFilterParam(), req.getClientId());
        }

        if (Objects.nonNull(req.getExtra())) {
            processPageReq.setQueryExtraCondition(bizProcessDataService.extraQueryCondition(req.getExtra(), "t1"));
        }

        return processPageReq;
    }

    public ProcessListRSP flowResp2RSP(ProcessResp resp) {
        ProcessListRSP processListRSP = new ProcessListRSP();
        copyFlowResp2RSP(resp, processListRSP);
        return processListRSP;
    }

    public void processListRSPFillName(List<ProcessListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(ProcessListRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, rspList.stream().map(ProcessListRSP::getProcessInstanceId).collect(Collectors.toSet())))
                .stream().filter(b -> Objects.nonNull(b.getClientId()))
                .collect(Collectors.toMap(BizProcessData::getProcessInstanceId, BizProcessData::getClientId, (k1, k2) -> k1));
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(clientProcessIdMap.values());


        // 人列表
        Set<Long> userIdSet = rspList.stream()
                .map(rsp -> {
                    Set<Long> set = new HashSet<>();
                    set.add(rsp.getStartUserId());
                    set.add(rsp.getFinalAssigneeId());
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
        List<String> instanceIdList = rspList.stream().map(ProcessListRSP::getProcessInstanceId).collect(Collectors.toList());
        List<FlowQueryExtra> extraList = getBean(FlowQueryExtraService.class).list(Wrappers.<FlowQueryExtra>lambdaQuery().in(FlowQueryExtra::getInstanceId, instanceIdList));
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
            rsp.setFinalAssigneeName(userNameMap.get(rsp.getFinalAssigneeId()));
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

    public CcProcessPageReq req2CcFlowReq(ProcessCcListREQ req) {
        CcProcessPageReq ccProcessPageReq = new CcProcessPageReq();
        ccProcessPageReq.setStartUserId(Optional.ofNullable(req.getStartUserId()).map(String::valueOf).orElse(null));
        ccProcessPageReq.setStartUserDeptId(Optional.ofNullable(req.getStartUserDeptId()).map(String::valueOf).orElse(null));
        ccProcessPageReq.setModelKey(req.getModelKey());
        ccProcessPageReq.setProcessInstanceIdLike(req.getProcessInstanceId());

        if (StringUtils.isNotBlank(req.getProcessStatus())) {
            ccProcessPageReq.setProcessStatusList(Arrays.asList(Integer.valueOf(req.getProcessStatus())));
        }

        ccProcessPageReq.setProcessInstanceName(req.getProcessName());
        ccProcessPageReq.setProcessCreateTimeFrom(Optional.ofNullable(req.getProcessCreateTimeFrom()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));
        ccProcessPageReq.setProcessCreateTimeTo(Optional.ofNullable(req.getProcessCreateTimeTo()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));
        ccProcessPageReq.setCcTimeFrom(Optional.ofNullable(req.getCcTimeFrom()).map(t -> new Date(LocalDateTimeUtil.toEpochMilli(t))).orElse(null));
        ccProcessPageReq.setCcTimeTo(Optional.ofNullable(req.getCcTimeTo()).map(t -> DateUtil.endOfDay(new Date(LocalDateTimeUtil.toEpochMilli(t)))).orElse(null));

        ccProcessPageReq.setSenderId(Optional.ofNullable(req.getSenderId()).map(String::valueOf).orElse(null));
        ccProcessPageReq.setReadFlag(req.getReadFlag());
        ccProcessPageReq.setPageIndex(req.getPage());
        ccProcessPageReq.setPageSize(req.getPageSize());

        if (Objects.nonNull(req.getClientId())) {
            ccProcessPageReq.setDynamicFilterSql(bizProcessDataService.ccProcessDynamicSqlClient());
            bizProcessDataService.dynamicParamClient(ccProcessPageReq.getDynamicFilterParam(), req.getClientId());
        }
        if (Objects.nonNull(req.getExtra())) {
            ccProcessPageReq.setQueryExtraCondition(bizProcessDataService.ccExtraQueryCondition(req.getExtra()));
        }
        return ccProcessPageReq;
    }

    public ProcessCcListRSP flowCcResp2RSP(CcProcessResp ccProcessResp) {
        ProcessCcListRSP processCcListRSP = new ProcessCcListRSP();
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

    public void ccProcessRSPFillName(List<ProcessCcListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        // 部门列表
        Set<Long> deptIdSet = rspList.stream().map(ProcessCcListRSP::getStartUserDeptId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIdSet);
        // 填充客户id
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, rspList.stream().map(ProcessCcListRSP::getProcessInstanceId).collect(Collectors.toSet())))
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
        List<String> instanceIdList = rspList.stream().map(ProcessCcListRSP::getProcessInstanceId).collect(Collectors.toList());
        List<FlowQueryExtra> extraList = getBean(FlowQueryExtraService.class).list(Wrappers.<FlowQueryExtra>lambdaQuery().in(FlowQueryExtra::getInstanceId, instanceIdList));
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

    public ProcessHistoryReq req2HistoryReq(ProcessHistoryREQ req) {
        ProcessHistoryReq processHistoryReq = new ProcessHistoryReq();
        processHistoryReq.setProcessInstanceId(req.getProcessInstanceId());
        processHistoryReq.setPageIndex(req.getPage());
        processHistoryReq.setPageSize(req.getPageSize());
        return processHistoryReq;

    }

    public ProcessHistoryRSP flowHistoryResp2RSP(ProcessHistoryResp processHistoryResp) {
        ProcessHistoryRSP processHistoryRSP = new ProcessHistoryRSP();
        processHistoryRSP.setType(processHistoryResp.getType());
        processHistoryRSP.setTypeName(processHistoryResp.getTypeName());
        processHistoryRSP.setMessage(processHistoryResp.getMessage());
        processHistoryRSP.setOperateTime(LocalDateTimeUtil.of(processHistoryResp.getOperateTime()));
        processHistoryRSP.setTaskNodeName(processHistoryResp.getTaskNodeName());
        processHistoryRSP.setTaskActivityId(processHistoryResp.getTaskActivityId());
        processHistoryRSP.setOperatorId(Optional.ofNullable(processHistoryResp.getOperatorId()).map(Long::valueOf).orElse(null));
        return processHistoryRSP;
    }

    public void historyRSPFillName(List<ProcessHistoryRSP> rspList) {
        // 人列表
        Set<Long> userIdSet = rspList.stream()
                .map(ProcessHistoryRSP::getOperatorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdSet);

        rspList.stream().forEach(rsp -> {
            rsp.setOperatorName(userNameMap.get(rsp.getOperatorId()));
        });
    }

    public void copyFlowResp2RSP(ProcessResp resp, ProcessListRSP processListRSP) {
        processListRSP.setProcessInstanceId(resp.getProcessInstanceId());
        processListRSP.setBusinessKey(resp.getBusinessKey());
        processListRSP.setModelName(FlowUtil.convertModelName(resp.getModelKey()));
        processListRSP.setProcessStatus(Optional.ofNullable(resp.getProcessStatus()).map(String::valueOf).orElse(null));
        processListRSP.setModelKey(resp.getModelKey());
        processListRSP.setStartUserId(Optional.ofNullable(resp.getStartUserId()).map(Long::valueOf).orElse(null));
        processListRSP.setStartTime(LocalDateTimeUtil.of(resp.getStartTime()));
        processListRSP.setEndTime(LocalDateTimeUtil.of(resp.getEndTime()));
        processListRSP.setCurTaskIds(resp.getCurTaskIds());
        processListRSP.setCurTaskActivityIds(resp.getCurTaskActivityIds());
        processListRSP.setCurTaskNames(resp.getCurTaskNames());
        processListRSP.setCurAssigneeIds(resp.getCurAssigneeIds());
        processListRSP.setProcessName(resp.getProcessInstanceName());
        processListRSP.setStartUserDeptId(Optional.ofNullable(resp.getStartUserDeptId()).map(Long::valueOf).orElse(null));
        processListRSP.setFinalAssigneeId(Optional.ofNullable(resp.getLastOperatorId()).map(Long::valueOf).orElse(null));
        processListRSP.setSubModule(resp.getSubModule());
        processListRSP.setMainModule(Optional.ofNullable(ProcessModelTypeEnum.getByName(processListRSP.getModelKey())).map(ProcessModelTypeEnum::getBusinessModuleName).orElse(null));
    }

    public ProcessDetailRSP flowResp2Detail(ProcessResp flowResp) {
        ProcessDetailRSP detailRSP = new ProcessDetailRSP();
        copyFlowResp2RSP(flowResp, detailRSP);
        return detailRSP;
    }
}
