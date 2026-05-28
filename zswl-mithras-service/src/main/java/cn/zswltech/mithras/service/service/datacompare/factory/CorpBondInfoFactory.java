package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpBondInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpBondInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpBondInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpBondInfoLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("corpBondInfo")
public class CorpBondInfoFactory implements EditdataCompareFactory {

    @Resource
    private CorpBondInfoLibMapper libMapper;
    @Resource
    private CorpBondInfoLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<CorpBondInfo, CorpBondInfoLib, CorpBondInfoListRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.CLIENT.name(), version);
    }
}