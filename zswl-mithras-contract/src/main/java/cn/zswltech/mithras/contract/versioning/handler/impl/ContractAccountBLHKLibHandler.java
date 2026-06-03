package cn.zswltech.mithras.contract.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccountLib;
import cn.zswltech.mithras.contract.versioning.handler.AbstractContractAccountLibHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/11/7
 * @description
 */
@Service
public class ContractAccountBLHKLibHandler extends AbstractContractAccountLibHandler {


    @Override
    public ContractLibModelEnum getSubModule() {
        return ContractLibModelEnum.BANK_ACCOUNT_BLHK;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    public ContractAccountUseEnum getAccountUseEnum() {
        return ContractAccountUseEnum.BLHK;
    }
}
