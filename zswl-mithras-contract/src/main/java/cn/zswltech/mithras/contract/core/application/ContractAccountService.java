package cn.zswltech.mithras.contract.core.application;

import cn.zswltech.mithras.dto.contract.account.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @description 合同-收款账户表
* @author vico
* @date 2022-08-12
*/
public interface ContractAccountService extends IService<ContractAccount> {

    String add(ContractAccountAddREQ req);

    void modify(List<ContractAccountModifyREQ> req);

    List<ContractAccountListRSP> list(ContractAccountListREQ req);

    Boolean remove(ContractAccountRemoveREQ req);

    List<ContractAccount> listByContractUse(Long contractId, String accountUse);
}