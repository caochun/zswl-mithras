package cn.zswltech.mithras.contract.core;

import cn.zswltech.mithras.contract.model.contract.ContractPledgeItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/10/10
 * @description
 */
public interface ContractPledgeItemService extends IService<ContractPledgeItem> {
    void removeByPledgeId(Long pledgeId);

    List<ContractPledgeItem> listByPledgeId(Long pledgeId);
}
