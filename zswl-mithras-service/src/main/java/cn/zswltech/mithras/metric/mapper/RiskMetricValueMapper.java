package cn.zswltech.mithras.metric.mapper;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricValue;
import cn.zswltech.mithras.metric.mapper.model.condition.RiskMetricValueListConditions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author yibin
 */
public interface RiskMetricValueMapper extends BaseMapper<RiskMetricValue> {

    Page<RiskMetricValue> list(Page<RiskMetricValue> page, @Param("conditions") RiskMetricValueListConditions conditions);
}
