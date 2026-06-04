package cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.ProjReviewLibAbstractHandler;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjReviewFactoringPriceLibHandler
        extends ProjReviewLibAbstractHandler<ProjReviewFactoringPriceLib, ProjReviewFactoringPrice, ProjReviewFactoringPriceRSP> {
    @Resource
    private ProjReviewBaseInfoService baseInfoService;
    @Resource
    private ContractService contractService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;

    @Override
    protected ProjReviewFactoringPriceLib entity2Lib(ProjReviewFactoringPrice f) {
        return BeanUtil.copyProperties(f, ProjReviewFactoringPriceLib.class);
    }

    @Override
    protected ProjReviewFactoringPrice lib2Entity(ProjReviewFactoringPriceLib t) {
        return BeanUtil.copyProperties(t, ProjReviewFactoringPrice.class);
    }

    @Override
    protected ProjReviewFactoringPriceRSP lib2Rsp(ProjReviewFactoringPriceLib f) {
        ProjReviewFactoringPriceRSP rsp = BeanUtil.copyProperties(f, ProjReviewFactoringPriceRSP.class);
        rsp.setContracts(projReviewPriceService.getSurvivingContract(rsp.getProjectId()));
        rsp.setId(f.getOriginId());
        rsp.setApprovedAmount(f.getProjectApprovalAmount());
        return rsp;
    }

    @Override
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.BL_PRICE;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
