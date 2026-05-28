package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.projestablish.ProjEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.projestablish.handler.impl.ProjEstablishBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("projEstablishBaseInfo")
public class ProjEstablishBaseInfoFactory implements EditdataCompareFactory {

    @Resource
    private ProjEstablishBaseInfoLibMapper libMapper;
    @Resource
    private ProjEstablishBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjEstablishBaseInfo,ProjEstablishBaseInfoLib, ProjEstablishBaseInfoListRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_ESTABLISH.name(), version);
    }
}
