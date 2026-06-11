package cn.zswltech.mithras.application.orchestration.riskcontrol.opinion;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.flow.search.ReceiveTaskListRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.application.orchestration.workflow.flow.convert.FlowTaskConvert;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ProjClientRole;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorService;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.workflow.process.FlowAssistService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.application.orchestration.client.ProjClientRoleService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isEmpty;
import static cn.hutool.core.util.ObjectUtil.isNotEmpty;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.RiskControlOpinionHandleAfterLaunchFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.RiskControlOpinionHandleFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.RiskControlPaymentFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.RiskControlWarnPaymentFlow;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author
 * @description 舆情流程管理
 * @date 2023-03-09
 */
@Slf4j
@Service
public class RiskControlOpinionVersionService {

    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private RiskControlWarnMonitorService riskControlWarnMonitorService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowTaskConvert flowTaskConvert;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;

    //发起预警审批
    @Transactional(rollbackFor = Exception.class)
    public void warnInitiateApproval(List<RiskControlWarnMonitor> allOpinion) {
        if (ObjectUtil.isEmpty(allOpinion)) {
            return;
        }
        List<Long> updateOpinionMonitorIds = new ArrayList<>();
        //1.找到所有客户信息
        List<Client> clients = clientService.list(Wrappers.<Client>lambdaQuery().in(Client::getUscCode, allOpinion.stream().map(RiskControlWarnMonitor::getCreditCode).collect(Collectors.toList())));
        if (ObjectUtil.isEmpty(clients)) {
            return;
        }
        List<Long> clientIds = clients.stream().map(Client::getId).collect(Collectors.toList());
        Map<String, Client> uscCode2Client = clients.stream().collect(Collectors.toMap(Client::getUscCode, e -> e, (a, b) -> a));
        //1.区分放款前后数据
        //查询客户对应风控经理
        Map<Long, Long> clientRiskManager = this.getClientRiskManager(clientIds);
        //查询已经放款客户
        Set<Long> paymentClientSet = getPaymentClient(clientIds);
        //查询客户风险敞口
        Set<Long> stockRiskExposureSet = new HashSet<>();
        Map<Long, Long> stockRiskExposureByClients = contractBaseInfoService.getStockRiskExposureByClients(paymentClientSet);
        stockRiskExposureByClients.forEach((k, v) -> {
            if (!ObjectUtil.equals(v, 0L)) {
                stockRiskExposureSet.add(k);
            }
        });
        //发起流程
        for (RiskControlWarnMonitor opinion : allOpinion) {
            Client client = uscCode2Client.get(opinion.getCreditCode());
            if (ObjectUtil.isEmpty(client)) {
                continue;
            }
            //发起审批流
            StartProcessReq startProcessReq;
            try {
                if (paymentClientSet.contains(client.getId())) {
                    if (stockRiskExposureSet.contains(client.getId())) {
                        //已经放款处理
                        startProcessReq = buildWarnStartProcessReq(opinion, client, null);
                        startProcessReq.setModelKey(ProcessModelTypeEnum.RiskControlWarnPaymentFlow.name());
                    } else {
                        continue;
                    }
                } else {
                    //未放款
                    Long riskManager = null;
                    if (ObjectUtil.isNotEmpty(clientRiskManager)) {
                        riskManager = clientRiskManager.get(client.getId());
                    }
                    if (ObjectUtil.isEmpty(riskManager)) {
                        continue;
                    }
                    startProcessReq = buildWarnStartProcessReq(opinion, client, riskManager);
                    //未放款
                    startProcessReq.setModelKey(ProcessModelTypeEnum.RiskControlWarnNotPaymentFlow.name());
                }
                String processInstanceId = getBean(FlowProcessApiService.class).start(startProcessReq);
                SpringContextHolder.getBean(BizProcessDataService.class).recordBizData(processInstanceId, client.getId());
                updateOpinionMonitorIds.add(opinion.getId());
            }catch (MithrasException e) {
                // 捕获构建流程请求时抛出的异常（例如：无法确定项目主办）
                // 记录错误日志，跳过当前这条数据，继续处理下一条
                log.error("发起预警审批流程失败，跳过当前数据。预警监控ID: {}, 客户名称: {}, 错误信息: {}", opinion.getId(), client.getClientName(), e.getMessage(), e);
            }
        }
        if (ObjectUtil.isNotEmpty(updateOpinionMonitorIds)) {
            LambdaUpdateWrapper<RiskControlWarnMonitor> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(RiskControlWarnMonitor::getHandleStatus, RiskControlOpinionHandleStatus.HANDLE_ING.name());
            updateWrapper.in(RiskControlWarnMonitor::getId, updateOpinionMonitorIds);
            //更新状态
            riskControlWarnMonitorService.update(null, updateWrapper);
        }
    }

    private StartProcessReq buildWarnStartProcessReq(RiskControlWarnMonitor opinion, Client client, Long riskControlManagerId) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(opinion.getId()));
        startProcessReq.setProcessInstanceName(String.format("【%s】预警处置流程-%s", client.getClientName(), opinion.getWarnCode()));
        startProcessReq.setStartUserId(String.valueOf(FinancialConstants.ADMIN_ID));
        Long belongSponsorId = client.getBelongSponsorId();
        // 找到客户关联的合同或者评审中，投放金额最大的对应的项目主办
        if (Objects.isNull(belongSponsorId)) {
            belongSponsorId = this.ensureMaxAmountSponsor(client.getId());
        }
        err(isNull(belongSponsorId), "无法确定审批流中的项目主办");
        Long deptId = null;
        List<OrgDO> deptList = getBean(SysUserService.class).getSpecificUserDeptList(belongSponsorId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        }
        err(isNull(deptId), "项目主办的业务部门不存在");
        startProcessReq.setStartUserDeptId(deptId.toString());
        Long finalDeptId = deptId;
        Long originDeptLeader = getBean(SysUserService.class).getUserIdByOrgJob(finalDeptId, JobEnum.businesshead.name());
        Long originDivisionLeader = getBean(SysUserService.class).getUserIdByOrgJob(finalDeptId, JobEnum.leaderincharge.name());
        //FLHGB_ZCBQ，法律合规部
        Long flhgbLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader(FlowAssistService.FLHGB, FlowAssistService.FLHGB_DESC, businesshead.name());
        List<String> riskControlManagerIds = SpringContextHolder.getBean(SysUserService.class).getRiskManagerIdsOrderByDeptId().get(finalDeptId);
        if (CollectionUtil.isEmpty(riskControlManagerIds)) {
            riskControlManagerIds = SpringContextHolder.getBean(SysUserService.class).getAllRiskControlManagerIds().stream().map(String::valueOf).collect(Collectors.toList());
        }
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("projectSponsors", ListUtil.toList(String.valueOf(belongSponsorId))),
                Pair.of("warnLevel", isNull(opinion.getWarnLevel()) ? 1 : opinion.getWarnLevel()),
                Pair.of("riskControlManagerId", ListUtil.toList(String.valueOf(riskControlManagerId))),
                Pair.of("deptLeader", Objects.isNull(originDeptLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(originDeptLeader))),
                Pair.of("divisionLeader", Objects.isNull(originDivisionLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(originDivisionLeader))),
                Pair.of("riskControlManager", riskControlManagerIds),
                Pair.of("flhgbDeptLeader", Objects.isNull(flhgbLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(flhgbLeader)))
        ));
        return startProcessReq;
    }

    //发起舆情审批
    @Transactional(rollbackFor = Exception.class)
    public void opinionInitiateApproval(List<RiskControlOpinionMonitor> allOpinion) {
        if (ObjectUtil.isEmpty(allOpinion)) {
            return;
        }
        List<Long> updateOpinionMonitorIds = new ArrayList<>();
        //1.找到所有客户信息
        List<Client> clients = clientService.list(Wrappers.<Client>lambdaQuery().in(Client::getUscCode, allOpinion.stream().map(RiskControlOpinionMonitor::getCreditCode).collect(Collectors.toList())));
        if (ObjectUtil.isEmpty(clients)) {
            return;
        }
        List<Long> clientIds = clients.stream().map(Client::getId).collect(Collectors.toList());
        Map<String, Client> uscCode2Client = clients.stream().collect(Collectors.toMap(Client::getUscCode, e -> e, (a, b) -> a));
        //1.区分放款前后数据
        //查询已经放款客户
        Set<Long> paymentClientSet = getPaymentClient(clientIds);
        //查询客户对应风控经理
        Map<Long, Long> clientRiskManager = this.getClientRiskManager(clientIds);
        //发起流程
        for (RiskControlOpinionMonitor opinion : allOpinion) {
            Client client = uscCode2Client.get(opinion.getCreditCode());
            if (ObjectUtil.isEmpty(client)) {
                continue;
            }
            //发起审批流
            StartProcessReq startProcessReq = null;
            if (paymentClientSet.contains(client.getId())) {
                startProcessReq = buildCommonStartProcessReq(opinion, client, null);
                //已经放款处理
                startProcessReq.setModelKey(ProcessModelTypeEnum.RiskControlPaymentFlow.name());
            } else {
                //未放款
                Long riskManager = clientRiskManager.get(client.getId());
                if (ObjectUtil.isEmpty(riskManager)) {
                    continue;
                }
                startProcessReq = buildCommonStartProcessReq(opinion, client, riskManager);
                startProcessReq.setModelKey(ProcessModelTypeEnum.RiskControlNotPaymentFlow.name());
            }
            String processInstanceId = getBean(FlowProcessApiService.class).start(startProcessReq);
            SpringContextHolder.getBean(BizProcessDataService.class).recordBizData(processInstanceId, client.getId());
            updateOpinionMonitorIds.add(opinion.getId());
        }
        if (ObjectUtil.isNotEmpty(updateOpinionMonitorIds)) {
            LambdaUpdateWrapper<RiskControlOpinionMonitor> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(RiskControlOpinionMonitor::getHandleStatus, RiskControlOpinionHandleStatus.HANDLE_ING.name());
            updateWrapper.in(RiskControlOpinionMonitor::getId, updateOpinionMonitorIds);
            //更新状态
            riskControlOpinionMonitorMapper.update(null, updateWrapper);
        }
    }

    //获取已放款客户
    private Set<Long> getPaymentClient(List<Long> clientIds)  {
        // 判断该客户关联的合同是否已存在已放款的
        Set<Long> contractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getClientId, clientIds)
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()))
                .stream()
                .map(ContractBaseInfo::getId).collect(Collectors.toSet());
        List<PaymentActualDetail> paymentActualDetails = new ArrayList<>();
        if (isNotEmpty(contractIds)) {
            paymentActualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .in(PaymentActualDetail::getContractId, contractIds)
                    .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        }
        Set<Long> paymentIds = paymentActualDetails.stream().map(PaymentActualDetail::getPaymentId).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(paymentIds)) {
            return new HashSet<>();
        }
        List<PaymentBaseInfo> paymentBaseInfos = getBean(PaymentBaseInfoService.class).listByIds(paymentIds);
        Set<Long> clientSet = new HashSet<>();
        paymentBaseInfos.forEach(e -> {
            clientSet.add(e.getClientId());
        });
        return clientSet;
    }

    //获取客户授信金额最多的风控经理
    private Map<Long, Long> getClientRiskManager(List<Long> clientIds) {
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByClients(clientIds);
        if (ObjectUtil.isEmpty(projReviewBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, List<ProjReviewBaseInfo>> clientId2ProjReview = projReviewBaseInfos.stream().collect(Collectors.groupingBy(ProjReviewBaseInfo::getClientId));
        Map<Long, ProjReviewPriceDetailRSP> projReviewPriceDetailRSPMap = projReviewPriceService.list(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
        Map<Long, Long> clientId2RiskManagerMap = new HashMap<>();
        clientIds.forEach(clientId -> {
            List<ProjReviewBaseInfo> tempProjReviewBase = clientId2ProjReview.get(clientId);
            if (ObjectUtil.isNotEmpty(tempProjReviewBase)) {
                Long riskId = null;
                Long applyCreditAmount = 0L;
                for (ProjReviewBaseInfo temp : tempProjReviewBase) {
                    ProjReviewPriceDetailRSP projReviewPriceDetailRSP = projReviewPriceDetailRSPMap.get(temp.getId());
                    if (isNotEmpty(projReviewPriceDetailRSP) && LongUtil.null2zero(projReviewPriceDetailRSP.getApplyCreditAmount()) > applyCreditAmount) {
                        applyCreditAmount = LongUtil.null2zero(projReviewPriceDetailRSP.getApplyCreditAmount());
                        riskId = temp.getRiskControlManagerId();
                    }
                }
                if (riskId != null) {
                    clientId2RiskManagerMap.put(clientId, riskId);
                }
            }
        });
        return clientId2RiskManagerMap;
    }
    private StartProcessReq buildCommonStartProcessReq(RiskControlOpinionMonitor opinion, Client client, Long riskControlManagerId) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(opinion.getId()));
        startProcessReq.setProcessInstanceName(String.format("【%s】舆情处置流程-%s", client.getClientName(), opinion.getId()));
        startProcessReq.setStartUserId(String.valueOf(FinancialConstants.ADMIN_ID));
        Long belongSponsorId = client.getBelongSponsorId();
        // 找到客户关联的合同或者评审中，投放金额最大的对应的项目主办
        if (Objects.isNull(belongSponsorId)) {
            belongSponsorId = this.ensureMaxAmountSponsor(client.getId());
        }
        //err(isNull(belongSponsorId), "无法确定审批流中的项目主办");
        Long deptId = null;
        List<OrgDO> deptList = getBean(SysUserService.class).getSpecificUserDeptList(belongSponsorId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        }
        err(isNull(deptId), "项目主办的业务部门不存在");
        startProcessReq.setStartUserDeptId(deptId.toString());
        Long finalDeptId = deptId;
        Long originDeptLeader = getBean(SysUserService.class).getUserIdByOrgJob(finalDeptId, JobEnum.businesshead.name());
        Long originDivisionLeader = getBean(SysUserService.class).getUserIdByOrgJob(finalDeptId, JobEnum.leaderincharge.name());
        //FLHGB_ZCBQ，法律合规部
        Long flhgbLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader(FlowAssistService.FLHGB, FlowAssistService.FLHGB_DESC, businesshead.name());
        List<String> riskControlManagerIds = SpringContextHolder.getBean(SysUserService.class).getRiskManagerIdsOrderByDeptId().get(finalDeptId);
        if (CollectionUtil.isEmpty(riskControlManagerIds)) {
            riskControlManagerIds = SpringContextHolder.getBean(SysUserService.class).getAllRiskControlManagerIds().stream().map(String::valueOf).collect(Collectors.toList());
        }
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("projectSponsors", Objects.isNull(belongSponsorId) ? new ArrayList<>() : ListUtil.toList(String.valueOf(belongSponsorId))),
                Pair.of("warnLevel", isNull(opinion.getWarnLevel()) ? 1 : opinion.getWarnLevel()),
                Pair.of("riskControlManagerId", Objects.isNull(riskControlManagerId) ? new ArrayList<>() : ListUtil.toList(String.valueOf(riskControlManagerId))),
                Pair.of("deptLeader", Objects.isNull(originDeptLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(originDeptLeader))),
                Pair.of("divisionLeader", Objects.isNull(originDivisionLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(originDivisionLeader))),
                Pair.of("riskControlManager", riskControlManagerIds),
                Pair.of("flhgbDeptLeader", Objects.isNull(flhgbLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(flhgbLeader)))
        ));
        return startProcessReq;
    }

    public Long ensureMaxAmountSponsor(Long clientId) {
        Map<String, List<ProjClientRole>> moduleGroup = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
                        .eq(ProjClientRole::getClientId, clientId)).stream()
                .collect(Collectors.groupingBy(ProjClientRole::getModuleType));
        // 优先找合同
        List<ProjClientRole> contractProjClientRoleList = moduleGroup.get(BusinessModuleEnum.CONTRACT.name());
        if (CollectionUtil.isNotEmpty(contractProjClientRoleList)) {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByIds(contractProjClientRoleList.stream().map(ProjClientRole::getMainId).collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
                contractBaseInfoList.sort(Comparator.comparing(ContractBaseInfo::getApplyCreditAmount));
                return contractBaseInfoList.get(contractBaseInfoList.size() - 1).getProjSponsorUserId();
            }
        }
        // 没有合同找评审
        List<ProjClientRole> projReviewProjClientRoleList = moduleGroup.get(BusinessModuleEnum.PROJ_REVIEW.name());
        if (CollectionUtil.isNotEmpty(projReviewProjClientRoleList)) {
            List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.listByIds(projReviewProjClientRoleList.stream().map(ProjClientRole::getMainId).collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(projReviewBaseInfoList)) {
                projReviewBaseInfoList.sort(Comparator.comparing(ProjReviewBaseInfo::getDeclaredAmount));
                return projReviewBaseInfoList.get(projReviewBaseInfoList.size() - 1).getProjSponsorUserId();
            }
        }
        return null;
    }

    @Resource
    private FlowTaskApiService flowTaskApiService;

    public String getProcessInstanceId(Long id) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(Arrays.asList(RiskControlOpinionHandleAfterLaunchFlow.name(), RiskControlOpinionHandleFlow.name()));
        processPageReq.setBusinessKey(String.valueOf(id));
        processPageReq.setSortType(1);
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        if (isEmpty(processRespPage.getContents())) {
            throw new MithrasException("该舆情处置不存在审批流程");
        }
        return processRespPage.getContents().get(0).getProcessInstanceId();
    }

    /**
     * 5个工作日未提交提醒
     * @author: luyujie
     * @date: 2025/11/27
     **/
    public void riskRemind(String id) {
        int pageSize = 20; // 每页大小
        int pageNo = 1; // 当前页码
        int maxPageNo = 1;
        // 循环分页处理
        while (pageNo <= maxPageNo) {
            TaskSystemPageReq flowReq = new TaskSystemPageReq();
            flowReq.setDynamicFilterParam(new HashMap<>());
            flowReq.setModelKeyList(Arrays.asList(
                    RiskControlPaymentFlow.name(),
                    RiskControlOpinionHandleAfterLaunchFlow.name(),
                    RiskControlWarnPaymentFlow.name()
            ));
            flowReq.setIsRunning(1);
            flowReq.setActivityId("userTask_projmanagers");
            flowReq.setSortType(1);
            flowReq.setPageIndex(pageNo);
            flowReq.setPageSize(pageSize);

            // 查询处于流程节点项目主办的舆情统一监测(已放款)流程,当前页
            Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
            PageHelper.clearPage();
            maxPageNo = flowTaskPage.getPages();
            // 如果当前页无数据，退出循环
            if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
                break;
            }

            // 存在流程节点为项目主办的流程并且当天是工作日发起提醒
            if (DateUtil.isWorkday(LocalDate.now())) {
                // 提取流程实例ID并获取流程详情
                List<String> processInstanceIdList = flowTaskPage.getContents().stream()
                        .map(TaskResp::getProcessInstanceId)
                        .distinct()
                        .collect(Collectors.toList());

                Map<String, ProcessResp> processRespMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
                    ProcessPageReq processFlowReq = new ProcessPageReq();
                    processFlowReq.setProcessInstanceIdList(processInstanceIdList);
                    processFlowReq.setPageSize(Integer.MAX_VALUE);
                    processRespMap.putAll(
                            taskApiService.queryProcess(processFlowReq).getContents().stream()
                                    .collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p))
                    );
                }

                // 调用独立方法处理当前页任务
                processTaskPage(flowTaskPage.getContents(), processRespMap);
            }

            // 更新页码
            pageNo++;
        }
    }

    /**
     * 新增的独立方法，用于处理单页任务数据
     */
    private void processTaskPage(List<TaskResp> taskResps, Map<String, ProcessResp> processRespMap) {
        // 构建接收任务列表
        List<ReceiveTaskListRSP> rspList = new ArrayList<>();

        for (TaskResp resp : taskResps) {
            try {
                // 尝试转换任务响应为接收任务对象
                ReceiveTaskListRSP rsp = flowTaskConvert.flowResp2ReceiveRSP(resp, processRespMap.get(resp.getProcessInstanceId()));
                rspList.add(rsp);
            } catch (Exception e) {
                // 记录异常日志，跳过当前任务
                log.warn("Failed to convert task response to ReceiveTaskListRSP, processInstanceId: {}, error: {}", resp.getProcessInstanceId(), e.getMessage(), e);
            }
        }

        // 填充名称信息
        flowTaskConvert.receiveTaskListRSPFillName(rspList);

        // 发送消息提醒（针对超过5个工作日未处理的任务）
        for (ReceiveTaskListRSP rsp : rspList) {
            try {
                if (DateUtil.countWorkdayNumber(rsp.getTaskCreateTime().toLocalDate(), LocalDate.now()) == 7
                        && rsp.getAssignee() != null) {
                    MessageAddREQ messageAddREQ = new MessageAddREQ();
                    messageAddREQ.setFrom("系统通知");
                    messageAddREQ.setTo(Collections.singletonList(rsp.getAssignee()));
                    messageAddREQ.setFlowid(rsp.getTaskId());
                    messageAddREQ.setRelation("【" + rsp.getClientName() + "】的" + rsp.getModelName());
                    messageAddREQ.setNeedOa(false);
                    messageAddREQ.setNoticeSource(NoticeSourceENUM.APPROVAL_PROCESS.name());
                    messageAddREQ.setMessageType(MessageTypeEnum.UNDER_APPROVAL.name());
                    MessageUrlEnum noticeContextEnum = MessageUrlEnum.NOTICE_CONTEXT;
                    messageAddREQ.setAppurl(StringUtils.format(noticeContextEnum.appUrl, rsp.getTaskId()));
                    messageAddREQ.setPcurl(StringUtils.format(noticeContextEnum.pcUrl, rsp.getTaskId(), rsp.getBusinessKey(), rsp.getSubModule()));
                    messageAddREQ.setTaskId(rsp.getTaskId());
                    messageAddREQ.setContent(rsp.getProcessInstanceId());
                    messageService.sendMessageAsync(messageConvert.reqToTodoMessage(messageAddREQ));
                }
            } catch (Exception e) {
                // 记录异常日志，跳过当前消息发送
                log.warn("Failed to send message for task, taskId: {}, error: {}", rsp.getTaskId(), e.getMessage(), e);
            }
        }
    }
}
