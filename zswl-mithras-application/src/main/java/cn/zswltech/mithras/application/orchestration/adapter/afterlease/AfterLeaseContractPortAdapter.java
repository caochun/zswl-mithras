package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseContractApprovalContext;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseContractSnapshot;
import cn.zswltech.mithras.afterlease.enums.ClientRole;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AfterLeaseContractPortAdapter implements AfterLeaseContractPort {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractTenantryService contractTenantryService;

    @Override
    public List<AfterLeaseContractSnapshot> listAllStartRent() {
        return contractBaseInfoService.listAllStartRent()
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<AfterLeaseContractSnapshot> listActiveByClientId(Long clientId) {
        return contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, clientId)
                .in(ContractBaseInfo::getContractStatus, Arrays.asList(
                        ContractStatus.TAKE_EFFECT.name(),
                        ContractStatus.START_RENT.name(),
                        ContractStatus.SETTLE.name())))
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<AfterLeaseContractSnapshot> listInRentContract(Long clientId) {
        return contractBaseInfoService.listInRentContract(clientId)
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public AfterLeaseContractApprovalContext getApprovalContextById(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (contractBaseInfo == null) {
            return null;
        }
        AfterLeaseContractApprovalContext context = new AfterLeaseContractApprovalContext();
        context.setClientId(contractBaseInfo.getClientId());
        context.setBizDeptId(contractBaseInfo.getBizDeptId());
        context.setBizDeptLeaderId(contractBaseInfo.getBizDeptLeaderId());
        context.setBizDivisionLeaderId(contractBaseInfo.getBizDivisionLeaderId());
        context.setBizType(contractBaseInfo.getBizType());
        context.setProjName(contractBaseInfo.getProjName());
        context.setProjCosponsorUserIds(contractBaseInfo.getProjCosponsorUserIds());
        context.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
        return context;
    }

    @Override
    public void markOverdueCollectionNotified(Long contractId) {
        ContractBaseInfo contractBaseInfo = new ContractBaseInfo();
        contractBaseInfo.setId(contractId);
        contractBaseInfo.setOverdueCollectionFlag(Long.valueOf(YesOrNoNumberEnum.YES.getCode()));
        contractBaseInfoService.updateById(contractBaseInfo);
    }

    @Override
    public Long getStockRiskExposure(Long clientId) {
        return contractBaseInfoService.getStockRiskExposure(clientId, null, null);
    }

    @Override
    public Map<Long, Set<String>> clientRoleNamesByContractId(Long contractId) {
        Map<Long, Set<String>> clientRoleNames = new HashMap<>();
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractId);
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            for (ContractTenantry contractTenantry : contractTenantryList) {
                ClientRole clientRole = toClientRole(contractTenantry.getLesseeType());
                if (clientRole == null) {
                    continue;
                }
                clientRoleNames
                        .computeIfAbsent(contractTenantry.getLesseeId(), item -> new HashSet<>())
                        .add(clientRole.name());
            }
        }
        Optional<List<Long>> guaranteeIds = contractGuarantorService.getGuaranteeIdByContractIds(Collections.singletonList(contractId));
        guaranteeIds.ifPresent(ids -> ids.forEach(id -> clientRoleNames
                .computeIfAbsent(id, item -> new HashSet<>())
                .add(ClientRole.GUARANTEE.name())));
        return clientRoleNames;
    }

    private AfterLeaseContractSnapshot toSnapshot(ContractBaseInfo contractBaseInfo) {
        AfterLeaseContractSnapshot snapshot = new AfterLeaseContractSnapshot();
        snapshot.setId(contractBaseInfo.getId());
        snapshot.setClientId(contractBaseInfo.getClientId());
        snapshot.setContractCode(contractBaseInfo.getContractCode());
        snapshot.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
        return snapshot;
    }

    private ClientRole toClientRole(String type) {
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
}
