package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewFactoringPriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.projreview.handler.impl.ProjReviewFactoringPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("projReviewFactoringPrice")
public class ProjReviewFactoringPriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjReviewFactoringPriceLibMapper libMapper;
    @Resource
    private ProjReviewFactoringPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjReviewFactoringPrice, ProjReviewFactoringPriceLib, ProjReviewFactoringPriceRSP>(rsps,libMapper,handler, commonVersionMapper,BusinessModuleEnum.PROJ_REVIEW.name(), version);
    }
}
