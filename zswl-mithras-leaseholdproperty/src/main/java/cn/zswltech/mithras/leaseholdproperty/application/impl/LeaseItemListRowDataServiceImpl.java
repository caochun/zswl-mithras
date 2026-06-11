package cn.zswltech.mithras.leaseholdproperty.application.impl;

import cn.zswltech.mithras.leaseholdproperty.mapper.LeaseItemListRowDataMapper;
import cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemListRowData;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemListRowDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/25
 * @description
 */
@Service
public class LeaseItemListRowDataServiceImpl extends ServiceImpl<LeaseItemListRowDataMapper, LeaseItemListRowData> implements LeaseItemListRowDataService {
    @Override
    public void removeByLeaseItemInfoId(Long leaseItemInfoId) {
        LambdaQueryWrapper<LeaseItemListRowData> query = Wrappers.lambdaQuery();
        query.eq(LeaseItemListRowData::getLeaseItemInfoId, leaseItemInfoId);
        this.remove(query);
    }

    @Override
    public List<LeaseItemListRowData> listByLeaseItemInfoId(Long leaseItemInfoId) {
        LambdaQueryWrapper<LeaseItemListRowData> query = Wrappers.lambdaQuery();
        query.eq(LeaseItemListRowData::getLeaseItemInfoId, leaseItemInfoId);
        return this.list(query);
    }

    @Override
    public List<LeaseItemListRowData> listFreeItem(Long leaseItemInfoId, Collection<Long> contractIds) {
        return this.getBaseMapper().listFreeItem(leaseItemInfoId, contractIds);
    }
}
