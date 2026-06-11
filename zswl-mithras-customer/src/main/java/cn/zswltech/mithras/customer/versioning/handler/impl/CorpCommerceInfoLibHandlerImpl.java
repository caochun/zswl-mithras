package cn.zswltech.mithras.customer.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoGroupNameService;
import cn.zswltech.mithras.customer.constant.LackDataMsg;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.model.client.IndustryType;
import cn.zswltech.mithras.customer.versioning.handler.ClientLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class CorpCommerceInfoLibHandlerImpl extends ClientLibAbstractHandler<CorpCommerceInfoLib, CorpCommerceInfo, CorpCommerceInfoDetailRSP> {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private CorpCommerceInfoGroupNameService groupNameService;

    @Override
    protected CorpCommerceInfoLib entity2Lib(CorpCommerceInfo corpCommerceInfo) {
        CorpCommerceInfoLib corpCommerceInfoLib = BeanUtil.copyProperties(corpCommerceInfo, CorpCommerceInfoLib.class);
        return corpCommerceInfoLib;
    }

    @Override
    protected CorpCommerceInfo lib2Entity(CorpCommerceInfoLib corpCommerceInfoLib) {
        CorpCommerceInfo corpCommerceInfo = BeanUtil.copyProperties(corpCommerceInfoLib, CorpCommerceInfo.class);
        return corpCommerceInfo;
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
//        List<CorpCommerceInfo> dataList = draftMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
//                .eq(CorpCommerceInfo::getClientId, client.getId()));
//        Util.errLackData(CollectionUtils.isEmpty(dataList), LackDataMsg.COMMERCE);
    }

    @Override
    protected CorpCommerceInfoDetailRSP lib2Rsp(CorpCommerceInfoLib f) {
        CorpCommerceInfoDetailRSP rsp = BeanUtil.copyProperties(f, CorpCommerceInfoDetailRSP.class);
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
    public List<CorpCommerceInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }
}
