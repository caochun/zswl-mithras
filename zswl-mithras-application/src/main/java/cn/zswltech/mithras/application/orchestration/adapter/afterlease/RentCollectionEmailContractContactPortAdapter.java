package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.RentCollectionEmailContractContactPort;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.customer.versioning.handler.impl.CorpContactInfoLibHandlerImpl;
import cn.zswltech.mithras.customer.model.client.CorpContactInfoLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

@Component
public class RentCollectionEmailContractContactPortAdapter implements RentCollectionEmailContractContactPort {
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private CorpContactInfoLibHandlerImpl corpContactInfoLibHandler;

    @Override
    public String findReceiverMail(Long contractId) {
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(contractId);
        ContractTenantryLib contractTenantry = contractTenantryLibMapper.selectOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, contractId)
                .eq(ContractTenantryLib::getLesseeId, contractBaseInfoLib.getClientId())
                .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                .isNotNull(ContractTenantryLib::getContactId)
                .last("LIMIT 1"));
        if (Objects.isNull(contractTenantry)) {
            return null;
        }
        CorpContactInfoLib corpContactInfoLib = corpContactInfoLibHandler.queryLatestDataByLibId(contractTenantry.getContactId());
        return Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getMail).orElse(null);
    }
}
