package cn.zswltech.mithras.projectprocess.mapper.lib.projpricing;

import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingFactoringPriceLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.projectprocess.application.projpricing.dto.ProjPricingPriceDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目定价-保理报价方案表 Mapper 接口
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
public interface ProjPricingFactoringPriceLibMapper extends CustomBaseMapper<ProjPricingFactoringPriceLib> {

    List<ProjPricingFactoringPriceLib> listNewestPrice(@Param("dto") ProjPricingPriceDto setProjPricingIds);
}
