package cn.zswltech.mithras.service.mapper.dashboard;

import cn.zswltech.mithras.service.mapper.model.dashboard.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DashboardOperationPayMapper {

    List<DashboardOperationPayResult> payList(DashboardOperationPayQuery query);

    List<DashboardOperationCapacityResult> capacityList(DashboardOperationCapacityQuery query);
}
