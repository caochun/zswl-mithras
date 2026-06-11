package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.versioning.handler.impl.CorpCommerceInfoLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("corpCommerceInfo")
public class CorpCommerceFactory implements EditdataCompareFactory {

    @Resource
    private CorpCommerceInfoLibMapper libMapper;
    @Resource
    private CorpCommerceInfoLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<CorpCommerceInfo, CorpCommerceInfoLib, CorpCommerceInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}