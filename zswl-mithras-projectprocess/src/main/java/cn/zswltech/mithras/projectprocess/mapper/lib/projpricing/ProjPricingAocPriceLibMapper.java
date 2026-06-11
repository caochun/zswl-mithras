package cn.zswltech.mithras.projectprocess.mapper.lib.projpricing;

import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.projectprocess.application.projpricing.dto.ProjPricingPriceDto;
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
