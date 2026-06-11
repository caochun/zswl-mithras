package cn.zswltech.mithras.projectprocess.mapper.projestablish;

import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zhaozhengkang
 * @description 租赁报价方案表
 * @date 2022-07-19
 */
public interface ProjEstablishLeasePriceMapper extends CustomBaseMapper<ProjEstablishLeasePrice> {
    @Select("SELECT * FROM proj_establish_lease_price where proj_establish_id=#{mainId}")
    ProjEstablishLeasePrice selectByMainId(@Param("mainId") Long mainId);
}