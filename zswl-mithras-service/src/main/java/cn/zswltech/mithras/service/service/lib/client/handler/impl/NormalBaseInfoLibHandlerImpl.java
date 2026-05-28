package cn.zswltech.mithras.service.service.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.service.constant.LackDataMsg;
import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.service.mapper.model.client.NormalBaseInfo;
import cn.zswltech.mithras.service.mapper.model.client.NormalBaseInfoLib;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.lib.client.handler.ClientLibAbstractHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
@Service
public class NormalBaseInfoLibHandlerImpl extends ClientLibAbstractHandler<NormalBaseInfoLib, NormalBaseInfo, NormalBaseInfoDetailRSP> {

    @Resource
    private ClientService clientService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    protected NormalBaseInfoLib entity2Lib(NormalBaseInfo normalBaseInfo) {
        NormalBaseInfoLib normalBaseInfoLib = BeanUtil.copyProperties(normalBaseInfo, NormalBaseInfoLib.class);
        return normalBaseInfoLib;
    }

    @Override
    protected NormalBaseInfo lib2Entity(NormalBaseInfoLib normalBaseInfoLib) {
        NormalBaseInfo normalBaseInfo = BeanUtil.copyProperties(normalBaseInfoLib, NormalBaseInfo.class);
        return normalBaseInfo;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.NORMAL.equals(clientType);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.NORMAL_BASE;
    }

    @Override
    public void validateData(Client client) {
        if (!needHandle(client.getId(), ClientType.of(client.getClientType()))) {
            return;
        }
        List<NormalBaseInfo> dataList = draftMapper.selectList(Wrappers.<NormalBaseInfo>lambdaQuery()
                .eq(NormalBaseInfo::getClientId, client.getId()));
        Util.errLackData(CollectionUtils.isEmpty(dataList), LackDataMsg.NORMAL_BASE);
    }

    @Override
    protected NormalBaseInfoDetailRSP lib2Rsp(NormalBaseInfoLib f) {
        NormalBaseInfoDetailRSP rsp = BeanUtil.copyProperties(f, NormalBaseInfoDetailRSP.class);
        rsp.setId(f.getOriginId());
        Client client = clientService.getById(f.getClientId());
        if (isNotNull(client)) {
            rsp.setClientType(client.getClientType());
            rsp.setClientName(client.getClientName());
            rsp.setCertNumber(client.getCertNumber());
            rsp.setCertType(client.getCertType());
        }
        AddressDictionary addressDictionary = addressDictionaryMapper
                .selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getCode, rsp.getCountry()));
        if (isNotNull(addressDictionary)) {
            rsp.setCountryName(addressDictionary.getDisplay());
        }
        return rsp;
    }

    @Override
    public List<NormalBaseInfo> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        return this.listNeedHandleEntity(mainId);
    }

}
