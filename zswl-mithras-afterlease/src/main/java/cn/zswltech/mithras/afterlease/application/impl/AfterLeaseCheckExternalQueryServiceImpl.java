package cn.zswltech.mithras.afterlease.application.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryDto;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListStatisticsRsp;
import cn.zswltech.mithras.afterlease.application.convert.ExternalQueryConverter;
import cn.zswltech.mithras.afterlease.application.job.AfterLeaseExternalQueryCreateJobService;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseProcessEndResult;
import cn.zswltech.mithras.afterlease.enums.ExternalQueryStatus;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckExternalQueryMapper;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckExternalQueryClientInfo;
import cn.zswltech.mithras.afterlease.application.bo.AfterLeaseClientDataBO;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserDataScopeResolver;
import cn.zswltech.mithras.foundation.port.OrgJobUserResolver;
import cn.zswltech.mithras.afterlease.application.AfterLeaseClientPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseClientSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCorpCommercePort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryClientInfoService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkflowPort;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckExternalQueryVersionService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询任务
 * @date 2022-11-17
 */
@Slf4j
@Service
public class AfterLeaseCheckExternalQueryServiceImpl
        extends ServiceImpl<NewAfterLeaseCheckExternalQueryMapper, NewAfterLeaseCheckExternalQuery>
        implements AfterLeaseCheckExternalQueryService, AfterLeaseExternalQueryCreateJobService {
    @Resource
    private CurrentUserDataScopeResolver currentUserDataScopeResolver;
    @Resource
    private OrgJobUserResolver orgJobUserResolver;
    @Resource
    private AfterLeaseWorkflowPort workflowPort;
    @Resource
    private AfterLeaseCheckExternalQueryVersionService afterLeaseCheckExternalQueryVersionService;
    @Resource
    private AfterLeaseCheckExternalQueryClientInfoService clientInfoService;
    @Resource
    private AfterLeaseContractPort afterLeaseContractPort;
    @Resource
    private AfterLeaseCheckReportBaseService projectReportBaseService;
    @Resource
    private ExternalQueryConverter externalQueryConverter;
    @Resource
    private AfterLeaseCorpCommercePort afterLeaseCorpCommercePort;
    @Resource
    private AfterLeaseClientPort afterLeaseClientPort;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void submit(Long id) {
        NewAfterLeaseCheckExternalQuery query = getById(id);
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("bizDeptLeader", Objects.nonNull(query.getBizDeptLeader()) ?
                ListUtil.toList(String.valueOf(query.getBizDeptLeader())) : new ArrayList<>());
        varMap.put("bizDivisionLeader", Objects.nonNull(query.getBizDivisionLeader()) ?
                ListUtil.toList(String.valueOf(query.getBizDivisionLeader())) : new ArrayList<>());

        AfterLeaseWorkflowPort.ExternalQueryApprovalStartContext context =
                new AfterLeaseWorkflowPort.ExternalQueryApprovalStartContext();
        context.setQueryId(id);
        context.setClientName(query.getClientName());
        context.setClientId(query.getClientId());
        context.setDeptId(query.getDeptId());
        context.setVariables(varMap);
        String processInstanceId = workflowPort.startExternalQueryApproval(context);
        query.setProcessInstanceId(processInstanceId);
        query.setApprovalStatus(ExternalQueryStatus.UNDER_APPROVAL.name());
        updateById(query);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long add(NewAfterLeaseCheckExternalQuery req) {
        baseMapper.insert(req);
        return req.getId();
    }

    @Override
    public void createExternalQuery() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime month = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
            Map<Long, List<AfterLeaseContractSnapshot>> clientContractsMap = afterLeaseContractPort.listAllStartRent()
                    .parallelStream().collect(java.util.stream.Collectors.groupingBy(AfterLeaseContractSnapshot::getClientId));
            if (ObjectUtil.isEmpty(clientContractsMap)) {
                return;
            }
            Map<Long, AfterLeaseClientSnapshot> clientMap = afterLeaseClientPort.listByIds(clientContractsMap.keySet());

            List<Long> clientIds = clientMap.values().parallelStream()
                    .map(AfterLeaseClientSnapshot::getClientId)
                    .collect(java.util.stream.Collectors.toList());
            Optional<Map<Long, String>> clientId2industryType =
                    afterLeaseCorpCommercePort.selectIndustryTypeBatchByIds(clientIds);

            Map<Long, AfterLeaseClientDataBO> afterLeaseProjectDataBOs =
                    projectReportBaseService.getAfterLeaseClientDataBO(clientContractsMap);

            clientContractsMap.forEach((clientId, contractBaseInfos) -> {
                try {
                    AfterLeaseClientSnapshot client = clientMap.get(clientId);
                    if (ObjectUtil.isEmpty(client)) {
                        return;
                    }
                    AfterLeaseClientDataBO bo = afterLeaseProjectDataBOs.get(clientId);
                    NewAfterLeaseCheckExternalQuery insertEntity = externalQueryConverter.dataBo2Entity(bo);
                    insertEntity.setInspectionMonth(month);
                    insertEntity.setClientId(clientId);
                    insertEntity.setClientName(client.getClientName());
                    insertEntity.setSponsorUserId(client.getBelongSponsorId());
                    insertEntity.setDeptId(client.getBelongDeptId());
                    Long businessHeadId = firstOrgJobUser(client.getBelongDeptId(), JobEnum.businesshead.name());
                    insertEntity.setBizDeptLeader(businessHeadId);
                    Long leaderId = firstOrgJobUser(client.getBelongDeptId(), JobEnum.leaderincharge.name());
                    insertEntity.setBizDivisionLeader(leaderId);
                    insertEntity.setApprovalStatus(ExternalQueryStatus.TO_BE_QUERY.name());
                    clientId2industryType.ifPresent(longStringMap -> insertEntity.setIndustryType(longStringMap.get(clientId)));
                    Long queryId = this.add(insertEntity);

                    List<NewAfterLeaseCheckExternalQueryClientInfo> clientInfos = new ArrayList<>();
                    List<AfterLeaseContractSnapshot> contractBaseInfoList = clientContractsMap.get(clientId);
                    Map<Long, InnerClientHelper> helperMap = new HashMap<>();
                    for (AfterLeaseContractSnapshot contractBaseInfo : contractBaseInfoList) {
                        afterLeaseContractPort.clientRoleNamesByContractId(contractBaseInfo.getId())
                                .forEach((relatedClientId, roleNames) -> {
                                    helperMap.putIfAbsent(relatedClientId, new InnerClientHelper(relatedClientId, new HashSet<>()));
                                    helperMap.get(relatedClientId).getClientRoleNames().addAll(roleNames);
                                });
                    }
                    if (CollectionUtil.isNotEmpty(helperMap)) {
                        Map<Long, AfterLeaseClientSnapshot> cMap = afterLeaseClientPort.listByIds(helperMap.keySet());
                        for (InnerClientHelper innerClientHelper : helperMap.values()) {
                            AfterLeaseClientSnapshot c = cMap.get(innerClientHelper.getClientId());
                            if (Objects.isNull(c)) {
                                continue;
                            }
                            for (String clientRoleName : innerClientHelper.getClientRoleNames()) {
                                NewAfterLeaseCheckExternalQueryClientInfo clientInfo = new NewAfterLeaseCheckExternalQueryClientInfo();
                                clientInfo.setQueryId(queryId);
                                clientInfo.setClientRole(clientRoleName);
                                clientInfo.setClientId(c.getClientId());
                                clientInfo.setClientType(c.getClientType());
                                clientInfos.add(clientInfo);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(clientInfos)) {
                        clientInfoService.saveBatch(clientInfos);
                    }
                } catch (Exception e) {
                    log.error("租后检查-外部查询任务-生成外部查询任务记录异常[clientId:{}]", clientId, e);
                }
            });
        } catch (Exception e) {
            log.error("租后检查-外部查询任务执行异常", e);
        }
    }

    private Long firstOrgJobUser(Long orgId, String jobCode) {
        List<Long> userIds = orgJobUserResolver.orgJobUsers(orgId, jobCode);
        if (CollectionUtil.isEmpty(userIds)) {
            return null;
        }
        return userIds.get(0);
    }

    @Override
    public Page<NewAfterLeaseCheckExternalQuery> list(AfterLeaseCheckExternalQueryListReq req) {
        AfterLeaseCheckExternalQueryDto dto = new AfterLeaseCheckExternalQueryDto();
        dto.setInspectionMonth(req.getTargetMonth().atStartOfDay());
        dto.setClientName(req.getClientName());
        dto.setApprovalStatus(req.getApprovalStatus());
        dto.setSponsorUserId(req.getSponsorId());
        List<Long> canViewDeptIds = currentUserDataScopeResolver.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        return baseMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
    }

    @Override
    public AfterLeaseCheckExternalQueryListStatisticsRsp listStatistics(AfterLeaseCheckExternalQueryListReq req) {
        AfterLeaseCheckExternalQueryListStatisticsRsp rsp = new AfterLeaseCheckExternalQueryListStatisticsRsp();
        AfterLeaseCheckExternalQueryDto dto = new AfterLeaseCheckExternalQueryDto();
        List<Long> canViewDeptIds = currentUserDataScopeResolver.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());

        dto.setInspectionMonth(req.getTargetMonth().atStartOfDay());
        rsp.setTotalCount(baseMapper.myListStatistics(dto));
        dto.setApprovalStatus(ExternalQueryStatus.APPROVAL_PASS.name());
        rsp.setDoneCount(baseMapper.myListStatistics(dto));
        return rsp;
    }

    @Override
    public List<NewAfterLeaseCheckExternalQuery> listByClientId(Long clientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckExternalQuery> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckExternalQuery::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void submitCheck(Long id) {
        NewAfterLeaseCheckExternalQuery query = getById(id);
        submitCheck(query);
    }

    @Override
    public void submitCheck(NewAfterLeaseCheckExternalQuery query) {
        LocalDateTime today = LocalDateTime.now();
        if (!query.getInspectionMonth().getMonth().equals(today.getMonth())) {
            throw new MithrasException("任务已跨月，不可提交");
        }
        Assert.notNull(query.getInspectionDate(), () -> MithrasException.newException("必填项'检查日期'为空"));
        List<NewAfterLeaseCheckExternalQueryClientInfo> clientInfos =
                clientInfoService.list(Wrappers.<NewAfterLeaseCheckExternalQueryClientInfo>lambdaQuery()
                        .eq(NewAfterLeaseCheckExternalQueryClientInfo::getQueryId, query.getId()));
        for (NewAfterLeaseCheckExternalQueryClientInfo clientInfo : clientInfos) {
            Assert.notNull(clientInfo.getQueryTimeFrom(), () -> MithrasException.newException("必填项'查询区间'为空"));
            Assert.notNull(clientInfo.getQueryTimeTo(), () -> MithrasException.newException("必填项'查询区间'为空"));
        }
    }

    @AllArgsConstructor
    @Getter
    private static class InnerClientHelper {
        private final Long clientId;
        private final Set<String> clientRoleNames;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, AfterLeaseProcessEndResult endResult, Long startUserId, String processInstanceId, String modelKey) {
        // 考虑到还有取消流程操作，因此将数据库操作放在if内
        if (endResult.pass()) {
            NewAfterLeaseCheckExternalQuery query = getById(id);
            query.setApprovalPassTime(LocalDateTime.now());
            query.setApprovalStatus(ExternalQueryStatus.APPROVAL_PASS.name());
            baseMapper.updateById(query);
            // 生成有效版本
            afterLeaseCheckExternalQueryVersionService.recordVersion(
                    id,
                    VersionTypeEnum.APPROVAL,
                    AccountUtil.getLoginInfo().getId(),
                    processInstanceId,
                    VersionTypeConstants.NORMAL
            );
        } else {
            NewAfterLeaseCheckExternalQuery query = getById(id);
            if (endResult.reject()) {
                query.setApprovalStatus(ExternalQueryStatus.APPROVAL_REJECT.name());
            }
            if (endResult.cancel()) {
                query.setApprovalStatus(ExternalQueryStatus.TO_BE_QUERY.name());
            }
            query.setProcessInstanceId(null);
            baseMapper.updateById(query);
            // 生成无效版本
            afterLeaseCheckExternalQueryVersionService.recordVersion(
                    id,
                    VersionTypeEnum.APPROVAL,
                    startUserId,
                    processInstanceId,
                    VersionTypeConstants.INVALID
            );
        }
    }
}
