package cn.zswltech.mithras.service.service.leaseholdproperty;

import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemListRowData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/25
 * @description
 */
public interface LeaseItemListRowDataService extends IService<LeaseItemListRowData> {
    void removeByLeaseItemInfoId(Long leaseItemInfoId);

    List<LeaseItemListRowData> listByLeaseItemInfoId(Long leaseItemInfoId);

    List<LeaseItemListRowData> listFreeItem(Long leaseItemInfoId, Collection<Long> contractIds);
}
