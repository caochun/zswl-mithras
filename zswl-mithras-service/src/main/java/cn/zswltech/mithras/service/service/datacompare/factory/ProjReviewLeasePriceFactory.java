package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.impl.ProjReviewLeasePriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("projReviewLeasePrice")
public class ProjReviewLeasePriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjReviewLeasePriceLibMapper libMapper;
    @Resource
    private ProjReviewLeasePriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjReviewLeasePrice, ProjReviewLeasePriceLib, ProjReviewLeasePriceRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_REVIEW.name(), version);
    }
}