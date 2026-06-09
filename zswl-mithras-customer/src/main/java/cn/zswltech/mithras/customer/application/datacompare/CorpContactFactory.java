package cn.zswltech.mithras.customer.application.datacompare;

import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpContactInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpContactInfoLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("corpContactInfo")
public class CorpContactFactory implements EditdataCompareFactory {

    @Resource
    private CorpContactInfoLibMapper libMapper;
    @Resource
    private CorpContactInfoLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<CorpContactInfo, CorpContactInfoLib, CorpContactInfoListRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}
