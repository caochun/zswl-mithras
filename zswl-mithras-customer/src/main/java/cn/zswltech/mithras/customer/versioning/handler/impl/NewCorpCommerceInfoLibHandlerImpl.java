package cn.zswltech.mithras.customer.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.client.commerceinfo.NewCorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoGroupNameService;
import cn.zswltech.mithras.customer.constant.LackDataMsg;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.model.client.*;
import cn.zswltech.mithras.foundation.exception.LackDataException;
import cn.zswltech.mithras.customer.versioning.handler.ClientLibAbstractHandler;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NewCorpCommerceInfoLibHandlerImpl extends ClientLibAbstractHandler<NewCorpCommerceInfoLib, NewCorpCommerceInfo, NewCorpCommerceInfoDetailRSP> {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private CorpCommerceInfoGroupNameService groupNameService;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;

    @Override
    protected NewCorpCommerceInfoLib entity2Lib(NewCorpCommerceInfo newCorpCommerceInfo) {
        NewCorpCommerceInfoLib newCorpCommerceInfoLib = BeanUtil.copyProperties(newCorpCommerceInfo, NewCorpCommerceInfoLib.class);
        return newCorpCommerceInfoLib;
    }

    @Override
    protected NewCorpCommerceInfo lib2Entity(NewCorpCommerceInfoLib newCorpCommerceInfoLib) {
        NewCorpCommerceInfo newCorpCommerceInfo = BeanUtil.copyProperties(newCorpCommerceInfoLib, NewCorpCommerceInfo.class);
        return newCorpCommerceInfo;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_COMMERCE;
    }

    @Override
    public void validateData(Client client) {
        if (!needHandle(client.getId(), ClientType.of(client.getClientType()))) {
            return;
        }
        Long userId = AccountUtil.getLoginInfo().getId();
        List<NewCorpCommerceInfo> dataList = draftMapper.selectList(Wrappers.<NewCorpCommerceInfo>lambdaQuery()
                .eq(NewCorpCommerceInfo::getClientId, client.getId())
                .eq(NewCorpCommerceInfo::getUserId, userId));
        errLackData(CollectionUtil.isEmpty(dataList), LackDataMsg.COMMERCE);
    }

    @Override
    protected NewCorpCommerceInfoDetailRSP lib2Rsp(NewCorpCommerceInfoLib f) {
        NewCorpCommerceInfoDetailRSP rsp = BeanUtil.copyProperties(f, NewCorpCommerceInfoDetailRSP.class);
        rsp.setId(f.getOriginId());
        Client client = clientMapper.selectById(rsp.getClientId());
        if (Objects.nonNull(client)) {
            rsp.setClientName(client.getClientName());
            rsp.setUscCode(client.getUscCode());
            rsp.setClientType(client.getClientType());
        }
        IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery()
                .eq(IndustryType::getCode, rsp.getIndustryType())
                .last("LIMIT 1"));
        if (Objects.nonNull(industryType)) {
            rsp.setIndustryTypeName(industryType.getDisplay());
        }
        rsp.setBelongGroupClientName(groupNameService.queryBelongGroupClientName(rsp.getClientId(), rsp.getBelongGroupClientId(), rsp.getClientName()));
        return rsp;
    }

    @Override
    public List<NewCorpCommerceInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

    private void errLackData(boolean condition, String msg) {
        if (condition) {
            throw new LackDataException(msg);
        }
    }
}
