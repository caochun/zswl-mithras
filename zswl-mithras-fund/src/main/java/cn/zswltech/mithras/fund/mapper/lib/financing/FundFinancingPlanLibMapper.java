package cn.zswltech.mithras.fund.mapper.lib.financing;

import cn.zswltech.mithras.fund.model.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface FundFinancingPlanLibMapper extends CustomBaseMapper<FundFinancingPlanLib> {

    List<FundFinancingPlanLib> queryLastestVersionLibs(@Param("financingIds") Set<Long> financingIds);
}
