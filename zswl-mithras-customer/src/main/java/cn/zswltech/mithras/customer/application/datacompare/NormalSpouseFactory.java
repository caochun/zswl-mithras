package cn.zswltech.mithras.customer.application.datacompare;

import cn.zswltech.mithras.dto.client.normal.NormalSpouseListRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.NormalSpouseLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NormalSpouse;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NormalSpouseLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.NormalSpouseLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("normalSpouse")
public class NormalSpouseFactory implements EditdataCompareFactory {

    @Resource
    private NormalSpouseLibMapper libMapper;
    @Resource
    private NormalSpouseLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NormalSpouse, NormalSpouseLib, NormalSpouseListRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}