package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Component
public class CorpContactInfoLibHandlerImpl extends ClientLibAbstractHandler<CorpContactInfoLib, CorpContactInfo, CorpContactInfoListRSP> {

    @Override
    protected CorpContactInfoLib entity2Lib(CorpContactInfo corpContactInfo) {
        CorpContactInfoLib corpContactInfoLib = BeanUtil.copyProperties(corpContactInfo, CorpContactInfoLib.class);
        return corpContactInfoLib;
    }

    @Override
    protected CorpContactInfo lib2Entity(CorpContactInfoLib corpContactInfoLib) {
        CorpContactInfo corpContactInfo = BeanUtil.copyProperties(corpContactInfoLib, CorpContactInfo.class);
        return corpContactInfo;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_CONTACT;
    }

    @Override
    protected CorpContactInfoListRSP lib2Rsp(CorpContactInfoLib f) {
        CorpContactInfoListRSP rsp = BeanUtil.copyProperties(f, CorpContactInfoListRSP.class);
        rsp.setId(f.getOriginId());
        rsp.setCreateTime(f.getDataCreateTime());
        return rsp;
    }

    @Override
    public void validateData(Client client) {
        if (!needHandle(client.getId(), ClientType.of(client.getClientType()))) {
            return;
        }
//        List<CorpContactInfo> corpContactInfoList = draftMapper.selectList(
//                Wrappers.<CorpContactInfo>lambdaQuery()
//                        .eq(ClientBaseModel::getClientId, client.getId()));
//        if (CollectionUtil.isEmpty(corpContactInfoList)) {
//            throw new MithrasException("联系人信息为空，请维护联系人信息后再提交流程");
//        }
//        for (CorpContactInfo corpContactInfo : corpContactInfoList) {
//            boolean nameIsBlank = StrUtil.isBlank(corpContactInfo.getName());
//            boolean positionIsBlank = StrUtil.isBlank(corpContactInfo.getPosition());
//            boolean telephoneIsBlank = StrUtil.isBlank(corpContactInfo.getTelephone());
//            if (nameIsBlank || positionIsBlank || telephoneIsBlank) {
//                throw new MithrasException("联系人的<姓名><职务><电话>不能为空");
//            }
//        }
    }

    @Override
    public List<CorpContactInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }
}
