package cn.zswltech.mithras.service.service.dashboard.todo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.*;
import cn.zswltech.flow.core.dao.OperateRecordMapper;
import cn.zswltech.flow.core.domain.req.task.CcProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.*;
import cn.zswltech.flow.core.enums.*;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dto.flow.search.ProcessCcListRSP;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListREQ;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListRSP;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.convert.dashboard.TodoProcessConvert;
import cn.zswltech.mithras.service.convert.dashboard.TodoTaskConvert;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.dashboard.TodoKeyEnum;
import cn.zswltech.mithras.service.enums.dashboard.TodoProcessEnum;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.ToDoOperateRecordMapper;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.OperateRecord;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.leaseholdproperty.impl.LeaseVehicleRegistrationServiceImpl;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.sleipnir.toolkit.OcrUtil;
import cn.zswltech.sleipnir.toolkit.request.VehicleRegistrationRequest;
import cn.zswltech.sleipnir.toolkit.response.VehicleRegistrationResponse;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.EnumUtil.fromStringQuietly;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.hutool.json.JSONUtil.toList;

/**
 * 任务
 *
 * @author zhouning
 * @date 2024/6/22 11:56 PM
 */

@Slf4j
@Service
public class DashboardTodoService {

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private TodoTaskConvert todoTaskConvert;
    @Resource
    private TodoProcessConvert todoProcessConvert;
    @Resource
    private CommonProcessPrepareService prepareService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ToDoOperateRecordMapper toDoOperateRecordMapper;

    public List<DashboardTodoMyTodoProcessTaskRSP> todoPageList(DashboardTodoREQ todoREQ) {
        List<DashboardTodoMyTodoProcessTaskRSP> result = new ArrayList<>();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
        List<Callable<List<DashboardTodoMyTodoProcessTaskRSP>>> tasks = new ArrayList<>();
        todoREQ.setPage(1);
        todoREQ.setPageSize(10000);
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        todoREQ.setAccount(startUserId);
        //我收到的-待审批
        tasks.add(new ApiTodoCallerTaskA(todoREQ));
        //我发起的-审批退回
        tasks.add(new ApiTodoCallerTaskB(todoREQ));
        //我发起的-待发起  待发起数据还没有发起人
        if(StringUtils.isBlank(todoREQ.getStartUserId())){
            tasks.add(new ApiTodoCallerTaskC(todoREQ));
        }
        try {
            List<Future<List<DashboardTodoMyTodoProcessTaskRSP>>> results = fixedThreadPool.invokeAll(tasks);
            for (Future<List<DashboardTodoMyTodoProcessTaskRSP>> res : results) {
                result.addAll(res.get());
            }
            fixedThreadPool.shutdown();
            while (true) {
                if (fixedThreadPool.isTerminated()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("待办的error", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // 工作台待办按收到时间降序排列，最新到达的待办展示为第一条
        if (CollectionUtil.isNotEmpty(result)) {
            result = result.stream()
                    .peek(e -> {
                        if (Objects.isNull(e.getTaskCreateTime())) {
                            e.setTaskCreateTime(e.getApplyTime());
                        }
                    })
                    .sorted(Comparator
                            .comparing(DashboardTodoMyTodoProcessTaskRSP::getTaskCreateTime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed()
                    )
                    .collect(Collectors.toList());
        }
        return result;
    }

    public Long todoPageListCount(DashboardTodoREQ todoREQ) {
        Long result = 0L;
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
        List<Callable<Long>> tasks = new ArrayList<>();
        todoREQ.setPage(1);
        todoREQ.setPageSize(20);
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        todoREQ.setAccount(startUserId);
        //我收到的-待审批
        tasks.add(new ApiTodoCallerCountTaskA(todoREQ));
        //我发起的-审批退回
        tasks.add(new ApiTodoCallerCountTaskB(todoREQ));
        //我发起的-待发起
        tasks.add(new ApiTodoCallerCountTaskC(todoREQ));
        try {
            List<Future<Long>> results = fixedThreadPool.invokeAll(tasks);
            for (Future<Long> res : results) {
                result += res.get();
            }
            fixedThreadPool.shutdown();
            while (true) {
                if (fixedThreadPool.isTerminated()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("待办的error", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    private List<DashboardTodoMyTodoProcessTaskRSP> flowA(DashboardTodoREQ todoREQ) {
        List<DashboardTodoMyTodoProcessTaskRSP> result = new ArrayList<>();
        TaskSystemPageReq flowReqA = new TaskSystemPageReq();
        flowReqA.setIsRunning(1);
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReqA.setProcessInstanceName(todoREQ.getProcessName());
        }
        //查询发起人
        if (StringUtils.isNotBlank(todoREQ.getStartUserId())) {
            flowReqA.setStartUserId(todoREQ.getStartUserId());
        }
        //模糊查询流程id
        if (StringUtils.isNotBlank(todoREQ.getProcessInstanceId())) {
            flowReqA.setProcessInstanceIdLike(todoREQ.getProcessInstanceId());
        }
        flowReqA.setAssignee(startUserId);
        flowReqA.setNotEqualsActivityId(FlowConstants.START_USER_TASK);
        flowReqA.setSortType(1);
        flowReqA.setPageIndex(1);
        flowReqA.setPageSize(5000);
        Page<TaskResp> flowTaskPageA = taskApiService.querySystemTask(flowReqA);
        if (!CollectionUtils.isEmpty(flowTaskPageA.getContents())) {
            // 填充流程数据
            List<String> processInstanceIdList = flowTaskPageA.getContents().stream().map(TaskResp::getProcessInstanceId).distinct().collect(Collectors.toList());
            Map<String, ProcessResp> processRespMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
                ProcessPageReq processFlowReq = new ProcessPageReq();
                processFlowReq.setProcessInstanceIdList(processInstanceIdList);
                processFlowReq.setPageSize(Integer.MAX_VALUE);
                processRespMap.putAll(taskApiService.queryProcess(processFlowReq).getContents().stream().collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p)));
            }
            List<DashboardTodoProcessRSP> rspListA = flowTaskPageA.getContents().stream().map(resp -> todoTaskConvert.flowResp2TodoProcessRSP(resp, processRespMap.get(resp.getProcessInstanceId()))).collect(Collectors.toList());
            todoTaskConvert.receiveTaskListRSPFillName(rspListA);
            for (DashboardTodoProcessRSP dashboardTodoProcessRSP : rspListA) {
                DashboardTodoMyTodoProcessTaskRSP dashboardTodoMyTodoProcessTaskRSP = new DashboardTodoMyTodoProcessTaskRSP();
                BeanUtil.copyProperties(dashboardTodoProcessRSP, dashboardTodoMyTodoProcessTaskRSP);
                dashboardTodoMyTodoProcessTaskRSP.setType(TodoKeyEnum.WAIT_APPROVE.name());
                result.add(dashboardTodoMyTodoProcessTaskRSP);
            }
        }
        return result;
    }

    private Long flowCountA(DashboardTodoREQ todoREQ) {
        TaskSystemPageReq flowReqA = new TaskSystemPageReq();
        flowReqA.setIsRunning(1);
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReqA.setProcessInstanceName(todoREQ.getProcessName());
        }
        flowReqA.setAssignee(startUserId);
        flowReqA.setNotEqualsActivityId(FlowConstants.START_USER_TASK);
        flowReqA.setSortType(1);
        flowReqA.setPageIndex(1);
        flowReqA.setPageSize(20);
        Long countA = taskApiService.querySystemTaskCount(flowReqA);
        return countA;
    }

    private List<DashboardTodoMyTodoProcessTaskRSP> flowB(DashboardTodoREQ todoREQ) {
        List<DashboardTodoMyTodoProcessTaskRSP> result = new ArrayList<>();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        TaskSystemPageReq flowReqB = new TaskSystemPageReq();
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReqB.setProcessInstanceName(todoREQ.getProcessName());
        }
        //查询发起人
        if (StringUtils.isNotBlank(todoREQ.getStartUserId())) {
            startUserId = todoREQ.getStartUserId();
        }
        //模糊查询流程id
        if (StringUtils.isNotBlank(todoREQ.getProcessInstanceId())) {
            flowReqB.setProcessInstanceIdLike(todoREQ.getProcessInstanceId());
        }
        flowReqB.setIsRunning(1);
        flowReqB.setStartUserBackFlag(2);
        flowReqB.setStartUserId(startUserId);
        flowReqB.setSortType(1);
        flowReqB.setPageIndex(1);
        flowReqB.setPageSize(5000);
        Page<TaskResp> flowTaskPageB = taskApiService.querySystemTask(flowReqB);
        if (!CollectionUtils.isEmpty(flowTaskPageB.getContents())) {
            List<DashboardTodoProcessRSP> rspListB = flowTaskPageB.getContents().stream().map(todoTaskConvert::flowResp2BackStepRSP).collect(Collectors.toList());
            todoTaskConvert.baskStepRSPFillName(rspListB);
            for (DashboardTodoProcessRSP dashboardTodoProcessRSP : rspListB) {
                DashboardTodoMyTodoProcessTaskRSP dashboardTodoMyTodoProcessTaskRSP = new DashboardTodoMyTodoProcessTaskRSP();
                BeanUtil.copyProperties(dashboardTodoProcessRSP, dashboardTodoMyTodoProcessTaskRSP);
                dashboardTodoMyTodoProcessTaskRSP.setType(TodoKeyEnum.APPROVE_RETURN.name());
                result.add(dashboardTodoMyTodoProcessTaskRSP);
            }
        }
        return result;
    }


    private Long flowCountB(DashboardTodoREQ todoREQ) {
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        TaskSystemPageReq flowReqB = new TaskSystemPageReq();
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReqB.setProcessInstanceName(todoREQ.getProcessName());
        }
        flowReqB.setIsRunning(1);
        flowReqB.setStartUserBackFlag(2);
        flowReqB.setStartUserId(startUserId);
        flowReqB.setSortType(1);
        flowReqB.setPageIndex(1);
        flowReqB.setPageSize(20);
        Long countB = taskApiService.querySystemTaskCount(flowReqB);
        return countB;
    }

    private List<DashboardTodoMyTodoProcessTaskRSP> flowC(DashboardTodoREQ todoREQ) {
        List<DashboardTodoMyTodoProcessTaskRSP> result = new ArrayList<>();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        List<DashboardTodoProcessRSP> rspListC = new ArrayList<>();
        ProcessPrepareListREQ req = new ProcessPrepareListREQ();
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            req.setFormName(todoREQ.getProcessName());
        }
        //查询待提交id
        if (StringUtils.isNotBlank(todoREQ.getProcessInstanceId())) {
            req.setProcessInstanceId(todoREQ.getProcessInstanceId());
        }
        req.setStartUserId(startUserId);
        req.setPage(1);
        req.setPageSize(5000);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CommonProcessPrepare> pageData = prepareService.list(req);
        List<ProcessPrepareListRSP> rspList = BeanUtil.copyToList(pageData.getRecords(), ProcessPrepareListRSP.class);
        Set<Long> userIdList = new HashSet<>();
        rspList.forEach(e -> {
            if (isNotBlank(e.getCurrentAssignee())) {
                userIdList.addAll(toList(e.getCurrentAssignee(), Long.class));
            }
        });
        Map<Long, String> idNameMap = getBean(UserService.class).getUserInfoByIds(new ArrayList<>(userIdList)).stream()
                .collect(Collectors.toMap(UserVO::getId, UserVO::getUserName, (v1, v2) -> v1));
        List<Long> paymentIds = rspList.stream().filter(e -> ObjectUtil.equals(e.getProcessTypeName(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name())).map(ProcessPrepareListRSP::getBusinessId).map(Long::valueOf).collect(Collectors.toList());
        Map<Long, Long> paymentBaseInfoMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(paymentIds)) {
            paymentBaseInfoMap.putAll(paymentBaseInfoService.listByIds(paymentIds).stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getContractId, (a, b) -> a)));
        }
        for (ProcessPrepareListRSP rsp : rspList) {
            String currentAssignee = rsp.getCurrentAssignee();
            if (isNotBlank(currentAssignee)) {
                List<Long> idList = toList(currentAssignee, Long.class);
                String currentAssigneeNames = idList.stream()
                        .map(idNameMap::get).collect(Collectors.joining("，"));
                rsp.setCurrentAssigneeNames(currentAssigneeNames);
            }
            //
            ProcessModelTypeEnum modelTypeEnum = fromStringQuietly(ProcessModelTypeEnum.class, rsp.getProcessType());
            if (null != modelTypeEnum) {
                rsp.setProcessTypeName(modelTypeEnum.getDisplay());
            }
            //这里业务id不一定是前端需要的，在这里做转换
            if (ObjectUtil.equals(rsp.getProcessType(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name())) {
                rsp.setBusinessId(String.valueOf(paymentBaseInfoMap.get(Long.valueOf(rsp.getBusinessId()))));
            } else {
                if (ObjectUtil.isEmpty(rsp.getBusinessId())) {
                    rsp.setBusinessId(String.valueOf(rsp.getId()));
                }
            }
            DashboardTodoProcessRSP dashboardTodoProcessRSP = new DashboardTodoProcessRSP();
            todoTaskConvert.processPrepare2TodoProcessRSP(rsp, dashboardTodoProcessRSP);
            rspListC.add(dashboardTodoProcessRSP);
        }
        if (CollectionUtil.isNotEmpty(rspListC)) {
            for (DashboardTodoProcessRSP dashboardTodoProcessRSP : rspListC) {
                DashboardTodoMyTodoProcessTaskRSP dashboardTodoMyTodoProcessTaskRSP = new DashboardTodoMyTodoProcessTaskRSP();
                BeanUtil.copyProperties(dashboardTodoProcessRSP, dashboardTodoMyTodoProcessTaskRSP);
                dashboardTodoMyTodoProcessTaskRSP.setType(TodoKeyEnum.WAIT_INITIATE.name());
                result.add(dashboardTodoMyTodoProcessTaskRSP);
            }
        }
        return result;
    }

    private Long flowCountC(DashboardTodoREQ todoREQ) {
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        List<DashboardTodoProcessRSP> rspListC = new ArrayList<>();
        ProcessPrepareListREQ req = new ProcessPrepareListREQ();
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            req.setFormName(todoREQ.getProcessName());
        }
        req.setStartUserId(startUserId);
        req.setPage(1);
        req.setPageSize(20);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CommonProcessPrepare> pageData = prepareService.list(req);
        return pageData.getTotal();
    }


    public PageR<DashboardTodoProcessRSP> myProcessApplyPageList(DashboardTodoREQ todoREQ) {
        ProcessPageReq flowReq = new ProcessPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        //在统一待办-我发起的中，模糊查询流程id
        if (StringUtils.isNotBlank(todoREQ.getProcessInstanceId())) {
            flowReq.setProcessInstanceIdLike(todoREQ.getProcessInstanceId());
        }
        flowReq.setStartUserId(startUserId);
        /*flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.REJECT.getType(),
                ProcessBusinessStatusEnum.CANCEL.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.REJECT_ALL.getType()));*/
        flowReq.setSortType(1);
        flowReq.setPageIndex(todoREQ.getPage());
        flowReq.setPageSize(todoREQ.getPageSize());
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (CollectionUtils.isEmpty(flowRespPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
        }
        List<DashboardTodoProcessRSP> rspList = flowRespPage.getContents().stream().map(todoProcessConvert::flowResp2TodoProcess).collect(Collectors.toList());
        todoProcessConvert.processListRSPFillName(rspList);
        List<DashboardTodoProcessRSP> res = new ArrayList<>();
        for (DashboardTodoProcessRSP rsp : rspList) {
            if (String.valueOf(ProcessBusinessStatusEnum.RUNNING.getType()).equals(rsp.getProcessStatus())) {
                List<OperateRecord> list = toDoOperateRecordMapper.selectList(Wrappers.<OperateRecord>lambdaQuery()
                        .eq(OperateRecord::getProcessInstanceId, rsp.getProcessInstanceId())
                        .orderByDesc(OperateRecord::getId));
                if (!list.isEmpty()) {
                    OperateRecord operateRecord = list.get(0);
                    if ("CHFQR".equalsIgnoreCase(operateRecord.getType())
                            || "BHFQR".equalsIgnoreCase(operateRecord.getType())
                            || "BHFQR_ZJDW".equalsIgnoreCase(operateRecord.getType())) {
                        rsp.setType(TodoProcessEnum.WITHDRAW.name());
                        res.add(rsp);
                    } else {
                        rsp.setType(TodoProcessEnum.APPROVING.name());
                        res.add(rsp);
                    }
                }
            } else if (String.valueOf(ProcessBusinessStatusEnum.PASS.getType()).equals(rsp.getProcessStatus())
                    || String.valueOf(ProcessBusinessStatusEnum.REJECT.getType()).equals(rsp.getProcessStatus())
                    || String.valueOf(ProcessBusinessStatusEnum.CANCEL.getType()).equals(rsp.getProcessStatus())
                    || String.valueOf(ProcessBusinessStatusEnum.PASS_ALL.getType()).equals(rsp.getProcessStatus())
                    || String.valueOf(ProcessBusinessStatusEnum.REJECT_ALL.getType()).equals(rsp.getProcessStatus())) {
                rsp.setType(TodoProcessEnum.FINISH.name());
                res.add(rsp);
            }
        }
        return PageR.of(res, flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
    }


    private Long myProcessApplyPageCountList(DashboardTodoREQ todoREQ) {
        ProcessPageReq flowReq = new ProcessPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        flowReq.setStartUserId(startUserId);
        /*flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.REJECT.getType(),
                ProcessBusinessStatusEnum.CANCEL.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.REJECT_ALL.getType()));*/
        flowReq.setSortType(1);
        flowReq.setPageIndex(todoREQ.getPage());
        flowReq.setPageSize(todoREQ.getPageSize());
        Long count = taskApiService.queryProcessCount(flowReq);
        return count;
    }

    public PageR<DashboardTodoProcessRSP> myProcessDoingPageList(DashboardTodoREQ todoREQ) {
        TaskSystemPageReq flowReq = new TaskSystemPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        //在统一待办-在办中，查询发起人
        if (StringUtils.isNotBlank(todoREQ.getStartUserId())) {
            flowReq.setStartUserId(todoREQ.getStartUserId());
        }
        //模糊查询流程id
        if (StringUtils.isNotBlank(todoREQ.getProcessInstanceId())) {
            flowReq.setProcessInstanceIdLike(todoREQ.getProcessInstanceId());
        }
        flowReq.setPageIndex(todoREQ.getPage());
        flowReq.setPageSize(todoREQ.getPageSize());
        flowReq.setIsDone(1);
        flowReq.setAssignee(startUserId);
        flowReq.setSortType(3);
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
        if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
        }
        // 填充流程数据
        List<String> processInstanceIdList = flowTaskPage.getContents().stream()
                .filter(resp -> ProcessBusinessStatusEnum.RUNNING.getType().equals(resp.getProcessStatus()))
                .map(TaskResp::getProcessInstanceId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, ProcessResp> processRespMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
            ProcessPageReq processFlowReq = new ProcessPageReq();
            processFlowReq.setProcessInstanceIdList(processInstanceIdList);
            processFlowReq.setPageSize(Integer.MAX_VALUE);
            processRespMap.putAll(taskApiService.queryProcess(processFlowReq).getContents().stream().collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p)));
        }
        List<DashboardTodoProcessRSP> rspList = flowTaskPage.getContents().stream().map(resp -> todoTaskConvert.flowResp2TodoProcessRSP(resp, processRespMap.getOrDefault(resp.getProcessInstanceId(), new ProcessResp()))).collect(Collectors.toList());
        todoTaskConvert.receiveTaskListRSPFillName(rspList);
        return PageR.of(rspList, flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
    }


    public Long myProcessDoingPageCountList(DashboardTodoREQ todoREQ) {
        TaskSystemPageReq flowReq = new TaskSystemPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        flowReq.setPageIndex(todoREQ.getPage());
        flowReq.setPageSize(todoREQ.getPageSize());
        flowReq.setIsDone(1);
        flowReq.setAssignee(startUserId);
        flowReq.setSortType(3);
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        Long count = taskApiService.querySystemTaskCount(flowReq);
        return count;
    }


    public PageR<DashboardTodoProcessRSP> myProcessFinishPageList(DashboardTodoREQ todoREQ) {
        TaskSystemPageReq flowReq = new TaskSystemPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        //在统一待办-已办中，查询发起人
        if (StringUtils.isNotBlank(todoREQ.getStartUserId())) {
            flowReq.setStartUserId(todoREQ.getStartUserId());
        }
        //模糊查询流程id
        if (StringUtils.isNotBlank(todoREQ.getProcessInstanceId())) {
            flowReq.setProcessInstanceIdLike(todoREQ.getProcessInstanceId());
        }
        flowReq.setPageIndex(todoREQ.getPage());
        flowReq.setPageSize(todoREQ.getPageSize());
        flowReq.setIsDone(1);
        flowReq.setAssignee(startUserId);
        flowReq.setSortType(3);
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.REJECT.getType(),
                ProcessBusinessStatusEnum.CANCEL.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.REJECT_ALL.getType()));
        Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
        if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
        }
        // 填充流程数据
        List<String> processInstanceIdList = flowTaskPage.getContents().stream()
                .filter(resp -> (ProcessBusinessStatusEnum.PASS.getType().equals(resp.getProcessStatus())
                        || ProcessBusinessStatusEnum.REJECT.getType().equals(resp.getProcessStatus())
                        || ProcessBusinessStatusEnum.CANCEL.getType().equals(resp.getProcessStatus())
                        || ProcessBusinessStatusEnum.PASS_ALL.getType().equals(resp.getProcessStatus())
                        || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(resp.getProcessStatus())))
                .map(TaskResp::getProcessInstanceId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, ProcessResp> processRespMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
            ProcessPageReq processFlowReq = new ProcessPageReq();
            processFlowReq.setProcessInstanceIdList(processInstanceIdList);
            processFlowReq.setPageSize(Integer.MAX_VALUE);
            processRespMap.putAll(taskApiService.queryProcess(processFlowReq).getContents().stream().collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p)));
        }
        List<DashboardTodoProcessRSP> rspList = flowTaskPage.getContents().stream().map(resp -> todoTaskConvert.flowResp2TodoProcessRSP(resp, processRespMap.getOrDefault(resp.getProcessInstanceId(), new ProcessResp()))).collect(Collectors.toList());
        todoTaskConvert.receiveTaskListRSPFillName(rspList);
        return PageR.of(rspList, flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
    }


    public Long myProcessFinishPageCountList(DashboardTodoREQ todoREQ) {
        TaskSystemPageReq flowReq = new TaskSystemPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            flowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        flowReq.setPageIndex(todoREQ.getPage());
        flowReq.setPageSize(todoREQ.getPageSize());
        flowReq.setIsDone(1);
        flowReq.setAssignee(startUserId);
        flowReq.setSortType(3);
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.REJECT.getType(),
                ProcessBusinessStatusEnum.CANCEL.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.REJECT_ALL.getType()));
        Long count = taskApiService.querySystemTaskCount(flowReq);
        return count;
    }


    public PageR<DashBoardProcessCcListRSP> myReceiveCcList(DashboardTodoREQ todoREQ) {
        CcProcessPageReq ccFlowReq = new CcProcessPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            ccFlowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        //在统一待办-流程抄送中，查询发起人
        if (StringUtils.isNotBlank(todoREQ.getStartUserId())) {
            ccFlowReq.setStartUserId(todoREQ.getStartUserId());
        }
        //模糊查询流程id
        if (StringUtils.isNotBlank(todoREQ.getProcessInstanceId())) {
            ccFlowReq.setProcessInstanceIdLike(todoREQ.getProcessInstanceId());
        }
        ccFlowReq.setReceiverId(startUserId);
        ccFlowReq.setSortType(1);
        ccFlowReq.setSortType(1);
        ccFlowReq.setPageIndex(todoREQ.getPage());
        ccFlowReq.setPageSize(todoREQ.getPageSize());
        Page<CcProcessResp> ccProcessPage = taskApiService.queryCcProcess(ccFlowReq);
        if (CollectionUtils.isEmpty(ccProcessPage.getContents())) {
            return PageR.of(new ArrayList<>(), ccProcessPage.getTotal(), ccProcessPage.getPages(), ccProcessPage.getCurPage(), ccProcessPage.getPageSize());
        }
        List<DashBoardProcessCcListRSP> rspList = ccProcessPage.getContents().stream().map(todoProcessConvert::flowCcResp2RSP).collect(Collectors.toList());
        todoProcessConvert.ccProcessRSPFillName(rspList);
        return PageR.of(rspList, ccProcessPage.getTotal(), ccProcessPage.getPages(), ccProcessPage.getCurPage(), ccProcessPage.getPageSize());
    }


    public Long myReceiveCcCountList(DashboardTodoREQ todoREQ) {
        CcProcessPageReq ccFlowReq = new CcProcessPageReq();
        String startUserId = null;
        if (StringUtils.isNotBlank(todoREQ.getAccount())) {
            startUserId = todoREQ.getAccount();
        } else {
            startUserId = String.valueOf(AccountUtil.getLoginInfo().getId());
        }
        if (StringUtils.isNotBlank(todoREQ.getProcessName())) {
            ccFlowReq.setProcessInstanceName(todoREQ.getProcessName());
        }
        ccFlowReq.setReceiverId(startUserId);
        ccFlowReq.setSortType(1);
        ccFlowReq.setSortType(1);
        ccFlowReq.setPageIndex(todoREQ.getPage());
        ccFlowReq.setPageSize(todoREQ.getPageSize());
        Long count = taskApiService.queryCcProcessCount(ccFlowReq);
        return count;
    }


    public DashboardTodoCountRSP myTabCount() {
        long startTime = System.currentTimeMillis();
        DashboardTodoCountRSP rsp = new DashboardTodoCountRSP();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors * 5);
        List<Callable<DashboardTodoRsp>> tasks = new ArrayList<>();
        DashboardTodoREQ todoREQ = new DashboardTodoREQ();
        todoREQ.setPage(1);
        todoREQ.setPageSize(20);
        todoREQ.setAccount(String.valueOf(AccountUtil.getLoginInfo().getId()));
        tasks.add(new ApiTodoCallerTask(todoREQ));
        tasks.add(new ApiApplyCallerTask(todoREQ));
        tasks.add(new ApiDoingCallerTask(todoREQ));
        tasks.add(new ApiFinishCallerTask(todoREQ));
        tasks.add(new ApiProcessCcCallerTask(todoREQ));
        try {
            List<Future<DashboardTodoRsp>> results = fixedThreadPool.invokeAll(tasks);
            for (Future<DashboardTodoRsp> result : results) {
                if ("todo".equalsIgnoreCase(result.get().getTabName())) {
                    rsp.setTodoCount(result.get().getValue());
                } else if ("apply".equalsIgnoreCase(result.get().getTabName())) {
                    rsp.setMyInitiateCount(result.get().getValue());
                } else if ("doing".equalsIgnoreCase(result.get().getTabName())) {
                    rsp.setDoingCount(result.get().getValue());
                } else if ("finish".equalsIgnoreCase(result.get().getTabName())) {
                    rsp.setDoneCount(result.get().getValue());
                } else if ("cc".equalsIgnoreCase(result.get().getTabName())) {
                    rsp.setCcCount(result.get().getValue());
                }
            }
            fixedThreadPool.shutdown();
            while (true) {
                if (fixedThreadPool.isTerminated()) {
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("代码运行时间：" + totalTime + "毫秒");
        log.info("代码运行时间：" + totalTime + "毫秒");
        return rsp;
    }

    public class ApiTodoCallerTask implements Callable<DashboardTodoRsp> {
        private DashboardTodoREQ todoREQ;

        public ApiTodoCallerTask(DashboardTodoREQ todoREQ) {
            this.todoREQ = todoREQ;
        }

        @Override
        public DashboardTodoRsp call() throws Exception {
            Long res = todoPageListCount(todoREQ);
            DashboardTodoRsp dashboardTodoRsp = new DashboardTodoRsp("todo", todoREQ.getAccount(), res.intValue());
            return dashboardTodoRsp;
        }
    }

    public class ApiApplyCallerTask implements Callable<DashboardTodoRsp> {
        private DashboardTodoREQ appplyREQ;

        public ApiApplyCallerTask(DashboardTodoREQ appplyREQ) {
            this.appplyREQ = appplyREQ;
        }

        @Override
        public DashboardTodoRsp call() throws Exception {
            Long res = myProcessApplyPageCountList(appplyREQ);
            DashboardTodoRsp dashboardTodoRsp = new DashboardTodoRsp("apply", appplyREQ.getAccount(), res.intValue());
            return dashboardTodoRsp;
        }
    }

    public class ApiDoingCallerTask implements Callable<DashboardTodoRsp> {
        private DashboardTodoREQ doingREQ;

        public ApiDoingCallerTask(DashboardTodoREQ doingREQ) {
            this.doingREQ = doingREQ;
        }

        @Override
        public DashboardTodoRsp call() throws Exception {
            Long res = myProcessDoingPageCountList(doingREQ);
            DashboardTodoRsp dashboardTodoRsp = new DashboardTodoRsp("doing", doingREQ.getAccount(), res.intValue());
            return dashboardTodoRsp;
        }
    }

    public class ApiFinishCallerTask implements Callable<DashboardTodoRsp> {
        private DashboardTodoREQ finishREQ;

        public ApiFinishCallerTask(DashboardTodoREQ finishREQ) {
            this.finishREQ = finishREQ;
        }

        @Override
        public DashboardTodoRsp call() throws Exception {
            Long res = myProcessFinishPageCountList(finishREQ);
            DashboardTodoRsp dashboardTodoRsp = new DashboardTodoRsp("finish", finishREQ.getAccount(), res.intValue());
            return dashboardTodoRsp;
        }
    }


    public class ApiProcessCcCallerTask implements Callable<DashboardTodoRsp> {
        private DashboardTodoREQ processCcREQ;

        public ApiProcessCcCallerTask(DashboardTodoREQ processCcREQ) {
            this.processCcREQ = processCcREQ;
        }

        @Override
        public DashboardTodoRsp call() throws Exception {
            Long res = myReceiveCcCountList(processCcREQ);
            DashboardTodoRsp dashboardTodoRsp = new DashboardTodoRsp("cc", processCcREQ.getAccount(), res.intValue());
            return dashboardTodoRsp;
        }
    }


    public class ApiTodoCallerTaskA implements Callable<List<DashboardTodoMyTodoProcessTaskRSP>> {
        private DashboardTodoREQ todoREQ;

        public ApiTodoCallerTaskA(DashboardTodoREQ todoREQ) {
            this.todoREQ = todoREQ;
        }

        @Override
        public List<DashboardTodoMyTodoProcessTaskRSP> call() throws Exception {
            List<DashboardTodoMyTodoProcessTaskRSP> res = flowA(todoREQ);
            return res;
        }
    }

    public class ApiTodoCallerCountTaskA implements Callable<Long> {
        private DashboardTodoREQ todoREQ;

        public ApiTodoCallerCountTaskA(DashboardTodoREQ todoREQ) {
            this.todoREQ = todoREQ;
        }

        @Override
        public Long call() throws Exception {
            Long res = flowCountA(todoREQ);
            return res;
        }
    }

    public class ApiTodoCallerTaskB implements Callable<List<DashboardTodoMyTodoProcessTaskRSP>> {
        private DashboardTodoREQ todoREQ;

        public ApiTodoCallerTaskB(DashboardTodoREQ todoREQ) {
            this.todoREQ = todoREQ;
        }

        @Override
        public List<DashboardTodoMyTodoProcessTaskRSP> call() throws Exception {
            List<DashboardTodoMyTodoProcessTaskRSP> res = flowB(todoREQ);
            return res;
        }
    }

    public class ApiTodoCallerCountTaskB implements Callable<Long> {
        private DashboardTodoREQ todoREQ;

        public ApiTodoCallerCountTaskB(DashboardTodoREQ todoREQ) {
            this.todoREQ = todoREQ;
        }

        @Override
        public Long call() throws Exception {
            Long res = flowCountB(todoREQ);
            return res;
        }
    }

    public class ApiTodoCallerTaskC implements Callable<List<DashboardTodoMyTodoProcessTaskRSP>> {
        private DashboardTodoREQ todoREQ;

        public ApiTodoCallerTaskC(DashboardTodoREQ todoREQ) {
            this.todoREQ = todoREQ;
        }

        @Override
        public List<DashboardTodoMyTodoProcessTaskRSP> call() throws Exception {
            List<DashboardTodoMyTodoProcessTaskRSP> res = flowC(todoREQ);
            return res;
        }
    }

    public class ApiTodoCallerCountTaskC implements Callable<Long> {
        private DashboardTodoREQ todoREQ;

        public ApiTodoCallerCountTaskC(DashboardTodoREQ todoREQ) {
            this.todoREQ = todoREQ;
        }

        @Override
        public Long call() throws Exception {
            Long res = flowCountC(todoREQ);
            return res;
        }
    }

}
