package cn.zswltech.mithras.service.convert.workbench;

import cn.zswltech.mithras.dto.workbench.WorkbenchCardMetricListRsp;
import cn.zswltech.mithras.service.mapper.model.workbench.WorkbenchCardMetric;
import org.mapstruct.Mapper;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Mapper(componentModel = "spring")
public interface WorkbenchCardMetricConverter {

    WorkbenchCardMetricListRsp entity2ListRsp(WorkbenchCardMetric workbenchCardMetrics);
}
