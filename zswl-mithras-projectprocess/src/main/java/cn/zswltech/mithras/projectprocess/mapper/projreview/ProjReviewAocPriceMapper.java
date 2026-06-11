package cn.zswltech.mithras.projectprocess.mapper.projreview;

import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPrice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @description 债权转让报价方案
* @author zhaozhengkang
* @date 2022-08-01
*/
public interface ProjReviewAocPriceMapper extends CustomBaseMapper<ProjReviewAocPrice> {

    @Select("SELECT * FROM proj_review_aoc_price where project_id=#{mainId}")
    ProjReviewAocPrice selectByMainId(@Param("mainId") Long mainId);
}