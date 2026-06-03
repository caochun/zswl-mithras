package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingCollectAccountLib;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingFeeDetailLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FundFinancingFeeDetailLibMapper extends CustomBaseMapper<FundFinancingFeeDetailLib> {
}
