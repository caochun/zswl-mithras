package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.convert.afterlease.ExternalQueryConverter;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.afterlease.domain.enums.ClientRole;
import cn.zswltech.mithras.afterlease.domain.enums.ExternalQueryStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQueryClientInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryClientInfoService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckExternalQueryService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportBaseService;
import cn.zswltech.mithras.afterlease.application.bo.AfterLeaseClientDataBO;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractGuarantorService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/18 16:44
 */
@Component
@Slf4j
public class AfterLeaseCheckExternalQueryJob {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private AfterLeaseCheckReportBaseService projectReportBaseService;
    @Resource
    private ExternalQueryConverter externalQueryConverter;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private AfterLeaseCheckExternalQueryService externalQueryService;
    @Resource
    private AfterLeaseCheckExternalQueryClientInfoService queryClientInfoService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private SysUserService sysUserService;

    @XxlJob(value = "createExternalQuery")
    public void createExternalQuery() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime month = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
            // 获取起租状态的合同，并按照客户id分组
            Map<Long, List<ContractBaseInfo>> clientContractsMap = contractBaseInfoService.getBaseMapper()
                    .selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()))
                    .parallelStream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
            if (ObjectUtil.isEmpty(clientContractsMap)) {
                return;
            }
            Map<Long, Client> clientMap = clientMapper
                    .selectBatchIds(clientContractsMap.keySet()).stream()
                    .collect(Collectors.toMap(Client::getId, item -> item));

            List<Long> clientIds = clientMap.values().parallelStream()
                    .map(Client::getId)
                    .collect(Collectors.toList());
            Optional<Map<Long, String>> clientId2industryType =
                    corpCommerceInfoService.selectIndustryTypeBatchByIds(clientIds);

            Map<Long, AfterLeaseClientDataBO> afterLeaseProjectDataBOs =
                    projectReportBaseService.getAfterLeaseClientDataBO(clientMap, clientContractsMap);

            clientContractsMap.forEach((clientId, contractBaseInfos) -> {
                try {
                    Client client = clientMap.get(clientId);
                    if (ObjectUtil.isEmpty(client)) {
                        return;
                    }
                    AfterLeaseClientDataBO bo = afterLeaseProjectDataBOs.get(clientId);
                    // 外部查询主表数据
                    NewAfterLeaseCheckExternalQuery insertEntity = externalQueryConverter.dataBo2Entity(bo);
                    insertEntity.setInspectionMonth(month);
                    insertEntity.setClientId(clientId);
                    insertEntity.setClientName(client.getClientName());
                    insertEntity.setSponsorUserId(client.getBelongSponsorId());
                    insertEntity.setDeptId(client.getBelongDeptId());
                    // 查询业务部门负责人
                    Long businessHeadId = sysUserService.getUserIdByOrgJob(client.getBelongDeptId(), JobEnum.businesshead.name());
                    insertEntity.setBizDeptLeader(businessHeadId);
                    // 查询分管领导
                    Long leaderId = sysUserService.getUserIdByOrgJob(client.getBelongDeptId(), JobEnum.leaderincharge.name());
                    insertEntity.setBizDivisionLeader(leaderId);
                    insertEntity.setApprovalStatus(ExternalQueryStatus.TO_BE_QUERY.name());
                    clientId2industryType.ifPresent(longStringMap -> insertEntity.setIndustryType(longStringMap.get(clientId)));
                    Long queryId = externalQueryService.add(insertEntity);

                    // 外部查询子表数据
                    List<NewAfterLeaseCheckExternalQueryClientInfo> clientInfos = new ArrayList<>();
                    List<ContractBaseInfo> contractBaseInfoList = clientContractsMap.get(clientId);
                    Map<Long, InnerClientHelper> helperMap = new HashMap<>();
                    for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
                        // 主承租人/联合承租人/债权人/债务人
                        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
                        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
                            for (ContractTenantry contractTenantry : contractTenantryList) {
                                ClientRole clientRole = this.ensureClientRole(contractTenantry.getLesseeType());
                                if (Objects.isNull(clientRole)) {
                                    continue;
                                }
                                helperMap.putIfAbsent(contractTenantry.getLesseeId(), new InnerClientHelper(contractTenantry.getLesseeId(), new HashSet<>()));
                                helperMap.get(contractTenantry.getLesseeId()).getClientRoleNames().add(clientRole.name());
                            }
                        }
                        // 担保人
                        Optional<List<Long>> optionalLongList = contractGuarantorService.getGuaranteeIdByContractIds(Collections.singletonList(contractBaseInfo.getId()));
                        if (optionalLongList.isPresent()) {
                            for (Long id : optionalLongList.get()) {
                                helperMap.putIfAbsent(id, new InnerClientHelper(id, new HashSet<>()));
                                helperMap.get(id).getClientRoleNames().add(ClientRole.GUARANTEE.name());
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(helperMap)) {
                        List<Client> cList = clientMapper.selectBatchIds(helperMap.keySet());
                        Map<Long, Client> cMap = cList.stream().collect(Collectors.toMap(Client::getId, e -> e));
                        for (InnerClientHelper innerClientHelper : helperMap.values()) {
                            Client c = cMap.get(innerClientHelper.getClientId());
                            if (Objects.isNull(c)) {
                                continue;
                            }
                            for (String clientRoleName : innerClientHelper.getClientRoleNames()) {
                                NewAfterLeaseCheckExternalQueryClientInfo clientInfo = new NewAfterLeaseCheckExternalQueryClientInfo();
                                clientInfo.setQueryId(queryId);
                                clientInfo.setClientRole(clientRoleName);
                                clientInfo.setClientId(c.getId());
                                clientInfo.setClientType(c.getClientType());
                                clientInfos.add(clientInfo);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(clientInfos)) {
                        queryClientInfoService.saveBatch(clientInfos);
                    }
                } catch (Exception e) {
                    log.error("租后检查-外部查询任务-生成外部查询任务记录异常[clientId:{}]", clientId, e);
                }
            });
        } catch (Exception e) {
            log.error("租后检查-外部查询任务执行异常", e);
        }
    }

    private ClientRole ensureClientRole(String type) {
        if (Objects.equals(type, LesseeTypeEnum.MAIN_LESSSEE.name())) {
            return ClientRole.MAIN_LESSEE;
        }
        if (Objects.equals(type, LesseeTypeEnum.JOINT_LESSEE.name())) {
            return ClientRole.LESSEE;
        }
        if (Objects.equals(type, CreditorDebtorTypeEnum.CREDITOR.name())) {
            return ClientRole.CREDITOR;
        }
        if (Objects.equals(type, CreditorDebtorTypeEnum.DEBTOR.name())) {
            return ClientRole.DEBTOR;
        }
        return null;
    }

    @AllArgsConstructor
    @Getter
    private static class InnerClientHelper {
        private final Long clientId;
        private final Set<String> clientRoleNames;
    }
}
