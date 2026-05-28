package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.dto.contract.price.ContractAocPriceModifyREQ;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAocPrice;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
public interface ContractAocPriceService extends IService<ContractAocPrice> {
    void modify(@Valid ContractAocPriceModifyREQ req);

    Long sumApplyByContractId(List<Long> ids);

    ContractAocPrice getByContractId(Long contractId);

    List<ContractAocPrice> listByContractIds(List<Long> contractIds);
}
