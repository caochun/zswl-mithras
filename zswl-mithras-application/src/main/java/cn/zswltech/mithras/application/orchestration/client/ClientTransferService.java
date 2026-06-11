package cn.zswltech.mithras.application.orchestration.client;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.client.client.SponsorClientDetailNewModifyREQ;
import cn.zswltech.mithras.dto.client.client.SponsorClientSubmitNewREQ;
import cn.zswltech.mithras.dto.client.client.SponsorClientSubmitREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.customer.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.customer.mapper.client.*;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClientLib;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientAuthority;
import cn.zswltech.mithras.customer.model.client.ClientTransfer;
import cn.zswltech.mithras.customer.model.client.ClientTransferWeight;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeight;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeight;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfoLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.ProjLifecycleEventMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.workflow.process.FlowAssistService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.customer.event.ClientViewAuthorityEvent;
import cn.zswltech.mithras.customer.application.client.ClientTimedTransferService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.customer.application.client.ClientTransferWeightService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.kpi.application.distribution.KpiProjectDistributionDeptWeightService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionWeightService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckPlanClientLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projpricing.ProjPricingBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.client.authority.ClientAuthorityUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.collection.ListUtil.toList;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.zswltech.mithras.foundation.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.foundation.enums.JobEnum.humanresourcessupervisor;
import static cn.zswltech.mithras.customer.enums.client.ClientTransferStatus.*;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static java.util.stream.Collectors.toList;

/**
 * @author yibin
 */
@Slf4j
@Service
public class ClientTransferService extends ServiceImpl<ClientTransferMapper, ClientTransfer> implements ClientTimedTransferService {
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private UserService userService;
    @Resource
    private ProjEstablishBaseInfoService etbService;
    @Resource
    private ProjEstablishBaseInfoLibService etbLibService;
    @Resource
    private ProjReviewBaseInfoService rvService;
    @Resource
    private ProjReviewBaseInfoLibService rvLibService;
    @Resource
    private ProjPricingBaseInfoService pricingBaseInfoService;
    @Resource
    private ProjPricingBaseInfoLibService pricingBaseInfoLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractLibService;
    @Resource
    private ExecutionApi executionApi;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjLifecycleEventMapper projLifecycleEventMapper;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckPlanClientLibService afterLeaseCheckPlanClientLibService;
    @Resource
    private AfterLeaseCheckExternalQueryService afterLeaseCheckExternalQueryService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private KpiProjectDistributionWeightService kpiProjectDistributionWeightService;
    @Resource
    private CommonProcessPrepareService prepareService;
    @Resource
    private ClientTransferWeightService clientTransferWeightService;
    @Resource
    private ClientTransferWeightMapper transferWeightMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private ClientAuthorityMapper clientAuthorityMapper;
    @Resource
    private ClientUserRefMapper clientUserRefMapper;
    @Resource
    private ClientAuthorityUtil clientAuthorityUtil;
    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private ProjEstablishBaseInfoMapper establishBaseInfoMapper;
    @Resource
    private ClientTransferMapper clientTransferMapper;
    @Resource
    private ClientTransferWeightService transferWeightService;
    @Resource
    private Id2NameService id2NameService;

    @Value("${mithras.org.riskManagementCode}")
    private String riskManagementOrgCode;
    @Value("${mithras.org.generalManagementCode}")
    private String generalManagementOrgCode;

    public List<ClientTransfer> listByBatchNo(String batchNo) {
        LambdaQueryWrapper<ClientTransfer> query = Wrappers.lambdaQuery();
        query.eq(ClientTransfer::getBatchNo, batchNo);
        return this.list(query);
    }

    public boolean inTransfer(Long clientId) {
        return isNotEmpty(this.baseMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery()
                .in(ClientTransfer::getTransferStatus, to_be_approved.name(), timed_approved.name())
                .eq(ClientTransfer::getClientId, clientId)
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    public void submit(SponsorClientSubmitREQ req) {
        List<SponsorClientSubmitREQ.TransferClient> clientList = req.getTransferClientList();
        List<ClientTransfer> inProcessClient = baseMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery()
                .in(ClientTransfer::getClientId, clientList.stream().map(SponsorClientSubmitREQ.TransferClient::getClientId).collect(toList()))
                .in(ClientTransfer::getTransferStatus, to_be_approved.name(), timed_approved.name())
        );
        err(isNotEmpty(inProcessClient), "该客户关联的有流程没结束，处于流程中不可移交");
        /*if (isNotEmpty(inProcessClient)) {
            err(join("、", inProcessClient.stream().map(ClientTransfer::getClientName).collect(toList()))
                    + "，已处在移交流程中"
            );
        }*/
        String batchNo = IdUtil.getSnowflakeNextIdStr();
        Long toDeptId = req.getToDeptId();
        Long toSponsorId = req.getToSponsorId();
        LocalDate transferDate = req.getTransferDate();
        List<ClientTransfer> transferList = new ArrayList<>(clientList.size());
        for (SponsorClientSubmitREQ.TransferClient transferClient : clientList) {
            ClientTransfer transfer = new ClientTransfer();
            transfer.setBatchNo(batchNo);
            transfer.setToDeptId(toDeptId);
            transfer.setToSponsorId(toSponsorId);
            transfer.setToCosponsorIds(req.getToCosponsorIds());
            transfer.setTransferDate(transferDate);
            transfer.setClientId(transferClient.getClientId());
            transfer.setClientName(transferClient.getClientName());
            transfer.setClientType(transferClient.getClientType());
            transfer.setClientCode(transferClient.getClientCode());
            transfer.setTransferStatus(to_be_approved.name());
            transfer.setBelongDeptId(transferClient.getBelongDeptId());
            transfer.setBelongSponsorId(transferClient.getBelongSponsorId());
            transferList.add(transfer);
        }
        this.saveBatch(transferList);
        // 生成流程实例
        StartProcessReq startProcessReq = buildCommonStartProcessReq(transferList);
        startProcessReq.setModelKey(ProcessModelTypeEnum.ClientTransferFlow.name());
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, null);
        Long id = AccountUtil.getLoginInfo().getId();
        LocalDateTime now = LocalDateTime.now();
        List<Long> ids = clientList.stream().map(SponsorClientSubmitREQ.TransferClient::getClientId).distinct().collect(toList());
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = etbService.listByClients(ids);
        if (CollectionUtil.isNotEmpty(projEstablishBaseInfos)) {
            List<ProjLifecycleEvent> events = new LinkedList<>();
            for (ProjEstablishBaseInfo baseInfo : projEstablishBaseInfos) {
                ProjLifecycleEvent event = getBaseEvent("客户移交审批", ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", baseInfo.getId(), id, now);
                events.add(event);
            }
            projLifecycleEventMapper.insertList(events);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void newSubmit(SponsorClientSubmitNewREQ req) {
        List<SponsorClientSubmitNewREQ.TransferClient> clientList = req.getTransferClientList();
        List<ClientTransfer> inProcessClient = baseMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery()
                .in(ClientTransfer::getClientId, clientList.stream().map(SponsorClientSubmitNewREQ.TransferClient::getId).collect(toList()))
                .in(ClientTransfer::getTransferStatus, to_be_approved.name(), timed_approved.name())
        );
        //判断是否公海客户不同项目
        if (isNotEmpty(inProcessClient)) {
            List<ClientTransfer> inProcessGroupClient = new ArrayList<>();
            for (ClientTransfer clientTransfer : inProcessClient) {
                if (clientAuthorityUtil.isIntraGroupCollaboration(clientTransfer.getClientId())) {
                    inProcessGroupClient.add(clientTransfer);
                }
            }
            if (!inProcessGroupClient.isEmpty()) {
                for (ClientTransfer clientTransfer : inProcessGroupClient) {
                    for (SponsorClientSubmitNewREQ.TransferClient submitTransferClient : clientList) {
                        if (clientTransfer.getClientId().equals(submitTransferClient.getId())) {
                            List<Long> projEstablishIds = clientTransfer.getProjEstablishIds();
                            List<ProjEstablishBaseInfo> projEstablishBaseInfoList = null;
                            Set<String> projCodeSet = new HashSet<>();
                            if (projEstablishIds != null && !projEstablishIds.isEmpty()) {
                                projEstablishBaseInfoList = establishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                                        .in(ProjEstablishBaseInfo::getId, projEstablishIds));
                                projCodeSet = projEstablishBaseInfoList.stream().map(ProjEstablishBaseInfo::getProjCode).collect(Collectors.toSet());
                            }
                            List<SponsorClientSubmitNewREQ.ClientProjRSP> clientProjRSPList = submitTransferClient.getClientProjRSPList();
                            for (SponsorClientSubmitNewREQ.ClientProjRSP projRSP : clientProjRSPList) {
                                if (!projCodeSet.isEmpty()
                                        && StringUtils.isNotBlank(projRSP.getProjCode())
                                        && projCodeSet.contains(projRSP.getProjCode())) {
                                    err(isNotEmpty(inProcessClient), "该客户关联的有流程没结束，处于流程中不可移交");
                                }
                            }
                        }
                    }
                }
            }
        } else {
            err(isNotEmpty(inProcessClient), "该客户关联的有流程没结束，处于流程中不可移交");
        }
        String batchNo = req.getBatchNo();
        LocalDate transferDate = req.getTransferDate();
        List<ClientTransfer> transferList = new ArrayList<>(clientList.size());
        List<Long> clientIds = new ArrayList<>();
        for (SponsorClientSubmitNewREQ.TransferClient transferClient : clientList) {
            List<SponsorClientSubmitNewREQ.ClientProjRSP> clientProjRSPList = transferClient.getClientProjRSPList();
            ClientTransfer transfer = new ClientTransfer();
            transfer.setBatchNo(batchNo);
            if (clientProjRSPList != null && !clientProjRSPList.isEmpty()) {
                SponsorClientSubmitNewREQ.ClientProjRSP clientProjRSP = clientProjRSPList.get(0);
                transfer.setToDeptId(clientProjRSP.getToBelongDeptId());
                transfer.setToSponsorId(clientProjRSP.getToSponsorId());
                transfer.setToCosponsorIds(clientProjRSP.getToCosponsorIds());
                if (clientAuthorityUtil.isIntraGroupCollaboration(transferClient.getId())) {
                    for (SponsorClientSubmitNewREQ.ClientProjRSP projRSP : clientProjRSPList) {
                        Set<String> set = new HashSet<>();
                        if (StringUtils.isNotBlank(projRSP.getProjCode())) {
                            set.add(projRSP.getProjCode());
                        }
                        if (!set.isEmpty()) {
                            List<ProjEstablishBaseInfo> projEstablishBaseInfoList = establishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                                    .in(ProjEstablishBaseInfo::getProjCode, set));
                            List<Long> establishBaseInfoIds = projEstablishBaseInfoList.stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toList());
                            transfer.setProjEstablishIds(establishBaseInfoIds);
                        }
                    }
                }
            }
            transfer.setTransferDate(transferDate);
            transfer.setClientId(transferClient.getId());
            transfer.setClientName(transferClient.getClientName());
            transfer.setClientType(transferClient.getClientType());
            transfer.setClientCode(transferClient.getClientCode());
            transfer.setTransferStatus(to_be_approved.name());
            if (clientAuthorityUtil.isIntraGroupCollaboration(transferClient.getId())) {
                transfer.setBelongDeptId(req.getBelongDeptId());
                transfer.setBelongSponsorId(req.getBelongSponsorId());
            } else {
                transfer.setBelongDeptId(transferClient.getBelongDeptId());
                transfer.setBelongSponsorId(transferClient.getBelongSponsorId());
            }
            transfer.setDescription(req.getDescription());
            transferList.add(transfer);
            clientIds.add(transferClient.getId());
            List<ClientTransferWeight> clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                    .eq(ClientTransferWeight::getClientId, transferClient.getId())
                    .eq(ClientTransferWeight::getDeleted, 0)
                    //.eq(isNotNull(transfer.getBelongSponsorId()), ClientTransferWeight::getCreateBy, transfer.getBelongSponsorId())
                    .isNull(ClientTransferWeight::getBatchNo));
            List<ClientTransferWeight> unused = removeUnusedClientTransferWeight(clientTransferWeightList);
            if (!unused.isEmpty()) {
                for (ClientTransferWeight unusedWeight : unused) {
                    LambdaUpdateWrapper<ClientTransferWeight> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(ClientTransferWeight::getDeleted, YesOrNoNumberEnum.YES.getCode());
                    updateWrapper.eq(ClientTransferWeight::getId, unusedWeight.getId());
                    transferWeightMapper.update(null, updateWrapper);
                }
            }
            clientTransferWeightList = getLatestTransferWeight(clientTransferWeightList, unused);
            for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                clientTransferWeight.setBatchNo(batchNo);
                transferWeightMapper.updateById(clientTransferWeight);
            }
        }
        this.saveBatch(transferList);

        // 生成流程实例
        StartProcessReq startProcessReq = buildNewCommonStartProcessReq(transferList);
        startProcessReq.setModelKey(ProcessModelTypeEnum.ClientTransferFlow.name());
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, clientIds.size() == 1 ? clientIds.get(0) : null);
        Long id = AccountUtil.getLoginInfo().getId();
        LocalDateTime now = LocalDateTime.now();
        List<Long> ids = clientList.stream().map(SponsorClientSubmitNewREQ.TransferClient::getId).distinct().collect(toList());
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = etbService.listByClients(ids);
        if (CollectionUtil.isNotEmpty(projEstablishBaseInfos)) {
            List<ProjLifecycleEvent> events = new LinkedList<>();
            for (ProjEstablishBaseInfo baseInfo : projEstablishBaseInfos) {
                ProjLifecycleEvent event = getBaseEvent("客户移交审批", ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", baseInfo.getId(), id, now);
                events.add(event);
            }
            projLifecycleEventMapper.insertList(events);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void newTransferDetailModify(SponsorClientDetailNewModifyREQ req) {
        String batchNo = req.getBatchNo();
        List<ClientTransfer> transferList = this.list(Wrappers.<ClientTransfer>lambdaQuery().eq(ClientTransfer::getBatchNo, batchNo));
        if (transferList.isEmpty()) {
            ClientTransfer clientTransfer = new ClientTransfer();
            clientTransfer.setBatchNo(req.getBatchNo());
            clientTransfer.setTransferDate(req.getTransferDate());
            clientTransfer.setDescription(req.getDescription());
            this.save(clientTransfer);
        } else {
            for (ClientTransfer clientTransfer : transferList) {
                clientTransfer.setBatchNo(req.getBatchNo());
                clientTransfer.setTransferDate(req.getTransferDate());
                clientTransfer.setDescription(req.getDescription());
                clientTransferMapper.updateById(clientTransfer);
            }
        }
    }

    private ProjLifecycleEvent getBaseEvent(String event, String eventType, String eventdesc, Long projId, Long id, LocalDateTime localDateTime) {
        ProjLifecycleEvent projLifecycleEvent = new ProjLifecycleEvent();
        projLifecycleEvent.setEvent(event);
        projLifecycleEvent.setEventTime(localDateTime);
        projLifecycleEvent.setEventType(eventType);
        projLifecycleEvent.setOperator(id);
        projLifecycleEvent.setEventdesc(eventdesc);
        projLifecycleEvent.setProjId(projId);
        return projLifecycleEvent;
    }

    @Transactional(rollbackFor = Exception.class)
    public void processEnd(String batchNo, Integer endType, Long startUserId, String processInstanceId) {
        log.info("client transfer process end. batchNo:{}, endType:{}, startUserId:{}, processInstanceId:{}", batchNo, endType, startUserId, processInstanceId);
        boolean passed = ProcessBusinessStatusEnum.success(endType);
        //1.更新转交记录状态
        ClientTransfer toBe = new ClientTransfer();
        toBe.setTransferStatus(passed ? timed_approved.name() : rejected.name());
        baseMapper.update(toBe, Wrappers.<ClientTransfer>lambdaUpdate().eq(ClientTransfer::getBatchNo, batchNo));
        //移交管护权
        if (passed) {
            List<ClientTransfer> transferList = baseMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery().eq(ClientTransfer::getBatchNo, batchNo));
            for (ClientTransfer clientTransfer : transferList) {
                Long belongSponsorId = clientTransfer.getBelongSponsorId();
                Long toSponsorId = clientTransfer.getToSponsorId();
                Long toDeptId = clientTransfer.getToDeptId();
                Long clientId = clientTransfer.getClientId();
                //清空客户移交分配信息-一份用于快照，一份用于客户移交编辑区展示清空
                List<ClientTransferWeight> clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                        .eq(ClientTransferWeight::getClientId, clientTransfer.getClientId())
                        .eq(ClientTransferWeight::getBatchNo, batchNo)
                        .eq(ClientTransferWeight::getDeleted, 0));
                //清空旧的历史记录
                List<ClientTransferWeight> clientTransferWeightHistoryList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                        .eq(ClientTransferWeight::getClientId, clientTransfer.getClientId())
                        .isNull(ClientTransferWeight::getBatchNo)
                        .eq(ClientTransferWeight::getDeleted, 0));
                if (!clientTransferWeightHistoryList.isEmpty()) {
                    for (ClientTransferWeight weight : clientTransferWeightHistoryList) {
                        LambdaUpdateWrapper<ClientTransferWeight> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.set(ClientTransferWeight::getDeleted, YesOrNoNumberEnum.YES.getCode());
                        updateWrapper.eq(ClientTransferWeight::getId, weight.getId());
                        transferWeightMapper.update(null, updateWrapper);
                    }
                }
                if (clientTransferWeightList != null && !clientTransferWeightList.isEmpty()) {
                    for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                        ClientTransferWeight temp = BeanUtil.copyProperties(clientTransferWeight, ClientTransferWeight.class, "id");
                        temp.setBatchNo(null);
                        temp.setRiskTransferValue(null);
                        temp.setIncomeTransferValue(null);
                        temp.setProjArchiveStatus(null);
                        temp.setToDeptId(null);
                        temp.setToCosponsorIds(null);
                        temp.setToSponsorId(null);
                        transferWeightMapper.insert(temp);
                    }
                }
            }
        }
//        //3. 抄送综合管理部门人员、风控部门人员(风险管理部（业务评审部）)
//        UserQuery query = new UserQuery();
//        query.setOrgCode(riskManagementOrgCode);
//        List<Long> idList = userService.queryUserSys(query).getContents().stream().map(UserDO::getId).collect(toList());
//        query.setOrgCode(generalManagementOrgCode);
//        idList.addAll(userService.queryUserSys(query).getContents().stream().map(UserDO::getId).collect(toList()));
//        ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
//        req.setProcessInstanceId(processInstanceId);
//        req.setCcUserIdList(idList);
//        req.setMessage(passed ? "审批通过" : "审批拒绝");
//        executionApi.cc(req);

        //立即触发一次定时移交的任务， 防止审批通过时间在正式移交时间之后，这种情况会立即移交
        timedPass();
    }

    @Transactional(rollbackFor = Exception.class)
    public void timedPass() {
        List<ClientTransfer> list = baseMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery()
                .eq(ClientTransfer::getTransferStatus, timed_approved.name())
                .le(ClientTransfer::getTransferDate, LocalDate.now())
        );
        if (CollUtil.isEmpty(list)) {
            log.info("需要移交的记录为空，跳过");
            return;
        }
        ClientTransfer entity = new ClientTransfer();
        entity.setTransferStatus(approved.name());
        this.update(entity, Wrappers.<ClientTransfer>lambdaUpdate().in(ClientTransfer::getId, list.stream().map(ClientTransfer::getId).collect(toList())));
        Set<String> batchNoSet = list.stream().map(ClientTransfer::getBatchNo).collect(Collectors.toSet());
        for (String batchNo : batchNoSet) {
            log.info("batchNo为：{}的记录开始移交客户、立项、评审、合同", batchNo);
            List<ClientTransfer> transferList = baseMapper.selectList(Wrappers.<ClientTransfer>lambdaQuery().eq(ClientTransfer::getBatchNo, batchNo));
            ClientTransfer record = transferList.get(0);
            List<Long> clientIdList = new ArrayList<>();
            List<Long> groupIdList = new ArrayList<>();
            List<Long> allList = transferList.stream().map(ClientTransfer::getClientId).collect(toList());
            for (Long clientId : allList) {
                if (clientAuthorityUtil.isIntraGroupCollaboration(clientId)) {
                    groupIdList.add(clientId);
                } else {
                    clientIdList.add(clientId);
                }
            }
            for (ClientTransfer clientTransfer : transferList) {
                Long belongSponsorId = clientTransfer.getBelongSponsorId();
                Long toSponsorId = clientTransfer.getToSponsorId();
                Long toDeptId = clientTransfer.getToDeptId();
                Long clientId = clientTransfer.getClientId();
                if (!clientAuthorityUtil.isIntraGroupCollaboration(clientId)) {
                    //数据拷贝
                    boolean copyDataResult = SpringUtil.getBean(ClientService.class).tryInitNewClientInfoOrIgnore(
                            clientTransfer.getClientId(),
                            toSponsorId,
                            null
                    );
                    ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                            .clientId(clientTransfer.getClientId())
                            .dbUserId(belongSponsorId)
                            .currentUserId(toSponsorId)
                            .moduleList(null)
                            .build();
                    if (!copyDataResult) {
                        clientAuthorityUtil.copyFromNewToNew(clientCopyInfoBO);
                    }
                    clientAuthorityUtil.copyMaterialListFromOldToNew(clientCopyInfoBO);
                    //移交前主办
                    LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
                    query.eq(ClientAuthority::getClientId, clientTransfer.getClientId());
                    query.eq(ClientAuthority::getUserId, belongSponsorId);
                    query.eq(ClientAuthority::getDeleted, 0);
                    clientAuthorityService.remove(query);
                    //移交后主办
                    ClientAuthority newClientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, clientTransfer.getClientId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                            .eq(ClientAuthority::getUserId, toSponsorId)
                            .eq(ClientAuthority::getDeleted, 0));
                    if (newClientAuthority == null) {
                        newClientAuthority = new ClientAuthority();
                        newClientAuthority.setClientId(clientTransfer.getClientId());
                        newClientAuthority.setUserId(toSponsorId);
                        newClientAuthority.setDeptId(toDeptId);
                        newClientAuthority.setSourceBusinessType(BusinessModuleEnum.CLIENT_TRANSFER.name());
                        newClientAuthority.setSourceId(batchNo);
                        newClientAuthority.setLevel(ClientLevelEnum.MANAGE.getLevel());
                        clientAuthorityMapper.insert(newClientAuthority);
                    } else {
                        newClientAuthority.setClientId(clientTransfer.getClientId());
                        newClientAuthority.setUserId(toSponsorId);
                        newClientAuthority.setDeptId(toDeptId);
                        newClientAuthority.setLevel(ClientLevelEnum.MANAGE.getLevel());
                        newClientAuthority.setSourceBusinessType(BusinessModuleEnum.CLIENT_TRANSFER.name());
                        newClientAuthority.setSourceId(batchNo);
                        clientAuthorityMapper.updateAnnotationIncludeNullById(newClientAuthority);
                    }
                }
            }
            //更新客户的经理
            if (!clientIdList.isEmpty()) {
                Client updateClient = new Client();
                updateClient.setBelongSponsorId(record.getToSponsorId());
                updateClient.setBelongDeptId(record.getToDeptId());
                clientMapper.update(updateClient, Wrappers.<Client>lambdaUpdate().in(Client::getId, clientIdList));
            }
            //2.更新对应的主办相关信息
            //立项列表
            List<ProjEstablishBaseInfo> etbList = new ArrayList<>();
            if (!clientIdList.isEmpty()) {
                etbList = etbService.listByClients(clientIdList);
            }
            //公海立项
            if (!groupIdList.isEmpty()) {
                List<ProjEstablishBaseInfo> groupEtbList = etbService.listByClients(groupIdList);
                if (!groupEtbList.isEmpty()) {
                    for (ProjEstablishBaseInfo baseInfo : groupEtbList) {
                        if (record.getBelongSponsorId().equals(baseInfo.getProjSponsorUserId())) {
                            etbList.add(baseInfo);
                        }
                    }
                }
            }
            List<ProjEstablishBaseInfo> updateEtbList = new ArrayList<>(etbList.size());
            List<ProjEstablishBaseInfoLib> updateEtbLibList = new ArrayList<>(etbList.size());
            for (ProjEstablishBaseInfo etb : etbList) {
                //
                ProjEstablishBaseInfo updateEtb = new ProjEstablishBaseInfo();
                updateEtb.setId(etb.getId());
                updateEtb.setProjSponsorUserId(record.getToSponsorId());
                if (CollUtil.isNotEmpty(record.getToCosponsorIds())) {
                    updateEtb.setProjCosponsorUserIds(JSONUtil.toJsonStr(record.getToCosponsorIds()));
                }
                updateEtb.setBizDeptId(record.getToDeptId());
                updateEtb.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.businesshead.name()));
                updateEtb.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.leaderincharge.name()));
                updateEtbList.add(updateEtb);
                //
                ProjEstablishBaseInfoLib etbLib = etbLibService.getOne(Wrappers.<ProjEstablishBaseInfoLib>lambdaQuery()
                        .eq(ProjEstablishBaseInfoLib::getOriginId, etb.getId())
                        .eq(ProjEstablishBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(ProjEstablishBaseInfoLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));
                if (isNotNull(etbLib)) {
                    ProjEstablishBaseInfoLib updateLib = BeanUtil.copyProperties(updateEtb, ProjEstablishBaseInfoLib.class);
                    updateLib.setId(etbLib.getId());
                    updateEtbLibList.add(updateLib);
                }
            }
            if (!updateEtbList.isEmpty()) {
                etbService.updateBatchById(updateEtbList);
                for (ProjEstablishBaseInfo establishBaseInfo : updateEtbList) {
                    try {
                        ApplicationContextUtil.getApplicationContext().publishEvent(
                                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                                        BusinessModuleEnum.PROJ_ESTABLISH,
                                        establishBaseInfo.getId().toString(),
                                        "客户移交-项目立项数据")
                                )
                        );
                    } catch (Exception e) {
                        log.error(String.format("客户移交-项目立项[%s]消息推送问题", establishBaseInfo.getProjName()));
                    }
                }
            }
            if (!updateEtbLibList.isEmpty()) {
                etbLibService.updateBatchById(updateEtbLibList);
            }
            //评审列表
            List<ProjReviewBaseInfo> rvList = new ArrayList<>();
            if (!clientIdList.isEmpty()) {
                rvList = rvService.listByClients(clientIdList);
            }
            //公海评审
            if (!groupIdList.isEmpty()) {
                List<ProjReviewBaseInfo> groupRvList = rvService.listByClients(groupIdList);
                if (!groupRvList.isEmpty()) {
                    for (ProjReviewBaseInfo baseInfo : groupRvList) {
                        if (record.getBelongSponsorId().equals(baseInfo.getProjSponsorUserId())) {
                            rvList.add(baseInfo);
                        }
                    }
                }
            }
            List<Long> projectReviewIds = rvList.stream().map(ProjReviewBaseInfo::getId).collect(toList());
            List<ProjReviewBaseInfo> updateRvList = new ArrayList<>(rvList.size());
            List<ProjReviewBaseInfoLib> updateRvLibList = new ArrayList<>(rvList.size());
            for (ProjReviewBaseInfo rv : rvList) {
                ProjReviewBaseInfo updateRv = new ProjReviewBaseInfo();
                updateRv.setId(rv.getId());
                updateRv.setProjSponsorUserId(record.getToSponsorId());
                if (CollUtil.isNotEmpty(record.getToCosponsorIds())) {
                    updateRv.setProjCosponsorUserIds(JSONUtil.toJsonStr(record.getToCosponsorIds()));
                }
                updateRv.setBizDeptId(record.getToDeptId());
                updateRv.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.businesshead.name()));
                updateRv.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.leaderincharge.name()));
                // 会更新null值的需要回填数据，不然会丢失
                updateRv.setGuaranteeInfo(rv.getGuaranteeInfo());
                updateRv.setDebtorInfo(rv.getDebtorInfo());
                updateRv.setPledgorInfo(rv.getPledgorInfo());
                updateRv.setMortgagorInfo(rv.getMortgagorInfo());

                updateRvList.add(updateRv);
                //
                ProjReviewBaseInfoLib rvLib = rvLibService.getOne(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery()
                        .eq(ProjReviewBaseInfoLib::getOriginId, rv.getId())
                        .eq(ProjReviewBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(ProjReviewBaseInfoLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));
                if (isNotNull(rvLib)) {
                    ProjReviewBaseInfoLib updateLib = BeanUtil.copyProperties(updateRv, ProjReviewBaseInfoLib.class);
                    updateLib.setId(rvLib.getId());
                    updateRvLibList.add(updateLib);
                }
            }
            if (!updateRvList.isEmpty()) {
                rvService.updateBatchById(updateRvList);
                for (ProjReviewBaseInfo reviewBaseInfo : updateRvList) {
                    try {
                        ApplicationContextUtil.getApplicationContext().publishEvent(
                                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                                        BusinessModuleEnum.PROJ_REVIEW,
                                        reviewBaseInfo.getId().toString(),
                                        "客户移交-项目评审数据")
                                )
                        );
                    } catch (Exception e) {
                        log.error(String.format("客户移交-项目评审[%s]消息推送问题", reviewBaseInfo.getProjName()));
                    }
                }
            }
            if (!updateRvLibList.isEmpty()) {
                rvLibService.updateBatchById(updateRvLibList);
            }

            //定价列表
            List<ProjPricingBaseInfo> pricingList = new ArrayList<>();
            if (!clientIdList.isEmpty()) {
                pricingList = pricingBaseInfoService.listByClients(clientIdList);
            }
            //公海定价
            if (!groupIdList.isEmpty()) {
                List<ProjPricingBaseInfo> groupPricingList = pricingBaseInfoService.listByClients(groupIdList);
                if (!groupPricingList.isEmpty()) {
                    for (ProjPricingBaseInfo baseInfo : groupPricingList) {
                        if (record.getBelongSponsorId().equals(baseInfo.getProjSponsorUserId())) {
                            pricingList.add(baseInfo);
                        }
                    }
                }
            }
            List<Long> pricingIds = pricingList.stream().map(ProjPricingBaseInfo::getId).collect(toList());
            List<ProjPricingBaseInfo> updatePricingList = new ArrayList<>(pricingList.size());
            List<ProjPricingBaseInfoLib> updatePricingLibList = new ArrayList<>(pricingList.size());
            for (ProjPricingBaseInfo pricing : pricingList) {
                ProjPricingBaseInfo updatePricing = new ProjPricingBaseInfo();
                updatePricing.setId(pricing.getId());
                updatePricing.setProjSponsorUserId(record.getToSponsorId());
                if (CollUtil.isNotEmpty(record.getToCosponsorIds())) {
                    updatePricing.setProjCosponsorUserIds(JSONUtil.toJsonStr(record.getToCosponsorIds()));
                }
                updatePricing.setBizDeptId(record.getToDeptId());
                updatePricing.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.businesshead.name()));
                updatePricing.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.leaderincharge.name()));
                updatePricingList.add(updatePricing);
                //
                ProjPricingBaseInfoLib pricingLib = pricingBaseInfoLibService.getOne(Wrappers.<ProjPricingBaseInfoLib>lambdaQuery()
                        .eq(ProjPricingBaseInfoLib::getOriginId, pricing.getId())
                        .eq(ProjPricingBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(ProjPricingBaseInfoLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));
                if (isNotNull(pricingLib)) {
                    ProjPricingBaseInfoLib updateLib = BeanUtil.copyProperties(updatePricing, ProjPricingBaseInfoLib.class);
                    updateLib.setId(updateLib.getId());
                    updatePricingLibList.add(updateLib);
                }
            }
            if (!updatePricingList.isEmpty()) {
                pricingBaseInfoService.updateBatchById(updatePricingList);
                for (ProjPricingBaseInfo pricingBaseInfo : updatePricingList) {
                    try {
                        ApplicationContextUtil.getApplicationContext().publishEvent(
                                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                                        BusinessModuleEnum.PROJ_PRICING,
                                        pricingBaseInfo.getId().toString(),
                                        "客户移交-项目定价数据")
                                )
                        );
                    } catch (Exception e) {
                        log.error(String.format("客户移交-项目定价[%s]消息推送问题", pricingBaseInfo.getProjName()));
                    }
                }
            }
            if (!updatePricingLibList.isEmpty()) {
                pricingBaseInfoLibService.updateBatchById(updatePricingLibList);
            }

            //合同列表
            List<ContractBaseInfo> contractList = new ArrayList<>();
            if (!clientIdList.isEmpty()) {
                contractList = contractBaseInfoService.listByClients(clientIdList);
            }
            //公海合同
            if (!groupIdList.isEmpty()) {
                List<ContractBaseInfo> groupContractList = contractBaseInfoService.listByClients(groupIdList);
                if (!groupContractList.isEmpty()) {
                    for (ContractBaseInfo baseInfo : groupContractList) {
                        if (record.getBelongSponsorId().equals(baseInfo.getProjSponsorUserId())) {
                            contractList.add(baseInfo);
                        }
                    }
                }
            }
            List<ContractBaseInfo> updateContractList = new ArrayList<>(contractList.size());
            List<ContractBaseInfoLib> updateContractLibList = new ArrayList<>(contractList.size());
            List<CommonProcessPrepare> commonProcessPrepareList = new ArrayList<>();
            List<ClientTransferWeight> clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                    .in(ClientTransferWeight::getClientId, allList)
                    .eq(ClientTransferWeight::getBatchNo, batchNo)
                    .eq(ClientTransferWeight::getDeleted, 0));
            for (ContractBaseInfo contract : contractList) {
                ContractBaseInfo updateContract = new ContractBaseInfo();
                updateContract.setId(contract.getId());
                for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                    if (StringUtils.isNotBlank(contract.getContractCode())
                            && contract.getContractCode().equalsIgnoreCase(clientTransferWeight.getContractCode())) {
                        if (CollUtil.isNotEmpty(clientTransferWeight.getToCosponsorIds())) {
                            updateContract.setProjCosponsorUserIds(JSONUtil.toJsonStr(clientTransferWeight.getToCosponsorIds()));
                        }
                    }
                }
                updateContract.setProjSponsorUserId(record.getToSponsorId());
                updateContract.setBizDeptId(record.getToDeptId());
                updateContract.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.businesshead.name()));
                updateContract.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(record.getToDeptId(), JobEnum.leaderincharge.name()));
                updateContractList.add(updateContract);
                //
                ContractBaseInfoLib contractLib = contractLibService.getOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                        .eq(ContractBaseInfoLib::getOriginId, contract.getId())
                        .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                        .orderByDesc(ContractBaseInfoLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));
                if (isNotNull(contractLib)) {
                    ContractBaseInfoLib updateLib = BeanUtil.copyProperties(updateContract, ContractBaseInfoLib.class);
                    updateLib.setId(contractLib.getId());
                    updateContractLibList.add(updateLib);
                }

                KpiProjectDistribution kpiProjectDistribution = kpiProjectDistributionService.getOneByContractId(contract.getId());
                if (Objects.isNull(kpiProjectDistribution)) {
                    log.info("没有找到合同对应的项目分配信息");
                    continue;
                }
                //项目分配表，已分配处理
                if (Objects.equals(kpiProjectDistribution.getDistributionStatus(), YesOrNoNumberEnum.YES.getCode()) && Objects.equals(ProcessStatus.APPROVAL_PASS.name(), kpiProjectDistribution.getApprovalStatus())) {
                    Long clientId = contract.getClientId();
                    //校验客户下的合同是否在今年之前全部结清（不包括今年）
                    //查询客户下的所有结清/作废合同并且结清日期在今年之前
                    List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .eq(ContractBaseInfo::getClientId,clientId)
                            .and(w->w.eq(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE)
                                    .or()
                                    .eq(ContractBaseInfo::getContractStatus, ContractStatus.INVALID))
                            .lt(ContractBaseInfo::getSettleTime,LocalDate.of(LocalDate.now().getYear(),1,1)));
                    //查询该客户下的所有合同
                    List<ContractBaseInfo> contractBaseInfoCount = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                                    .eq(ContractBaseInfo::getClientId,clientId));
                    //如果该客户下的合同全部在今年之前结清，跳过
                    if(contractBaseInfos.size()!=contractBaseInfoCount.size()){
                        CommonProcessPrepare commonProcessPrepare = buildCommonProcessPrepare(kpiProjectDistribution.getId(), record.getId(), contract.getContractCode(), record.getToSponsorId());
                        commonProcessPrepareList.add(commonProcessPrepare);
                        SpringUtil.getBean(ClientTransferService.class).handlerDistributionDeptWeight(kpiProjectDistribution, record);
                        reCaculateWeightInfo(kpiProjectDistribution, record);
                    }
                }
                //项目分配表，未分配处理
                if (Objects.equals(kpiProjectDistribution.getDistributionStatus(), YesOrNoNumberEnum.NO.getCode()) && Objects.equals(ProcessStatus.UN_SUBMIT.name(), kpiProjectDistribution.getApprovalStatus())) {
                    SpringUtil.getBean(ClientTransferService.class).handlerDeptWeight(kpiProjectDistribution, record);
                    reCaculateWeightInfoUndistributed(kpiProjectDistribution, record);
                }
            }
            if (!updateContractList.isEmpty()) {
                contractBaseInfoService.updateBatchById(updateContractList);
                for (ContractBaseInfo contractBaseInfo : updateContractList) {
                    try {
                        ApplicationContextUtil.getApplicationContext().publishEvent(
                                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                                        BusinessModuleEnum.CONTRACT,
                                        contractBaseInfo.getId().toString(),
                                        "客户移交-合同数据")
                                )
                        );
                    } catch (Exception e) {
                        log.error(String.format("客户移交-合同[%s]消息推送问题", contractBaseInfo.getContractCode()));
                    }
                }
            }
            if (!updateContractLibList.isEmpty()) {
                contractLibService.updateBatchById(updateContractLibList);
            }
            if (!commonProcessPrepareList.isEmpty()) {
                prepareService.saveBatch(commonProcessPrepareList);
            }
            // 租后检查项目，理论上待移交数据范围和客户数据一致
            if (CollectionUtil.isNotEmpty(rvList)) {
                List<NewAfterLeaseCheckPlanClient> checkPlanClientList = new ArrayList<>();
                if (!clientIdList.isEmpty()) {
                    checkPlanClientList = afterLeaseCheckPlanClientService.listByClientIds(clientIdList);
                }
                //公海租后检查
                if (!groupIdList.isEmpty()) {
                    List<NewAfterLeaseCheckPlanClient> groupCheckPlanClientList = afterLeaseCheckPlanClientService.listByClientIds(groupIdList);
                    if (!groupCheckPlanClientList.isEmpty()) {
                        for (NewAfterLeaseCheckPlanClient planClient : groupCheckPlanClientList) {
                            if (record.getBelongSponsorId().equals(planClient.getBelongSponsorId())) {
                                checkPlanClientList.add(planClient);
                            }
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(checkPlanClientList)) {
                    List<NewAfterLeaseCheckPlanClient> updateCheckClientList = new ArrayList<>(checkPlanClientList.size());
                    List<NewAfterLeaseCheckPlanClientLib> updateCheckClientLibList = new ArrayList<>(checkPlanClientList.size());
                    for (NewAfterLeaseCheckPlanClient checkProject : checkPlanClientList) {
                        NewAfterLeaseCheckPlanClient updateCheckClient = new NewAfterLeaseCheckPlanClient();
                        updateCheckClient.setId(checkProject.getId());
                        updateCheckClient.setBelongSponsorId(record.getToSponsorId());
                        updateCheckClient.setBelongDeptId(record.getToDeptId());
                        updateCheckClientList.add(updateCheckClient);
                        NewAfterLeaseCheckPlanClientLib checkClientLib = afterLeaseCheckPlanClientLibService.getOne(Wrappers.<NewAfterLeaseCheckPlanClientLib>lambdaQuery()
                                .eq(NewAfterLeaseCheckPlanClientLib::getOriginId, checkProject.getId())
                                .eq(NewAfterLeaseCheckPlanClientLib::getVersionType, VersionTypeConstants.NORMAL)
                                .orderByDesc(NewAfterLeaseCheckPlanClientLib::getVersion)
                                .last(StringUtil.mysqlLimitOne()));
                        if (isNotNull(checkClientLib)) {
                            checkClientLib.setBelongSponsorId(record.getToSponsorId());
                            checkClientLib.setBelongDeptId(record.getToDeptId());
                            updateCheckClientLibList.add(checkClientLib);
                        }
                    }
                    if (CollectionUtil.isNotEmpty(updateCheckClientList)) {
                        afterLeaseCheckPlanClientService.updateBatchById(updateCheckClientList);
                    }
                    if (CollectionUtil.isNotEmpty(updateCheckClientLibList)) {
                        afterLeaseCheckPlanClientLibService.updateBatchById(updateCheckClientLibList);
                    }
                }
            }
            // 租后检查-外部查询，理论上待移交数据范围和客户数据一致
            if (CollectionUtil.isNotEmpty(rvList)) {
                List<NewAfterLeaseCheckExternalQuery> checkPlanClientList = new ArrayList<>();
                if (!clientIdList.isEmpty()) {
                    checkPlanClientList = afterLeaseCheckExternalQueryService.getBaseMapper().selectList(Wrappers.<NewAfterLeaseCheckExternalQuery>lambdaQuery()
                            .in(NewAfterLeaseCheckExternalQuery::getClientId, clientIdList)
                    );
                }
                //公海租后检查-外部查询
                if (!groupIdList.isEmpty()) {
                    List<NewAfterLeaseCheckExternalQuery> groupExternal = afterLeaseCheckExternalQueryService.getBaseMapper().selectList(Wrappers.<NewAfterLeaseCheckExternalQuery>lambdaQuery()
                            .in(NewAfterLeaseCheckExternalQuery::getClientId, groupIdList)
                    );
                    if (!groupExternal.isEmpty()) {
                        for (NewAfterLeaseCheckExternalQuery leaseCheckExternalQuery : groupExternal) {
                            if (record.getBelongSponsorId().equals(leaseCheckExternalQuery.getSponsorUserId())) {
                                checkPlanClientList.add(leaseCheckExternalQuery);
                            }
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(checkPlanClientList)) {
                    for (NewAfterLeaseCheckExternalQuery checkExternalQuery : checkPlanClientList) {
                        LambdaUpdateWrapper<NewAfterLeaseCheckExternalQuery> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(NewAfterLeaseCheckExternalQuery::getId, checkExternalQuery.getId());
                        updateWrapper.set(NewAfterLeaseCheckExternalQuery::getSponsorUserId, record.getToSponsorId());
                        updateWrapper.set(NewAfterLeaseCheckExternalQuery::getDeptId, record.getToDeptId());
                        afterLeaseCheckExternalQueryService.getBaseMapper().update(null, updateWrapper);
                    }
                }
            }
            log.info("batchNo为：{}的记录移交客户、立项、评审、合同、租后检查完成", batchNo);
        }
    }

    public void handlerDistributionDeptWeight(KpiProjectDistribution kpiProjectDistribution, ClientTransfer record) {
        // 如果现在的版本表存在目标部门的分润，则啥也不干，否则插入一条为0的记录
        if (SpringUtil.getBean(KpiProjectDistributionDeptWeightService.class).getBaseMapper().selectCount(Wrappers.<KpiProjectDistributionDeptWeight>lambdaQuery()
                .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, kpiProjectDistribution.getId())
                .eq(KpiProjectDistributionDeptWeight::getWeightTarget, record.getToDeptId())
        ) > 0) {
            // do nothing there
        } else {
            SpringUtil.getBean(ClientTransferService.class).handlerDeptWeight(kpiProjectDistribution, record);
        }
    }

    public void handlerDeptWeight(KpiProjectDistribution kpiProjectDistribution, ClientTransfer record) {
        KpiProjectDistributionDeptWeightService deptWeightService = SpringUtil.getBean(KpiProjectDistributionDeptWeightService.class);
        // 删除原有的分配权重
        deptWeightService.remove(Wrappers.<KpiProjectDistributionDeptWeight>lambdaQuery()
                .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, kpiProjectDistribution.getId()));
        KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
        deptWeight.setProjectDistributionId(kpiProjectDistribution.getId());
        deptWeight.setWeightTarget(record.getToDeptId());
        deptWeight.setWeightValue(1000000);
        deptWeightService.save(deptWeight);
    }

    private StartProcessReq buildCommonStartProcessReq(List<ClientTransfer> clientTransferList) {
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        String currentUserName = sysUserService.getUserName(currentUserId);
        StartProcessReq startProcessReq = new StartProcessReq();
        String batchNo = clientTransferList.get(0).getBatchNo();
        startProcessReq.setBusinessKey(batchNo);
        // 审批流列表表单名称展示
        StringBuilder stringBuilder = new StringBuilder();
        ClientTransfer firstClientTransfer = clientTransferList.get(0);
        stringBuilder.append(currentUserName).append("发起的客户移交流程-").append(firstClientTransfer.getClientName());
        if (clientTransferList.size() > 1) {
            stringBuilder.append("等").append(clientTransferList.size()).append("个客户");
        }
        startProcessReq.setProcessInstanceName(stringBuilder.toString());
        startProcessReq.setStartUserId(currentUserId.toString());
        Long deptId = null;
        Long belongSponsorId = clientTransferList.get(0).getBelongSponsorId();
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(belongSponsorId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        }
        err(isNull(deptId), "原主办的部门不存在");
        startProcessReq.setStartUserDeptId(deptId.toString());
        Long finalDeptId = deptId;
        Long originDeptLeader = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.businesshead.name());
        Long originDivisionLeader = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.leaderincharge.name());
        Long targetDeptLeader = sysUserService.getUserIdByOrgJob(clientTransferList.get(0).getToDeptId(), JobEnum.businesshead.name());
        Long targetDivisionLeader = sysUserService.getUserIdByOrgJob(clientTransferList.get(0).getToDeptId(), JobEnum.leaderincharge.name());

        //如果原部门和目标部门的审批人是一样的，则审批一次就行。通过设置空的审批人来实现。
        if (equal(originDeptLeader, targetDeptLeader)) {
            targetDeptLeader = null;
        }
        if (equal(originDeptLeader, targetDivisionLeader)) {
            targetDivisionLeader = null;
        }

        startProcessReq.setVariables(MapUtil.of(
                Pair.of("receiver", toList(String.valueOf(clientTransferList.get(0).getToSponsorId()))),
                Pair.of("originDeptLeader", Objects.isNull(originDeptLeader) ? new ArrayList<>() : toList(String.valueOf(originDeptLeader))),
                Pair.of("originDivisionLeader", Objects.isNull(originDivisionLeader) ? new ArrayList<>() : toList(String.valueOf(originDivisionLeader))),
                Pair.of("targetDeptLeader", Objects.isNull(targetDeptLeader) ? new ArrayList<>() : toList(String.valueOf(targetDeptLeader))),
                Pair.of("targetDivisionLeader", Objects.isNull(targetDivisionLeader) ? new ArrayList<>() : toList(String.valueOf(targetDivisionLeader)))
        ));
        return startProcessReq;
    }


    private StartProcessReq buildNewCommonStartProcessReq(List<ClientTransfer> clientTransferList) {
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        String currentUserName = sysUserService.getUserName(currentUserId);
        StartProcessReq startProcessReq = new StartProcessReq();
        String batchNo = clientTransferList.get(0).getBatchNo();
        startProcessReq.setBusinessKey(batchNo);
        // 审批流列表表单名称展示
        StringBuilder stringBuilder = new StringBuilder();
        ClientTransfer firstClientTransfer = clientTransferList.get(0);
        stringBuilder.append(currentUserName).append("发起的客户移交流程-").append(firstClientTransfer.getClientName());
        if (clientTransferList.size() > 1) {
            stringBuilder.append("等").append(clientTransferList.size()).append("个客户");
        }
        startProcessReq.setProcessInstanceName(stringBuilder.toString());
        startProcessReq.setStartUserId(currentUserId.toString());
        Long deptId = null;
        Long belongSponsorId = clientTransferList.get(0).getBelongSponsorId();
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(belongSponsorId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        }
        err(isNull(deptId), "原主办的部门不存在");
        startProcessReq.setStartUserDeptId(deptId.toString());
        Long finalDeptId = deptId;
        Long originDeptLeader = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.businesshead.name());
        Long originDivisionLeader = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.leaderincharge.name());
        Long targetDeptLeader = sysUserService.getUserIdByOrgJob(clientTransferList.get(0).getToDeptId(), JobEnum.businesshead.name());
        Long targetDivisionLeader = sysUserService.getUserIdByOrgJob(clientTransferList.get(0).getToDeptId(), JobEnum.leaderincharge.name());
        //移交是否同一个部门
        boolean isSameDept = (finalDeptId.equals(clientTransferList.get(0).getToDeptId()));
        //人力资源部负责人
        Long rlzybDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader("RLZYB", "人力资源部", humanresourcessupervisor.name());
        //运营管理部负责人
        Long yyglbDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader("YYGLB", FlowAssistService.YYGLB_DESC, businesshead.name());
        //如果原部门和目标部门的审批人是一样的，则审批一次就行。通过设置空的审批人来实现。
        if (equal(originDeptLeader, targetDeptLeader)) {
            targetDeptLeader = null;
        }
        if (equal(originDeptLeader, targetDivisionLeader)) {
            targetDivisionLeader = null;
        }

        startProcessReq.setVariables(MapUtil.of(
                Pair.of("isSameDept", isSameDept ? 1 : 0),
                Pair.of("receiver", toList(String.valueOf(clientTransferList.get(0).getToSponsorId()))),
                Pair.of("originDeptLeader", Objects.isNull(originDeptLeader) ? new ArrayList<>() : toList(String.valueOf(originDeptLeader))),
                Pair.of("originDivisionLeader", Objects.isNull(originDivisionLeader) ? new ArrayList<>() : toList(String.valueOf(originDivisionLeader))),
                Pair.of("rlzybDeptLeader", Objects.isNull(rlzybDeptLeader) ? new ArrayList<>() : toList(String.valueOf(rlzybDeptLeader))),
                Pair.of("yyglbDeptLeader", Objects.isNull(yyglbDeptLeader) ? new ArrayList<>() : toList(String.valueOf(yyglbDeptLeader))),
                //不同部门
                Pair.of("receiver", toList(String.valueOf(clientTransferList.get(0).getToSponsorId()))),
                Pair.of("originDeptLeader", Objects.isNull(originDeptLeader) ? new ArrayList<>() : toList(String.valueOf(originDeptLeader))),
                Pair.of("originDivisionLeader", Objects.isNull(originDivisionLeader) ? new ArrayList<>() : toList(String.valueOf(originDivisionLeader))),
                Pair.of("targetDeptLeader", Objects.isNull(targetDeptLeader) ? new ArrayList<>() : toList(String.valueOf(targetDeptLeader))),
                Pair.of("targetDivisionLeader", Objects.isNull(targetDivisionLeader) ? new ArrayList<>() : toList(String.valueOf(targetDivisionLeader)))
        ));
        return startProcessReq;
    }

    //项目分配表重新分配
    private void reCaculateWeightInfoUndistributed(KpiProjectDistribution kpiProjectDistribution, ClientTransfer record) {
        //清理原分配信息
        /*kpiProjectDistributionWeightService.remove(Wrappers.<KpiProjectDistributionWeight>lambdaQuery()
        .eq(KpiProjectDistributionWeight::getProjectDistributionId, kpiProjectDistribution.getId()));*/
        //处理原数据
        List<KpiProjectDistributionWeight> weightList = kpiProjectDistributionWeightService.listByProjectDistributionId(kpiProjectDistribution.getId());
        List<ClientTransferWeight> clientTransferWeightList = transferWeightMapper.selectList(Wrappers.<ClientTransferWeight>lambdaQuery()
                .eq(ClientTransferWeight::getClientId, record.getClientId())
                .eq(ClientTransferWeight::getBatchNo, record.getBatchNo())
                .eq(ClientTransferWeight::getDeleted, 0));
        Set<Long> newToCosponsorIds = new HashSet<>();
        Set<Long> prevToCosponsorIds = new HashSet<>();
        Set<Long> prevSponsorIds = new HashSet<>();
        Set<Long> prevDeptIds = new HashSet<>();
        if (kpiProjectDistribution.getContractId() != null) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(kpiProjectDistribution.getContractId());
            for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
                if (contractBaseInfo.getContractCode().equalsIgnoreCase(clientTransferWeight.getContractCode())) {
                    List<Long> toCosponsorIds = clientTransferWeight.getToCosponsorIds();
                    if (toCosponsorIds != null && !toCosponsorIds.isEmpty()) {
                        newToCosponsorIds.addAll(toCosponsorIds);
                    }
                }
            }
        }
        for (KpiProjectDistributionWeight weight : weightList) {
            if (KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name().equalsIgnoreCase(weight.getWeightType())) {
                prevToCosponsorIds.add(Long.valueOf(weight.getWeightTarget()));
            } else if (KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name().equalsIgnoreCase(weight.getWeightType())) {
                prevSponsorIds.add(Long.valueOf(weight.getWeightTarget()));
            } else if (KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equalsIgnoreCase(weight.getWeightType())) {
                prevDeptIds.add(Long.valueOf(weight.getWeightTarget()));
            }
        }
        List<KpiProjectDistributionWeight> cosponsorRes = new ArrayList<>();
        for (KpiProjectDistributionWeight weight : weightList) {
            KpiProjectWeightTypeEnum weightTypeEnum = KpiProjectWeightTypeEnum.find(weight.getWeightType());
            if (Objects.isNull(weightTypeEnum)) {
                throw new MithrasException("未知的分配比重类型[" + weight.getWeightType() + "]");
            }
            if (KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name().equalsIgnoreCase(weight.getWeightType())) {
                if (weight.getWeightTarget().equalsIgnoreCase(record.getBelongSponsorId().toString())) {
                    weight.setWeightValue(500000);
                } else {
                    weight.setWeightValue(null);
                }
            } else if (KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equalsIgnoreCase(weight.getWeightType())) {
                if (!prevDeptIds.contains(record.getToDeptId())) {
                    //新的部门
                    KpiProjectDistributionWeight toModel = new KpiProjectDistributionWeight();
                    toModel.setProjectDistributionId(kpiProjectDistribution.getId());
                    toModel.setWeightTarget(record.getToDeptId().toString());
                    toModel.setWeightType(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name());
                    toModel.setWeightValue(null);
                    kpiProjectDistributionWeightService.save(toModel);
                } else {
                    weight.setWeightValue(null);
                }
            } else if (KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name().equalsIgnoreCase(weight.getWeightType())) {
                //List<Long> toCosponsorIds = record.getToCosponsorIds();
                //之前2位协办移交后去掉一位，不需要新增协办信息
                if (!prevToCosponsorIds.isEmpty() && !newToCosponsorIds.isEmpty() && prevToCosponsorIds.containsAll(newToCosponsorIds)) {
                    weight.setWeightValue(null);
                    continue;
                }
                if (!newToCosponsorIds.isEmpty()) {
                    //旧的协办为空，或者旧的协办不包括新的协办
                    for (Long toCosponsorId : newToCosponsorIds) {
                        if (prevToCosponsorIds.isEmpty() || !prevToCosponsorIds.contains(toCosponsorId)) {
                            KpiProjectDistributionWeight toModel = new KpiProjectDistributionWeight();
                            toModel.setProjectDistributionId(kpiProjectDistribution.getId());
                            toModel.setWeightTarget(String.valueOf(toCosponsorId));
                            toModel.setWeightType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
                            toModel.setWeightValue(null);
                            cosponsorRes.add(toModel);
                        }
                    }
                } else {
                    weight.setWeightValue(null);
                }
            }
        }
        //新主办
        if (!prevSponsorIds.contains(record.getToSponsorId())) {
            KpiProjectDistributionWeight toModel = new KpiProjectDistributionWeight();
            toModel.setProjectDistributionId(kpiProjectDistribution.getId());
            toModel.setWeightTarget(record.getToSponsorId().toString());
            toModel.setWeightType(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
            toModel.setWeightValue(500000);
            kpiProjectDistributionWeightService.save(toModel);
            kpiProjectDistributionWeightService.updateBatchById(weightList);
        }
        //旧协办为空创建新的协办
        if (prevToCosponsorIds.isEmpty()) {
            //List<Long> toCosponsorIds = record.getToCosponsorIds();
            if (!newToCosponsorIds.isEmpty()) {
                for (Long toCosponsorId : newToCosponsorIds) {
                    KpiProjectDistributionWeight newModel = new KpiProjectDistributionWeight();
                    newModel.setProjectDistributionId(kpiProjectDistribution.getId());
                    newModel.setWeightTarget(String.valueOf(toCosponsorId));
                    newModel.setWeightType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
                    newModel.setWeightValue(null);
                    kpiProjectDistributionWeightService.save(newModel);
                }
            }
        }
        //旧的协办为空，或者旧的协办不包括新的协办
        List<KpiProjectDistributionWeight> finalCosponsorRes = new ArrayList<>();
        if (!cosponsorRes.isEmpty()) {
            Set<String> set = new HashSet<>();
            for (KpiProjectDistributionWeight cosponsor : cosponsorRes) {
                if (!set.contains(cosponsor.getWeightTarget())) {
                    finalCosponsorRes.add(cosponsor);
                    set.add(cosponsor.getWeightTarget());
                }
            }
            if (!finalCosponsorRes.isEmpty()) {
                for (KpiProjectDistributionWeight newModel : finalCosponsorRes) {
                    kpiProjectDistributionWeightService.save(newModel);
                }
            }
        }
    }

    private void reCaculateWeightInfo(KpiProjectDistribution kpiProjectDistribution, ClientTransfer record) {
        List<KpiProjectDistributionWeight> weightList = kpiProjectDistributionWeightService.listByProjectDistributionId(kpiProjectDistribution.getId());
        List<KpiProjectDistributionWeight> toInsertList = new LinkedList<>();
        List<KpiProjectDistributionWeight> toUpdateList = new LinkedList<>();
        boolean hasCosponsorMatch = false;
        for (KpiProjectDistributionWeight weight : weightList) {
            KpiProjectWeightTypeEnum weightTypeEnum = KpiProjectWeightTypeEnum.find(weight.getWeightType());
            if (Objects.isNull(weightTypeEnum)) {
                throw new MithrasException("未知的分配比重类型[" + weight.getWeightType() + "]");
            }
            if (KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name().equalsIgnoreCase(weight.getWeightType())
                    && weight.getWeightTarget().equalsIgnoreCase(record.getBelongSponsorId().toString())) {
                KpiProjectDistributionWeight dbModel = new KpiProjectDistributionWeight();
                dbModel.setProjectDistributionId(kpiProjectDistribution.getId());
                dbModel.setWeightTarget(record.getToSponsorId().toString());
                dbModel.setWeightType(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
                if (weight.getWeightValue() != null) {
                    dbModel.setWeightValue(weight.getWeightValue() / 2);
                    weight.setWeightValue(weight.getWeightValue() / 2);
                    toUpdateList.add(weight);
                }
                toInsertList.add(dbModel);
            } else if (KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name().
                    equalsIgnoreCase(weight.getWeightType())) {
                int size = 0;
                if (record.getToCosponsorIds() != null
                        && !record.getToCosponsorIds().isEmpty()) {
                    size = record.getToCosponsorIds().size();
                }
                if (size > 0) {
                    hasCosponsorMatch = true;
                    for (Long toCosponsorId : record.getToCosponsorIds()) {
                        KpiProjectDistributionWeight dbModel = new KpiProjectDistributionWeight();
                        dbModel.setProjectDistributionId(kpiProjectDistribution.getId());
                        dbModel.setWeightTarget(toCosponsorId.toString());
                        dbModel.setWeightType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
                        if (weight.getWeightValue() != null) {
                            dbModel.setWeightValue(weight.getWeightValue() / (size + 1));
                            weight.setWeightValue(weight.getWeightValue() / (size + 1));
                            toUpdateList.add(weight);
                        }
                        toInsertList.add(dbModel);
                    }
                }
            }
        }
        //原无协办，移交后有协办
        if (!hasCosponsorMatch && record.getToCosponsorIds() != null && !record.getToCosponsorIds().isEmpty()) {
            for (Long toCosponsorId : record.getToCosponsorIds()) {
                KpiProjectDistributionWeight dbModel = new KpiProjectDistributionWeight();
                dbModel.setProjectDistributionId(kpiProjectDistribution.getId());
                dbModel.setWeightTarget(toCosponsorId.toString());
                dbModel.setWeightType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
                toInsertList.add(dbModel);
            }
        }
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            this.kpiProjectDistributionWeightService.saveBatch(toInsertList);
        }
        if (CollectionUtil.isNotEmpty(toUpdateList)) {
            this.kpiProjectDistributionWeightService.updateBatchById(toUpdateList);
        }
        // 项目分配表状态变为待提交
        KpiProjectDistribution toUpdate = new KpiProjectDistribution();
        toUpdate.setId(kpiProjectDistribution.getId());
        toUpdate.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
        kpiProjectDistributionService.updateById(toUpdate);
    }

    private CommonProcessPrepare buildCommonProcessPrepare(Long projectDistributionId, Long clientTransferId, String contractCode, Long toSponsorId) {
        String name = id2NameService.sysUserId2NameSingle(toSponsorId);
        List<Long> currentAssignee = new ArrayList<>();
        currentAssignee.add(toSponsorId);
        CommonProcessPrepare toUpdate = CommonProcessPrepare.builder().processType(ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name()).
                businessId(projectDistributionId.toString()).businessData(clientTransferId.toString()).formName(contractCode + "-" + ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.getDisplay()).currentNode(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.display() + "-" + name).currentAssignee(currentAssignee.toString()).
                applyTime(LocalDateTime.now()).status(CommonProcessPrepareStatus.PEND_COMMIT.name()).build();
        return toUpdate;
    }

    //清除历史遗留数据
    public List<ClientTransferWeight> removeUnusedClientTransferWeight(List<ClientTransferWeight> clientTransferWeightList) {
        TreeMap<String, List<ClientTransferWeight>> map = new TreeMap<>();
        List<ClientTransferWeight> res = new ArrayList<>();
        for (ClientTransferWeight weight : clientTransferWeightList) {
            if (StringUtils.isNotBlank(weight.getContractCode())) {
                map.putIfAbsent(weight.getContractCode(), new ArrayList<>());
                map.get(weight.getContractCode()).add(weight);
            }
        }
        if (!map.isEmpty()) {
            for (Map.Entry<String, List<ClientTransferWeight>> entry : map.entrySet()) {
                List<ClientTransferWeight> weightList = entry.getValue();
                if (weightList != null && !weightList.isEmpty() && weightList.size() > 1) {
                    LocalDateTime current = null;
                    for (ClientTransferWeight weight : weightList) {
                        LocalDateTime key = weight.getCreateTime();
                        if (current == null) {
                            current = weight.getCreateTime();
                        } else {
                            if (current.isBefore(key)) {
                                current = key;
                            }
                        }
                    }
                    for (ClientTransferWeight weight : weightList) {
                        LocalDateTime key = weight.getCreateTime();
                        if (!key.isEqual(current)) {
                            res.add(weight);
                        }
                    }
                }
            }
        }
        return res;
    }

    public List<ClientTransferWeight> getLatestTransferWeight(List<ClientTransferWeight> clientTransferWeightList, List<ClientTransferWeight> unUsed) {
        if (clientTransferWeightList.isEmpty()) {
            return clientTransferWeightList;
        }
        if (unUsed.isEmpty()) {
            return clientTransferWeightList;
        }
        List<ClientTransferWeight> res = new ArrayList<>();
        for (ClientTransferWeight clientTransferWeight : clientTransferWeightList) {
            boolean found = false;
            for (ClientTransferWeight unusedWeight : unUsed) {
                if (clientTransferWeight.getId().equals(unusedWeight.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                res.add(clientTransferWeight);
            }
        }
        return res;
    }
}
