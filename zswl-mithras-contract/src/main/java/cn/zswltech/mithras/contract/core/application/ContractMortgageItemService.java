package cn.zswltech.mithras.contract.core.application;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgageItem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ContractMortgageItemService extends IService<ContractMortgageItem> {
    List<ContractMortgageItem> listByMortgageId(Long mortgageId);

    void removeByMortgageId(Long mortgageId);
}
