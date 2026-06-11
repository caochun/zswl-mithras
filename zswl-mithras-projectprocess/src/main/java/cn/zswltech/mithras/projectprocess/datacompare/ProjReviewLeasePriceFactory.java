package cn.zswltech.mithras.projectprocess.datacompare;

import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl.ProjReviewLeasePriceLibHandler;
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
        return new DefaultDataCompare<ProjReviewLeasePrice, ProjReviewLeasePriceLib, ProjReviewLeasePriceRSP>(rsps, libMapper, handler, commonVersionMapper,"PROJ_REVIEW", version);
    }
}