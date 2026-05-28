package cn.zswltech.mithras.service.mapper.projestablish;

import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-07-19
 */
public interface ProjEstablishFactoringPriceMapper extends CustomBaseMapper<ProjEstablishFactoringPrice> {
    @Select("SELECT * FROM proj_establish_factoring_price where proj_establish_id=#{mainId}")
    ProjEstablishFactoringPrice selectByMainId(@Param("mainId") Long mainId);

}