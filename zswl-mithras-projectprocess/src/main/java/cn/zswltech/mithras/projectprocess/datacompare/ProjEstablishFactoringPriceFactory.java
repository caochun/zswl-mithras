package cn.zswltech.mithras.projectprocess.datacompare;

import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.versioning.projestablish.handler.impl.ProjEstablishFactoringPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("projEstablishFactoringPrice")
public class ProjEstablishFactoringPriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjEstablishFactoringPriceLibMapper libMapper;
    @Resource
    private ProjEstablishFactoringPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjEstablishFactoringPrice, ProjEstablishFactoringPriceLib, ProjEstablishFactoringPriceRSP>(rsps,libMapper,handler, commonVersionMapper,"PROJ_ESTABLISH", version);
    }
}
