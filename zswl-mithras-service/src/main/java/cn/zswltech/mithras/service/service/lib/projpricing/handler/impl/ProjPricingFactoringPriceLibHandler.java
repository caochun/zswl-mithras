package cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingFactoringPriceRSP;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingInfoModule;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingFactoringPriceLib;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.ProjPricingLibAbstractHandler;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingPriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjPricingFactoringPriceLibHandler
        extends ProjPricingLibAbstractHandler<ProjPricingFactoringPriceLib, ProjPricingFactoringPrice, ProjPricingFactoringPriceRSP> {
    @Resource
    private ProjPricingBaseInfoService baseInfoService;
    @Resource
    private ContractService contractService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;

    @Override
    protected ProjPricingFactoringPriceLib entity2Lib(ProjPricingFactoringPrice f) {
        return BeanUtil.copyProperties(f, ProjPricingFactoringPriceLib.class);
    }

    @Override
    protected ProjPricingFactoringPrice lib2Entity(ProjPricingFactoringPriceLib t) {
        return BeanUtil.copyProperties(t, ProjPricingFactoringPrice.class);
    }

    @Override
    protected ProjPricingFactoringPriceRSP lib2Rsp(ProjPricingFactoringPriceLib f) {
        ProjPricingFactoringPriceRSP rsp = BeanUtil.copyProperties(f, ProjPricingFactoringPriceRSP.class);
        rsp.setContracts(projPricingPriceService.getSurvivingContract(rsp.getProjectId()));
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public ProjPricingInfoModule getSubModule() {
        return ProjPricingInfoModule.BL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
