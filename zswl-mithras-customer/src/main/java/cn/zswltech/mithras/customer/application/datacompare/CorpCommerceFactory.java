package cn.zswltech.mithras.customer.application.datacompare;

import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpCommerceInfoLibHandlerImpl;
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