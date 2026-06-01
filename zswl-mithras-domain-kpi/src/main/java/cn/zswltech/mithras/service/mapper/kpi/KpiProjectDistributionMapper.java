package cn.zswltech.mithras.service.mapper.kpi;

import cn.zswltech.mithras.service.mapper.kpi.query.KpiProjectDistributionQuery;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistribution;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.service.bo.KpiProjectDistributionBO;
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
