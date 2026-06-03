package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingFeeDetail;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @description 间融-费用明细
* @author zhaozhengkang
* @date 2023-06-17
*/
@Mapper
public interface FundFinancingFeeDetailMapper extends CustomBaseMapper<FundFinancingFeeDetail> {

}
