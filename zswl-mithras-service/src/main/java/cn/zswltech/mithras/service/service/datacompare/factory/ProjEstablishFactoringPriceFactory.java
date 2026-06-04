package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.impl.ProjEstablishFactoringPriceLibHandler;
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
        return new DefaultDataCompare<ProjEstablishFactoringPrice, ProjEstablishFactoringPriceLib, ProjEstablishFactoringPriceRSP>(rsps,libMapper,handler, commonVersionMapper,BusinessModuleEnum.PROJ_ESTABLISH.name(), version);
    }
}
