package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.NormalBaseInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.NormalBaseInfo;
import cn.zswltech.mithras.customer.model.client.NormalBaseInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.NormalBaseInfoLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("normalBaseInfo")
public class NormalBaseInfoFactory implements EditdataCompareFactory {

    @Resource
    private NormalBaseInfoLibMapper libMapper;
    @Resource
    private NormalBaseInfoLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NormalBaseInfo, NormalBaseInfoLib, NormalBaseInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}