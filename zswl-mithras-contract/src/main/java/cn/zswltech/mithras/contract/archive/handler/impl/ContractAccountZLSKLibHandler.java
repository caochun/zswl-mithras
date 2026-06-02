package cn.zswltech.mithras.contract.archive.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccountLib;
import cn.zswltech.mithras.contract.archive.handler.AbstractContractAccountLibHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
