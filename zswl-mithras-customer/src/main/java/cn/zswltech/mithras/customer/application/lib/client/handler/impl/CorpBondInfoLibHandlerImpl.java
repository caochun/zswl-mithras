package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.model.client.CorpBondInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpBondInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class CorpBondInfoLibHandlerImpl extends ClientLibAbstractHandler<CorpBondInfoLib, CorpBondInfo, CorpBondInfoListRSP> {

    @Override
    protected CorpBondInfoLib entity2Lib(CorpBondInfo corpBondInfo) {
        CorpBondInfoLib corpBondInfoLib = BeanUtil.copyProperties(corpBondInfo, CorpBondInfoLib.class);
        return corpBondInfoLib;
    }

    @Override
    protected CorpBondInfo lib2Entity(CorpBondInfoLib corpBondInfoLib) {
        CorpBondInfo corpBondInfo = BeanUtil.copyProperties(corpBondInfoLib, CorpBondInfo.class);
        return corpBondInfo;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_BOND;
    }

    @Override
    protected CorpBondInfoListRSP lib2Rsp(CorpBondInfoLib f) {
        CorpBondInfoListRSP rsp = BeanUtil.copyProperties(f, CorpBondInfoListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public List<CorpBondInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
