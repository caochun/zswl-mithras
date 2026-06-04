package cn.zswltech.mithras.projectprocess.mapper.lib.projpricing;

import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.projectprocess.service.projpricing.dto.ProjPricingPriceDto;
import cn.zswltech.mithras.projectprocess.service.riskcontrol.dto.ProjReviewPriceDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目定价-债权转让报价方案lib表 Mapper 接口
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
public interface ProjPricingAocPriceLibMapper extends CustomBaseMapper<ProjPricingAocPriceLib> {
    List<ProjPricingAocPriceLib> listNewestPrice(@Param("dto") ProjPricingPriceDto dto);

}
