package cn.zswltech.mithras.customer.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.client.contactinfo.NewCorpContactInfoListRSP;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.corp.CorpContactInfoMapper;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.versioning.handler.ClientLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static cn.hutool.core.bean.BeanUtil.copyProperties;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Component
public class NewCorpContactInfoLibHandlerImpl extends ClientLibAbstractHandler<NewCorpContactInfoLib, NewCorpContactInfo, NewCorpContactInfoListRSP> {

    @Resource
    private CorpContactInfoMapper corpContactInfoMapper;

    @Override
    protected NewCorpContactInfoLib entity2Lib(NewCorpContactInfo newCorpContactInfo) {
        NewCorpContactInfoLib newCorpContactInfoLib = BeanUtil.copyProperties(newCorpContactInfo, NewCorpContactInfoLib.class);
        return newCorpContactInfoLib;
    }

    @Override
    protected NewCorpContactInfo lib2Entity(NewCorpContactInfoLib newCorpContactInfoLib) {
        NewCorpContactInfo newCorpContactInfo = BeanUtil.copyProperties(newCorpContactInfoLib, NewCorpContactInfo.class);
        return newCorpContactInfo;
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
    protected NewCorpContactInfoListRSP lib2Rsp(NewCorpContactInfoLib f) {
        NewCorpContactInfoListRSP rsp = BeanUtil.copyProperties(f, NewCorpContactInfoListRSP.class);
        rsp.setId(f.getOriginId());
        rsp.setCreateTime(f.getDataCreateTime());
        return rsp;
    }

    @Override
    public void validateData(Client client) {
        if (!needHandle(client.getId(), ClientType.of(client.getClientType()))) {
            return;
        }
        Long userId = AccountUtil.getLoginInfo().getId();
        List<NewCorpContactInfo> newCorpContactInfoList = draftMapper.selectList(
                Wrappers.<NewCorpContactInfo>lambdaQuery()
                        .eq(NewCorpContactInfo::getClientId, client.getId())
                        .eq(NewCorpContactInfo::getUserId, userId));
        if (CollectionUtil.isEmpty(newCorpContactInfoList)) {
            throw new MithrasException("联系人信息为空，请维护联系人信息后再提交流程");
        }
        for (NewCorpContactInfo newCorpContactInfo : newCorpContactInfoList) {
            boolean nameIsBlank = StrUtil.isBlank(newCorpContactInfo.getName());
            boolean positionIsBlank = StrUtil.isBlank(newCorpContactInfo.getPosition());
            boolean telephoneIsBlank = StrUtil.isBlank(newCorpContactInfo.getTelephone());
            if (nameIsBlank || positionIsBlank || telephoneIsBlank) {
                throw new MithrasException("联系人的<姓名><职务><电话>不能为空");
            }
        }
    }

    @Override
    public List<NewCorpContactInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }
}
