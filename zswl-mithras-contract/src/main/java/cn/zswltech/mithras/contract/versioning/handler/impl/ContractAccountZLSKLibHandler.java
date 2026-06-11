package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.versioning.handler.AbstractContractAccountLibHandler;
import org.springframework.stereotype.Service;


/**
 * @author
 * @description
 * @since
 */
@Service
public class ContractAccountZLSKLibHandler extends AbstractContractAccountLibHandler {


    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.BANK_ACCOUNT_ZLSK;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

    @Override
    public ContractAccountUseEnum getAccountUseEnum() {
        return ContractAccountUseEnum.ZLSK;
    }
}
