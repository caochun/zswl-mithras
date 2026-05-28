package cn.zswltech.mithras.service.mapper.projreview;

import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @description 租赁报价方案
* @author zhaozhengkang
* @date 2022-08-01
*/
public interface ProjReviewLeasePriceMapper extends CustomBaseMapper<ProjReviewLeasePrice> {
    @Select("SELECT * FROM proj_review_lease_price where project_id=#{mainId}")
    ProjReviewLeasePrice selectByMainId(@Param("mainId") Long mainId);
}