package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.projestablish.ProjEstablishAocPriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishAocPriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.projestablish.handler.impl.ProjEstablishAocPriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("projEstablishAocPrice")
public class ProjaocPriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjEstablishAocPriceLibMapper libMapper;
    @Resource
    private ProjEstablishAocPriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjEstablishAocPrice, ProjEstablishAocPriceLib, ProjEstablishAocPriceRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_ESTABLISH.name(), version);
    }
}