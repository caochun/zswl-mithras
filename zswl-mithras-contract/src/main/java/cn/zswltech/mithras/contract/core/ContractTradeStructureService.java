package cn.zswltech.mithras.contract.core;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import cn.zswltech.mithras.contract.core.ContractMortgageService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTradeStructureRoleEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractTradeStructureMapper;
import cn.zswltech.mithras.contract.model.contract.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/9/17
 * @description
 */
@Slf4j
@Service
public class ContractTradeStructureService extends ServiceImpl<ContractTradeStructureMapper, ContractTradeStructure> {
    public List<Long> listClientIdsByContractId(Long contractId) {
        LambdaQueryWrapper<ContractTradeStructure> query = Wrappers.lambdaQuery();
        query.eq(ContractTradeStructure::getContractId, contractId);
        return this.list(query).stream().map(ContractTradeStructure::getClientId).distinct().collect(Collectors.toList());
    }

    public Set<Long> listContractIdsByClientId(Long clientId) {
        LambdaQueryWrapper<ContractTradeStructure> query = Wrappers.lambdaQuery();
        query.eq(ContractTradeStructure::getClientId, clientId);
        return this.list(query).stream().map(ContractTradeStructure::getContractId).collect(Collectors.toSet());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void syncTradeStructure(Long contractId, ContractTradeStructureRoleEnum roleEnum) {
        List<ContractTradeStructure> toInsertList = this.buildList(contractId, roleEnum);
        SpringUtil.getBean(ContractTradeStructureService.class).remove(
                Wrappers.<ContractTradeStructure>lambdaQuery()
                        .eq(ContractTradeStructure::getContractId, contractId)
                        .eq(ContractTradeStructure::getRole, roleEnum.name())
        );
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            SpringUtil.getBean(ContractTradeStructureService.class).saveBatch(toInsertList);
        }
    }

    private List<ContractTradeStructure> buildList(Long contractId, ContractTradeStructureRoleEnum roleEnum) {
        List<ContractTradeStructure> toInsertList = new LinkedList<>();
        switch (roleEnum) {
            case LESSEE: {
                List<ContractTenantry> contractTenantryList = SpringUtil.getBean(ContractTenantryService.class).listByContractId(contractId);
                if (CollectionUtil.isEmpty(contractTenantryList)) {
                    break;
                }
                for (ContractTenantry contractTenantry : contractTenantryList) {
                    ContractTradeStructure contractTradeStructure = new ContractTradeStructure();
                    contractTradeStructure.setContractId(contractTenantry.getContractId());
                    contractTradeStructure.setClientId(contractTenantry.getLesseeId());
                    if (StrUtil.equals(contractTenantry.getLesseeType(), CreditorDebtorTypeEnum.CREDITOR.name())) {
                        contractTradeStructure.setRole(ContractTradeStructureRoleEnum.CREDITOR.name());
                    } else if (StrUtil.equals(contractTenantry.getLesseeType(), CreditorDebtorTypeEnum.DEBTOR.name())) {
                        contractTradeStructure.setRole(ContractTradeStructureRoleEnum.DEBTOR.name());
                    } else {
                        contractTradeStructure.setRole(ContractTradeStructureRoleEnum.LESSEE.name());
                    }
                    toInsertList.add(contractTradeStructure);
                }
                break;
            }
            case GUARANTOR: {
                List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractId(contractId);
                if (CollectionUtil.isEmpty(contractGuarantorList)) {
                    break;
                }
                Set<Long> clientIds = new HashSet<>();
                for (ContractGuarantor contractGuarantor : contractGuarantorList) {
                    if (StrUtil.isBlank(contractGuarantor.getGuarantorIds())) {
                        continue;
                    }
                    List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
                    if (CollectionUtil.isNotEmpty(ids)) {
                        clientIds.addAll(ids);
                    }
                }
                for (Long clientId : clientIds) {
                    ContractTradeStructure contractTradeStructure = new ContractTradeStructure();
                    contractTradeStructure.setContractId(contractId);
                    contractTradeStructure.setClientId(clientId);
                    contractTradeStructure.setRole(ContractTradeStructureRoleEnum.GUARANTOR.name());
                    toInsertList.add(contractTradeStructure);
                }
                break;
            }
            case MORTGAGE: {
                List<ContractMortgage> contractMortgageList = SpringUtil.getBean(ContractMortgageService.class).listByContractId(contractId);
                if (CollectionUtil.isEmpty(contractMortgageList)) {
                    break;
                }
                Set<Long> clientIds = new HashSet<>();
                for (ContractMortgage contractMortgage : contractMortgageList) {
                    if (StrUtil.isBlank(contractMortgage.getMortgageIds())) {
                        continue;
                    }
                    List<Long> ids = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
                    if (CollectionUtil.isNotEmpty(ids)) {
                        clientIds.addAll(ids);
                    }
                }
                for (Long clientId : clientIds) {
                    ContractTradeStructure contractTradeStructure = new ContractTradeStructure();
                    contractTradeStructure.setContractId(contractId);
                    contractTradeStructure.setClientId(clientId);
                    contractTradeStructure.setRole(ContractTradeStructureRoleEnum.MORTGAGE.name());
                    toInsertList.add(contractTradeStructure);
                }
                break;
            }
            case PLEDGE: {
                List<ContractPledge> contractPledgeList = SpringUtil.getBean(ContractPledgeService.class).listByContractId(contractId);
                if (CollectionUtil.isEmpty(contractPledgeList)) {
                    break;
                }
                Set<Long> clientIds = new HashSet<>();
                for (ContractPledge contractPledge : contractPledgeList) {
                    if (StrUtil.isBlank(contractPledge.getPledgeIds())) {
                        continue;
                    }
                    List<Long> ids = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
                    if (CollectionUtil.isNotEmpty(ids)) {
                        clientIds.addAll(ids);
                    }
                }
                for (Long clientId : clientIds) {
                    ContractTradeStructure contractTradeStructure = new ContractTradeStructure();
                    contractTradeStructure.setContractId(contractId);
                    contractTradeStructure.setClientId(clientId);
                    contractTradeStructure.setRole(ContractTradeStructureRoleEnum.PLEDGE.name());
                    toInsertList.add(contractTradeStructure);
                }
                break;
            }
            default: {
                // do nothing
            }
        }
        return toInsertList;
    }
}
