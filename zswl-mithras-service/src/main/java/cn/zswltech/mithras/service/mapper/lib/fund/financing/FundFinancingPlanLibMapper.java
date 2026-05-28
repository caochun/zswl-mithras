package cn.zswltech.mithras.service.mapper.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface FundFinancingPlanLibMapper extends CustomBaseMapper<FundFinancingPlanLib> {

    List<FundFinancingPlanLib> queryLastestVersionLibs(@Param("financingIds") Set<Long> financingIds);
}
