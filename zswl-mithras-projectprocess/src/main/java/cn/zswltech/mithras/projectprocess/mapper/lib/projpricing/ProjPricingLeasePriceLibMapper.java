package cn.zswltech.mithras.projectprocess.mapper.lib.projpricing;

import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePriceLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.projectprocess.application.projpricing.dto.ProjPricingPriceDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目定价-租赁报价方案表 Mapper 接口
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
public interface ProjPricingLeasePriceLibMapper extends CustomBaseMapper<ProjPricingLeasePriceLib> {

    List<ProjPricingLeasePriceLib> listNewestPrice(@Param("dto") ProjPricingPriceDto setProjPricingIds);
}
