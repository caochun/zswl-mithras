package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.shareholder.NewCorpShareholderInfoListRSP;
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
public class NewCorpShareholderInfoLibHandlerImpl extends ClientLibAbstractHandler<NewCorpShareholderInfoLib, NewCorpShareholderInfo, NewCorpShareholderInfoListRSP> {

    @Override
    protected NewCorpShareholderInfoLib entity2Lib(NewCorpShareholderInfo newCorpShareholderInfo) {
        NewCorpShareholderInfoLib newCorpShareholderInfoLib = BeanUtil.copyProperties(newCorpShareholderInfo, NewCorpShareholderInfoLib.class);
        return newCorpShareholderInfoLib;
    }

    @Override
    protected NewCorpShareholderInfo lib2Entity(NewCorpShareholderInfoLib newCorpShareholderInfoLib) {
        NewCorpShareholderInfo newCorpShareholderInfo = BeanUtil.copyProperties(newCorpShareholderInfoLib, NewCorpShareholderInfo.class);
        return newCorpShareholderInfo;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_SHAREHOLDER;
    }

    @Override
    protected NewCorpShareholderInfoListRSP lib2Rsp(NewCorpShareholderInfoLib f) {
        NewCorpShareholderInfoListRSP rsp = BeanUtil.copyProperties(f, NewCorpShareholderInfoListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public List<NewCorpShareholderInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
