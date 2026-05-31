package cn.zswltech.mithras.service.mapper.monthly;

import cn.zswltech.mithras.service.mapper.model.monthly.FundsDailyCost;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description
 */
public interface FundsDailyCostMapper extends CustomBaseMapper<FundsDailyCost> {
    @Select("select main_id as mainId, ifnull(sum(financing_cost),0) as sumAmount, ifnull(sum(financing_cost_after_tax),0) as sumAmountAfterTax from funds_daily_cost where deleted = 0 and main_id > 0 and interest_date >= #{queryStartDate} and interest_date <= #{queryEndDate} group by main_id")
    List<SumGroupMainBO> selectSumGroupByMainId(@Param("queryStartDate") LocalDate queryStartDate, @Param("queryEndDate") LocalDate queryEndDate);

    @Select("select ifnull(sum(financing_cost), 0) from funds_daily_cost where deleted = 0 and main_id = #{mainId} and interest_date >= #{startDate} and interest_date <= #{endDate}")
    Long sumCostByMainId(@Param("mainId") Long mainId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("select ifnull(sum(financing_cost), 0) from funds_daily_cost where deleted = 0 and main_id = #{mainId} and financing_product_id = #{productId} and interest_date >= #{startDate} and interest_date <= #{endDate}")
    Long sumCostByMainIdAndProductId(@Param("mainId") Long mainId, @Param("productId") Long productId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Data
    class SumGroupMainBO {
        private Long mainId;
        private Long sumAmount;
        private Long sumAmountAfterTax;
    }
}
