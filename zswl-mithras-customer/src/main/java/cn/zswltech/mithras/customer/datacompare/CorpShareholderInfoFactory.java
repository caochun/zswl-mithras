package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpShareholderInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpShareholderInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpShareholderInfoLibHandlerImpl;
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
        return new DefaultDataCompare<CorpShareholderInfo, CorpShareholderInfoLib, CorpShareholderInfoListRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}