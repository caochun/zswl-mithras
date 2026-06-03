package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.DashboardCorpCommerceInfoLibDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardOperationConversionMapper {

    List<CorpCommerceInfoLib> listNewestCommerceInfo(@Param("dto") DashboardCorpCommerceInfoLibDto dto);
}
