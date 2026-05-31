package cn.zswltech.mithras.service.mapper.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface FundFinancingBaseInfoLibMapper extends CustomBaseMapper<FundFinancingBaseInfoLib> {

    /**
     * 寻找最新版本的融资lib id列表
     * 用作查询
     */
    List<Long> queryLastestVersionLibIdList(@Param("financingIdList") List<Long> financingIdList);


    List<FundFinancingBaseInfoLib> queryLastestVersionLibList(@Param("financingIdList") Collection<Long> financingIdList);

    List<FundFinancingBaseInfoLib> queryLastestVersionLibs(@Param("dto") FinancingQueryDto dto);
}
