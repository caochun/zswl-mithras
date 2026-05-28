package cn.zswltech.mithras.service.mapper.lib.fund.financing;

import cn.zswltech.mithras.service.mapper.dto.FundFinancingRepayActualDTO;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActualLib;
import cn.zswltech.mithras.service.mapper.model.margin.MarginBaseInfo;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface FundFinancingRepayActualLibMapper extends CustomBaseMapper<FundFinancingRepayActualLib> {


    List<FundFinancingRepayActualLib> queryNewestLibs(@Param("financingIds") Set<Long> financingIds,
                                                      @Param("startDate") LocalDate start,
                                                      @Param("endDate") LocalDate end);

    Page<FundFinancingRepayActualLib> stockPageList(Page<MarginBaseInfo> page, @Param("dto") FundFinancingRepayActualDTO dto);

}
