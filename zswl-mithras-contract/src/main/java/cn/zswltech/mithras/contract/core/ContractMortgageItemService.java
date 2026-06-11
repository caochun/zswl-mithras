package cn.zswltech.mithras.contract.core;

import cn.zswltech.mithras.contract.model.contract.ContractMortgageItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ContractMortgageItemService extends IService<ContractMortgageItem> {
    List<ContractMortgageItem> listByMortgageId(Long mortgageId);

    void removeByMortgageId(Long mortgageId);
}
