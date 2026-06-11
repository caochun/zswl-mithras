package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.bondinfo.NewCorpBondInfoListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NewCorpBondInfoLibHandlerImpl extends ClientLibAbstractHandler<NewCorpBondInfoLib, NewCorpBondInfo, NewCorpBondInfoListRSP> {

    @Override
    protected NewCorpBondInfoLib entity2Lib(NewCorpBondInfo newCorpBondInfo) {
        NewCorpBondInfoLib newCorpBondInfoLib = BeanUtil.copyProperties(newCorpBondInfo, NewCorpBondInfoLib.class);
        return newCorpBondInfoLib;
    }

    @Override
    protected NewCorpBondInfo lib2Entity(NewCorpBondInfoLib newCorpBondInfoLib) {
        NewCorpBondInfo newCorpBondInfo = BeanUtil.copyProperties(newCorpBondInfoLib, NewCorpBondInfo.class);
        return newCorpBondInfo;
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
    protected NewCorpBondInfoListRSP lib2Rsp(NewCorpBondInfoLib f) {
        NewCorpBondInfoListRSP rsp = BeanUtil.copyProperties(f, NewCorpBondInfoListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public List<NewCorpBondInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
