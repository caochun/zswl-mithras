package cn.zswltech.mithras.service.mapper.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
