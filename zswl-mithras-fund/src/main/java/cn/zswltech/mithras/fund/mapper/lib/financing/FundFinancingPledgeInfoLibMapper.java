package cn.zswltech.mithras.fund.mapper.lib.financing;

import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FundFinancingPledgeInfoLibMapper extends CustomBaseMapper<FundFinancingPledgeInfoLib> {

    /**
     * 寻找最新版本的质押物清单lib id列表
     * 用作查询
     */
    List<Long> queryLastestVersionLibIdList(@Param("financingIdList") List<Long> financingIdList);

    List<FundFinancingPledgeInfoLib> getLastestByContractId(@Param("contractId") Long contractId);
}
