package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.dto.contract.price.ContractFactoringPriceModifyREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.service.mapper.model.contract.ContractFactoringPrice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
public interface ContractFactoringPriceService extends IService<ContractFactoringPrice> {
    Boolean modify(ContractFactoringPriceModifyREQ req);

    Boolean modifyChange(ContractFactoringPriceModifyREQ req);

    ContractFactoringPrice detail(ContractPriceDetailREQ req);

    Long sumApplyByContractId(List<Long> ids);

    ContractFactoringPrice getByContractId(Long contractId);

    List<ContractFactoringPrice> listByContractIds(List<Long> contractIds);
}
