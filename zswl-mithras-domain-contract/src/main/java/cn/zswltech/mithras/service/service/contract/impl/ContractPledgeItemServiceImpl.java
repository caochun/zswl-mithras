package cn.zswltech.mithras.service.service.contract.impl;

import cn.zswltech.mithras.service.mapper.contract.ContractPledgeItemMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledgeItem;
import cn.zswltech.mithras.service.service.contract.ContractPledgeItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/10/10
 * @description
 */
@Service
public class ContractPledgeItemServiceImpl extends ServiceImpl<ContractPledgeItemMapper, ContractPledgeItem> implements ContractPledgeItemService {
    @Override
    public void removeByPledgeId(Long pledgeId) {
        LambdaQueryWrapper<ContractPledgeItem> query = Wrappers.lambdaQuery();
        query.eq(ContractPledgeItem::getPledgeId, pledgeId);
        this.remove(query);
    }

    @Override
    public List<ContractPledgeItem> listByPledgeId(Long pledgeId) {
        LambdaQueryWrapper<ContractPledgeItem> query = Wrappers.lambdaQuery();
        query.eq(ContractPledgeItem::getPledgeId, pledgeId);
        return this.list(query);
    }
}
