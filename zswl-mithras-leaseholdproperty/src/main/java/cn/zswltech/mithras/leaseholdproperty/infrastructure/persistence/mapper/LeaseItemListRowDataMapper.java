package cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper;

import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemListRowData;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/25
 * @description
 */
public interface LeaseItemListRowDataMapper extends CustomBaseMapper<LeaseItemListRowData> {
    List<LeaseItemListRowData> listFreeItem(@Param("leaseItemInfoId") Long leaseItemInfoId, @Param("contractIds") Collection<Long> contractIds);
}
