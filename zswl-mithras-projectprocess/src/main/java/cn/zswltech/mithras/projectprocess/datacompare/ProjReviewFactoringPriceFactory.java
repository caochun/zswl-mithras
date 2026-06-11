package cn.zswltech.mithras.projectprocess.datacompare;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewFactoringPriceLibHandler;
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
        return new DefaultDataCompare<ProjReviewFactoringPrice, ProjReviewFactoringPriceLib, ProjReviewFactoringPriceRSP>(rsps,libMapper,handler, commonVersionMapper,"PROJ_REVIEW", version);
    }
}
