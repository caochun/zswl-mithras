package cn.zswltech.mithras.fund.persistence.mapper.financing;

import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.fund.application.financing.model.FundPledgeSupervisedBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FundFinancingPledgeInfoMapper extends CustomBaseMapper<FundFinancingPledgeInfo> {

    List<FundPledgeSupervisedBO> getFundSupervisedBo(@Param("contractIds") List<Long> contractIds);
}
