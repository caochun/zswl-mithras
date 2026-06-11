package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpBondInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.CorpBondInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpBondInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpBondInfoLibHandlerImpl;
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
        return new DefaultDataCompare<CorpBondInfo, CorpBondInfoLib, CorpBondInfoListRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}