package cn.zswltech.mithras.kpi.mapper;

import cn.zswltech.mithras.kpi.dto.persistence.KpiProjectDistributionQuery;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionWeight;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
public interface KpiProjectDistributionWeightMapper extends CustomBaseMapper<KpiProjectDistributionWeight> {
    /**
     * 获取项目id
     * @param query 参数
     * @return 结果
     */
    List<Long> listProjectDistributionIdsByCondition(@Param("query") KpiProjectDistributionQuery query);
}
