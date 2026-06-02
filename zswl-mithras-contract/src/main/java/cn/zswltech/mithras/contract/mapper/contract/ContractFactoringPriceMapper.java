package cn.zswltech.mithras.contract.mapper.contract;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
* @description 保理报价方案
* @author
* @date 2022-08-01
*/
@Repository
public interface ContractFactoringPriceMapper extends CustomBaseMapper<ContractFactoringPrice> {
    @Select("SELECT * FROM contract_factoring_price where project_id=#{mainId}")
    ContractFactoringPrice selectByMainId(@Param("mainId") Long mainId);
}