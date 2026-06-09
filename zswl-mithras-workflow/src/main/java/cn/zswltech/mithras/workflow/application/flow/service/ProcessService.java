package cn.zswltech.mithras.workflow.application.flow.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowExecutionApiService;
import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.NodeBackRecordReq;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.*;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.flow.core.enums.NodeDefineEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.OrgJobVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.workflow.application.flow.convert.FlowProcessConvert;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程
 *
 * @author wangchuanhao
 * @date 2022/6/23 12:06 AM
 */
@Service
public class ProcessService {

    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;
    @Resource
    private BpmnXMLConverter bpmnXMLConverter;
    @Resource
    private FlowModelApiService modelApiService;
    @Resource
    private FlowExecutionApiService executionApiService;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;

    public List<SelectRSP> listTransferUser(TransferUserListREQ req) {
        ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.getByName(req.getProcessModelType());
        if (Objects.isNull(processModelTypeEnum)) {
            throw new MithrasException("未定义的流程类型");
        }
        List<UserDO> userList = null;
        switch (processModelTypeEnum) {
            case ProjEstablishCreateFlow:
            case ProjEstablishModifyFlow: {
                if (Objects.equals(req.getActivityId(), "userTask_riskManager")) {
                    // 仅查询风控经理
                    List<Long> userIds = userOrgJobDOMapper.selectUserIdByJobCode(JobEnum.riskmanager.name());
                    if (CollectionUtil.isNotEmpty(userIds)) {
                        Example example = new Example(UserDO.class);
                        Example.Criteria criteria = example.createCriteria();
                        criteria.andIn("id", userIds);
                        if (StrUtil.isNotBlank(req.getUserName())) {
                            criteria.andLike("userName", "%" + req.getUserName().trim() + "%");
                        }
                        userList = userDOMapper.selectByExample(example);
                    }
                }
                break;
            }
            default: {
                // 默认给所有用户
                userList = userDOMapper.selectAll();
            }
        }
        if (CollectionUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }
        // 去掉当前登陆用户
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null);
        if (Objects.nonNull(currentUserId)) {
            userList.removeIf(e -> Objects.equals(currentUserId, e.getId()));
        }
        return userList.stream().map(e -> {
            SelectRSP rsp = new SelectRSP();
            rsp.setLabel(e.getUserName());
            rsp.setValue(e.getId().toString());
            return rsp;
        }).collect(Collectors.toList());
    }

    public boolean isInProcess(String businessKey, List<String> processModelKeyList) {
        return Objects.nonNull(this.findRelatedProcess(businessKey, processModelKeyList));
    }

    public ProcessResp findRelatedProcess(String businessKey, List<String> processModelKeyList) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(businessKey);
        processPageReq.setModelKeyList(processModelKeyList);
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public PageR<ProcessHistoryRSP> history(ProcessHistoryREQ req) {
        ProcessHistoryReq flowReq = flowProcessConvert.req2HistoryReq(req);
        Page<ProcessHistoryResp> historyRespPage = processApiService.history(flowReq);
        List<ProcessHistoryRSP> rspList = historyRespPage.getContents().stream()
                .map(flowProcessConvert::flowHistoryResp2RSP).collect(Collectors.toList());
        flowProcessConvert.historyRSPFillName(rspList);
        //只有一个节点
        String targetCommentType = CommentTypeEnum.JS.name();
        if (CollUtil.isNotEmpty(rspList)
                && rspList.size() == 2
                && rspList.stream().anyMatch(processHistory -> targetCommentType.equals(processHistory.getType()))
                && Objects.equals(rspList.get(0).getType(), targetCommentType)) {
            ProcessHistoryRSP tempElement = rspList.get(0);
            rspList.set(0, rspList.get(1));
            rspList.set(1, tempElement);
        }
        // 隐藏投票动作 只有评委会秘书可看 暂时先写死节点id
        Long curLongUserId = AccountUtil.getLoginInfo().getId();
        UserVO userVO = userServiceAPI.getUserInfoById(curLongUserId).getData();
        // 是否评审会秘书 评审会秘书才能看投票记录
        //todo 岗位
//        boolean jurySecretaryFlag = userVO.getJobsName().stream().map(DictionaryDO::getCode).filter(JobEnum.secretaryjury.name()::equals).findFirst().orElse(null) != null;
        boolean jurySecretaryFlag = userVO.getJobsName().stream()
                .flatMap(orgJobVO -> orgJobVO.getJobNames().stream().map(OrgJobVO.Job::getJobCode))
                .filter(JobEnum.secretaryjury.name()::equals)
                .findFirst().orElse(null) != null;
        boolean pricingSecretaryFlag = userVO.getJobsName().stream()
                .flatMap(orgJobVO -> orgJobVO.getJobNames().stream().map(OrgJobVO.Job::getJobCode))
                .filter(JobEnum.pricingcommitteesecretary.name()::equals).findFirst().orElse(null) != null;
        if (!jurySecretaryFlag) {
            rspList.forEach(r -> {
                if (curLongUserId.equals(r.getOperatorId())) {
                    return;
                }
                if ("userTask_juryVote".equals(r.getTaskActivityId())) {
                    r.setType("评委投票");
                    r.setTypeName("*******");
                    r.setMessage("*******");
                }
            });
        }
        if (!pricingSecretaryFlag) {
            rspList.forEach(r -> {
                if(curLongUserId.equals(r.getOperatorId())){
                    return;
                }
                if ("userTask_pricing_committee".equals(r.getTaskActivityId())) {
                    if ("VOTE_DISAGREE".equals(r.getType()) || "VOTE_AGREE".equals(r.getType())) {
                        r.setType("委员投票");
                        r.setTypeName("*******");
                        r.setMessage("*******");
                    }
                }
            });
        }
        /*boolean projManagerFlag = userVO.getJobsName().stream()
                .flatMap(orgJobVO -> orgJobVO.getJobNames().stream().map(OrgJobVO.Job::getJobCode))
                .filter(JobEnum.projmanager.name()::equals).findFirst().orElse(null) != null;
        ProcessResp processResp = flowTaskApiService.queryProcessById(req.getProcessInstanceId());
        if (processResp == null) {
            throw new MithrasException("流程不存在");
        }
        boolean opinionFlowFlag = ProcessModelTypeEnum.RiskControlOpinionHandleAfterLaunchFlow.name().equals(processResp.getModelKey()) ||
                ProcessModelTypeEnum.RiskControlOpinionHandleFlow.name().equals(processResp.getModelKey());
        if (opinionFlowFlag && projManagerFlag) {
            rspList.forEach(r -> {
                if (curLongUserId.equals(r.getOperatorId())) {
                    return;
                }
                if ("userTask_projmanagers".equals(r.getTaskActivityId())) {
                    r.setMessage("*******");
                }
            });
        }*/
        return PageR.of(rspList, historyRespPage.getTotal(), historyRespPage.getPages(), historyRespPage.getCurPage(), historyRespPage.getPageSize());
    }

    public byte[] getProcessBpmnXml(String processInstanceId) {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (Objects.isNull(processInstance)) {
            throw new MithrasException("流程不存在");
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        return bpmnXMLConverter.convertToXML(bpmnModel);
    }

    public ProcessPictureRSP getProcessPictureData(String processInstanceId) {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (Objects.isNull(processInstance)) {
            throw new MithrasException("流程不存在");
        }

        // 节点定义列表
        Map<String, NodeDefineResp> nodeDefineMap = modelApiService.getNodeDefineListByProcessDefinitionId(processInstance.getProcessDefinitionId())
                .stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, n -> n));
        // 计算两个节点之间
        Map<String, FlowElement> flowElementMap = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId()).getMainProcess().getFlowElementMap();

        ProcessPictureRSP processPictureRSP = ProcessPictureRSP
                .builder()
                .highLine(new ArrayList<>())
                .highPoint(new ArrayList<>())
                .iDo(new ArrayList<>())
                .waitingToDo(new ArrayList<>())
                .backNodeList(new ArrayList<>())
                .build();

        List<HistoricActivityInstance> hisActInsList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .desc()
                .list();

        Map<String, List<HistoricActivityInstance>> hisActInsMap = hisActInsList.stream().collect(Collectors.groupingBy(HistoricActivityInstance::getActivityId));

        NodeBackRecordReq nodeBackRecordReq = new NodeBackRecordReq();
        nodeBackRecordReq.setPageSize(Integer.MAX_VALUE);
        nodeBackRecordReq.setType(2);
        nodeBackRecordReq.setProcessInstanceIdList(Arrays.asList(processInstanceId));
        // 计算回退的逻辑
        List<NodeBackRecordResp> nodeBackRecordRespList = flowTaskApiService.queryNodeBackRecord(nodeBackRecordReq).getContents();

        // 加上回退节点 对回退后已经重新走过的，进行剔除
        processPictureRSP.getBackNodeList().addAll(nodeBackRecordRespList.stream()
                .filter(n -> CollectionUtils.isEmpty(hisActInsMap.get(n.getSourceTaskActivityId())) || hisActInsMap.get(n.getSourceTaskActivityId()).stream().filter(h -> h.getStartTime().after(n.getBackTime())).findFirst().orElse(null) == null)
                .map(NodeBackRecordResp::getSourceTaskActivityId).collect(Collectors.toList()));

        // 如果有个要逐级审批的回退没处理过，就会被判为被退回至的节点 退回之前最后一次经过的时间 到回退时间中间的路径走的都不算数 需要过滤掉这部分actinst
        nodeBackRecordRespList.forEach(n -> {
            if (Objects.equals(1, n.getJumpToSourceFlag())) {
                return;
            }
            List<HistoricActivityInstance> sinkActInst = hisActInsMap.getOrDefault(n.getSinkTaskActivityId(), new ArrayList<>());
            Date sinkActInstStartTime = sinkActInst.stream().filter(s -> s.getStartTime().before(n.getBackTime())).sorted(Comparator.comparing(HistoricActivityInstance::getStartTime).reversed()).findFirst().map(HistoricActivityInstance::getStartTime).orElse(processInstance.getStartTime());
            // 只过滤A走到B可能走到的节点
            Set<String> actIdSet = new HashSet<>();
            calNodeA2BPath(nodeDefineMap, flowElementMap, actIdSet, new HashSet<>(), n.getSinkTaskActivityId(), n.getSourceTaskActivityId());
            hisActInsList.removeIf(h -> actIdSet.contains(h.getActivityId()) && h.getStartTime().after(sinkActInstStartTime) && h.getStartTime().before(n.getBackTime()));
        });

        hisActInsList.forEach(historicActivityInstance -> {
            if("sequenceFlow".equals(historicActivityInstance.getActivityType())) {
                // 添加高亮连线
                processPictureRSP.getHighLine().add(historicActivityInstance.getActivityId());
            } else {
                // 添加高亮节点
                processPictureRSP.getHighPoint().add(historicActivityInstance.getActivityId());
                if ("userTask".equals(historicActivityInstance.getActivityType())) {
                    processPictureRSP.getIDo().add(historicActivityInstance.getActivityId());
                }
            }
        });

        // 去重
        processPictureRSP.setHighLine(processPictureRSP.getHighLine().stream().distinct().collect(Collectors.toList()));
        processPictureRSP.setHighPoint(processPictureRSP.getHighPoint().stream().distinct().collect(Collectors.toList()));
        processPictureRSP.setIDo(processPictureRSP.getIDo().stream().distinct().collect(Collectors.toList()));
        processPictureRSP.setWaitingToDo(processPictureRSP.getWaitingToDo().stream().distinct().collect(Collectors.toList()));
        processPictureRSP.setBackNodeList(processPictureRSP.getBackNodeList().stream().distinct().collect(Collectors.toList()));

        if (Objects.isNull(processInstance.getEndTime())) {
            // 流程未结束 最新的执行实例为待办
            processPictureRSP.getIDo().remove(hisActInsList.get(0).getActivityId());
            processPictureRSP.getWaitingToDo().add(hisActInsList.get(0).getActivityId());
        }

        return processPictureRSP;
    }

    /**
     * 计算节点A走到节点B可能经过的节点
     */
    public static void calNodeA2BPath(Map<String, NodeDefineResp> nodeRespMap, Map<String, FlowElement> flowElementMap,
                                      Set<String> allCodeSet, Set<String> curCodeSet, String curNodeId, String targetNodeId) {
        if (allCodeSet.contains(curNodeId) || Objects.equals(curNodeId, targetNodeId)) {
            // 如果当前节点为目标节点 或 该节点最终能走到结束
            allCodeSet.addAll(curCodeSet);
            return;
        }
        NodeDefineResp nodeDefineResp = nodeRespMap.get(curNodeId);
        FlowNode flowNode = (FlowNode) flowElementMap.get(curNodeId);
        if (curCodeSet.contains(curNodeId) || NodeDefineEnum.END_NONE_EVENT.getType().equalsIgnoreCase(nodeDefineResp.getType())) {
            // 防止环出现死循环
            return;
        }
        curCodeSet.add(curNodeId);
        for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
            Set<String> newSet = new HashSet<>(curCodeSet);
            newSet.add(sequenceFlow.getId());
            calNodeA2BPath(nodeRespMap, flowElementMap, allCodeSet, newSet, sequenceFlow.getTargetRef(), targetNodeId);
        }
    }

    public List<ProcessNodeRSP> queryCanJumpNodes(String processInstanceId) {
        List<NodeResp> nodeRespList = executionApiService.queryCanJumpNodes(processInstanceId);
        List<ProcessNodeRSP> resList = nodeRespList.stream().map(n -> ProcessNodeRSP.builder().name(n.getName()).activityId(n.getActivityId()).build()).collect(Collectors.toList());
        return resList;
    }
}
