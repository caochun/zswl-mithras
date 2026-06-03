package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
