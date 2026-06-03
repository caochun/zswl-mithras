package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper;

import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DashboardOperationPayMapper {

    List<DashboardOperationPayResult> payList(DashboardOperationPayQuery query);

    List<DashboardOperationCapacityResult> capacityList(DashboardOperationCapacityQuery query);
}
