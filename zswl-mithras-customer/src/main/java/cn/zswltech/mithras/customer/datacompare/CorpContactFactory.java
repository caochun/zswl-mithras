package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpContactInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.CorpContactInfo;
import cn.zswltech.mithras.customer.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.versioning.handler.impl.CorpContactInfoLibHandlerImpl;
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
