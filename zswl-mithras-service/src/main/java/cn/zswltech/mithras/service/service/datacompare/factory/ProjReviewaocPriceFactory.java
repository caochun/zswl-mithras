package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.impl.ProjReviewAocPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("projReviewAocPrice")
public class ProjReviewaocPriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjReviewAocPriceLibMapper libMapper;
    @Resource
    private ProjReviewAocPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjReviewAocPrice, ProjReviewAocPriceLib, ProjReviewAocPriceRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_REVIEW.name(), version);
    }
}