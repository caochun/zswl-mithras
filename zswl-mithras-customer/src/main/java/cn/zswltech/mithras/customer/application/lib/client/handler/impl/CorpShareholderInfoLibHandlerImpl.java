package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.customer.model.client.CorpShareholderInfoLib;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class CorpShareholderInfoLibHandlerImpl extends ClientLibAbstractHandler<CorpShareholderInfoLib, CorpShareholderInfo, CorpShareholderInfoListRSP> {

    @Override
    protected CorpShareholderInfoLib entity2Lib(CorpShareholderInfo corpShareholderInfo) {
        CorpShareholderInfoLib corpShareholderInfoLib = BeanUtil.copyProperties(corpShareholderInfo, CorpShareholderInfoLib.class);
        return corpShareholderInfoLib;
    }

    @Override
    protected CorpShareholderInfo lib2Entity(CorpShareholderInfoLib corpShareholderInfoLib) {
        CorpShareholderInfo corpShareholderInfo = BeanUtil.copyProperties(corpShareholderInfoLib, CorpShareholderInfo.class);
        return corpShareholderInfo;
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
    protected CorpShareholderInfoListRSP lib2Rsp(CorpShareholderInfoLib f) {
        CorpShareholderInfoListRSP rsp = BeanUtil.copyProperties(f, CorpShareholderInfoListRSP.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public List<CorpShareholderInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
