package cn.zswltech.mithras.service.mapper.fund.financing;

import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FundFinancingPlanMapper extends CustomBaseMapper<FundFinancingPlan> {

    /**
     *查询生效、起息的利率类型为浮动利率的融资方案
     * @return
     */
    List<FundFinancingPlan> queryEffectFloatPlan(@Param("financingCode") String financingCode);
}
