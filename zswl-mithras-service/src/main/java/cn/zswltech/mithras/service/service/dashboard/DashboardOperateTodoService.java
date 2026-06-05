package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.dao.NodeBackRecordMapper;
import cn.zswltech.flow.core.dao.OperateRecordMapper;
import cn.zswltech.flow.core.domain.entity.NodeBackRecord;
import cn.zswltech.flow.core.domain.entity.OperateRecord;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.flow.core.service.impl.FlowModelService;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveREQ;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveRSP;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
public class DashboardOperateTodoService implements cn.zswltech.mithras.dashboard.application.DashboardOperateTodoApplicationService {
    public static final String YYJB = "经办";
    public static final String YYFH = "复核";
    public static final String YYFZR = "运营部负责人";
    public static final String HJ = "合计";

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowModelService flowModelService;
    @Resource
    private OperateRecordMapper operateRecordMapper;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private NodeBackRecordMapper nodeBackRecordMapper;

//    private static final ThreadPoolExecutor todoPool = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100), new NamedThreadFactory("MithrasDashBoardTodoThread-", false));


    /**
     * 三页待办对应的流程
     */
    private final List<List<String>> modelKeyListOne = Arrays.asList(
            Arrays.asList(ProcessModelTypeEnum.LeaseCreateFlow.name(),ProcessModelTypeEnum.LeaseModifyFlow.name()),
            Arrays.asList(ProcessModelTypeEnum.ContractCreateFlow.name(),ProcessModelTypeEnum.ContractModifyFlow.name()),
            Collections.singletonList(ProcessModelTypeEnum.PaymentCreateFlow.name()));

    private final List<List<String>> modelKeyListTwo = Arrays.asList(
            Collections.singletonList(ProcessModelTypeEnum.ClientModifyFlow.name()),
            Collections.singletonList(ProcessModelTypeEnum.ClientTransferFlow.name()));

    private final List<List<String>> modelKeyListThree = Arrays.asList(
            Arrays.asList(ProcessModelTypeEnum.ContractEarlySettleFlow.name(),ProcessModelTypeEnum.ContractNormalSettleFlow.name()),
            Collections.singletonList(ProcessModelTypeEnum.ContractEarlyRepayFlow.name()));

    public static final List<String> modelNameListOne = Arrays.asList("租赁物审核流程","合同审批流程","付款申请流程");
    private final List<String> modelNameListTwo = Arrays.asList("客户创建流程","客户权限申请","客户移交流程");
    private final List<String> modelNameListThree = Arrays.asList("合同结清流程","提前还款流程","保证金抵扣/退还申请流程");


    private final List<String> modelKeyListAll = Stream.of(modelKeyListOne/*,modelKeyListTwo,modelKeyListThree*/).flatMap(List::stream).flatMap(List::stream).collect(Collectors.toList());

    private final List<String> operateNodeList = Arrays.asList(JobEnum.yunYingGuanLiReview.name(),JobEnum.yunYingGuanLi.name(),JobEnum.operationManagementReview.name(),JobEnum.operationManagement.name(),JobEnum.headofyyglb.name());

    public List<DashboardOperateTodoArriveRSP> todoStatisticsArrive(DashboardOperateTodoArriveREQ req) {
        Map<String, Map<String, List<UserTaskExt>>> modelJobNodeExt = getModelJobNodeExt(modelKeyListAll, operateNodeList);
        // 获取各节点的流程
        ProcessPageReq fhReq = buildProcessQuery(req.getType(),modelJobNodeExt,JobEnum.yunYingGuanLiReview.name());
        Map<String,List<ProcessResp>> fhMap = taskApiService.queryProcess(fhReq).getContents().stream().collect(Collectors.groupingBy(ProcessResp::getModelKey));
        ProcessPageReq jbReq = buildProcessQuery(req.getType(),modelJobNodeExt,JobEnum.yunYingGuanLi.name());
        Map<String,List<ProcessResp>> jbMap = taskApiService.queryProcess(jbReq).getContents().stream().collect(Collectors.groupingBy(ProcessResp::getModelKey));
        ProcessPageReq fhJlReq = buildProcessQuery(req.getType(),modelJobNodeExt,JobEnum.operationManagementReview.name());
        Map<String,List<ProcessResp>> fhJlMap = taskApiService.queryProcess(fhJlReq).getContents().stream().collect(Collectors.groupingBy(ProcessResp::getModelKey));
        ProcessPageReq jbJlReq = buildProcessQuery(req.getType(),modelJobNodeExt,JobEnum.operationManagement.name());
        Map<String,List<ProcessResp>> JbJlMap = taskApiService.queryProcess(jbJlReq).getContents().stream().collect(Collectors.groupingBy(ProcessResp::getModelKey));
        ProcessPageReq fzrReq = buildProcessQuery(req.getType(),modelJobNodeExt,JobEnum.headofyyglb.name());
        Map<String,List<ProcessResp>> fzrMap = taskApiService.queryProcess(fzrReq).getContents().stream().collect(Collectors.groupingBy(ProcessResp::getModelKey));

        List<DashboardOperateTodoArriveRSP> oneList = generateRsp(modelKeyListOne, modelNameListOne, modelJobNodeExt, fhMap, jbMap, fhJlMap, JbJlMap, fzrMap);
//        List<DashboardOperateTodoArriveRSP> twoList = generateRsp(modelKeyListTwo, modelNameListTwo, modelJobNodeExt, fhMap, jbMap, fhJlMap, JbJlMap, fzrMap);
//        List<DashboardOperateTodoArriveRSP> threeList = generateRsp(modelKeyListThree, modelNameListThree, modelJobNodeExt, fhMap, jbMap, fhJlMap, JbJlMap, fzrMap);
        return Stream.of(oneList).flatMap(List::stream).collect(Collectors.toList());
    }


    public List<DashboardOperateTodoArriveRSP> todoStatisticsWillArrive(DashboardOperateTodoArriveREQ req) {
        Map<String, Map<String, List<UserTaskExt>>> modelJobNodeExt = getModelJobNodeExt(modelKeyListAll, operateNodeList);
        // 获取对应模型的所有流程
        ProcessPageReq fhReq = buildProcessQuery(req.getType(),modelJobNodeExt,null);
        Map<String,List<ProcessResp>> allModelMap = taskApiService.queryProcess(fhReq).getContents().stream().collect(Collectors.groupingBy(ProcessResp::getModelKey));

        // 深拷贝多份，分别处理
        Map<String, List<ProcessResp>> fhMap = allModelMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));
        Map<String, List<ProcessResp>> jbMap = allModelMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));
        Map<String, List<ProcessResp>> fhJlMap = allModelMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));
        Map<String, List<ProcessResp>> JbJlMap = allModelMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));
        Map<String, List<ProcessResp>> fzrMap = allModelMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));

        Map<String, List<List<String>>> modelNodeList = flowProcessApiService.queryModelNodeSequence(new ArrayList<>(modelJobNodeExt.keySet()));

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            filterWillArrive(fhMap, modelJobNodeExt, modelNodeList, JobEnum.yunYingGuanLiReview.name());
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            filterWillArrive(jbMap, modelJobNodeExt, modelNodeList, JobEnum.yunYingGuanLi.name());
        });
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            filterWillArrive(fhJlMap, modelJobNodeExt, modelNodeList, JobEnum.operationManagementReview.name());
        });
        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {
            filterWillArrive(JbJlMap, modelJobNodeExt, modelNodeList, JobEnum.operationManagement.name());
        });
        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> {
            filterWillArrive(fzrMap, modelJobNodeExt, modelNodeList, JobEnum.headofyyglb.name());
        });
//        filterWillArrive(fhMap, modelJobNodeExt, modelNodeList, JobEnum.yunYingGuanLiReview.name());
//        filterWillArrive(jbMap, modelJobNodeExt, modelNodeList, JobEnum.yunYingGuanLi.name());
//        filterWillArrive(fhJlMap, modelJobNodeExt, modelNodeList, JobEnum.operationManagementReview.name());
//        filterWillArrive(JbJlMap, modelJobNodeExt, modelNodeList, JobEnum.operationManagement.name());
//        filterWillArrive(fzrMap, modelJobNodeExt, modelNodeList, JobEnum.headofyyglb.name());
        try {
            future1.get();future2.get();future3.get();future4.get();future5.get();
        } catch (Exception e) {
            throw new MithrasException("运营视角-待办统计-将到达发生未知异常");
        }
        List<DashboardOperateTodoArriveRSP> oneList = generateRsp(modelKeyListOne, modelNameListOne, modelJobNodeExt, fhMap, jbMap, fhJlMap, JbJlMap, fzrMap);
//        List<DashboardOperateTodoArriveRSP> twoList = generateRsp(modelKeyListTwo, modelNameListTwo, modelJobNodeExt, fhMap, jbMap, fhJlMap, JbJlMap, fzrMap);
//        List<DashboardOperateTodoArriveRSP> threeList = generateRsp(modelKeyListThree, modelNameListThree, modelJobNodeExt, fhMap, jbMap, fhJlMap, JbJlMap, fzrMap);
        return Stream.of(oneList).flatMap(List::stream).collect(Collectors.toList());
    }


    /**
     * 根据operateRecord表剔除已经审批通过的节点
     * @param fhMap
     * @param modelJobNodeExt
     * @param modelNodeSequenceMap
     * @param jobCode
     */
    private void filterWillArrive(Map<String,List<ProcessResp>> fhMap,Map<String, Map<String, List<UserTaskExt>>> modelJobNodeExt,Map<String,List<List<String>>> modelNodeSequenceMap,String jobCode) {
        List<String> processInstanceIdList = fhMap.values().stream().flatMap(List::stream).map(ProcessResp::getProcessInstanceId).collect(Collectors.toList());
        Example example = new Example(OperateRecord.class);
        example.createCriteria().andIn("processInstanceId", processInstanceIdList);
        List<OperateRecord> operateRecordList = operateRecordMapper.selectByCondition(example);
        // 根据processDefinitionId的前缀取出modelKey, 用modelKey和processInstanceId分组 ,并根据时间排序
        Map<String, Map<String, List<OperateRecord>>> operateRecordMap = operateRecordList.stream()
                .collect(Collectors.groupingBy(record -> record.getProcessDefinitionId().split(":")[0],
                        Collectors.groupingBy(OperateRecord::getProcessInstanceId,
                                Collectors.mapping(Function.identity(), Collectors.collectingAndThen(Collectors.toList(),
                                        list -> {
                                            list.sort(Comparator.comparing(OperateRecord::getGmtCreate));
                                            return list;
                                        })))));
        Example nodeExample = new Example(NodeBackRecord.class);
        nodeExample.createCriteria().andIn("processInstanceId",processInstanceIdList);
        nodeExample.orderBy("gmtCreate").desc();
        List<NodeBackRecord> nodeBackRecords = nodeBackRecordMapper.selectByCondition(nodeExample);
        Map<String, Integer> nodeBackRecordsMap = nodeBackRecords.stream().collect(Collectors.toMap(NodeBackRecord::getProcessInstanceId, NodeBackRecord::getJumpToSourceFlag, (m1, m2) -> m1));
        fhMap.forEach((modelKey, list) -> {
            Map<String, List<OperateRecord>> operateProcessMap = operateRecordMap.get(modelKey);
            if (CollectionUtils.isNotEmpty(operateProcessMap)) {
                List<UserTaskExt> userTaskExtList = modelJobNodeExt.getOrDefault(modelKey, new HashMap<>()).getOrDefault(jobCode, new ArrayList<>());
                if(CollectionUtils.isEmpty(userTaskExtList)){
                    fhMap.put(modelKey, new ArrayList<>());
                    return;
                }
                // 对流程进行遍历
                for (ProcessResp processResp : new ArrayList<>(list)) {
                    for (UserTaskExt userTaskExt : userTaskExtList) {
                        List<OperateRecord> operateRecords = operateProcessMap.get(processResp.getProcessInstanceId());
                        String processActivityId = null;
                        String excludeActivityId = null;
                        // 最近一次操作是否为退回(直达本节点)
                        if (CommentTypeEnum.BHFQR_ZJDW.name().equals(operateRecords.get(operateRecords.size() - 1).getType()) ||
                                (CommentTypeEnum.BH.name().equals(operateRecords.get(operateRecords.size() - 1).getType()) && Objects.equals(nodeBackRecordsMap.get(processResp.getProcessInstanceId()),1))) {
                            processActivityId = operateRecords.get(operateRecords.size() - 1).getTaskActivityId();
                            // 是否被退回到了运营节点
                            if(processResp.getCurTaskActivityIds().equals(userTaskExt.getActivityId())){
                                excludeActivityId = processResp.getCurTaskActivityIds();
                            }
                        } else {
                            processActivityId = processResp.getCurTaskActivityIds();
                        }
                        List<List<String>> modelNodeSequenceList = modelNodeSequenceMap.get(modelKey);
                        List<String> alreadyAgreeNode = getAlreadyAgreeNode(modelNodeSequenceList, processActivityId, excludeActivityId);
                        // 若审批记录中存在该节点的审批通过的记录，则剔除
                        if (alreadyAgreeNode.contains(userTaskExt.getActivityId()) || userTaskExt.getActivityId().equals(processResp.getCurTaskActivityIds())) {
                            list.remove(processResp);
                        }
                    }
                }
            }
        });

    }

    /**
     * 根据流程节点的顺序找到当前节点之前的所有节点 , 需求改动：找到当前节点之前(包括当前节点)的所有节点
     * @param modelNodeSequenceList 流程节点的顺序
     * @param processActivityId 当前节点
     * @param excludeActivityId 需要排除的节点
     * @return
     */
    private List<String> getAlreadyAgreeNode(List<List<String>> modelNodeSequenceList, String processActivityId, String excludeActivityId) {
        return modelNodeSequenceList.stream().map(modelNodeSequence -> {
            int index = modelNodeSequence.indexOf(processActivityId);
            return modelNodeSequence.subList(0, index == -1 ? 0 : index);
        }).flatMap(Collection::stream).distinct().filter(f -> !f.equals(excludeActivityId)).collect(Collectors.toList());
    }

    /**
     * 封装返回体
     * @return
     */
    private List<DashboardOperateTodoArriveRSP> generateRsp(List<List<String>> modelKeyListOne, List<String> modelNameListOne, Map<String, Map<String, List<UserTaskExt>>> modelJobNodeExt,
                                                            Map<String, List<ProcessResp>> fhMap, Map<String, List<ProcessResp>> jbMap ,Map<String, List<ProcessResp>> fhJlMap , Map<String, List<ProcessResp>> jbJlMap, Map<String, List<ProcessResp>> fzrMap) {
        List<DashboardOperateTodoArriveRSP> rspList = new ArrayList<>();
        for (int i = 0; i < modelKeyListOne.size(); i++) {
            DashboardOperateTodoArriveRSP rsp = new DashboardOperateTodoArriveRSP();
            List<String> modelKeyList = modelKeyListOne.get(i);
            List<String> fhList = modelKeyList.stream().map(fhMap::get).filter(Objects::nonNull).flatMap(List::stream).map(ProcessResp::getProcessInstanceId).collect(Collectors.toList());
            List<String> jbList = modelKeyList.stream().map(jbMap::get).filter(Objects::nonNull).flatMap(List::stream).map(ProcessResp::getProcessInstanceId).collect(Collectors.toList());
            List<String> fhJlList = modelKeyList.stream().map(fhJlMap::get).filter(Objects::nonNull).flatMap(List::stream).map(ProcessResp::getProcessInstanceId).collect(Collectors.toList());
            List<String> jbJlList = modelKeyList.stream().map(jbJlMap::get).filter(Objects::nonNull).flatMap(List::stream).map(ProcessResp::getProcessInstanceId).collect(Collectors.toList());
            List<String> fzrList = modelKeyList.stream().map(fzrMap::get).filter(Objects::nonNull).flatMap(List::stream).map(ProcessResp::getProcessInstanceId).collect(Collectors.toList());

            // 管理和运营归到一起
            fhList = Stream.of(fhList, fhJlList).flatMap(List::stream).distinct().collect(Collectors.toList());
            jbList = Stream.of(jbList, jbJlList).flatMap(List::stream).distinct().collect(Collectors.toList());
            List<String> processIdSumList = Stream.of(fhList, jbList, fzrList).flatMap(List::stream).distinct().collect(Collectors.toList());

            // 将流程不存在的节点置空
            long fhExist = modelKeyList.stream().map(modelJobNodeExt::get).map(m -> m.get(JobEnum.yunYingGuanLiReview.name())).filter(Objects::nonNull).count();
            long jbExist = modelKeyList.stream().map(modelJobNodeExt::get).map(m -> m.get(JobEnum.yunYingGuanLi.name())).filter(Objects::nonNull).count();
            long fhJlExist = modelKeyList.stream().map(modelJobNodeExt::get).map(m -> m.get(JobEnum.operationManagementReview.name())).filter(Objects::nonNull).count();
            long jbJlExist = modelKeyList.stream().map(modelJobNodeExt::get).map(m -> m.get(JobEnum.operationManagement.name())).filter(Objects::nonNull).count();
            long fzrExist = modelKeyList.stream().map(modelJobNodeExt::get).map(m -> m.get(JobEnum.headofyyglb.name())).filter(Objects::nonNull).count();

            List<DashboardOperateTodoArriveRSP.content> contentList = Arrays.asList(
                    getContent((jbExist == 0 && jbJlExist == 0) ? null : jbList.size(), YYJB, jbList),
                    getContent((fhExist == 0 && fhJlExist == 0) ? null : fhList.size(), YYFH, fhList),
                    getContent(fzrExist == 0 ? null : fzrList.size(), YYFZR, fzrList),
                    getContent(processIdSumList.size(),  HJ, processIdSumList));
            rsp.setModelKeyList(modelKeyList);
            rsp.setContent(contentList);
            rsp.setModelName(modelNameListOne.get(i));

//            rsp.setProcessInstanceIdList(new ArrayList<>(Stream.of(
//                    jbCount != null ? jbList : (jbJlCount != null ? jbJlList : new ArrayList<String>()),
//                    fhCount != null ? fhList : (fhJlCount != null ? fhJlList : new ArrayList<String>()),
//                    fzrList
//            ).flatMap(List::stream).collect(Collectors.toSet())));
            rspList.add(rsp);
        }
        return rspList;
    }


    /**
     * 根据JobCode获取流程模型中的activityId
     * @param modelKeyList
     * @param modelJobNodeExt
     * @param jobCodeList
     * @return
     */
    private List<String> getActivityIdByJob(List<List<String>> modelKeyList, Map<String, Map<String, UserTaskExt>> modelJobNodeExt ,List<String> jobCodeList){
        return modelKeyList.stream().flatMap(List::stream).map(modelKey -> {
            Map<String, UserTaskExt> userTaskExtMap = modelJobNodeExt.get(modelKey);
            if (CollectionUtils.isNotEmpty(userTaskExtMap)) {
                List<UserTaskExt> collect = jobCodeList.stream().map(userTaskExtMap::get).filter(Objects::nonNull).collect(Collectors.toList());
                return CollectionUtils.isNotEmpty(collect) ? collect.get(0) : null;
            }
            return null;
        }).filter(Objects::nonNull).map(UserTaskExt::getActivityId).distinct().collect(Collectors.toList());
    }

    private DashboardOperateTodoArriveRSP.content getContent(Integer count,String deptDisplay, List<String> processIdList){
        DashboardOperateTodoArriveRSP.content content = new DashboardOperateTodoArriveRSP.content();
        content.setCount(count);
        content.setProcessInstanceIdList(processIdList);
        content.setActivityDisplay(deptDisplay);
        return content;
    }


    private ProcessPageReq buildProcessQuery(String type, Map<String, Map<String, List<UserTaskExt>>> modelJobNodeExt, String JobCode) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(Integer.MAX_VALUE);
        processPageReq.setModelKeyList(new ArrayList<>(modelJobNodeExt.keySet()));
//        processPageReq.setProcessCreateTimeFrom(DateUtil.parse("2024-01-01", DatePattern.NORM_DATE_PATTERN));
        switch (type){
            case DashboardOperateTodoArriveREQ.ARRIVE:
                // 已到达
//                Collection<Map<String, List<UserTaskExt>>> values = modelJobNodeExt.values();
//                List<UserTaskExt> collect = values.stream().map(m -> m.get(JobCode)).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
//                String collect1 = collect.stream().map(UserTaskExt::getActivityId).map(m -> String.format("%s"+m+"%s","'","'")).collect(Collectors.joining(","));
//                processPageReq.setQueryExtraCondition(String.format("AND t4.TASK_DEF_KEY_ in (%s)",collect1));
                // 流程节点需要配合流程一起使用，此处修改sql
                List<String> queryExtraCondition = new LinkedList<>();
                for (Map.Entry<String, Map<String, List<UserTaskExt>>> entry : modelJobNodeExt.entrySet()) {
                    List<UserTaskExt> collect = Stream.of(entry.getValue()).map(m -> m.get(JobCode)).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
                    String collect1 = collect.stream().map(UserTaskExt::getActivityId).map(m -> String.format("%s"+m+"%s","'","'")).collect(Collectors.joining(","));
                    if (StrUtil.isNotBlank(collect1)) {
                        queryExtraCondition.add(String.format("(t4.TASK_DEF_KEY_ in (%s) AND t2.KEY_ = '%s')", collect1, entry.getKey()));
                    }
                }
                if (CollectionUtil.isNotEmpty(queryExtraCondition)) {
                    processPageReq.setQueryExtraCondition(String.format("AND (%s)", CharSequenceUtil.join(" OR ", queryExtraCondition)));
                } else {
                    // 理论上不会走这个分支，如果真的没有查询条件，给个不可能的查询条件吧
                    processPageReq.setModelKeyList(Collections.singletonList("impossible"));
                }
                break;
            case DashboardOperateTodoArriveREQ.WILL_ARRIVE:
                // 将到达：先筛选出审批中的流程，后续再进行过滤
                processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
                break;
        }
        return processPageReq;
    }

    /**
     * 获取模型中的指定岗位节点的拓展信息
     * @param modelKeyList 指定模型
     * @param JobCodeList 指定节点
     * @return key = modelKey , value = {key:jobCode,value:UserTaskExt}
     */
    private Map<String,Map<String, List<UserTaskExt>>> getModelJobNodeExt(List<String> modelKeyList,List<String> JobCodeList){
        List<Model> modelList = repositoryService.createModelQuery().latestVersion().list();
        Map<String, String> modelMap = modelList.stream()
                .filter(flow -> modelKeyList.contains(flow.getKey()))
                .collect(Collectors.toMap(Model::getKey, Model::getId));

        Map<String, GlobalExt> modelExtByModelIds = flowModelService.findModelExtByModelIds(new HashSet<>(modelMap.values()));
        Map<String,Map<String, List<UserTaskExt>>> flowMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(modelExtByModelIds)){
            modelMap.forEach((modelKey,modelId) ->{
                GlobalExt globalExt = modelExtByModelIds.get(modelId);
                List<UserTaskExt> values = globalExt.getUserTaskExtMap().values().stream()
                        .filter(f -> JobCodeList.contains(f.getJobCode())).collect(Collectors.toList());
                Map<String, List<UserTaskExt>> userTaskExtMap = values.stream().collect(Collectors.groupingBy(UserTaskExt::getJobCode));
                flowMap.put(modelKey,userTaskExtMap);
            });
        }
        return flowMap;
    }

}
