package cn.zswltech.mithras.service.mapper.fund.financing;

import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingFeeDetail;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingFeeDetail;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @description 间融-费用明细
* @author zhaozhengkang
* @date 2023-06-17
*/
@Mapper
public interface FundFinancingFeeDetailMapper extends CustomBaseMapper<FundFinancingFeeDetail> {

}