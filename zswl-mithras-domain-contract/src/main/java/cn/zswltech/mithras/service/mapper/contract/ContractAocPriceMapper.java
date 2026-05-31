package cn.zswltech.mithras.service.mapper.contract;

import cn.zswltech.mithras.service.mapper.model.contract.ContractAocPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @description 债权转让报价方案
* @author
* @date 2022-08-01
*/
@Repository
public interface ContractAocPriceMapper extends CustomBaseMapper<ContractAocPrice> {

    @Select("SELECT * FROM contract_aoc_price where project_id=#{mainId}")
    ContractAocPrice selectByMainId(@Param("mainId") Long mainId);
}