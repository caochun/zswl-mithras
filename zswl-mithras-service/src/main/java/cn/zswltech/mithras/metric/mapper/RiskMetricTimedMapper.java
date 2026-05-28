package cn.zswltech.mithras.metric.mapper;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.mapper.model.condition.RiskMetricTimedCustomConditions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author yibin
 */
public interface RiskMetricTimedMapper extends BaseMapper<RiskMetricTimed> {

    Long stat(@Param("conditions") RiskMetricTimedCustomConditions conditions);
}
