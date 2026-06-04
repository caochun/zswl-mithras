package cn.zswltech.mithras.projectprocess.service.lib.projpricing;

import cn.zswltech.mithras.dto.projpricing.price.ProjPricingLeasePriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/8/2 10:34 上午
 **/
public interface ProjPricingLeasePriceLibService extends IService<ProjPricingLeasePriceLib> {

    ProjPricingLeasePriceRSP getOldEdition(Long projId);

    ProjPricingLeasePriceLib getByProjPricingIdAndVersion(Long ProjPricingId, String version);

    List<ProjPricingLeasePriceLib> listNewestByProjPricingIds(Set<Long> ProjPricingIds);
}
