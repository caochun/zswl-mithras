package cn.zswltech.mithras.kpi.mapper;

import cn.zswltech.mithras.kpi.dto.persistence.KpiProjectDistributionQuery;
import cn.zswltech.mithras.kpi.model.KpiProjectDistribution;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.kpi.bo.KpiProjectDistributionBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
public interface KpiProjectDistributionMapper extends CustomBaseMapper<KpiProjectDistribution> {
    List<KpiProjectDistributionBO> myPageList(@Param("query") KpiProjectDistributionQuery query);

    int myPageListCount(@Param("query") KpiProjectDistributionQuery query);
}
