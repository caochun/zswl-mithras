package cn.zswltech.mithras.service.mapper.projreview;

import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @description 保理报价方案
* @author zhaozhengkang
* @date 2022-08-01
*/
public interface ProjReviewFactoringPriceMapper extends CustomBaseMapper<ProjReviewFactoringPrice> {
    @Select("SELECT * FROM proj_review_factoring_price where project_id=#{mainId}")
    ProjReviewFactoringPrice selectByMainId(@Param("mainId") Long mainId);
}