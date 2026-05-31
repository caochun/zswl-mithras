package cn.zswltech.mithras.service.mapper.projestablish;

import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-07-19
 */
public interface ProjEstablishAocPriceMapper extends CustomBaseMapper<ProjEstablishAocPrice> {
    @Select("SELECT * FROM proj_establish_aoc_price where proj_establish_id=#{mainId}")
    ProjEstablishAocPrice selectByMainId(@Param("mainId") Long mainId);
}