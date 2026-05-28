package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpShareholderInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpShareholderInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpShareholderInfoLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("corpShareholderInfo")
public class CorpShareholderInfoFactory implements EditdataCompareFactory {

    @Resource
    private CorpShareholderInfoLibMapper libMapper;
    @Resource
    private CorpShareholderInfoLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<CorpShareholderInfo, CorpShareholderInfoLib, CorpShareholderInfoListRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.CLIENT.name(), version);
    }
}