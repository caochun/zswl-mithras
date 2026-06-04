package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishLeasePriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.impl.ProjEstablishLeasePriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("projEstablishLeasePrice")
public class ProjLeasePriceFactory implements EditdataCompareFactory {

    @Resource
    private ProjEstablishLeasePriceLibMapper libMapper;
    @Resource
    private ProjEstablishLeasePriceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjEstablishLeasePrice, ProjEstablishLeasePriceLib, ProjEstablishLeasePriceRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_ESTABLISH.name(), version);
    }
}