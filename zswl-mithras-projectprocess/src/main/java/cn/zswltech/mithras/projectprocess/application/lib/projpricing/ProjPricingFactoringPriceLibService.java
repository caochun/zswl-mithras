package cn.zswltech.mithras.projectprocess.application.lib.projpricing;

import cn.zswltech.mithras.dto.projpricing.price.ProjPricingFactoringPriceRSP;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingFactoringPriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/8/2 10:34 上午
 **/
public interface ProjPricingFactoringPriceLibService extends IService<ProjPricingFactoringPriceLib> {

    ProjPricingFactoringPriceRSP getOldEdition(Long projId);

    ProjPricingFactoringPriceLib getByProjPricingIdAndVersion(Long ProjPricingId, String version);

    List<ProjPricingFactoringPriceLib> listNewestByProjPricingIds(Set<Long> ProjPricingIds);
}
