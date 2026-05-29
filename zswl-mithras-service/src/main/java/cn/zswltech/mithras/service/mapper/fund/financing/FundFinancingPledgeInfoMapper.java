package cn.zswltech.mithras.service.mapper.fund.financing;

import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.service.capital.write_off.bo.FundPledgeSupervisedBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FundFinancingPledgeInfoMapper extends CustomBaseMapper<FundFinancingPledgeInfo> {

    List<FundPledgeSupervisedBO> getFundSupervisedBo(@Param("contractIds") List<Long> contractIds);
}
