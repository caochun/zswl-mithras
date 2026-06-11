package cn.zswltech.mithras.fund.mapper.financing;

import cn.zswltech.mithras.fund.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
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
