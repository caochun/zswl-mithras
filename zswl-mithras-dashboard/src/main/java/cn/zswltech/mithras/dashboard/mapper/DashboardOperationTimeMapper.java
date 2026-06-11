package cn.zswltech.mithras.dashboard.mapper;

import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.dashboard.model.DashboardOperationCapacityQuery;
import cn.zswltech.mithras.dashboard.model.DashboardOperationCapacityResult;
import cn.zswltech.mithras.dashboard.model.DashboardOperationPayQuery;
import cn.zswltech.mithras.dashboard.model.DashboardOperationPayResult;
import cn.zswltech.mithras.dashboard.model.DashboardCorpCommerceInfoLibDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardOperationTimeMapper {

    List<CorpCommerceInfoLib> listNewestCommerceInfo(@Param("dto") DashboardCorpCommerceInfoLibDto dto);
}
