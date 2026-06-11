package cn.zswltech.mithras.fund.mapper.financing;

import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.fund.application.financing.bo.FundPledgeSupervisedBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FundFinancingPledgeInfoMapper extends CustomBaseMapper<FundFinancingPledgeInfo> {

    List<FundPledgeSupervisedBO> getFundSupervisedBo(@Param("contractIds") List<Long> contractIds);
}
