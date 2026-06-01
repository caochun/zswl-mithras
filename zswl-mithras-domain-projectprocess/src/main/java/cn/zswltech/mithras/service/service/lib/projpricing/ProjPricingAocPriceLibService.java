package cn.zswltech.mithras.service.service.lib.projpricing;

import cn.zswltech.mithras.dto.projpricing.price.ProjPricingAocPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:34 上午
 **/
public interface ProjPricingAocPriceLibService extends IService<ProjPricingAocPriceLib> {

    ProjPricingAocPriceRSP getOldEdition(Long projId);

    ProjPricingAocPriceLib getByProjPricingIdAndVersion(Long projPricingId, String version);

    List<ProjPricingAocPriceLib> listNewestByProjPricingIds(Set<Long> projPricingIds);

}
