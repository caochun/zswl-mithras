package cn.zswltech.mithras.service.mapper.dashboard;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardOperationCapacityQuery;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardOperationCapacityResult;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardOperationPayQuery;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardOperationPayResult;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardCorpCommerceInfoLibDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardOperationTimeMapper {

    List<CorpCommerceInfoLib> listNewestCommerceInfo(@Param("dto") DashboardCorpCommerceInfoLibDto dto);
}
