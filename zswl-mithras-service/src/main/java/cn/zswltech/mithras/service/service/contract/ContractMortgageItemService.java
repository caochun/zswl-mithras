package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageItem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ContractMortgageItemService extends IService<ContractMortgageItem> {
    List<ContractMortgageItem> listByMortgageId(Long mortgageId);

    void removeByMortgageId(Long mortgageId);
}
