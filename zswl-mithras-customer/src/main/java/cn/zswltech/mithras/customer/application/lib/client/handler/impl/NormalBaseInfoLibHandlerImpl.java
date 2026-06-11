package cn.zswltech.mithras.customer.application.lib.client.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.customer.constant.LackDataMsg;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.customer.mapper.model.client.NormalBaseInfo;
import cn.zswltech.mithras.customer.mapper.model.client.NormalBaseInfoLib;
import cn.zswltech.mithras.foundation.exception.LackDataException;
import cn.zswltech.mithras.customer.application.lib.client.handler.ClientLibAbstractHandler;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    private ClientMapper clientMapper;
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
        errLackData(CollectionUtil.isEmpty(dataList), LackDataMsg.NORMAL_BASE);
    }

    @Override
    protected NormalBaseInfoDetailRSP lib2Rsp(NormalBaseInfoLib f) {
        NormalBaseInfoDetailRSP rsp = BeanUtil.copyProperties(f, NormalBaseInfoDetailRSP.class);
        rsp.setId(f.getOriginId());
        Client client = clientMapper.selectById(f.getClientId());
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

    private void errLackData(boolean condition, String msg) {
        if (condition) {
            throw new LackDataException(msg);
        }
    }
}
