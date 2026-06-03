package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.fund.application.capital.write_off.bo.FundPledgeSupervisedBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FundFinancingPledgeInfoMapper extends CustomBaseMapper<FundFinancingPledgeInfo> {

    List<FundPledgeSupervisedBO> getFundSupervisedBo(@Param("contractIds") List<Long> contractIds);
}
