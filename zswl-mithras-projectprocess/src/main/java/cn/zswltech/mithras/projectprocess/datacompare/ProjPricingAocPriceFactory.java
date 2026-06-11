package cn.zswltech.mithras.projectprocess.datacompare;

import cn.zswltech.mithras.dto.projpricing.price.ProjPricingAocPriceRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingAocPrice;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingAocPriceLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.handler.impl.ProjPricingAocPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("projPricingAocPrice")
public class ProjPricingAocPriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjPricingAocPriceLibMapper libMapper;
    @Resource
    private ProjPricingAocPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjPricingAocPrice, ProjPricingAocPriceLib, ProjPricingAocPriceRSP>(rsps, libMapper, handler, commonVersionMapper,"PROJ_PRICING", version);
    }
}