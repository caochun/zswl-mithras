package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.dto.contract.price.ContractLeasePriceModifyREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author vico
 * @description 合同-租赁报价方案表
 * @date 2022-08-12
 */
public interface ContractLeasePriceService extends IService<ContractLeasePrice> {


    Boolean modify(ContractLeasePriceModifyREQ req);

    Boolean modifyChange(ContractLeasePriceModifyREQ req);

    ContractLeasePrice detail(ContractPriceDetailREQ req);

    Long sumApplyByContractId(List<Long> ids);

    ContractLeasePrice getByContractId(Long contractId);

    List<ContractLeasePrice> listByContractIds(List<Long> contractIds);
}